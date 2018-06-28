An example Maven project for Jadex.

This project can be imported into IDEs like eclipse.


======== Contents ========

* The 'pom.xml' is used by Maven to e.g. find
  project dependencies like the Jadex libraries.
  It tells maven to use the release libraries from the maven central
  repository or the latest Jadex libraries from the snapshot repository
  instead of the (possibly newer). The desired Jadex version can be
  specified in the POM before importing, but can also be changed later
  causing a reload of the dependencies in the IDE.

* The 'src/main/java' folder contains a simple "Hello world!" agent.
  As a default, compiled classes are placed by Maven in the
  'target/classes' folder.

* The 'default.settings.xml' is a preconfigured platform
  setting, that already includes the 'target/classes' folder
  in the library path and the explorer view.


======== Installation (for eclipse) ========

* Unzip the example project to a place of your choice.

* When using eclipse you need the Maven plugin, which is available at:
  http://m2eclipse.sonatype.org/

* Choose "Import..." -> "Maven/Existing Maven Projects"
  and select the unzipped example directory as root directory.

* To start Jadex, right-click on the imported project and choose
  "Run As" -> "Java Application".
  Select the 'Starter' class from package 'jadex.base'.
  Click "Run" and the Jadex platform should start.
  
* In the JCC, the 'HelloAgent' should be already selected.
  Click "Start" to start the agent and check the console for the
  output "Hello world!".
  
* Eclipse remembers the launch configuration. Therefore in the future,
  you can simply select the "Starter" configuration from the run history
  to start the platform.
