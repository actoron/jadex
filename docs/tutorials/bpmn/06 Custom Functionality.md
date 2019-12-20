Chapter 6. Custom Functionality
============================================

In the previous chapters the, processes have been built only from the built-in functionality of Jadex BPMN. For any practical application one needs to add application-specific functionality to the system. This chapter deals with the various ways to extend Jadex BPMN and introduce custom functionality.

Exercise E1 - Custom Java Objects
----------------------------------------------

Many processes are about the processing of data. While data can be represented in basic data types such as integer and string it is usually advantageous to have application-specific data type, so called domain or business objects. This exercise shows how to use custom Java classes to represent data and operations on that data.

For this purpose, we the example of an insurance company that wants to sell a contract to a new customer. Based on customer properties, it is decided, if a high-risk or a standard contract is sold.

### Defining the Customer Object

The customer should be represented in its own Java class. In addition to customer properties such as name, age, gender, and marital status, the class should implement a function to assess the risk of the customer. A simple business rule is used here: a customer is assumed to be risk taking, if it is a single male of age below 40. The code of the Customer class is shown below.


```java

package jadex.bpmn.tutorial;

public class Customer
{
  protected String name;
  protected String gender;
  protected int age;
  protected boolean married;

  public Customer(String name, String gender, int age, boolean married)
  {
    this.name = name;
    this.gender = gender;
    this.age = age;
    this.married = married;
  }

  public boolean isRiskTaking()
  {
    return gender.equals("male") && age < 40 && !married;
  }	

  public String toString()
  {
    return "Customer("+name+")";
  }
}

```


 

For simplicity Java class should be created in the same package as the process. If you want to create the class in a different package, you need to add a corresponding import statement in the process (see below).

### Creating the Process

Create a new process in the same package as the customer class. Make sure to set the 'package' property of the process accordingly, otherwise the customer class can not be resolved and the process will not execute.

![06 Custom Functionality@06 Custom Functionality@eclipsecustomobject.png](../06%20Custom%20Functionality/06%20Custom%20Functionality-eclipsecustomobject.png) 

Use a UserInteractionTask with 'name', 'gender', 'age', and 'married' as in-parameters for the 'Input Customer Data' activity. Set the types of the parameters to string, string, int, and boolean as required for the attributes of the customer Java class. Also, set some values for the parameters. This will save typing when later testing the process.

The 'Create Customer Object' activity does not require a task class. Just add an out-parameter 'customer' of type 'Customer' with value 'new Customer(name, gender, age, married)'. The value is a Java expression, which calls the constructor of the customer class with the values provided by the previous 'Input Customer Data' activity.

Set the condition of upper flow connector ('Risk Taking') to 'customer.isRiskTaking()'. The condition executes the business rule defined in the Java class. The 'Sell High Risk Contract' and 'Sell Standard Contract' activities can be set to UserInteractionTask again. Thus, when executing the process, we can observe, which path has been taken.

### Executing the Process

Start the process and enter some customer values (or use the default values, if you have specified some in the properties). Test that a standard or high risk contract will be sold depending on the entered customer data.

If the process produces an error, e.g. 'Class Customer not found in imports', you should make sure that the customer class is in the same package as the process and that the 'package' property of the process is set appropriately. Also, you can verify that the directory that you added in the JCC contains the process .bpmn file as well as the compiled Java .class file.

Exercise E2 - Custom Tasks
---------------------------------------

Most of the functionality of a process is encapsulated in the tasks. For complex functionality, one usally needs to provide custom implementations of task behavior that go beyond simple print statements. Writing custom tasks is the simplest way to add application-specific business functionality to the BPMN engine. Tasks can be developed to be only used in a single process or to be reused across many processes of an application.

Jadex BPMN allows two types of tasks: 1) simple atomic tasks, that block process execution until they are finished, and 2) asynchronous tasks, that only block the branch of the process that contains the task, while other branches of the process can continue to execute. This lesson introduces the first type of task. The following lesson will then introduce the second type and highlight the differences between both.

### A Simple OK Task

Suppose we want to have a simple task that opens a requester with only an 'OK' button. This functionality can easily be achieved using the JOptionPane from swing. The following code shows how to wrap this functionality into a task implementation that can be embedded into a Jadex BPMN process.


```java

package jadex.bpmn.tutorial;

import javax.swing.JOptionPane;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

public class OKTask extends AbstractTask
{
  public void doExecute(ITaskContext context, BpmnInterpreter instance)
  {
    String message = (String)context.getParameterValue("message");
    String title = (String)context.getParameterValue("title");
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
  }
}

```


 

To implement a simple (synchronous) task, extend the *jadex.bpmn.runtime.task.AbstractTask* class. This class defines one abstract method, that you have to implement: *doExecute(ITaskContext context, BpmnInterpreter instance)*. In this method you can put any custom functionality as required by your application, e.g. simple calculations, calling legacy systems, etc. The parameters *context* and *instance* provide access to the running process, e.g. to read or write process data.

The 'OKTask' first reads two parameter values from the process context, which are used for the requester title and message. Then the JOptionPane is activated using the extracted parameter values.

