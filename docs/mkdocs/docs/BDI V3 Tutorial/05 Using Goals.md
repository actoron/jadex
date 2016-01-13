Goal-oriented programming is one of the key concepts in the agent-oriented paradigm. It denotes the fact that an agent commits itself to a certain objective and maybe tries all the possibilities to achieve its goal. A good example for a goal that ultimately has to be achieved is the safe landing of an aircraft. The agent will try all its plans until this goal has succeeded, otherwise it will not have the opportunity to reach any other goal when the aircraft crashes. When talking about goals one can consider different kinds of goals. What we discussed above is called an achieve goal, because the agent wants to achieve a certain state of affairs. Similar to an achieve goal is the query goal which aims at information retrieval. To find the requested information plans are only executed when necessary. E.g. a cleaner agent could use a query goal to find out where the nearest wastebin is. Another kind is represented through a maintain goal, that has to keep the properties (its maintain condition) satisfied all the time. When the condition is not satisfied any longer, plans are invoked to re-establish a normal state. An example for a maintain goal is to keep the temperature of a nuclear reactor below some specified limit. When this limit is exceeded, the agent has to act and normalize the state. The fourth kind of goal is the perform goal, which is directly related to some kind of action one wants the agent to perform. An example for a perform goal is an agent that as to patrol at some kind of frontier.

The goal representation has changed a bit in V3 compared to the former versions of Jadex. In Jadex V3 a goal is a Java pojo, i.e. a user defined object. Furthermore, the different kinds of goals need not to be specified explicitly. Instead, it is sufficient to just use the type of conditions that are required to produce the disired behaviour. 

Concretely, a goal type can come in the following flavors in V3:

-   **Inner Class**: If a goal is private to an agent it is often elegant and helpful to use an inner class to represent the goal type. The inner class has natural access to the fields and beliefs of the agent which makes programming less complex. In some cases it is also required to use a static inner class. In this case the aforementioned advantage is not existent, but you could pass the agent as explicit argument in the constructor to gain access to the agent aspects.
-   **Class**: A goal can also be represented as a normal Java class. In this case there is no direct connection to the agent available and one again has to pass whatever is need via the constructor call or other methods.  

<span>D1 - Using a Top-Level Goal</span> 
----------------------------------------

The first thing we will try out in this exercise is dispatching a top-level goal. The difference between a top-level and a subgoal can be understood as its part in the BDI goal-plan hierarchy. For each goal different plans can be tried out, which in turn may produce subgoals to fulfill parts of their work. These subgoals again may have other subgoals leading to the already mentioned goal-plan tree. In this sense a top-level goal is just a goal that has no parent, i.e. which is on the top level of the hierarchy.   

-   We create a new TranslationBDI agent Java file and add the @Agent annotation to the class itself. Furthermore we need two fields, one for the agent API called agent and another one for the wordtable of type Map&lt;String, String&gt;. As you will remember, we need to add the @Agent annotation to the agent field and we make the wordtable a belief of the agent by adding @Belief.

<!-- -->

-   Create and setup the wordtable in the init method of the agent. The method needs to use the @AgentCreated annotation to be recognized as init method of the agent. Furthermore, add some word pairs to the word table in the method, too.

<!-- -->

-   Now, we create a new goal type as inner class of the agent. To make the class a goal the @Goal annotation has to be added to the class definition. We name the class Translate and give it two String fields, one called eword and one called gword for the English and German word respectively. The English word will be passed as parameter to the goal and the German word will be delievered as result. Hence, we also add a constructor that takes the English word as parameter and saves it in the corresponding field. Finally, we add getter and setter methods for both fields.


```java

@Goal
public class Translate
{
  protected String eword;
  
  protected String gword;
		
  public Translate(String eword)
  {
    this.eword = eword;
  }

  public String getEWord()
  {
    return eword;
  }

  public String getGWord()
  {
    return gword;
  }

  public void setGWord(String gword)
  {
    this.gword = gword;
  }
}

```


