# api-adaptor-gradle-plugin
生成swagger接口的对接接口

使用方法：
buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath 'shangying:api-adaptor-gradle-plugin:1.0.0'
    }
}


group 'shangying'
version '2.0'

apply plugin: 'groovy'
apply plugin: 'java'
apply plugin: 'api-adaptor'

sourceCompatibility = 1.8

apiAdaptorConfig {
    swaggerApiHost = "localhost"
    swaggerApiPort = "8080"
    fileLocation = "src/main/groovy/"
}

dependencies {
    compile localGroovy()
}

