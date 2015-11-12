<span>Chapter 3 - Basic Processes</span> 
========================================

In this chapter you will learn how to model your own processes. This chapter covers basic issues such as activities and control flow. It is assumed that you have some initial understanding of BPMN and its graphical elements. If you think that you need more background information on BPMN, please refer to documentation available elsewhere. Below is a short list of suggestions, but you will easily find other sources of information on the Web.

-   <span class="wikiexternallink">[Official BPMN homepage (www.bpmn.org)](http://www.bpmn.org/)</span>
    -   <span class="wikiexternallink">[Introduction to BPMN (article, PDF)](http://www.bpmn.org/Documents/Introduction_to_BPMN.pdf)</span>
    -   <span class="wikiexternallink">[BPMN Tutorial (slides, PDF)](http://www.bpmn.org/Documents/OMG_BPMN_Tutorial.pdf)</span>
    -   <span class="wikiexternallink">[BPMN Specification (download site)](http://www.omg.org/spec/BPMN/1.2/)</span>
-   <span class="wikiexternallink">[BPMN Corner at Uni Potsdam](http://bpt.hpi.uni-potsdam.de/Public/BPMNCorner)</span>
    -   <span class="wikiexternallink">[BPMN 2.0 Poster (pdf)](http://www.bpmb.de/images/BPMN2_0_Poster_DE.pdf)</span>
    -   <span class="wikiexternallink">[BPMN 1.2 Poster (pdf)](http://bpt.hpi.uni-potsdam.de/pub/Public/BPMNCorner/BPMN1_1_Poster_EN.pdf)</span>
-   <span class="wikiexternallink">[Wikipedia entry for BPMN](http://en.wikipedia.org/wiki/BPMN)</span>

<span>Exercise B1 - Creating a First Process</span> 
---------------------------------------------------

In this lesson, you will create and execute a first process. First, start the Jadex BPMN editor using the run configuration created in the last section. The editor will automatically create a new unnamed BPMN model. To save the model under a custom name e.g. 'B1\_simple' use 'Save as' in the 'File' menu. The destination folder should be located in the Java 'src' folder of your eclipse 'bpmntutorial' project, i.e. you need to navigate to the eclipse workspace to find it. For easy reference the tutorial files are named according to the corresponding lesson, but you are free to choose a different name.  

### <span>Jadex Project Setup</span> 

The just created process can already be executed without further editing. As you have created the BPMN process using the Jadex editor, first you will have to refresh the 'bpmntutorial' project using 'refresh' from the popup menu or by selecting the project and pressing 'F5'. Now, start the Jadex platform using your existing launch configuration (see <span class="wikiexternallink">[Exercise A3](02%20Installation)</span>). The JCC window will appear, probably showing the example project that you created in Lesson A3. Right-click in the model explorer and choose 'Add Path'. Browse to your eclipse workspace an select the 'bin' or 'classes' folder from the eclipse project that you created in the beginning of this lesson. When you unfold the contents, you should find the package(s) that you created and the process contained within.

![03 Basic Processes@1.png](1.png)

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Select your process in the tree and click the 'Start' button. You might think that nothing happens, but actually the process is instantiated and executed. The reason that you cannot observe anything is that the process does not contain activities and therefore immediately terminates without producing any output. In the next sections, you will change this and actually see some process output. Before going back to the diagram editor, you should save the JCC project ('Save Settings' in 'File' menu).

### <span>Process Properties</span> 

The lower area in the BPMN editor allows you to see and edit the details of the currently selected diagram element (e.g. a task in the process). In addition to properties of the visible diagram elements, the process as a whole also has properties that can be edited. You can access the process properties by selecting a pool or the empty background of the diagram.

You can see that a process has the following properties:

-   **Name:** The process name - should be the same as the filename.
-   **Description:** A text with documentation about the process.
-   **Package:** The package in which the process is contained (like the package of a Java class file).
-   **Imports:** Import classes and packages that you want to use inside the process.
-   **Configurations:** A configuration allows for starting a process with a specific set of settings.
-   **Start Elements:** For each configuration the elements that should be started can be selected (e.g. if a pool should be active).
-   **Parameters:** Parameters can be used to hold global data. Additionally, parameters can be made to arguments and results as well.
-   **Provided Services:** Services that are offered by the process.
-   **Required Services:** Services that are needed by the process.
-   **Subcomponents:** The subcomponent model definitions.

The description can contain arbitrary text and HTML tags. The description is, e.g., displayed in the JCC, when selecting the process. Enter a description for your process, restart the Jadex platform (or just reload the process) and see how the description is displayed. Please note, that you alays have to refresh the eclipse project, otherwise the changes will not be recognized.

You can also enter a package for your process. The package should always correspond to the directory structure, where your process is located. Otherwise you will run into problems later, when you try to use your Java classes in the process. Imports, parameters, arguments and results will be covered in later lessons and can stay empty for now.

### <span>Printing to the Console</span> 

Finally, you probably want to see that the process is really executed, when you click the start button. This can be achieved by changing the task in the process to print some text to the console. Open the diagram in the BPMN editor (if not already open) and add a 'Task' element. Selecting the task will show its properties in three tabs (Task, Properties, Parameters) in the lower area:

-   **Task:** In the task view you can enter the Java class that should be executed when the task is invoked. Below the classname usage information of the task is presented including a description and the used parameters.
-   **Properties:** Properties are settings that are directly related to the BPMN element, e.g. a time duration for a timer event. Thus, all BPMN elements of the same type expose the same properties. 
-   **Parameters:** Input and output parameters for the activity.

Jadex provides some ready-to-use task implementations, which can be choosen from the drop-down list. The available contents of the list is found by scanning the class path of the editor. To include the standard Jadex task implementation we need to add the Jadex jars to the classpath of the editor. To do this, go to the 'File' menu and open the 'Settings' dialog. Switch to the 'Class Path' tab and choose 'Add Project'. Here, choose the Jadex distribution directory. The editor will automatically scan the folder structure for jars and add them to the classpath. You should see the Jadex jars in the dialog afterwards. After exiting the settings dialog you will see the editor refreshing its class cache used for the autocompletion. You can also manually start rescanning by pressing the refresh button ![03 Basic Processes@2.png](2.png) at the lower left of the editor panel. 

![03 Basic Processes@3.png](3.png)

<div class="wikimodel-emptyline">

</div>

You can also implement your own tasks, by writing corresponding Java classes. The available task implementations as well as how to produce your own tasks will be covered later. For this lesson, just select the 'jadex.bpmn.runtime.task.PrintTask', which allows printing some text to the console.

You will see that some description text about the task is displayed. Among other things, the description tells you that this task implementation expects an input parameter 'text' of type String. To set the text that should be printed we first have to switch to the 'Parameters' tab and afterwards include the default parameters of the selected task class. This is done by clicked this button ![03 Basic Processes@4.png](4.png). Enter "The task has been executed" in the 'Initial Value' column of the parameter table. The value is entered as a Java expression, which is why you have to enclose your text in quotes. To make the process better readable, also draw a start and end event and connect them to the task. It should look like the diagram below.

![03 Basic Processes@5.png](5.png)

<div class="wikimodel-emptyline">

</div>

Save the model, refresh Eclipse and restart the Jadex platform. Observe that your text gets printed to the eclipse console every time that you start your process.

<span>Exercise B2 - Sequence of Tasks</span> 
--------------------------------------------

In this lesson you will learn how to execute tasks in sequence, i.e. one after another. 

### <span>Creating Tasks and Flow Connectors</span> 

Create a new BPMN diagram with a name of your choice, e.g. 'B2\_Sequence'. Create three tasks connected by flow connection arrows. Flow connection arrows have a continuous line and a solid head. There are different options to create tasks and flow connections. You can select the task or flow connection element in the palette above the diagram and add the task at the required place or draw a connection between tasks. Another way is using the input/output connectors that appear when clicking in the middle of existing elements as shown below. Just drag a connector to an empty place and select the element to be created.

![03 Basic Processes@6.png](6.png)

<div class="wikimodel-emptyline">

</div>

To easily observe how the different tasks are executed, change the task implementations to the PrintTask and enter some message in each of the text parameters (see last lesson for details). Execute your process using the JCC and observe the console output. You might have to refresh the model tree (e.g. by right-clicking of the folder and selecting 'Refresh' or by just pressing \[F5\]) for the new process to show up.

<span>Exercise B3 - Parallel Activities</span> 
----------------------------------------------

This lesson introduces forms of parallelism in processes. Each process can execute any number of so called process threads, which are independent control flows inside the process. Such control flows can appear and vanish during the execution of a process, e.g. at split or join nodes.

### <span>Creating the Process</span> 

Create a new process called, e.g. 'B3\_Parallel'. Add tasks, connectors and gateways as shown below.

![03 Basic Processes@7.png](7.png)

<div class="wikimodel-emptyline">

</div>

In this process, parallelism is introduced at two places. First, the tasks 'Task 1a' and 'Task 1b' are parallel to each other. A process always starts execution at start events and node(s) without incoming control flow connections. As the process has two start events it has two starting points.

Second, the tasks 'Task 2a I' and 'Task 2a II' are parallel tasks, because of the explicit parallel gateway 'Gateway 1' before the nodes. Parallel gateways are sometimes also called AND gateways. The two control flows of 'Task 2a I' and 'Task 2a II' are merged together by the second AND gateway 'Gateway 2'. The two forms of AND gateways are also called 'split' and 'join' gateways. They are represented by the same symbol, but can be distinguished, because a split has only one *incoming* edge while a join has only one *outgoing* edge. The semantics of the join is that 'Task 3a' may only be executed after both 'Task 2a I' and 'Task 2a II' have been completed.

### <span>Observing the Execution</span> 

To see some results during the execution, you can make use of the PrintTask as in the previous lessons. You can also observe the execution of the process in the debugger. 

![03 Basic Processes@8.png](8.png)

<div class="wikimodel-emptyline">

</div>

In the example screenshot you can see, that there are currently three control flows in the process with numbers 1, 2 and 4. Process thread 1 has proceeded to 'Task 1b', thread 2 is at the parallel join gateway and thread 4 is at 'Task 2aII'. Please note that thread 1 and 4 are ready, i.e. can execute the next step while thread 2 is waiting for the second thread at the gateway and thus cannot immediately proceed. The different states (waiting vs. ready) are also signalled by the color of the corresponding elements in the diagram (red vs. green). Play around with the process in the debugger. Also try out using breakpoints. You will notice, that the process is suspended whenever one of the control flows hits a breakpoint. Because the process is suspended as a whole this means that also the other control flows will stop executing when a breakpoint is hit.

<span>Exercise B4 - Conditional Branch</span> 
---------------------------------------------

In this lesson, an XOR gateway is used to split the control flow into one of two branches. Therefore it is shown how to add conditions to flow connectors. Create a new BPMN diagram called, e.g., 'B4\_Choice'. Draw BPMN elements as shown in the picture.

![03 Basic Processes@9.png](9.png)

The process simulates the toss of a coin. Either the 'Head' activity should be executed or the 'Tail' activity. Note the use of the XOR gateway to distinguish between the two cases. The expression 'Math.random() &gt; 0.5' is Java code. To enter the condition expression, first click on the upper sequence flow. This will activate the properties tab in the lower area of the editor. In this view the expression can be placed in the 'Condition' input field. 

*Math.random()* is a function that generates a random value between 0 and 1. The expression is evaluated when the process is executed. When the expression is true (i.e. a value greater than 0.5 is generated) the upper path is chosen. Otherwise the lower path is chosen, called the default branch. The default branch is indicated by the small dash. You can set the default branch by activating the 'Default edge' checkbox in the properties panel. 

There are some more notable things about this process that you might have figured out yourself already. First, the XOR split does not have a corresponding XOR join, e.g. after the 'Head' and 'Tail' activities. Such a join is not necessary, because there are no multiple control flows executing at once. Remember that in the previous example, the AND join was the place where the process execution had to wait that the activities on both branches were completed before continuing the execution. An XOR join could be added for clarity in the B4 example process, e.g. to make the process more readable. Yet, at an XOR join the execution would not stop, because there is always *only one* incoming branch executed at all.

To execute the example process (either version) edit the 'Head' and 'Tail' to print some text to the console. Observe that when executing the process several times, either one or the other activity is executed.

<span>Exercise B5 - Subprocesses</span> 
---------------------------------------

Besides basic tasks, BPMN also supports complex tasks, which are themselves composed of one or more activities. These complex tasks are called subprocesses. In Jadex, subprocesses can be either internal or external. Activities of internal subprocesses are drawn into the same diagram as the outer process. External subprocess have their own diagram, which is referenced in the diagram of the outer (parent) process.

The subprocess element of the BPMN editor is used to specify both types of suprocesses. In this lesson, an *external* subprocess will be defined. An example of an *internal* subprocess can be found in <span class="wikiexternallink">[Exercise C3](04%20Data%20and%20Parameters)</span>.

### <span>Defining a Subprocess</span> 

Draw a new process diagram as shown below. The 'Print Finished' task should be a PrintTask that prints out some finished message. Instead of drawing tasks into to subprocess (as you would do with an internal subprocess), the file name of an external diagram is specified. The 'file' property is used for this purpose. Enter the file name of another process, e.g. 'B2\_Sequence.bpmn'. 

![03 Basic Processes@10.png](10.png)

It is useful to understand, how the file of the subprocess will be loaded at runtime. The process files are loaded from the classpath in the same way that Java loads Java classes. If you used a package for the processes, it is useful to set the package property as recommended in <span class="wikiexternallink">[Exercise B1]()</span>. Because the B5 and B2 processes are in the same package, you do not need to fully qualify the name of the subprocess. Otherwise, you will have to write e.g. 'jadex/bpmn/tutorial/B2\_Sequence.bpmn' or add a 'jadex.tutorial.bpmn.\*' to the imports section of the outer process (in case you used jadex.bpmn.tutorial as package).

### <span>Execute the Process</span> 

Execute the process and observe its output. Verify that the tasks of the external subprocess get executed before the final task of the parent process. In the parent process, the subprocess and the final task element are in the sequence relation. Therefore the outer process waits for the subprocess to finish before executing any further activities.

You may also start the process in suspended mode and watch its execution in the debugger. As the outer process is in step mode, the subprocess will be started in step mode also. Thus you can see it appearing as a child of the outer process in the process tree.