-   Besides the goal we also need a plan to handle the goal. For this purpose we add a new method plan called translate. In the @Plan annotation as trigger the goal has to be specified. Furthermore, we define a parameter named goal of type Transloate for the method. This will allow us to fetch the goal the plan is executed for and extract the word it should translate. The plan body should just fetch the word via goal.getEWord() and feed it to the wordtable to look up the translation. Afterwards, the gword should be set as result in the goal via goal.setGWord(gword).


```java

@Plan(trigger=@Trigger(goals=Translate.class))
protected void translate(Translate goal)
{
  String eword = goal.getEWord();
  String gword = wordtable.get(eword);
  goal.setGWord(gword);
}

```


-   Finally, the agent body method has to be created. In this method we actually create an instance of our new goal type and dispatch it as top-level goal. We then wait for the result and print it out.


```java

@AgentBody
public void body()
{
  String eword = "cat";
  Translate goal = (Translate)agent.dispatchTopLevelGoal(new Translate(eword)).get();
  System.out.println("Translated: "+eword+" "+goal.getGWord());
} 

```


**Start and test the agent**

After starting the agent it should print out the word for which we have created and dispatched a goal.

<span>D2 - Using Parameters and Results</span> 
----------------------------------------------

\
The approach used in the last exercise is perfectly feasible, but has a few minor drawbacks. One point is that it is not very comfortable to extract the parameters of a goal manually. The same drawback exists for the goal result in the above solution. Using goal parameters and results this can be overcome in a very simple way.

-   As starting point we use the agent file of the last exercise, copy it and modify some minor aspects.

<!-- -->

-   First, remove the getter and setter methods in the goal. Instead a @GoalParameter annotation to the eword field and a @GoalResult parameter to the gword field.


```java

@Goal
public class Translate
{
  @GoalParameter
  protected String eword;
		
  @GoalResult
  protected String gword;
		
  public Translate(String eword)
  {
    this.eword = eword;
  }
}

```


-   In the agent body method we can change the invocation of the goal dispatch to directly retrieve the result of the goal. The framework automatically scans the goal for a result annotation and delivers the value of that field (or getter method) to the caller.


```java

@AgentBody
public void body()
{
  String eword = "cat";
  String gword = (String)agent.dispatchTopLevelGoal(new Translate(eword)).get();
  System.out.println("Translated: "+eword+" "+gword);
}

```


-   In the plan we can now also simplify the parameter handling by just declaring a method parameter for our goal parameter. The engine will automatically match goal parameters with plan parameter declarations and deliver them as needed.


```java

 
@Plan(trigger=@Trigger(goals=Translate.class))
protected String translate(String eword)
{
  return wordtable.get(eword);
}

```


**Start and test the agent**

After starting the agent it should behave in the same way as in the last exercise.

<span>D3 - Goal Retry</span> 
----------------------------

 

A goal if different from a plan because it describes an objective without exactly stating how it should be achieved. This means that different plans can be tried out to finally reach a given objective. Besides the specification of plans that can in principle help achieving a goal, the means-end reasoning process can be adjusted in many ways use the various BDI flags. One of the fundamental flags is the retry setting which defines if another plan can be tried out when the first one fails or does not achieve the goal completely. In this exercise we will try out the retry behaviour by using two plans from which the first will fail. 

-   Create a TranslationBDI file by copying from the last exercise.

<!-- -->

-   Modify the existing plan to throw a PlanFailureException after having printed out a text message, e.g. "Plan A".

<!-- -->

-   Add a second method plan named translateB that should look the same like first, i.e. the same plan annotation, same parameter. In the plan print "Plan B" and return the tranlated word from the map.


```java

@Plan(trigger=@Trigger(goals=Translate.class))
protected String translateA(String eword)
{
  System.out.println("Plan A");
  throw new PlanFailureException();
}
	
@Plan(trigger=@Trigger(goals=Translate.class))
protected String translateB(String eword)
{
  System.out.println("Plan B");
  return wordtable.get(eword);
}

```


