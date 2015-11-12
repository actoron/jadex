This document will guide you through the setup that is necessary to develop applications using Jadex-Android.

**Please <span class="wikiexternallink">[report](http://sourceforge.net/projects/jadex/forums/forum/274112)</span>** any difficulties or errors in this document as well as in the provided *jadex-android* libraries.

<span>Installation</span> 
-------------------------

You have multiple Options:

-   If you don't use eclipse or maven, just follow step 1.
-   If you want to use eclipse, but not maven, follow step 1 and 2.
-   If you want to use eclipse and maven, follow step 1, 2 and 3.
-   To compile the example project with eclipse and maven, follow steps 1-4.
-   To compile the example project with eclipse without maven follow steps 1,2 and 5.

You can also compile the example project without using eclipse by following steps 1 and 3 and then manually starting the maven build (*mvn package*).

<span>Step 1</span> 
-------------------

To develop Android applications with jadex-android you need to:

-   Install the Android SDK (from <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://developer.android.com/sdk/index.html</span>](http://developer.android.com/sdk/index.html)</span>)
-   Download the SDK Platform API for Android 2.2 or higher using the *Android SDK Manager*
-   Download and extract *jadex-android* (<span class="wikiexternallink">[Download Section](/Download/Overview)</span>)

Proceed to the next step if you want to use Eclipse OR start using jadex-android by adding the libraries in the extracted folder *lib* to your projects build path

<span>Step 2</span> 
-------------------

In order to develop Android applications using Eclipse (with or without maven), you need:

-   Eclipse 3.6 or newer (<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://www.eclipse.org/downloads/</span>](http://www.eclipse.org/downloads/)</span>)
-   The Android Developer Tools (ADT), Eclipse Update Site: <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">https://dl-ssl.google.com/android/eclipse/</span>](https://dl-ssl.google.com/android/eclipse/)</span>
-   In Eclipse, set the path to the Android SDK in *Window -&gt; Preferences -&gt; Android *and click *Apply*

Proceed to the next step if you wish to use maven OR to step 5 if you don't.

<span>Step 3</span> 
-------------------

If you want to use Maven as Build System (required for the included example project):

-   set the *\$ANDROID\_HOME* environment variable to the directory where the Android SDK is located
-   install a JDK (not JRE), available at <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://www.oracle.com/technetwork/java/javase/downloads/index.html</span>](http://www.oracle.com/technetwork/java/javase/downloads/index.html)</span>
-   configure Eclipse to run with a JDK (<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://matsim.org/node/372</span>](http://matsim.org/node/372)</span>) and set a JDK as default JRE (*Window -&gt; Preferences -&gt; Java -&gt; Installed JREs*, add your JDK here and check it)
-   install m2eclipse plugin in eclipse (for eclipse 3.6 users, use the old update site: <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://download.eclipse.org/technology/m2e/releases</span>](http://download.eclipse.org/technology/m2e/releases)</span>)

Since Jadex 2.2 the jadex-android artifacts are available through the maven central repository. Eclipse will download these as necessary for your Jadex Android projects.

<span>Step 4</span> 
-------------------

To import the maven sample project in eclipse (after following steps 2 and 3)

-   extract the *jadex-android-example-projects.zip*, which is included in the jadex-android distribution
-   copy the *jadex-android-example-project-maven* directory to your workspace
-   choose *File -&gt; Import -&gt; Maven -&gt; Existing Maven Projects*
-   select your workspace folder, select the *jadex-android-example-project-maven* directory and click *next* / *finish* until import is completed
-   you will be prompted to install some Eclipse Plugins (*m2e android connector*) and to restart eclipse
-   after restarting, the project should build without any errors
-   Starting from Version 2.1, the eclipse build will work (just click *run as Android Application*), if eclipse hangs see Hints on the bottom of this page
-   to build the project with maven (**required for jadex-android &lt; 2.1**), use the included launch config *Build example project with maven* 
-   to run the maven build, use the included launch config *Run example project with maven*, which will deploy and run the project on any android devices plugged in or emulators running.

<span>Step 5</span> 
-------------------

To import the non-maven sample project in eclipse (after following step 1 and 2)

-   extract the *jadex-android-example-projects.zip*, which is included in the jadex-android distribution
-   select *File -&gt; Import -&gt; General -&gt; Existing Projects into Workspace*
-   locate the extracted *jadex-android-example-project* directory
-   optionally check *copy projects to workspace*
-   click Finish
-   copy the jadex-android libraries to the *libs* directory of the project and add them to the projects build path (you could skip jadex-android-bdi, bpmn and bdibpmn as the sample only uses a MicroAgent)
-   project should compile without errors
-   Right-click on the example project an choose *Run As -&gt; Android Application* to start the application.

<span>Hints</span> 
------------------

-   if Eclipse cannot find a suitable M2E connector and you already have an older version of m2e-android installed, try updating it manually using the Eclipse Installer and the following Update Site: <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://rgladwell.github.com/m2e-android/updates/</span>](http://rgladwell.github.com/m2e-android/updates/)</span>
-   if building is slow or if you get Exceptions related to memory ("Out of Heap Space", "GC overhead limit") during compilation, try setting *-Xms128m -Xmx1024m* in your *eclipse.ini*
-   bug in m2e-android (<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">https://github.com/rgladwell/m2e-android/issues/31%29</span>](https://github.com/rgladwell/m2e-android/issues/31%29)</span>
-   ~~NIOTCP Transport doesn't work in a 2.2 emulator, see <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://code.google.com/p/android/issues/detail?id=9431</span>](http://code.google.com/p/android/issues/detail?id=9431)</span> ~~ **Update (Feb. 2012):** This is now fixed, NIOTCP was now successfully tested with FroYo (Android 2.2) and Gingerbread (Android 2.3).

