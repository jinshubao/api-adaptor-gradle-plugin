package com.shangying.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by jinshubao on 2017/1/23.
 */
class ApiAdaptorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("apiAdaptorConfig", ApiAdaptorConfig)
        project.task('generateApiAdaptor', type: ApiAdaptorTask)
    }
}

