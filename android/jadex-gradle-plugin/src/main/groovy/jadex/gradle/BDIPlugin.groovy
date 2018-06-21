package jadex.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin

class BDIPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.extensions.create('jadexBdi', JadexPluginExtension, project)

        def logger = project.logger
        if (!project.hasProperty('android')) {
            logger.error('Error, no android plugin found for project: ' + project.name);
        }

        project.plugins.withType(JavaPlugin) {
        }

        project.plugins.withType(GroovyPlugin) {
        }

        project.plugins.withId('com.android.application') {
            project.apply plugin: BDIAndroidPlugin
        }

        project.plugins.withId('com.android.library') {
            project.apply plugin: BDIAndroidPlugin
        }

        project.plugins.withType(ApplicationPlugin) {
        }

    }

}

