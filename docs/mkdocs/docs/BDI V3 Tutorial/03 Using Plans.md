# Plans

Plans play a central role in Jadex, because they encapsulate the recipe for achieving some state of affair. A plan defines two aspects. 

In the **head** of the plan (i.e. in its @Plan annotation) meta information about the plan is defined. This means that in the plan head several properties of the plan can be specified, e.g. the circumstances under which it is activated and its importance in relation to other plans.

The **body** of a plan contains the concrete instruction that should be carried out. 
The concrete representation of a plan in Jadex can vary as it is the case for beliefs and goals as well. 
The rationale behind this is that we wanted to achieve language orthogonality between BDI and object oriented concepts. For this reason it is possible to use the following elements as a plan by adding a @Plan annotation to it:

-   **Method**: in this case not all plan aspects can be used, e.g. no pre- or context conditions are possible)
-   **Inner Class**: in case of a non-static inner class allows for easy access of agent beliefs and fields
-   **Class**: facilitates reuse in different agents and projects

For a plan, the triggering events and goals can be specified in the plan head to let the agent know what kinds of events this plan can handle. When an agent receives an event, the BDI reasoning engine builds up the so called applicable plan list (that are all plans which can handle the current event or goal) and candidate(s) are selected and instantiated for execution. 

Often a plan does some action and then wants to wait until the action has been done before continuing (e.g. dispatching a subgoal). Therefore a plan can use one of the various waitFor() methods of the plan API, that come in quite different flavors. 
The plan API can be retrieved as an object via two mechanisms. 
First, the @PlanAPI annotation can used above a field of type IPlan in plan classes. 
The engine will automatically inject the plan API when a plan instance is created. 
When using a method as plan this is not possible. Hence, the signature of the plan method can be used to retrieve the plan API just by adding a parameter of type IPlan. Please note that in Jadex methods that are invoked by the framework can have any signature. The engine will do its best to automatically determine which values are expected and set them as parameter values. If the engine does not find a suitable value of a given type the value will be null.

Exercise B1 - A Plan as Normal Java Class
------------------------------------------------------

In this exercise we will use a plan for translating words from English to German.  
Create a new TranslationBDI.java file by copying the file from the last lecture.

### Creating the Plan

Create a new file called TranslationPlan.java responsible for a basic word translation with the following properties:

-   Content of the plan class:


```java

@Plan
public class TranslationPlan
{
  protected Map<String, String> wordtable;

  public TranslationPlan()
  {
    // Init the wordtable and add some words
  }
 
  @PlanBody
  public void translateEnglishGerman()
  {
    // Fetch some word from the table and print the translation
  }
}

```


-   In this first version we will use a very simple plan that does not allow for translating words on request. Instead we here just use a hash table as kind of dictionary for a few word pairs. The dictionary should be created in the constructor and some word pairs should be added.
-   In the body method (the name of the method and its signature does not matter, the annotation is important) we just look up one word and print the translation in the form:  
    System.out.println("Translated: "+eword+" - "+gword);  
    letting eword and gword being the English and German words respectively.

### Adding the plan to the agent

-   Add the annotation to the agent class: @Plans(@Plan(body=@Body(TranslationPlan.class)))
-   Add a field called bdi to the agent class and annotate it with @AgentFeature. The field should be of type IBDIAgentFeature. This will let the engine automatically inject the bdi agent (api) to the pojo agent class. 
```java
@AgentFeature 
protected IBDIAgentFeature bdiFeature;
```

-   Add an agent body method that is automatically invoked when the agent is started and adopt a plan using

```java

@AgentBody
public void body()
{
  bdiFeature.adoptPlan(new TranslationPlan());
}

```

### Starting and testing the agent
Create a translation agent via the Jadex Control Center and observe the output. You should see it printing the translated word.

Exercise B2 - A Plan as Inner Class
------------------------------------------------

In the lecture we will use an inner class as plan instead of an extra plan class. The functionality remains the same. Again, copy the translation agent class from the last lecture and apply the following changes:

-   Remove the @Plans annotation from the class file completely. Only extra plan classes need to be declared in this way. Inline elements will be found automatically when scanning the class file.
-   Copy the contents from the plan class of the last lecture in the agent class file (as inner class).


```java

@Agent
@Description("The translation agent B2. <br>  Declare and activate an inline plan (declared as inner class).")
public class TranslationBDI
{
  ...
  
  @Plan
  public class TranslationPlan
  {
    ...
  }

```


-   Adapt the adoptPlan() method call to use the new inner class

### Starting and testing the agent
Start the agent as explained in the preceding exercise. Observe if the same output is produced.

Exercise B3 - Plan as Method
-----------------------------------------

Once again, in this lecture the same functionality will be created. But this time, the plan will be represented as method. This can be very helpful, if the plan is rather simple. Furthermore, using methods as plans helps reducing the number of classes in a project.

### Changing the agent 

Again, copy the agent file from the last lecture and do the following:

-   Copy the word table field from the inner to the agent class
-   Copy the init code for the word table to the newly created init method of the agent

```java

@AgentCreated
public void init()
{
  ...
}

```

-   Adapt the adoptPlan() method call to 
```java
bdiFeature.adoptPlan("translateEnglishGerman");
```

