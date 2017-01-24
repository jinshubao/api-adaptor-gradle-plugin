package com.shangying.plugin

import com.shangying.plugin.model.ClassComment
import com.shangying.plugin.model.ClassFiled
import com.shangying.plugin.model.ClassMethod

/**
 *
 * Created by jinshubao on 2017/1/23.
 */
class ClassFileBuilder {

    private String packageName
    private StringBuilder classCommentBuilder = new StringBuilder()
    private StringBuilder importBuilder = new StringBuilder()
    private String superClass
    private String className
    private StringBuilder interfaceBuilder = new StringBuilder()

    private List<ClassMethod> methods = []
    private List<ClassFiled> fields = []


    static ClassFileBuilder createClassFile(String packageName, String className) {
        if (!className) {
            throw new Exception("className 不能为空")
        }
        def builder = new ClassFileBuilder(className: "class ${className}")
        if (packageName) {
            builder.packageName = "package ${packageName}"
        }
        return builder
    }

    ClassFileBuilder extend(String className) {
        if (className) {
            this.superClass = "extends ${className}"
        }
        return this
    }

    ClassFileBuilder impls(String... classNames) {
        if (classNames && classNames.length > 0) {
            if (this.interfaceBuilder.length() > 0) {
                this.interfaceBuilder.append(", ")
            }
            this.interfaceBuilder.append("${classNames.join(", ")}")
        }
        return this
    }


    ClassFileBuilder imports(String... imports) {
        if (imports) {
            imports.each {
                this.importBuilder.append("import ").append(it).append(Constant.newLine)
            }
        }
        return this
    }

    /**
     * 添加文件注释
     * @param comment
     * @return
     */
    ClassFileBuilder classComment(ClassComment comment) {
        this.classCommentBuilder.append(comment.toString())
        return this
    }

    ClassFileBuilder fields(ClassFiled... fields) {
        if (fields) {
            this.fields.addAll(fields)
        }
        return this
    }

    ClassFileBuilder methods(ClassMethod... methods) {
        if (methods) {
            this.methods.addAll(methods)
        }
        return this
    }


    String build() {
        StringBuffer buffer = new StringBuffer()
        if (this.packageName) {
            buffer.append(this.packageName).append(Constant.newLine).append(Constant.newLine)
        }

        if (this.importBuilder) {
            buffer.append(this.importBuilder)
        }

        buffer.append(Constant.newLine)
        if (this.classCommentBuilder) {
            buffer.append(this.classCommentBuilder)
        }

        buffer.append(this.className)
        if (this.superClass) {
            buffer.append(" ").append(this.superClass)
        }
        if (this.interfaceBuilder) {
            buffer.append(" implements ").append(this.interfaceBuilder)
        }
        buffer.append(" {").append(Constant.newLine).append(Constant.newLine)
        fields.each {
            buffer.append(it.toString()).append(Constant.newLine).append(Constant.newLine)
        }
        buffer.append(Constant.newLine)
        methods.each {
            buffer.append(it.toString()).append(Constant.newLine)
        }
        buffer.append("}")
        return buffer.toString()
    }

}