**Start and test the agent**

After starting the agent you should see that first plan A and afterwards plan B is executed. Think about what happens if the second plan would also throw an exception? Also ask yourself what the agent would do when the plans would be declared in different order or if the second would have a higher priority? Verify your thoughs by trying these things out in the source code.

<span>D4 - Goal Creation Condition</span> 
-----------------------------------------

Until now we have created goals manually, but ofter one also wants to create a goal in response to a belief change. This we will try out in the following. Concretely, we want our translation agent to create a new translation goal whenever our belief with the English word changes.

-   Again we start by copying the code from lecture D2.

<!-- -->

-   We add a belief for the current English word of type String named eword.


```java

@Belief
protected String eword;

```


-   In the goal class we add a creation condition. Here, we just add the corresponding annotation @GoalCreationCondition to the constructor of the goal. Additionally, we have to state which change provoke the creation of a new goal. In this case we just say that always when our eword belief is modified we want to obtain a new goal.


```java

@GoalCreationCondition(beliefs="eword")
public Translate(String eword)
{
  this.eword = eword;
}

```


-   In the agent body method we just assign different values to the eword belief.


```java

@AgentBody
public void body()
{
  eword = "cat";
  eword = "milk";
}

```


**Start and test the agent**

After starting the agent you should see that for each belief assignment a new print out is produced. In this exercise we have used the constructor as creation condition because we wanted to create a goal on every change of that belief. But where to place condition code if we want to perform some checks and create a goal only in certain circumstances? 

<span>D5 - Goal Recur</span> 
----------------------------

 

The process of means-end reasoning is started when a goal becomes active in the agent. It continues to execute plans until the goal is achieved or no more plans can be executed. If this is the case and no more plans exist although the goal has not been fulfilled yet the recur setting determines how is proceeded. Per default, goals are considered as short-term objectives and recur is turned off. The corresponding behaviour is that the goal fails with an exception and reasoning is finished. If this should not happen and the goal should persist in the agent even if it cannot be achieved immediately the goal can be made a long-term goal by turning on the recur flag. As result the goal will be paused until the recur condition indicates that means-end reasoning should be executed again. This also means that the set of already tried plans is cleared and means-end reasoning will be performed in the same way as in the first round.

In this exercise we will change the translation agent to keep a translation goal even when a word is not contained in the dictionary. it will then be paused until the corresponding word pair has been added. As recur condition we will use any detected changes to the agent's dictionary.

-   Create a new TranslationBDI file as copy of the solution of D2.

<!-- -->

-   Turn on the goal recur mode by setting recur=true in the @Goal annotation.


```java

@Goal(recur=true)
public class Translate

```


-   Add a recur condition to the goal that reacts on changes to the dictionary map. To achieve this create a new method named checkRecur with boolean return value. Add the @GoalRecurCondition to the method and set beliefs to wordtable.


```java

@GoalRecurCondition(beliefs="wordtable")
public boolean checkRecur()
{
  return true;
}

```


-   Change the plan body to throw a PlanFailureException when a word is not contained in the dictionary.


```java

@Plan(trigger=@Trigger(goals=Translate.class))
protected String translate(String eword)
{
  String ret = wordtable.get(eword);
  if(ret==null)
    throw new PlanFailureException();
  return ret;
}

```


-   In the agent body we create the translation goal and additionally schedule a timed action that will automatically add the word that we will look up. For this purpose we use the method scheduleStep which is available via the agent API. As parameters we first add a new component step which adds the new word pair to the dictionary using wordtable.put("bugger", "Flegel"). The second parameter is the delay for the step, here we use 3000 to state that we want it to be executed in 3 seconds. After having scheduled the step we create and dispatch a translation goal with "bugger" as translation request.


