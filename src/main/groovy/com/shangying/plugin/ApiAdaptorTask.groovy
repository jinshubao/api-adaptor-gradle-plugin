package com.shangying.plugin

import com.shangying.plugin.model.*
import com.shangying.plugin.utils.StringUtils
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

/**
 *
 * Created by jinshubao on 2017/1/23.
 */
class ApiAdaptorTask extends DefaultTask {
    protected String definitionsPrefix = "#/definitions/"
    protected Map models = [:]
    protected ApiAdaptorConfig myConfig


    @TaskAction
    void generate() {
        myConfig = project["apiAdaptorConfig"] as ApiAdaptorConfig
        def url = "http://${myConfig.swaggerApiHost}:${myConfig.swaggerApiPort}/swagger-resources"

        def buff = new StringBuffer()
        url.toURL().eachLine(StandardCharsets.UTF_8.toString()) {
            buff << it
        }

        def resources = new JsonSlurper().parseText(buff.toString()) as List

        resources.each {
            def name = it["name"] as String
            def location = it["location"] as String
            if (location.contains("?")) {
                def strings = location.split("\\?")
                def args = strings[1].split("&")
                location = strings[0] + "?"
                args.each {
                    def param = it.split("=")
                    location = location + param[0] + "=" + URLEncoder.encode(param[1], StandardCharsets.UTF_8.toString())
                }
            }
            url = "http://${myConfig.swaggerApiHost}:${myConfig.swaggerApiPort}${location}"
            buff.length = 0
            url.toURL().eachLine(StandardCharsets.UTF_8.toString()) {
                buff << it
            }
            def json = new JsonSlurper().parseText(buff.toString()) as Map
            def paths = json["paths"] as Map
            def definitions = json["definitions"] as Map
            generateAdaptorClass(myConfig.adaptorClassName, name, paths, definitions)
        }
    }


    void generateAdaptorClass(String adaptorName, String groupName, Map paths, Map definitions) {
        def builder = ClassFileBuilder.createClassFile(myConfig.adaptorPackage, adaptorName).imports("${myConfig.requestModelPackage}.*", "${myConfig.responseModelPackage}.*")
        def comment = ""
        if (myConfig.adaptorComment) {
            comment += myConfig.adaptorComment
        }
        if (groupName) {
            comment += "-${groupName}"
        }
        builder.classComment(new ClassComment(comment: comment))

        paths.each { requestMapping, hand ->
            //方法
            def sps = (requestMapping as String).split("/")
            def ls = []
            sps.each {
                ls << StringUtils.uppercaseFirst(it)
            }
            def methodName = StringUtils.lowercaseFirst(ls.join("")).replaceAll("-", "_")
            hand.each { method, reqInfo ->

                //生成返回类型对象
                def paramName = methodName.replaceFirst("Api", "")
                def responseParamClassName = "void"
                if (reqInfo) {
                    def responses = reqInfo["responses"]
                    if (responses) {
                        def res200 = responses["200"]
                        if (res200) {
                            def schema = res200["schema"] as Map
                            if (schema) {
                                responseParamClassName = createResponseParameterClass(paramName, "", schema, definitions)
                            } else {
                                responseParamClassName = convertType(res200['type'] as String, res200['format'] as String)
                            }
                        }
                    }
                }

                def methodComment = new MethodComment()
                def parameters = reqInfo.parameters as List
                methodComment.comment = reqInfo.summary as String
                def operationId = reqInfo["operationId"] as String

                def classMethod = new ClassMethod(type: responseParamClassName, name: operationId)
                parameters.eachWithIndex { param, idx ->
                    def argName = param["name"] as String
                    if (param["in"] == "query") {
                        def type = convertType(param["type"] as String, param["format"] as String)
                        classMethod.parameters << new MethodParameter(type: type, name: argName)
                        methodComment.parameters << new CommentParameter(name: param["name"], description: param["description"])
                    } else if (param["in"] == "body") {
                        def schema = param["schema"] as Map
                        def requestParamClassName = createRequestParameterClass(methodName, argName, schema, definitions)
                        classMethod.parameters << new MethodParameter(type: requestParamClassName, name: argName)
                        methodComment.parameters << new CommentParameter(name: argName, description: param["description"])
                    }
                }
                if (myConfig.adaptorContent) {
                    def methodContent = new StringBuffer()
                    methodContent.append(Constant.tab_2).append("def url = '${requestMapping}'").append(Constant.newLine)
                    methodContent.append(Constant.tab_2).append("//TODO").append(Constant.newLine)
                    methodContent.append(Constant.tab_2).append("//def object = ${myConfig.httpObjectInstanceName}.")
                            .append(method == "get" ? myConfig.httpObjectInstanceGetMethodName : myConfig.httpObjectInstancePostMethodName)
                            .append("(url, null, null)").append(Constant.newLine)
                    methodContent.append(Constant.tab_2).append("//return object").append(Constant.newLine)
                    methodContent.append(Constant.tab_2).append("return null")
                    classMethod.content = methodContent.toString()
                }

                classMethod.comment = methodComment
                builder.methods(classMethod)
            }
        }
        writeFile(myConfig.adaptorPackage, adaptorName, builder.build())
        logger.debug("[{}]创建完成", adaptorName)
    }


