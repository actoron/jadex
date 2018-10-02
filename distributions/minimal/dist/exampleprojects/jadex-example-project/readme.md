An example project for Jadex
============================

This project can be imported into IDEs like Eclipse.


Contents
--------

* The `src/main/java` folder contains some simple example agents as
  starting point for the [Jadex tutorials](https://download.actoron.com/docs/nightlies/jadex-${jadex_build_version}/jadex-mkdocs/).

* The `build.gradle` is used by the Gradle build system e.g. to find
  project dependencies like the Jadex libraries.
  It tells Gradle to use the release libraries from the maven central
  repository or the nightly Jadex libraries from the actoron repository
  depending on the Jadex version of which you downloaded this `jadex-example-project.zip`.
  The desired Jadex version can be specified in the build.gradle before importing,
  but can also be changed later for issuing a reload of the dependencies in the IDE.


Installation (for Eclipse)
--------------------------

1. Unzip the example project to a place of your choice.

2. Choose *File/Import... -> Gradle/Existing Gradle Projects*
  and select the unzipped example directory as root directory.

3. To start Jadex, open the imported project, right-click on the `src/main/java` folder and choose
  *Run As -> Java Application*.
  Select the `Main` class from either package `tutorial` for the [Active Components Tutorial](https://download.actoron.com/docs/nightlies/jadex-${jadex_build_version}/jadex-mkdocs/getting-started/getting-started/)
  or from the package `quickstart.cleanerworld` for the [BDI Quickstart Tutorial](https://download.actoron.com/docs/nightlies/jadex-${jadex_build_version}/jadex-mkdocs/getting-started/bdi-quickstart/01 Introduction/)
  Click *OK* and the Jadex program should start.
  
4. Eclipse remembers the launch configuration. Therefore in the future,
  you can simply select the *Main* configuration from the run history
  to start the program.
