Chapter 4 - Data and Parameters
============================================

This chapter covers the handling of data in processes. Data is made available as parameters, which can be global to the process or local to a task. Therefore, it is possible to map data to input/output values of activities as well as, e.g., storing results for later use.

Exercise C1 - Global Parameters
--------------------------------------------

In this lesson global parameters are introduced. Global parameters can hold data value that belong to a process instance as a whole. The data is available to all tasks of the process and can be read and/or written depending on the parameter specification.

### Specifying Parameters

Create a new diagram as shown below. Edit the process properties (i.e. select the pool) and switch wo the parameters section. Add two parameters, one for holding the name of the customer and one for counting the number of logins. The first parameter is of type string and second one is an integer. You can use any Java type for a parameter, including custom Java classes. The value that can be specified in the process properties represents an initial value for the parameter that is set when the process is instantiated. The value has to be specified as Java expression, e.g. a string has to be enclosed in quotes.

![04 Data and Parameters@1.png](1.png)



Use a print task for the 'Welcome' and 'Status' activities. Print some text that includes the parameter values in the string expressions. E.g. set the texts to '"Welcome "+customer+"!"' for the welcome task and '"Customer "+customer+" has logged in "+logins+" times."' for the status task. If you run the process, you will see that the values of the parameters are filled in the text.

### Updating Parameter Values

Now we want to increment the value of the 'logins' parameter after the welcome task. This can be done by using a new task of type 'WriteParameterTask' (or also 'WriteContextTask' which is only applicable for global parameters). Add a new task between the existing ones and set its type to 'WriteParameterTask'. In the task parameters fetch the default ones and delete the 'key' parameter (it is only needed in we want to insert values into collections of maps). The 'name' should be set to 'logins' and the value to 'logins+1', which access the old value and increments it.

![04 Data and Parameters@2.png](2.png)



Execute the process and observe that the incremented value is printed now.

Exercise C2 - Local Parameters
-------------------------------------------

Some of the previous examples already have used local parameters for the 'text' value of the print task. This lesson takes a closer look at how local parameters work.

### User Interaction

Create a process as shown in the figure. Use the class 'jadex.bpmn.runtime.task.UserInteractionTask' for the 'Enter Address' task. The user interaction task displays all declared parameters and allows to input values for the parameters with direction 'out' and 'inout'. If you find using 'out' for input values counterintuitive you should take the perspective of the task and not the user. The values provided by the user represent the output of the task after its execution.

![04 Data and Parameters@3.png](3.png)



Add a parameter 'address' of type string. Leave the other tasks empty for now (i.e. do not set a task class). Execute the process to see how the user interaction task works.

![04 Data and Parameters@userinteraction.png](userinteraction.png)



### Using Local Parameter Values

Data flow between tasks is modelled using data edges. A data edge is a connection between the out parameter of a task with an in parameter of another task. Data edges allow for transferring parameter values between arbitrary tasks of the same level, i.e. one cannot draw a data edge to a task inside a subprocess - instead one has to first connect the subtask itself per data edge and afterwards route it to the corresponding task. 

In the example we want to access the address the user entered in both subsequent tasks. To enable this we need to draw two data edges that connect the 'address' out parameter with in parameters of the other tasks. Make both tasks to PrintTasks and fetch the corresponding 'text' parameter. Then draw two edges connecting the 'address' parameter with the 'text' parameter for each task. In order to print not only the address itself, we want to modify the parameter value when transferred to the 'text' parameter by using a 'Value Mapping'. This can be found as property of the data edges. Set the first mapping to '"Shipping to: "+address' and the mapping of the second task to '"Arrived at: "+address' as indicated below.

![04 Data and Parameters@4.png](4.png)



Run the process and observe if the data arrives at the tasks.

Exercise C3 - Parameter Scopes
-------------------------------------------

In BPMN, an internal subprocess represents a task that is recursively composed of inner tasks (see [Exercise B5](03%20Basic%20Processes) ). The tasks in the subprocess are executed as if they were a separate process, but they have access to the context of the outer process. Therefore subprocesses can be used to define custom scopes for parameters. This lesson shows how the example from the last lesson can be improved by using a subprocess as a parameter scope.

Create a Subprocess
--------------------------------

Create a process similar to the one from C2, but place the shipment activities into a subprocess 'Handle Shipping'. Add a parameter called 'address' of type string to the subprocess. Edit the two last activities of the subprocess to print out some message that includes the 'address' value, e.g., '"Shipping to: "+address' and '"Arrived at: "+address'. In the first activity, which uses the interaction task remove the 'address' parameter. The interaction task will check if its current task has own parameters and if no ones are declared it will search further upwards for parameters. Here, the subprocess declares a parameter 'address' which it will show and automatically assign to the user value.

![04 Data and Parameters@5.png](5.png)



Execute the process and verify that it works as expected.

This example shows that a subprocess scope can be used to avoid having to pass parameters through a sequence of tasks. Still the parameters of the subprocess do not clutter up the global namespace.
