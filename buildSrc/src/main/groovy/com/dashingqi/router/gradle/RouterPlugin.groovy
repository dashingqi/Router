package com.dashingqi.router.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {


    // 实现apply方法，注入插件的逻辑
    @Override
    void apply(Project project) {

        println("this is router plugin")

        project.getExtensions().create("router", RouterExtension)

        project.afterEvaluate {
            // 当前工程的配置阶段完成，此时是获取Extension的时机

            RouterExtension extension = project["router"]

            println("用户设置的WIKI路径为 -> ${extension.wikiDir}")
        }
    }
}