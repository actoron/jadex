Using BDIv3 on Android
===================================

The BDI v3 programming model heavily depends on code-generation based on java annotations.
It's using the ASM Bytecode manipulation framework to generate the code.
Android not only uses a different virtual machine than any Java SE environment, the Dalvik Virtual Machine (DVM), it also uses a different bytecode representation, which is not supported by Jadex BDIV3.

As runtime bytecode generation is slow on android anyway, the classes are transformed during compile-time for android.
This is done by the *jadex-gradle-plugin*, which means you **need to use gradle to use BDIv3 components**! 
Since Android Studio uses gradle by default, this is usually not a problem.

To make BDIv3 components work, you need to include the jadex-gradle plugin in your *build.gradle*.
For compatibility with specific android tools version, please refer to the [compatibility section](#compatibility). 

# Limitations
Currently, the jadex-gradle-plugin does not work together with *Instant Run*, a feature which has been introduced with recent Android Studio updates. Please **disable Instant Run**** for now if you use BDIv3.

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
        classpath 'com.android.tools.build:gradle:2.0.0' // this is probably already there
        classpath "org.activecomponents.jadex:jadex-gradle-plugin:${ireallyneedajadexversion}"
    }
}

apply plugin: 'com.android.application' // this is probably already there
// for bdiv3 code generation:
apply plugin: jadex.gradle.BDIPlugin

```

If you compile your project with gradle or android studio now (just click on "run"), the plugin will run, detect all BDIV3 Agents and will enhance the classes as needed by the Jadex runtime.


## Compatibility

### jadex-gradle-plugin 3.0.0-RC42
Starting with RC42, the jadex-gradle-plugin uses the new Android Transform API to enhance BDI classes.
This means version 2.0.0 of the android build tools is required to use the jadex-gradle-plugin now.
```groovy
dependencies {
    classpath 'com.android.tools.build:gradle:2.0.0'
}
```

### jadex-gradle-plugin up to 3.0.0-RC41
Please be aware that versions before 3.0.0-RC41 are only compatible with android build tools version up to 1.3.0. 
This means you have to make sure version 1.3.0 of the build tools is used:
```groovy
dependencies {
    classpath 'com.android.tools.build:gradle:1.3.0'
}
```