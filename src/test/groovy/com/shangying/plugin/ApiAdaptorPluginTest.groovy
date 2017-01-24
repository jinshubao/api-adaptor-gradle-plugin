package com.shangying.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

/**
 * Created by jinshubao on 2017/1/24.
 */
class ApiAdaptorPluginTest {

    @Test
    void test() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply('api-adaptor')

        def myConfig = project["apiAdaptorConfig"] as ApiAdaptorConfig
        myConfig.swaggerApiHost = "10.52.2.170"
        myConfig.swaggerApiPort = "7002"
        myConfig.fileLocation = "src/main/groovy/"
        def task = project.tasks.generateApiAdaptor as ApiAdaptorTask

        task.generate()

    }
}
