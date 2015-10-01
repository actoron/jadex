package jadex.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class BDIPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create('jadexArgs', JadexPluginExtension)

        if (!project.hasProperty('android')) {
            println 'Error, no android plugin found for project: ' + project.name
        }

        def AppExtension android = project.android

        project.afterEvaluate {
            applyForVariants(project, android.applicationVariants)
        }

    }

    void applyForVariants(Project project, Collection<ApplicationVariant> applicationVariants) {
        applicationVariants.each { variant ->
            createEnhanceTaskForVariant(project, variant)
        }
    }

    void createEnhanceTaskForVariant(Project project, ApplicationVariant variant) {
        def name = variant.buildType.name
        def camelName = name.charAt(0).toUpperCase().toString() + name.substring(1)
        def File dir = variant.getJavaCompiler().destinationDir;

        def JadexBdiEnhanceTask enhanceTask = project.task("jadexBdiEnhance${camelName}", type: JadexBdiEnhanceTask)

        enhanceTask.buildType = variant.buildType
        enhanceTask.inputDir = dir

        enhanceTask.dependsOn(variant.getJavaCompiler())
        variant.getDex().dependsOn(enhanceTask)
    }
}

class JadexPluginExtension {
    String name
    String message

}