Instead a plan object we just give the name of the method representing the plan.
-   Create a method as plan using the following code

```java

@Plan
public void translateEnglishGerman()
{
}

```

-   Then remove the inner plan class completely.


### Method Plans with Parameters
When you create your plans as inner classes, you can just pass parameters as constructor arguments.
However, it is also possible to have parameterized plans using method plans.
First, declare a parameter of type *ChangeEvent*:
```java
@Plan
public void translateEnglishGerman(ChangeEvent<Object[]> event)
{
    String word = (String)ev.getValue()[0];
}
```

Next, pass the parameter when you adopt the plan:
```java
bdiFeature.adoptPlan("translateEnglishGerman", "dog");
```

All parameters passed this way will be available inside the *ChangeEvent.getValue()* array passed to the plan method.

### Starting and testing the agent
Test and verify that the agent behavior is the same as in the last exercise.

Exercise B4 - Using Other Plan Methods
---------------------------------------------------

In this exercise we will explore other plan methods. Besides the already known body method three other plan lifecycle methods exist, which are called respectively when the plan passes successfully (@PlanPassed), fails with exception (@PlanFailed) or is aborted (@PlanAborted) e.g. when the context of plan becomes invalid. 

This time, we need a translation agent with an inner plan class to be able to add the aforementioned method. Hence, it is most convenient to take the class from exercise B2 as starting point and copy its content to the new file. Afterwards we need to apply the following changes:

### Changing the agent

-   Add a try-catch-block to the adoptPlan() call and wait for the plan to be finished using get() at the end of the invocation. The get() turns the future based asynchronous call into a synchronous one. For more information about asynchronous programming with futures in Jadex please refer to the [AC User Guide](../AC User Guide/03 Asynchronous Programming). The agent body method should look like this:


```java

try
{
  bdiFeature.adoptPlan(new TranslatePlan()).get();
}
catch(Exception e)
{
  e.printStackTrace();
}

```

### Changing the plan

-   Add the three lifecycle methods to the plan inner class in the following way:


```java

@PlanPassed
public void passed()
{
  System.out.println("Plan finished successfully.");
}
  
@PlanAborted
public void aborted()
{
  System.out.println("Plan aborted.");
}
  
@PlanFailed
public void failed(Exception e)
{
  System.out.println("Plan failed: "+e);
}

```


-   Modify the plan body to throw an exception:


```java

@PlanBody
public void translateEnglishGerman()
{
  throw new PlanFailureException();
  // System.out.println("Translated: dog - " + wordtable.get("dog"));
}

```


### Starting and testing the agent
After starting the agent you should observe that due to the exception in the plan body the failed method is invoked. In the agent body the exception is rethrown when the get() on the result future of adoptPlan() is invoked. Also try out what happens when you do not throw the exception in the plan body.

Exercise B5 - Plan Context Conditions
--------------------------------------------------

Besides the lifecycle methods that have been introduced in the former exercise a plan may also have a pre- and/or a context condition. The precondition is evaluated before a plan is going to be executed and if it evaluates to false to plan will be excluded. In contrast, the context condition has to hold during all the time a plan is executing. If it turns to false at some point in time, the plan will be aborted. In this execise we will learn how a context condition can be used.

### Creating the agent
As preparation we can copy the agent from the last exercise and modify the following:

-   We add a field named context of boolean type and put an @Belief annotation above it. Details about the meaning of beliefs will be explained in the next chapter. 


```java

  @Belief
  protected boolean context = true;

```


-   To access the waitFor methods we add another @AgentFeature of type IExecutionFeature to our agent. 


```java

  @AgentFeature
  protected IExecutionFeature execFeature;

```


-   In the agent body method we do not wait until plan completion. Instead we wait for one second and afterwards set the context field to false. 


```java

try
{
  bdiFeature.adoptPlan(new TranslatePlan());
  execFeature.waitForDelay(1000).get();
  context = false;
  System.out.println("context set to false");
}
catch(Exception e)
{
  e.printStackTrace();
}

```

### Changing the plan

-   In the inner plan class we add a field for the plan API and a method for the context condition. The plan API is of type IPlan and needs the @PlanAPI annotation. This ensures that the API will be automatically injected to the field when the plan is created. The context method should have a @PlanContextCondition annotation. Furthermore, we want the condition to be reevaluated whenever the belief context changes. This is achieved by adding a dependency to the context belief via the beliefs declaration in the annotation. The method itself should simply return the value of the context field. 


```java

@PlanAPI
protected IPlan plan;

@PlanContextCondition(beliefs="context")
public boolean checkCondition()
{
  return context;
}

```


-   Finally, the plan logic has to be changed in order to be active a longer period of time. To achieve this we first print 'Plan started' and then use a waitFor() statement to let the plan wait for 10 seconds. The wait methods are accessible via the injected plan API. Finally, we add a print statement with 'Plan resumed'.


```java

@PlanBody
public void translateEnglishGerman()
{
  System.out.println("Plan started.");
  plan.waitFor(10000).get();
  System.out.println("Plan resumed.");

  System.out.println("Translated: dog - " + wordtable.get("dog"));
}

```


### Starting and testing the agent
This time the agent should start executing the plan but automatically abort it after one second when the context becomes invalid. To verify this you should check if you see the print of the plan aborted method.
