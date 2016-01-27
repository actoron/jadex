# Using Goals

Goal-oriented programming is one of the key concepts in the agent-oriented paradigm. It denotes the fact that an agent commits itself to a certain objective and maybe tries all the possibilities to achieve its goal. 
A good example for a goal that ultimately has to be achieved is the safe landing of an aircraft. The agent will try all its plans until this goal has succeeded, otherwise it will not have the opportunity to reach any other goal when the aircraft crashes.  
 When talking about goals one can consider different kinds of goals.  
 What we discussed above is called an **achieve goal**, because the agent wants to achieve a certain state of affairs. Similar to an achieve goal is the **query goal** which aims at information retrieval. To find the requested information plans are only executed when necessary. E.g. a cleaner agent could use a query goal to find out where the nearest wastebin is.  
  Another kind is represented through a **maintain goal**, that has to keep the properties (its maintain condition) satisfied all the time. When the condition is not satisfied any longer, plans are invoked to re-establish a normal state.
  An example for a maintain goal is to keep the temperature of a nuclear reactor below some specified limit. When this limit is exceeded, the agent has to act and normalize the state.  
  The fourth kind of goal is the **perform goal**, which is directly related to some kind of action one wants the agent to perform.
  An example for a perform goal is an agent that as to patrol at some kind of frontier.

## Exercise E1 - Subgoals
In this exercise we will use a subgoal for translating words. Extend the translation agent C2 to have a second translation plan for translations from English to French. Introduce a ProcessTranslationRequestPlanE1 that receives all incoming translation requests and uses a subtask triggered by an achieve goal to perform the translation.

### Removing, creating and modifying plans

-   Remove the EnglishGermanAddWordPlan to keep the agent simple.
-   Create a new initial ProcessTranslationRequestPlanE1 that reacts on all incoming messages with performative type request and creates subgoals for all (correctly formatted) requests. Because we are using a service plan implement the body method with an infinite loop and start waiting for a message to process. Assuming that the plan has extracted the action (translate), the language direction (english_german or english_french) and the word(s) from an incoming message the following code can be used to create, dispatch and wait for a subgoal:

```java
IGoal goal = createGoal("translate");
goal.getParameter("direction").setValue(dir);
goal.getParameter("word").setValue(word);
try
{
  dispatchSubgoalAndWait(goal);
  getLogger().info("Translated from "+goal+" "+
  word+" - "+goal.getParameter("result").getValue());
}
catch(GoalFailureException e)
{
  getLogger().info("Word is not in database: "+word);
};
```

-   After the goal returns successfully, read the result from the goal and log some translation message.
-   Modify the EnglishGermanTranslationPlanE1 so that it can handle a translation goal with direction="english_german". Therefore, the body method has to be adapted so that it extracts the word from the plan parameter mapping (using `getParameter("word").getValue()`). After having performed the query on the wordtable, set the result using `getParameter("result").setValue(gword)`. When no translation could be retrieved, the plan has failed and this should be indicated calling the `fail()` method (which throws a plan failure exception).
-   Create a new EnglishFrenchTranslationPlanE1 as a copy of the EnglishGermanTranslationPlanE1 and make sure to work on a new wordtable belief efwords. Modify the query_word and the body method accordingly.

### Modifying the ADF

-   Add the ProcessTranslationRequestPlanE1 to the ADF as initial plan with a waitqueue for translation requests. Add an configurations section and declare a configuration with an initial plan for the ProcessTranslationRequestPlanE1.
-   Adapt the plan head declarations of both plans to include plan parameters and the new triggers. The plan parameters are directly mapped to the corresponding goal parameters so that the input as well as the result are automatically transferred from resp. to the goal. In addition, both translation plans should handle exactly suitable translation goals. In the following the modified plan head for the EnglishGermanTranslationPlanE1 is depicted:

```xml
<plan name="egtrans">
    <parameter name="word" class="String">
        <goalmapping ref="translate.word"/>
    </parameter>
    <parameter name="result" class="String" direction="out">
        <goalmapping ref="translate.result"/>
    </parameter>
    <body class="EnglishGermanTranslationPlanE1"/>
    <trigger>
		<goal ref="translate">
            <match>"english_german".equals($goal.getParameter("direction").getValue())</match>
        </goal>
    </trigger>
</plan>
```
     

-   Introduce a new goals section and declare the achieve goal for translations:

```xml
<goals>
	<achievegoal name="translate">
        <parameter name="direction" class="String"/>
        <parameter name="word" class="String"/>
        <parameter name="result" class="String" direction="out"/>
    </achievegoal>
</goals>
```