### Planning a Party

We use a simple checklist process to demonstrate the custom task. The process includes three unrelated tasks that are all mapped to the new 'OKTask' implementation. The process terminates, when all requesters have been closed. The picture below shows the process and also the inclusion of the 'OKTask'.

![06 Custom Functionality@eclipseparty.png](../06%20Custom%20Functionality/06%20Custom%20Functionality-eclipseparty.png) 

As we expect that the checklist items can be checked in any order, we do not impose ordering restrictions between the tasks, i.e. there are no flow connectors in the process. Also, because the process and the custom task implementation reside in the same package, we can simply write 'OKTask' instead of a fully qualified name like 'jadex.bpmn.tutorial.OKTask'. If you have problems ('Class OKTask not found in imports') remember to set the package property of the process accordingly.

In the BPMN diagram, the tasks contain two parameters: 'message' and 'title'. These are the parameters that we used in the task implementation. 

When the process is executed, the requesters for the tasks appear one after another. Because the JOptionPane blocks the thread when *showMessageDialog()* is called, the whole process waits until the requester is closed. Only then the process will activate the next task and open the next requester.

The simple atomic or blocking task type introduced in this lesson is most useful for functionality that quickly completes, such as a calculation or a database query. During the task execution, the whole process is blocked, which is advantagues with respect to consistency, e.g. no other task can alter process data during an ongoing calculation. The task type is less well-suited for long-term activities, e.g. that involve human interaction. For this kind of activities asynchroneous tasks as introduced in the next lesson are a better fit.

### Task Meta Informaion

The *OKTask* requires the *message* and *title* parameter to be specified in the process. This is usually not obvious for other developers that might want to use your custom tasks. Therefore, you can add annotations to the task class that e.g. declare the parameters. Add the annotations from the 'jadex.bpmn.annotation' package to the *OKTask* as shown below.


```java

@Task(description="A task that displays a message using a JOptionPane.", parameters={
  @TaskParameter(name="message", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The message to be shown."),
  @TaskParameter(name="title", clazz=String.class, direction=TaskParameter.DIRECTION_IN, description="The title of the dialog.")
})
public class OKTask extends AbstractTask
{
  ...
}

```




The annotations provide a human readable description for the task as well as information about the task parameters, such as name, type (clazz), direction, initial value (not shown) and a description of the parameter. When you edit the party process in the BPMN editor, you will notice that this information is displayed, when the task class is selected. In addition, you have the option to add the parameters to the parameter section of the properties editor.

Exercise E3 - Asynchroneous Tasks
----------------------------------------------

Consider the party planning checklist process from the last exercise. It makes perfect sense to execute the three tasks of the process (organizing drinks, music, and people) in parallel and the process description in BPMN imposes no restrictions with respect to parallel task execution. Yet, the implementation of the task causes the activities to be executed in sequence - one after the other.

Whenever there are external activities involved in a process, e.g. a human user working on a worklist item, it should be considered to execute this activity asynchroneous to the process. Thus, different external activities (e.g. organizing drinks and inviting people) can be executed in parallel, when the process description allows this.

This lesson shows how to implement an asynchoneous task.

### Asynchroneous Task Implementation

For a simple blocking task, you can implement the *doExecute()* method of the *AbstractTask* class. When the method returns, the task is completed and the process continues. For an asynchroneous task, the implementation is somewhat more complex, because the activation of the task and the completion have to be handled separately.

To implement an asynchroneous task, you have to implement the *execute()* method of the *jadex.bpmn.runtime.ITask* interface. When the method is called, the task execution is activated (e.g. inserting an entry into the work list of a workflow management system. When the method returns, the process will not continue, but wait until the callback result listener that is supplied as argument to the execute() method is called. Until then, the process may execute other parallel activities. When the task is completed (e.g. the work item is marked as finished by a user), the result listener has to be called, causing the process to continue.


```java

package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.SGUI;
import jadex.commons.concurrent.IResultListener;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class AsynchronousOKTask implements ITask
{
  public void execute(ITaskContext context, BpmnInterpreter process, final IResultListener listener)
  {
    String message = (String)context.getParameterValue("message");
    String title = (String)context.getParameterValue("title");
    int offset = context.hasParameterValue("y_offset") ? ((Integer)context.getParameterValue("y_offset")).intValue() : 0;

    JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
    final JDialog dialog = new JDialog((JDialog)null, title, false);
    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dialog.setContentPane(pane);
    dialog.pack();
    Point loc = SGUI.calculateMiddlePosition(dialog);
    dialog.setLocation(loc.x, loc.y+offset);
		
    pane.addPropertyChangeListener(new PropertyChangeListener()
    {
      public void propertyChange(PropertyChangeEvent e)
      {
        String prop = e.getPropertyName();
        if(prop.equals(JOptionPane.VALUE_PROPERTY))
        {
          dialog.setVisible(false);
          listener.resultAvailable(this, null);
        }
      }
    });
    dialog.setVisible(true);
  }
}

```


 
