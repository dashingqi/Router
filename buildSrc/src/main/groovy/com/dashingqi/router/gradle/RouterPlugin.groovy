package com.dashingqi.router.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.json.JsonSlurper

class RouterPlugin implements Plugin<Project> {

    /** 生成的Json文件的父目录*/
    private def ROUTER_DOCUMENT = "router_document"
    /** kapt 插件名*/
    private def PREFIX_KAPT = "kapt"
    /** 便衣插件前缀*/
    private def PREFIX_COMPILE = "compile"
    /** JavaWithJavac 结尾 */
    private def END_JAVAC = "JavaWithJavac"
    /** .json 结尾 */
    private def END_JSON = ".json"
    /** key router */
    private def KEY_ROUTER = "router"
    /** key root project dir */
    private def KEY_PROJECT_DIR = "root_project_dir"
    /** key path */
    private def KEY_PATH = "path"
    /** key description */
    private def KEY_DESCRIPTION = "description"
    /** key real path */
    private def KEY_REAL_PATH = "realPath"
    /** scheme 的前缀*/
    private def PREFIX_ROUTER_SCHEME = "router://"
    /** 文档名称*/
    private def DOCUMENT_NAME = "页面文档.md"


    // 实现apply方法，注入插件的逻辑
    @Override
    void apply(Project project) {

        // 1. 自动将路径参数传递到注解处理器中，无需开发者手动声明
        if (project.extensions.findByName(PREFIX_KAPT) != null) {
            project.extensions.findByName(PREFIX_KAPT).arguments {
                arg(KEY_PROJECT_DIR, project.rootProject.projectDir.absolutePath)
            }
        }

        // 2. 实现旧的构建产物自动清理
        project.clean.doFirst {
            File routerDocument =
                    new File(project.rootProject.projectDir, ROUTER_DOCUMENT)
            if (routerDocument.exists()) {
                routerDocument.deleteDir()
            }
        }


        project.getExtensions().create(KEY_ROUTER, RouterExtension)


        project.afterEvaluate {

            // 当前工程的配置阶段完成，此时是获取Extension的时机
            RouterExtension extension = project[KEY_ROUTER]
            println("用户设置的WIKI路径为 -> ${extension.wikiDir}")

            // 3. 在Javac任务执行后写成文档
            project.tasks.findAll { task -> task.name.startsWith(PREFIX_COMPILE) && task.name.endsWith(END_JAVAC)
            }.each { targetTask ->
                targetTask.doLast {
                    File routerDocumentDir =
                            new File(project.rootProject.projectDir, ROUTER_DOCUMENT)
                    if (!routerDocumentDir.exists()) {
                        return
                    }

                    File[] childFiles = routerDocumentDir.listFiles()

                    if (childFiles.size() < 1) {
                        return
                    }
                    StringBuilder sb = new StringBuilder()

                    sb.append("# 页面文档 \n\n")

                    childFiles.each { file ->
                        if (file.name.endsWith(END_JSON)) {
                            JsonSlurper jsonSlurper = new JsonSlurper()
                            def jsonFile = jsonSlurper.parse(file)
                            jsonFile.each { item ->
                                def path = item[KEY_PATH]
                                def description = item[KEY_DESCRIPTION]
                                def realPath = item[KEY_REAL_PATH]
                                sb.append("## $description \n")
                                sb.append("- url: $PREFIX_ROUTER_SCHEME$path\n")
                                sb.append("- realPath: $realPath\n\n")
                            }
                        }
                    }

                    // 写入文件
                    File wikiDir = new File(extension.wikiDir)
                    if (!wikiDir.exists()) {
                        wikiDir.mkdir()
                    }

                    File documentFile = new File(wikiDir, DOCUMENT_NAME)
                    if (documentFile.exists()) {
                        documentFile.delete()
                    }
                    documentFile.write(sb.toString())
                }
            }
        }
    }
}