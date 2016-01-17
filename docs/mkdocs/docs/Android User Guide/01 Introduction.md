Introduction
=========================

Jadex Android is a framework for developing software agents running on the Android platform. Agent-oriented Software Engineering (AOSE) is a software development paradigm especially suited for distributed Systems as the main buildings blocks are constituted by software agents, whose outstanding characteristics are - among others - autonomy, message-based and asynchronous communication, re- and proactivity, social abilities and cooperation, etc.\
![JadexAndroid-Logo.png](JadexAndroid-Logo.png)

On the one hand, AOSE allows to model active objects, meaning that e.g. the user and her needs can naturally be represented by an agents, taking the responsibilty to autonomously achieve the user's goals. On the other hand AOSE abstracts from lower-level details such as multithreading and communication issues, as in this respect the Jadex framework takes care of all these.

-   [Chapter 2 - Installation](02%20Installation)  describes how to install the required tools and libaries and start development of Jadex Android applications.
-   [Chapter 3 - Using Jadex on Android](03%20Using%20Jadex%20on%20Android)  gives an overview of available API functions

State of Implementation
------------------------------------

Except for the UI, multiple Jadex modules were ported to android, replacing incompatible libraries and calls with ones that are compatible with android. Although we try to keep up with new jadex features, some things are still missing on android.\
You can follow the release notes below to get an impression of the ongoing development, the latest implementation can be found in the [download](https://www.activecomponents.org/bin/view/Download/Overview)  section.



### Supported Modules

This is a list of all supported jadex modules as of version 2.4:

-   jadex-kernel-base
-   jadex-kernel-bdi
-   jadex-kernel-bdiv3 (only with compile-time generation, see here)
-   jadex-kernel-bpmn
-   jadex-kernel-bdibpmn
-   jadex-kernel-component
-   jadex-kernel-micro
-   jadex-platform-extension-webservice (REST client only)

Release Notes
--------------------------

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
-   communication between platforms fixed, so remote mobile platform components are visible in JCC\
    (This requires the HTTP Relay Transport to be enabled if running in an emulator)
-   added AndroidSettingsService for File Access on Android Devices
-   introduced AndroidContextService to provide access to android files (TODO: properties)
-   Security Service is active by default. The generated Plattform Password will be written to LogCat and saved in\
    */data/data/&lt;packagename&gt;/files/&lt;platformname&gt;.settings.xml*.\
    To disable the Security Service, just uncomment the Service in your platform.component.xml

### 0.0.3

-   uses Woodstox XML Parser instead of broken StaX reference implementation

