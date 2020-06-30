package jadex.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class BDIAndroidPlugin implements Plugin<Project> {

    void apply(Project project) {
        def isLibrary = project.plugins.hasPlugin(LibraryPlugin)
        def jadexExtension = project.extensions.getByType(JadexPluginExtension)
        def transform = new BDITransform(project, jadexExtension)

        if (isLibrary) {
            def android = project.extensions.getByType(LibraryExtension)

            android.registerTransform(transform)

//            android.libraryVariants.all { BaseVariant variant ->
//                configureCompileJavaTask(project, variant, variant.javaCompile, transform)
//            }
//            android.testVariants.all { TestVariant variant ->
//                configureCompileJavaTask(project, variant, variant.javaCompile, transform)
//            }

        } else {
            def android = project.extensions.getByType(AppExtension)

            android.registerTransform(transform)

//            android.applicationVariants.all { BaseVariant variant ->
//                configureCompileJavaTask(project, variant, variant.javaCompile, transform)
//            }
//            android.testVariants.all { TestVariant variant ->
//                configureCompileJavaTask(project, variant, variant.javaCompile, transform)
//            }
        }
    }
}