-   Modify the ADF by adjusting the plan declarations to include the new EnglishFrenchTranslationPlanE1 and exclude the add word plan. Additionally a new belief efwords has to be declared in the beliefs section:

```xml
<beliefset name="efwords" class="Tuple">
    <fact>new Tuple("milk", "lait")</fact>
    <fact>new Tuple("cow", "vache")</fact>
    <fact>new Tuple("cat", "chat")</fact>
    <fact>new Tuple("dog", "chien")</fact>
</beliefset>
```

-   Introduce a second query for the new belief efword in the expressions section of the ADF:

```xml
<expression name="query_efword">
  select one $wordpair.get(1)
  from Tuple $wordpair in $beliefbase.efwords
  where $wordpair.get(0).equals($eword)
```

### *Starting and testing the agent*

Start the agent and supply it with some translation requests. Observe which plans are activated in what sequence and how the goal processing is done. Change the translation direction in the requests and check if the right plan is invoked.

## Exercise E2 - Retrying a Goal

Using the BDI-retry mechanism for trying out different plans for one goal. This can be useful, for example if there are  several plans for one specific goal, but all plans work under different circumstances. With the retry mechanism, all plans will be tried until one plan lets the goal succeed or any plan has been tried.

### Modifying the plans** 

-   Modify the trigger of both translating plans so, that they react on every translation goal by removing the lines:

```xml
<match>"english_german/english_french".equals($goal.getParameter("direction").getValue())</match>
```
     
-   Having done this causes the translation plans to react on every translation goal, even when they can't handle the translation direction or language.  
-   Introduce a new plan parameter for the translation direction and supply it with a corresponding goal mapping:

```xml
<parameter name="direction" class="String">
    <goalmapping ref="translate.direction"/>
</parameter>
```
     
-   Modify the translation plans so that they check the translation direction in the body method before translating. When the direction cannot be handled, they should indicate that they failed to achieve the goal by calling the ~fail()~ method. Addtionally the plans should log or print some warning message, when they fail to process a goal:
       
```java
    if("english_french".equals(getParameter("direction").getValue()))
        //print out some message and fail() if this is not the english-french plan
```
-   You need not explicitly set the BDI-retry flag of the goal, because in the standard configuration for goals all BDI-mechanisms (retry, exclude and meta-level reasoning) are enabled. This means, that a failed goal will be retried by different plan candidates until it succeeds or all possible candidates have failed to handle the goal and it is finally failed.

### Starting and testing the agent

Provide the agent with some translation work and watch out how the goal processing is done this time. Observe by changing the translation direction of the request how different plans are scheduled to handle a goal.
  
## Exercise E3 - Maintain Goals

Using a maintain goal to keep the number of wordtable entries below a specified maximum value. For this exercise we will use the TranslationAgentD2 as starting point.

**Create a new file RemoveWordPlanE3.java**

-   This plan has the purpose to delete an entry from the wordtable.
-   In the body method use the following code to delete one entry from the set:

```java
Object[] facts = getBeliefbase().getBeliefSet("egwords").getFacts();
getBeliefbase().getBeliefSet("egwords").removeFact(facts[0]);
```
     
**Create the TranslationE3.agent.xml as copy from the TranslationD2.agent.xml**

-   Remove the FindSynonymsPlanD2 from the plans section and remove the event section completely.
-   Add the RemoveWordPlanE3 to the plans section:

```xml
<plan name="remegword">
  <body class="RemoveWordPlanE3"/>
  <trigger>
    <goal ref="keepstorage"/>
  </trigger>
  <precondition>$beliefbase.egwords.length > 0</precondition>
</plan>
```

-   Add the following beliefs to the belief section:

```xml
<beliefsetref name="egwords">
    <concrete ref="transcap.egwords" />
</beliefsetref>
<belief name="maxstorage" class="int">
    <fact>8</fact>
</belief>
```    

-   Add a new maitain goal declaration to the goals section: 

```xml
<maintaingoal name="keepstorage" exclude="when_failed">
    <maintaincondition>
        $beliefbase.egwords.length <= $beliefbase.maxstorage
    </maintaincondition>
</maintaingoal>
```
     

-   Create a configurations section with one configuration that creates a maintain goal instance on startup.

```xml
<configurations>
    <configuration name="default">
        <goals>
            <initialgoal ref="keepstorage"/>
        </goals>
    </configuration>
</configurations>
```

### Starting and testing the agent

Start the translation agent and add new wordpairs to the dictionary. The maintain condition is violated and the goal should be activated. This leads to a subsequent removal of entries in the belief set, until the condition holds again. 
