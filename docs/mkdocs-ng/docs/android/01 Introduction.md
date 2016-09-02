#  Introduction

Jadex Android is a framework for developing software agents running on the Android platform. Agent-Oriented Software Engineering (AOSE) is a software development paradigm especially suited for distributed Systems as the main buildings blocks are constituted by software agents, whose outstanding characteristics are - among others - autonomy, message-based and asynchronous communication, re- and proactivity, social abilities and cooperation. 
![JadexAndroid-Logo.png](JadexAndroid-Logo.png)

From the perspective of an Android developer, there are several advantages of using Jadex.
Communication and thus developing distributed software is made easy, because Jadex provides features such as awareness (auto-discovery of other platforms), remote service calls and secure communication.
Decomposing the software into Active Components allows the developer to migrate compute-intense tasks to a cloud infrastructure without touching interfaces or implementation. 

Most of the content of this guide assumes you are already familiar with Jadex and only discusses Android specifics and additions.

-   [Chapter 2 - Installation](02%20Installation)  describes how to install the required tools and libaries and start development of Jadex Android applications.
-   [Chapter 3 - Using Jadex on Android](03%20Using%20Jadex%20on%20Android) gives an overview of available API functions

## General Notes on Jadex Android

Most of the Jadex features are available on Android, too.
The probably most important difference is that there is no JCC and Android Apps are initialized very differently - you can read more about that in [Chapter 3](03 Using Jadex on Android/#differences-to-the-desktop-version-of-jadex).

You can follow the release notes below to get an impression of the ongoing development, the latest distribution can be found on the [download page](https://www.activecomponents.org/bin/view/Download/Distributions).

### Unsupported Modules

Most notably, there is no JCC and no envsupport available on Android.
This is a more complete list of currently unsupported modules on Jadex Android:

-   jadex-json
-   jadex-kernel-application
-   jadex-kernel-extension-agr
-   jadex-kernel-extension-envsupport
-   jadex-platform-extension-management
-   jadex-platform-extension-maven
-   jadex-platform-extension-securetransport
-   jadex-platform-extension-webservice (REST client is supported)
-   jadex-servletfilter
-   several jadex-tools modules

##  Release Notes

### 3.0.0-RC68
-	Better error messages when generating BDIV3 agents at compile time using jadex-gradle-plugin

### 3.0.0-RC42

-   Ported the jadex-gradle-plugin to the new Transform API (android build tools 2.0.0 required)

### 3.0.0-RC16

-   PlatformConfiguration object for platform configuration
-   Android Studio support (Eclipse support dropped!)

### 2.5-SNAPSHOT

-   mainly working on PlatformApp/ClientApp Separation

### 2.4

-   Maven Plugin to generate BDIV3 Agent code at compile time -&gt; enables the use of BDIV3 on Android.
-   JadexAndroidEvents for dispatching events from Agent to Service/Activity
-   synchronous getsService() method in JadexService
-   awareness set to enabled by default, as it is default on desktop platforms
-   new PlatformApp/ClientApp functionality: separate Jadex Platform into a standalone app.

### 2.3

-   API Changes! Please refer to the example project on how to start the platform.
-   fixed problems with BDI Agents
-   added REST client api + demo (working since 2012-11-14)
-   new demo applications project
-   included chat application

### 2.2.1

-   No specific changes

### 2.1

-   provides a simple control center application (see example project)
-   since 2012-05-31: based on modular jadex distribution instead of separate artifacts (NOTE: Your Android applications will require different dependencies now!)
-   adjusted version numbering to Jadex' Version
-   Jadex-Android uses android xml pull parser now instead of woodstox, reduces memory footprint
-   Jadex-Android stores settings in Android Shared Preferences now. It will, however, prefer properties stored in a default.settings.xml (/data/data/&lt;application package name&gt;/files/default.settings.xml)

### 0.0.5

-   fixed bug, preventing service calls from Desktop-Jadex to Android-Jadex. Please note, that due to the Android emulator's virtual network environment, it is still not possible to call services offered by Desktop-Jadex. You have to manually create a ProxyAgent on the Android device to communicate. On real devices, if you are not in a private Wifi network you need to use Jadex' relay server as broadcasts are generally not supported over the Internet.

### 0.0.4

-   updated maven projects to maven-andoid-plugin-3.0.0-alpha-14 -&gt; supports ADT R15
-   communication between platforms fixed, so remote mobile platform components are visible in JCC
    (This requires the HTTP Relay Transport to be enabled if running in an emulator)
-   added AndroidSettingsService for File Access on Android Devices
-   introduced AndroidContextService to provide access to android files
-   Security Service is active by default. The generated Plattform Password will be written to LogCat and saved in
    */data/data/<packagename\>/files/<platformname\>.settings.xml*.
    To disable the Security Service, just uncomment the Service in your platform.component.xml

### 0.0.3

-   uses Woodstox XML Parser instead of broken StaX reference implementation