```java

@AgentBody
public void body()
{
  agent.scheduleStep(new IComponentStep<Void>()
  {
    public IFuture<Void> execute(IInternalAccess ia)
    {
      wordtable.put("bugger", "Flegel");
      return IFuture.DONE;
    }
  }, 3000);
		
  String eword = "bugger";
  String gword = (String)agent.dispatchTopLevelGoal(new Translate(eword)).get();
  System.out.println("Translated: "+eword+" "+gword);
}

```


**Start and test the agent**

Start the agent and check whether the goal is reactivated by the recur condition triggers.

<span>D6 - Maintain Goals</span> 
--------------------------------

As we have illustrated in the introduction of this lecture, in Jadex different goal types can be used. In the new BDI V3 we have changed the way these goal types can be used. Instead of declaring a goal type explicitly (as in BDI V1 & V2) in V3 it is sufficient to use the corresponding condition types, i.e. a maintain condition in case of a maintain goal. In the translation example we will use a maintain goal to restrict the number of word pairs in the dictionary.

-   We start by creating a TranslationBDI Java file by copying it from the last exercise.

<!-- -->

-   We delete the translation goal and instead create a new MaintainStorageGoal as inner class. In the goal annotation we set the exclude mode to never (excludemode=ExcludeMode.Never). This allows a plan to be executed again and again without being excluded. 

<!-- -->

-   Furthermore, we add two methods (without paramters and boolean return value), one called maintain and the other one target. To the first we add the @GoalMaintainCondition(beliefs="wordtable") and to the second @GoalTargetCondition(beliefs="wordtable") annotation. This leads to a reevaluation of the conditions whenever the dictionary wordtable changes. We want the maintain condition to trigger when the number of entries in the dictionary exceeds. As consequence a plan for removing entries is triggered and it will remove entries until less than 3 word pairs are contained.


```java

@Goal(excludemode=ExcludeMode.Never)
public class MaintainStorageGoal
{
  @GoalMaintainCondition(beliefs="wordtable")
  protected boolean maintain()
  {
    return wordtable.size()<=4;
  }
		
  @GoalTargetCondition(beliefs="wordtable")
  protected boolean target()
  {
    return wordtable.size()<3;
  }
}

```


-   As next step we add a method plan called removeEntry without parameters and return value. In the plan annotation add the maintain storage goal as trigger. In the method just fetch an arbitrary entry from the dictionary wordtable and remove it and print out which entry has been removed.

<!-- -->

-   Finally we add an agent body method called body using the @AgentBody annotation. In the method we first create and dispatch a maintain storage goal as top-level goal of the agent. Next, we create the wordtable hash table and add four different word pairs. To activate the maintain goal we add a new word pair every two seconds. For this purpose we create a component step that declares an integer field cnt (as counter). In the execute method of the step we add a new word pair using the cnt to make it unique each time. The cnt should be increased after it has been used for one word pair. Afterwards, we print the contents of the dictionary and use the wait method of the agent to reschedule the step after it has been executed. In order to activate the step in the agent we also use the waitFor method at the end of the agent body method.


```java

@AgentBody
public void body()
{
  agent.dispatchTopLevelGoal(new MaintainStorageGoal());

  wordtable = new HashMap<String, String>();
  wordtable.put("milk", "Milch");
  wordtable.put("cow", "Kuh");
  wordtable.put("cat", "Katze");
  wordtable.put("dog", "Hund");

  IComponentStep<Void> step = new IComponentStep<Void>()
  {
    int cnt = 0;
    public IFuture<Void> execute(IInternalAccess ia)
    {
      wordtable.put("eword_#"+cnt, "gword_#"+cnt);
      cnt++;
      System.out.println("wordtable: "+wordtable);
      agent.waitFor(2000, this);
      return IFuture.DONE;
    }
  };
		
  agent.waitFor(2000, step);
}

```


**Start and test the agent**

Start the agent and observe the print outs on the console. You should see that constantly entries are added. When five entries are in the dictionary the maintain goal is activated and entries get removed until only two remain. The process starts in the same way again.
