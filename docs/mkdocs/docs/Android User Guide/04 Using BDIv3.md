Using BDIv3 on Android
===================================

The BDI v3 programming model heavily depends on code-generation based on java annotations.
It's using the ASM Bytecode manipulation framework to generate the code.
Android not only uses a different virtual machine than any Java SE environment, the Dalvik Virtual Machine (DVM), it also uses a different bytecode representation, which is not supported by ASM.

As runtime bytecode generation is slow on android anyway, we use a different mechanism to make BDIv3 work: Generating the bytecode at compile time, which is then processed and converted by standard android tools.

For this method to work, however, we need to include an additional compile step, which is done using a gradle plugin.
So you **need to use gradle (android studio default) use BDIv3**!

Additionally, you need to include the jadex-gradle plugin in your *build.gradle*.

# Applying the jadex-gradle BDIPlugin

The simplest way to use the Jadex gradle plugin is to include the jadex repositories as buildscript dependency repositories, like the following extract from build.gradle shows:


```groovy
buildscript {
    repositories {
        jcenter()
        mavenLocal()
        mavenCentral()
        maven
        {
            name 'jadexsnapshots'
            url 'https://nexus.actoron.com/content/repositories/oss-nightlies/'
        }
        maven
        {
            name 'jadexreleases'
            url 'https://nexus.actoron.com/content/repositories/oss/'
        }
    }

    dependencies {
        classpath 'com.android.tools.build:gradle:1.3.0' // this is probably already there
        classpath "org.activecomponents.jadex:jadex-gradle-plugin:${ireallyneedajadexversion}"
    }
}

apply plugin: 'com.android.application' // this is probably already there
// for bdiv3 code generation:
apply plugin: jadex.gradle.BDIPlugin
```

If you compile your project with gradle or android studio now (just click on "run"), the plugin will run, detect all BDIV3 Agents and will enhance the classes as needed by the Jadex runtime.
