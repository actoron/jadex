# Installation

This document will guide you through the setup that is necessary to develop applications using Jadex-Android.

**Please [report](http://sourceforge.net/projects/jadex/forums/forum/274112) ** any difficulties or errors in this document as well as in the provided *jadex-android* libraries.

## Requirements

- Java JDK, tested with JDK 7. On Linux, look for packages from your distributor. On Windows, get the JDK from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html#javasejdk).
- Download Android Studio (currently tested with 1.5.1) from [here](http://developer.android.com/sdk/index.html) (Be sure to download **Android Studio**, not only the SDK)
- Android SDK (usually included in Android Studio)
- Download the latest Jadex Android distribution (release or nightly) from the [downloads page](https://www.activecomponents.org/bin/view/Download/Distributions) 

### **Note for 64-bit Ubuntu installations:**
> If you have a 64bit ubuntu distributions, install the following packages first:  
> ```sudo apt-get install lib32stdc++6 lib32z1```

### Install Android Studio
First, set the *JAVA_HOME* environment variable to your JDK (This may be done automatically).

To install Android Studio, just extract it and execute *studio.sh* or *studio.exe* located in the *bin/* directory.  
On the first start, you can decide whether to import settings from a previous version. Choose *I do not have a previous version* if this is your first installation and click *OK*.  
Complete the Setup Wizard. Be sure to pay attention to the page named **Emulator Settings**, which provides information on how to speed up the Android emulator.  
After Android Studio has downloaded the necessary SDK components, it will show a welcome screen.

## Extract the Jadex distribution

- Install Android Studio, following the descriptions from their download page.
- Extract the jadex-android-*version*.zip. You will see two example projects in the extracted directory.
- Extract *jadex-android-example-project-gradle.zip*. It contains a Jadex Android example project, which can be opened in Android Studio.

## Import the example project in Android Studio

Open Android Studio.
If you see a welcome screen, choose *Import project*.  
If you already have another project opened, choose *File -> Open*.

Navigate to the folder which contains the extracted the example project and click *OK*.  
If Android Studio asks whether to open the project in a new window, choose *New Window*, if unsure.  
Android Studio should now import the project, download all necessary libraries and build the example project.

### Download required SDK Platform
If you get an error like this:

    Error: Cause: failed to find target with hash string 'android-21' in: [...]
    *Open Android SDK Manager*  
Open the SDK Manager by clicking on the provided link inside the error message, click the checkbox next to the SDK Platform with the API Level given in the error message (e.g. choose **Android 5.0.1** for API Level **21**) and click *OK*.  
The required SDK platform will be downloaded and the project should build sucessfully.

## Setting up an Android Virtual Device (AVD)
If you haven't set up an Android Virtual Device at this point, follow this instructions.  
Open the AVD manager by clicking on ![](studio_avd_icon.png). Click on *Create Virtual Device* and follow the instructions.  
We recommend to choose a device which supports an API Level of **21**.  
Choosing an x86 ABI will result in a faster emulator, but can only run if hardware emulation is enabled on your system.
Read more about this in the [Emulator documentation](http://developer.android.com/tools/devices/emulator.html#accel-vm)

## Run example Project

When successfully imported and built, you can view the project files by activating the *Project* Tab on the left (![](studio_project_tab.png)).
A run configuration will appear in the upper toolbar.  
Click on the green arrow on the right of the configuration *jadex-android-example-project-gradle* to launch the project (![](studio_build_config_run.png)). Choose a running device or launch a new emulator as requested.  
Once the APK is generated, it will be uploaded onto the AVD and executed.  
Proceed to the next chapter to learn about how to create your own Jadex Android Application.

