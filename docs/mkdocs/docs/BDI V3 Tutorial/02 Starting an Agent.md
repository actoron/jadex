# Chapter 2. Starting an Agent

Setting up the Jadex environment properly is pretty easy and can be done in a few simple steps. The Jadex distribution should be extracted to some local directory, called JADEX_HOME here. The easiest way the start the platform is by using the operating dependent scripts in the main directory called **jadex.bat** and **jadex.sh** respectively.

To start the platform via Java you basically have two options. The first is by setting the Java CLASSPATH variable by hand to all the jars contained in the JADEX_HOME/lib directory. The second one is by using the -jar option when starting via the java command.



The alternative commands for launching the Jadex Standalone platform are:


```java jadex.standalone.Platform```   <!-- 2 spaces generate a non-paragraph-line-break -->
or     
```java -jar libjadex-platform-standalone-launch-3.0.0-RC1.jar```   
(As the name of the jar depends on the current release please be sure to use the correct version).

## Exercise A1 - Creating a Project


Start the Jadex platform with the command explained above. 
After some short time the Jadex Control Center (JCC) should show up with its user interface.  
The JCC provides a project management facility that simplifies working on specific applications. 
Basically, a project contains settings about the used project folders as well as miscellaneous tool and user interface settings. 
All your settings - as window settings, added paths and so on - will be stored in a .project.xml file and additional properties-files will be created automatically to store the individual settings for the plugins you are using. 

To add files to your project, hit the "Add Path" button. 
For saving the project settings on disk select "Save Settings" from the "File"-menu. 
Additionally, the settings are also saved automatically when shutting down the JCC via its window close button or "Exit" from the "File"-menu. 
You can also use multiple projects with different settings. 
To switch projects simply click on "Load Settings from File" in the "File"-menu and choose the settings file of the project you want to work with.

### Verify project behaviour
In order to verify that you project has been set up correctly you should perform some visible changes such as changing the JCC's window size or switching to another plugin than the starter. After that you should shut down the JCC and platform and restart it. If the project has been created correctly, the JCC will show up in exactly the same state you left it, i.e. the last active plugin will be activated and the gui settings are remembered, too.

## Exercise A2 - Executing Example Applications

The Jadex distribution already contains a couple of example applications. 
The objective of this exercise consists in trying out the examples and also in roughly understanding their application purpose. Open the JCC and select the starter view as described in exercise A1.  
On the left hand side of the starter you can see the agent and application files that can be loaded in a tree like structure. As top-level elements the starter can handle jar files or directories representing the root of Java packages (often named "classes", "bin" or "build").
Since the newer versions of Jadex a default project is automatically loaded at startup so that you can already see example folders in that panel.

In case you want to add a new directory with agent models, you should choose the ![](newaddfolder.png) button ("Add Path") and browse to the corresponding directory you want to add.
All Jadex examples are contained in the different 'jadex-applications-xyz.jar' files in the JADEX_HOME/lib directory. 
The content of the new jar will be added as new node to the model tree.  
At the bottom you can now see this jar-file beeing scanned (if the scanning option has not been disabled). 
Next, you can expand the model tree by double clicking on the corresponding top-level node, which represents the jar-file. 
After openening the folders "jadex" and "examples" you will see the different example folders containing several different kinds of single- and multi-agent applications. Concretely the following BDI V3 example applications are currently available:

-   **Blocksworld:** Stacking blocks on a table to reach a specific target configuration of blocks.
-   **Cleanerworld:** Simulated cleaner robots collecting waste at day and patrolling at night.
-   **Garbagecollector:** Simplified version of the cleaner task using a grid world.
-   **Helloworld:** Very simple agent that prints "hello world" to the console output and then kills itself.
-   **Marsworld:** Cooperative exploitation of ore on mars by different kinds of robots.
-   **Puzzle:** An agent that tries to solve a puzzle by trying out moves and taking them possibly back.
-   **Shop:** Example for BDI with services. Buying virtual goods from seller agents.
-   **University:** Simple implementation of teaching example from the book "Developing Intelligent Agent Systems: A Practical Guide" written by L. Padgham and M. Winikoff.

Starting an example can in most cases be done by opening the corresponding folder and searching for an application file (a file ending with ".application.xml").
Such an application definition has the purpose to start-up all relevant application agents and may also setup further application components.  
In case there is no application file the example can be started by selecting the agent directly (e.g. HelloworldBDI or PuzzleBDI).

![](marsworld.png)  
*Figure 1 The marsworld example*

In the same way you can add other paths and execute your own applications and agents later. In this case you will not add jar-files, but the root directory of your packages.

### Explain example behaviour
 Choose one of the more complex examples (e.g. marsworld) for a more detailed analysis. 
 Select the application of the example and read through its documentation shown in the starter panel.
 Then look into the selected example directory and read also the documentation of the agents which belong to that application.  
 Finally, open the source code of these agents in your source code editor and try to grasp roughly of what they are comprised. 
 Write down a (simple!) explanation how the multi-agent system and the involved agents work.

##Exercise A3 - Create first simple Jadex agent

Open a source code editor or an IDE of your choice and create a new package called a1 and an agent class called TranslationBDI.java (cf. Figure "A1 Translation agent").  
The agent is a normal Java class that uses the @Agent annotation to state that it is an agent. 
Also please note that it is currently required that the Java file ends with "BDI". 
Otherwise it will not be recognized as BDI agent. 
Optionally, the @Description annotation can be used to specify a documentation text that is displayed when the agent is loaded with the JCC.

```java

package a1;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;

@Agent
@Description("The translation agent A1. <br> Empty agent that can be loaded and started.")
public class TranslationBDI
{
}

```
*A1 Translation agent*



### Start your first Jadex agent
Start the JCC and use the "Add Path" button explained above to add the root directory of your example package. Then open the folder until you can see your file "TranslationBDI". 
The effect of selecting the input file is that the agent model is loaded.  
When it contains no errors, the description of the model, taken from the @Description annotation, is shown in the description view. 
In case there are errors in the model, correct the errors shown in the description view and restart the platform (class reloading is not supported).  
Below the file name, the agent name and its default configuration are shown.
After pressing the start button the new agent should appear in the agent tree (at the bottom left). 
It is also possible to start an agent simply by double-clicking it in the model tree.

*Please note that when you use a double-click on the model name in the left tree view to start an agent, the settings on the right will be ignored.*

<!--You can also start a second JCC by choosing it from:-->

<!--**jadex/tools/jcc/JCC.agent.xml**-->
 <!--               -->
<!--and giving it a name like JCC2.-->