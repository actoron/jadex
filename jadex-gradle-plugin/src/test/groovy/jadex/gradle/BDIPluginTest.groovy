package jadex.gradle

import com.android.build.gradle.AppExtension
import jadex.bdiv3.IBDIClassGenerator
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logging
import org.gradle.internal.TrueTimeProvider
import org.gradle.logging.internal.slf4j.OutputEventListenerBackedLogger
import org.gradle.logging.internal.slf4j.OutputEventListenerBackedLoggerContext
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

import java.lang.reflect.Field
import java.util.logging.Level
import java.util.logging.Logger

import static junit.framework.TestCase.assertNotNull
import static junit.framework.TestCase.assertTrue

class BDIPluginTest {

    def Project project
    def AppExtension android

    @Before
    public void init() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'com.android.application'
        project.pluginManager.apply 'jadex-bdi'

//        project.pluginManager.apply 'jadex.gradle.BDIPlugin'
        android = project.android

        def props = new Properties()

        def file = new File("../gradle.properties")
        if (!file.exists())  {
            file = new File("gradle.properties")
        }

        props.load(new FileReader(file))

        android.buildToolsVersion props.getProperty("android_buildToolsVersion")
//        android.buildToolsVersion '22.0.1'
        android.compileSdkVersion props.getProperty("android_compileSdkVersion")

    }

    @Test
    public void BDIPluginCanBeAppliedToAndroid() {
    }

    @Test
    public void BDIPluginCreatesTasksWithCorrectDependencies() {
        evaluateProject()

        def BDIPlugin bdiPlugin = project.plugins.findPlugin(BDIPlugin.class)


        assertTrue(project.tasks.jadexBdiEnhanceDebug instanceof JadexBdiEnhanceTask)
        assertTrue(project.tasks.jadexBdiEnhanceRelease instanceof JadexBdiEnhanceTask)


        android.applicationVariants.each {variant ->
            def camelName = variant.getBaseName().charAt(0).toUpperCase().toString() + variant.getBaseName().substring(1)

            def task = project.tasks.getByName("jadexBdiEnhance${camelName}")

//            println variant.getJavaCompiler()
//            println variant.getDex()

            assertTrue(task.getDependsOn().contains(variant.getJavaCompiler()))

            assertTrue(variant.getDex().getDependsOn().contains(task))
        }
    }

    @Test void executeTask() {
//        project.logging.captureStandardOutput(LogLevel.INFO)
//        project.logging.level = LogLevel.INFO

        evaluateProject()
        def JadexBdiEnhanceTask task = project.tasks.getByName("jadexBdiEnhanceDebug")

        // use DummyBDI to test enhancements
        task.inputDir = new File("build/classes/test/")
//        task.logging.level = LogLevel.INFO

        task.generateBDI(task.inputDir)

        def FileCollection cp = project.buildscript.configurations.classpath



        // TODO: use different classloader to load the enhanced class
//        def parentCl = new URLClassLoader(cp.collect {it.toURI().toURL()} as URL[], null as ClassLoader)
//
//        def cl = new URLClassLoader([task.inputDir.toURI().toURL()] as URL[], parentCl)
//        def clazz = cl.loadClass("jadex.gradle.DummyBDI")
//        def field = clazz.getField(IBDIClassGenerator.AGENT_FIELD_NAME);
//        assertNotNull(field)
    }

    def void evaluateProject() {
        try {
            project.evaluate()
        } catch (IllegalStateException e) {
            e.printStackTrace()
            System.err.println('If Test Exceptions occur, verify that buildTools are available in the version specified!')
        }

    }
}