    String createRequestParameterClass(String contentClassName, String referName, Map schema, Map definitions) {
        def returnType = createClass(myConfig.requestModelPackage, contentClassName, referName, myConfig.requestModelPrefix, myConfig.requestModelSuffix, myConfig.requestModelsSuperClass, schema, definitions)
        return returnType
    }

    String createResponseParameterClass(String contentClassName, String referName, Map schema, Map definitions) {
        def returnType = createClass(myConfig.responseModelPackage, contentClassName, referName, myConfig.responseModelPrefix, myConfig.responseModelSuffix, myConfig.responseModelsSuperClass, schema, definitions)
        return returnType

    }

    /**
     * 创建请求参数或者响应参数
     * @param packages
     * @param contentClassName
     * @param referName
     * @param classNamePrefix
     * @param classNameSuffix
     * @param superClass
     * @param schema
     * @param definitions
     * @return
     */
    String createClass(String packages, String contentClassName, String referName, String classNamePrefix, String classNameSuffix, String superClass, Map schema, Map definitions) {

        def type = schema["type"]
        def ref
        if (type == 'array') {
            ref = schema['items']['$ref'] as String
        } else {
            ref = schema['$ref'] as String
        }
        if (!ref) {
            return "void"
        }
        ref = ref.replace(definitionsPrefix, "")
        def className = className(contentClassName, referName, classNamePrefix, classNameSuffix)
        def returnClassName = className
        if (type == 'array') {
            returnClassName = "List<${className}>"
            if (models.containsKey(className)) {
                return returnClassName
            }
        }

        def definition = definitions.get(ref) as Map

        if (!definition) {
            return ref
        }
        def properties = definition['properties'] as Map

        def builder = ClassFileBuilder.createClassFile(packages, className).extend(superClass)
        builder.classComment(new ClassComment(comment: ref))

        properties.each { key, value ->
            if (key != "metaClass") {
                def fieldClassName
                def fieldComment = value["description"] as String
                if (!value['type'] || value['type'] == 'array') {
                    fieldClassName = createClass(packages, className, key as String, classNamePrefix, classNameSuffix, superClass, value as Map, definitions)
                } else {
                    fieldClassName = StringUtils.uppercaseFirst(convertType(value['type'] as String, value['format'] as String))
                }

                def classFiled = new ClassFiled(type: fieldClassName, name: key)
                classFiled.comment = new FieldComment(comment: fieldComment)
                builder.fields(classFiled)
            }
        }
        models.put(className, builder)
        writeFile(packages, className, builder.build())
        logger.debug("[{}]创建完成", returnClassName)
        return returnClassName
    }


    String className(String contentClassName, String referName, String classNamePrefix, String classNameSuffix) {

        def names = []
        if (classNamePrefix) {
            names << classNamePrefix
        }
        names << StringUtils.uppercaseFirst(contentClassName)
        if (referName) {
            names << StringUtils.uppercaseFirst(referName)
        }
        if (classNameSuffix) {
            names << StringUtils.uppercaseFirst(classNameSuffix)
        }
        names = names.unique()
        def className = names.join("")
        names = className.collect {
            it == it.toUpperCase() ? "_" + it : it
        }
        names = names.join("").split("_") as List
        names.unique()
        className = names.join("")
        return className
    }

    void writeFile(String packages, String className, String content) {
        def filePath = myConfig.fileLocation
        if (!filePath.endsWith("/")) {
            filePath += "/"
        }
        filePath += packages.replaceAll("\\.", "/")
        def file = Paths.get(filePath).toFile()
        if (!file.exists()) {
            file.mkdirs()
        }
        def out = new FileOutputStream("${filePath}/${className}.groovy")
        out.write(content.getBytes("UTF-8"))
        out.close()
    }

    String convertType(String t, String format) {
        def type = t
        if (type == "Array") {
            type = "List"
        } else if (type == "number") {
            type = "BigDecimal"
        } else if (type == "integer") {
            if (format == "int64") {
                type = "Long"
            }
        }
        return StringUtils.uppercaseFirst(type)
    }
}
