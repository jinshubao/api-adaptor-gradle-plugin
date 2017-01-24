package com.shangying.plugin

/**
 * Created by jinshubao on 2017/1/24.
 */
class ApiAdaptorConfig {

    Boolean adaptorInterface = false                                //是否需要生成adaptor接口
    Boolean adaptorRequestModel = true                             // 是否需要生成请求参数对象
    Boolean adaptorResponseModel = true                            // 是否需要生成响应对象
    Boolean adaptorContent = true                                  //是否需要实现adaptor

    String swaggerApiHost = "localhost"
    String swaggerApiPort = "8080"
    String adaptorComment = "api"

    String fileLocation = "build/"
//    String fileLocation = "src/main/groovy/"
    String adaptorPackage = "com.shangying.core.adaptor.api"
    String adaptorClassName = "ApiAdaptor"
    String requestModelPackage = "com.shangying.models.adaptor.request"
    String responseModelPackage = "com.shangying.models.adaptor.response"
    String requestModelPrefix = ""
    String requestModelSuffix = "Request"
    String responseModelPrefix = ""
    String responseModelSuffix = "Response"
    String requestModelsSuperClass = ""
    String responseModelsSuperClass = ""
    String httpObjectInstanceName = "restTemplate"
    String httpObjectInstanceGetMethodName = "nGet"
    String httpObjectInstancePostMethodName = "nPost"

}
