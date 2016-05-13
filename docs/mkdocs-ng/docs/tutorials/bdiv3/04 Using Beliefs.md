# Using Beliefs

An agent's beliefbase represents its knowledge about the world. The agent is aware of this knowledge and can use it to reason. On the one hand, the beliefs can drive the actions of an agent by e.g. initiating goals or plans and on the other hand the beliefs also control the ongoing behaviour by determining when a goal is achieved or by rendering plans applicable or not. In Jadex BDI V3 beliefs are represented in an object-oriented way as:

-   **Field**: This representation is the most common one and treats a field of an agent as its belief. The field can be of any type whereby special support exists for collection types and arrays. 
-   **Getter/Setter Method Pair**: Using a getter/setter pair as belief allows for putting additional logic into the getter or setter. It also allows for using beliefs without a field.
-   **Unimplemented Getter/Setter Method Pair**: Using a getter/setter pair that is declared native, i.e. without implemention, can be used to have abstract beliefs in capabilities. Such abstract beliefs can already be used in the capability but its representation is assigned by the using agent (or capability). This is useful if the belief should be shared among different capabilities.

If you already know the former versions of Jadex BDI, you may be aware of the distinction between beliefs and belief sets. This distinction is not necessary in V3 any longer and instead all elements are marked with the @Belief annotation.  
Please note that in case of a getter/setter pair it is required to add @Belief to both methods. 
The function of making things beliefs is that the agent becomes aware of changes of these elements. 
This means that is the value of a belief is set to a new value the agent recognizes this change and can act according to this change. 

Exercise C1 - Belief Triggering Plan
-------------------------------------------------

In this exercise we will develop a translation agent that checks if only good word pairs are added to his dictionary. For this purpose we will make the wordtable become a belief and create a check plan that is activated always when the dictionary changes. This time we start with a fresh agent file and do the following:

-   Create a new TranslationBDI agent Java class file and add the @Agent annotation to the class
-   Add two fields to the class representing the agent API and the wordtable


```java

@Agent
protected BDIAgent agent;
	
@Belief
protected Map<String, String> wordtable;

```


-   Add an init method for the agent (using @AgentCreated) and create the wordtable map in it. Additionally, add some example word pairs as usual. As last entry add the following colloquial word pair, which we will check for in the check plan.


```java

wordtable.put("bugger", "Flegel");

```


-   Add a method based plan that reacts to the belief and checks whether an added word is allowed. If a colloquial word is added print a warning to the console. Please note that the added wordpair is automatically passed to the plan method whenever the wordtable changes.


```java

@Plan(trigger=@Trigger(factaddeds="wordtable"))
public void checkWordPairPlan(ChangeEvent event)
{
    ChangeInfo<String> change = ((ChangeInfo<String>)event.getValue());
    if(change.getInfo().equals("bugger"))
        System.out.println("Warning, a colloquial word pair has been added: "+change.getInfo()+" "+change.getValue());
}

```


### Starting and testing the agent
Create a translation agent via the Jadex Control Center and observe the output. You should see it printing the warning.

Exercise C2 - Dynamic Beliefs
------------------------------------------

Besides normal beliefs it is sometimes helpful to have a belief that directly depends on other beliefs and is automatically reevaluated whenever one of the beliefs changes it relies on. For such dynamic beliefs it is required that they are fields with an init expression directly in its declaration, i.e. e.g. *private String name = othername+id*, assuming that othername and id are other beliefs.

-   Create a TranslationBDI class by copying it from the last exercise. 
-   Change the belief definition in two ways. First already create the wordtable as part of the declaration and second add a new belief named alarm of type boolean. The alarm expression should check if the wordtable contains the key 'bugger'. 


```java

@Belief
protected Map<String, String> wordtable = new HashMap<String, String>();

@Belief(dynamic=true)
protected boolean alarm = wordtable.containsKey("bugger");

```


-   Change the plan to now react on changes of the alarm belief and in case of an alarm just add a print statement in the plan body. The ChangeEvent object can be injected into all plan methods. 
Unlike using the changed fact itself like it was done with the wordpair in Exercise C1, using the change event has the advantage that you can also inspect the old value. For this purpose, the change event contains a ChangeInfo object.


```java

@Plan(trigger=@Trigger(factchangeds="alarm"))
public void checkWordPairPlan(ChangeEvent event)
{
  ChangeInfo<Boolean> change = (ChangeInfo<Boolean>)event.getValue();
  // Print warning when value changes from false to true.
  if(Boolean.FALSE.equals(change.getOldValue()) && Boolean.TRUE.equals(change.getValue()))
  {
    System.out.println("Warning, a colloquial word pair has been added.");
  }
}

```


### Starting and testing the agent
Start the agent and verify that it behaves the same way as in the last exercise.

Exercise C3 - Getter/Setter Belief
-----------------------------------------------

In this and the following exercises in this chapter we will use a different example as it better fits to show further belief features. The example is a very simple clock which is able to print the current time to the standard out. 

-   Create a file called ClockBDI and add the @Agent annotation to the class.
-   Add two fields. One called time of type long and another called formatter of type SimpleDateFormat. The formatter can be initialized with new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"). It will be used to print the current time and date.
-   Add a getter and a setter method for the time belief:


```java

@Belief
public long getTime()
{
  return time;
}

@Belief
public void setTime(long time)
{
  this.time = time;
}

```

-   Add a method based plan that reacts on fact changes of the time belief. The plan body should just print out the time belief.

```java

@Plan(trigger=@Trigger(factchangeds="time"))
protected void printTime()
{
  System.out.println(formatter.format(getTime()));
}

```


-   Add an agent body in which you call setTime() with the current time. The current time can be obtained using System.currentTimeMillis().

### Starting and testing the agent
Start the agent and check that it prints out the current time.

Exercise C4 - Getter/Setter Belief without Field
-------------------------------------------------------------

This lecture will show that you can use also a getter/setter belief without an underlying field representation.  

-   Copy the clock agent from the last lecture and remove the time field.
-   Modify the getter method to just return the current time via System.currentTimeMillis().
-   Modify the setter method to just do nothing. Optionally, you can also delete the parameter of the method.
-   In the body of the agent just call setTime().
-   The plan remains completely the same as in the last exercise.

### Starting and testing the agent
Start the agent and check that it prints out the current time. Think about why it works? You just call an empty method (setTime()), don't you?

Exercise C5 - Belief with Update Rate
--------------------------------------------------

In order to print out the current time regularily and not just once we will use a belief with update rate. This means that the value of the belief is automatically reevaluated in certain time intervals. 

-   Copy the agent file from the last exercise and keep the plan as well as the formatter. Everything else can be deleted (also the body method of the agent).
-   Add a belief named time of type long and assign it System.currentTimeMillis(). Furthermore set the update rate in the belief annotation to 1000, i.e. one second.

```java

@Belief(updaterate=1000)
protected long time = System.currentTimeMillis();

```

### Starting and testing the agent
Start the agent and check that it prints out the current time every second.
