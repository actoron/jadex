<span>Introduction</span> 
=========================

Jadex Android is a framework for developing software agents running on the Android platform. Agent-oriented Software Engineering (AOSE) is a software development paradigm especially suited for distributed Systems as the main buildings blocks are constituted by software agents, whose outstanding characteristics are - among others - autonomy, message-based and asynchronous communication, re- and proactivity, social abilities and cooperation, etc.\
![JadexAndroid-Logo.png](https://www0.activecomponents.org/bin/download/Android+User+Guide/01+Introduction/JadexAndroid%2DLogo.png)

On the one hand, AOSE allows to model active objects, meaning that e.g. the user and her needs can naturally be represented by an agents, taking the responsibilty to autonomously achieve the user's goals. On the other hand AOSE abstracts from lower-level details such as multithreading and communication issues, as in this respect the Jadex framework takes care of all these.

-   <span class="wikiexternallink">[Chapter 2 - Installation](02%20Installation)</span> describes how to install the required tools and libaries and start development of Jadex Android applications.
-   <span class="wikiexternallink">[Chapter 3 - Using Jadex on Android](03%20Using%20Jadex%20on%20Android)</span> gives an overview of available API functions

<span>State of Implementation</span> 
------------------------------------

Except for the UI, multiple Jadex modules were ported to android, replacing incompatible libraries and calls with ones that are compatible with android. Although we try to keep up with new jadex features, some things are still missing on android.\
You can follow the release notes below to get an impression of the ongoing developmen, the latest implementation can be found in the <span class="wikiexternallink">[download](/Download/Overview)</span> section.

<div class="wikimodel-emptyline">

</div>

### <span>Supported Modules</span> 

This is a list of all supported jadex modules as of version 2.4:

-   jadex-kernel-base
-   jadex-kernel-bdi
-   jadex-kernel-bdiv3 (only with compile-time generation, see here)
-   jadex-kernel-bpmn
-   jadex-kernel-bdibpmn
-   jadex-kernel-component
-   jadex-kernel-micro
-   jadex-platform-extension-webservice (REST client only)

<span>Release Notes</span> 
--------------------------

### <span>2.5-SNAPSHOT</span> 

-   mainly working on PlatformApp/ClientApp Separation

### <span>2.4</span> 

-   Maven Plugin to generate BDIV3 Agent code at compile time -&gt; enables the use of BDIV3 on Android.
-   JadexAndroidEvents for dispatching events from Agent to Service/Activity
-   synchronous getsService() method in JadexService
-   awareness set to enabled by default, as it is default on desktop platforms
-   new PlatformApp/ClientApp functionality: separate Jadex Platform into a standalone app.

### <span>2.3</span> 

-   API Changes! Please refer to the example project on how to start the platform.
-   fixed problems with BDI Agents
-   added REST client api + demo (working since 2012-11-14)
-   new demo applications project
-   included chat application

### <span>2.2.1</span> 

-   No specific changes

### <span>2.1</span> 

-   provides a simple control center application (see example project)
-   since 2012-05-31: based on modular jadex distribution instead of separate artifacts (NOTE: Your Android applications will require different dependencies now!)
-   adjusted version numbering to Jadex' Version
-   Jadex-Android uses android xml pull parser now instead of woodstox, reduces memory footprint
-   Jadex-Android stores settings in Android Shared Preferences now. It will, however, prefer properties stored in a default.settings.xml (/data/data/&lt;application package name&gt;/files/default.settings.xml)

### <span>0.0.5</span> 

-   fixed bug, preventing service calls from Desktop-Jadex to Android-Jadex. Please note, that due to the Android emulator's virtual network environment, it is still not possible to call services offered by Desktop-Jadex. You have to manually create a ProxyAgent on the Android device to communicate. On real devices, if you are not in a private Wifi network you need to use Jadex' relay server as broadcasts are generally not supported over the Internet.

### <span>0.0.4</span> 

-   updated maven projects to maven-andoid-plugin-3.0.0-alpha-14 -&gt; supports ADT R15
-   communication between platforms fixed, so remote mobile platform components are visible in JCC\
    (This requires the HTTP Relay Transport to be enabled if running in an emulator)
-   added AndroidSettingsService for File Access on Android Devices
-   introduced AndroidContextService to provide access to android files (TODO: properties)
-   Security Service is active by default. The generated Plattform Password will be written to LogCat and saved in\
    */data/data/&lt;packagename&gt;/files/&lt;platformname&gt;.settings.xml*.\
    To disable the Security Service, just uncomment the Service in your platform.component.xml

### <span>0.0.3</span> 

-   uses Woodstox XML Parser instead of broken StaX reference implementation

