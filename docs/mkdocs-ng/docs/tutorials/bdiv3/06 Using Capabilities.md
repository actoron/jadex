# Using Capabilities

Reusability is a key aspect of software engineering as it allows for applying a once developed solution at several places. 
Regarding BDI agents, reusability can be achieved by two different approaches.
 
First, in BDIV3 it is possible to exploit the existing **Java inheritance mechanism** and design a base agent class that contains common functionality and is extended by different application agent classes.  
Of course, this mechanism suffers from the fact that in Java no multi-inheritance is possible.
 
Hence, in case you want to reuse different functionalities, these can be encapsulated in so called **BDI capabilities**.
A BDI capability represents a module that may contain beliefs, goals and plans like a normal agent.  
Capabilities realize a hierarchical (de)composition concept meaning that it is possible to include any number of subcapabilities that may again represent composite entities. 

In Jadex BDIV3 a capability is typically represented as a **class**:  
An instance of a capability class is declared and instantiated as normal field in the agent with corresponding meta information in terms of an annotation. 

# E1 - Creating a Capability

In this first exercise we just create a capability that encapsulates the translation agent behaviour. The agent itself is reduced to use the capability and dispatch a translation goal from the capability.

-   Create a new class file called *TranslationCapability* and add the ```@Capability``` annotation above the class definition.

```java
@Capability
public class TranslationCapability
{
  ...
}
```

-   Add a belief named *wordtable* of type Map&lt;String, String&gt;  and initialize it.
-   Add a constructor to *TranslationCapability*, which adds some word pairs to the word table.

For the actual goal we will use an inner class called *Translate*:

-   Create a goal as inner class named *Translate*. Add two fields of type String named *eword* and *gword* to the inner class and annotate them with ```@GoalParameter``` and ```@GoalResult``` respectively.
-   Add a constructor which takes the *eword* as parameter and assigns it to the goal parameter.
-   Add a method plan that reacts to the translate goal. It should take the *eword* as parameter and return the translated word using the normal lookup in the word table.
Your class should look like this now:

```java
@Belief
protected Map<String, String> wordtable = new HashMap<String, String>();

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

public TranslationCapability()
{
    wordtable.put("milk", "Milch");
    wordtable.put("cow", "Kuh");
    wordtable.put("cat", "Katze");
    wordtable.put("dog", "Hund");
}

@Plan(trigger=@Trigger(goals=Translate.class))
protected String translate(String eword)
{
    return wordtable.get(eword);
}
```

Last we will need to create the actual agent:

-   Create an empty agent file called *TranslationBDI* with the corresponding ```@Agent``` annotation.
-   Add a field of type ```IBDIAgentFeature``` called bdiFeature and add the ```@AgentFeature``` annotation.
-   Add a field called *capability* of type ```TranslationCapability``` , annotate it with ```@Capability``` and assign an instance of it.

```java
@AgentFeature
protected IBDIAgentFeature bdiFeature;

@Capability
protected TranslationCapability capability = new TranslationCapability();
```

-   Create an agent body which creates and dispatches a translation goal from the included capability. Wait for the goal to be finished and print out the result of the translation.

```java
@AgentBody
public void body()
{
    String eword = "dog";
    String gword = (String) bdiFeature.dispatchTopLevelGoal(capability.new Translate(eword)).get();
    System.out.printf("Translating %s to %s", eword, gword);
}
```

** Starting and testing the agent **

After starting the agent you should see again the print out of the translated word.

# E2 - Using an Abstract Belief

In this exercise we will show how an abstract belief can be used. The application idea here is that the agent itself manages the word table (instead of the capability) and provide another plan to search for synonyms in that table.

<x-hint title="This is just a simplified example">
In this simple case an alternative solution would have been making the word table accessible via public getter/setter methods. But the design here suggests that if more functionalities share a data structure that none of them really owns exclusively that it is better to move it to the agent level.
</x-hint>

-   Copy both files from the last lecture.
-   Delete the *wordtable* belief and the constructor from the capability definitions.
-   Add a native getter/setter pair, i.e. an abstract belief named *get-* and *setWordtable*:


```java
@Belief
public native Map<String, String> getWordtable();
	
@Belief
public native void setWordtable(Map<String, String> wordtable);
```

-   In the translate plan body replace the direct field access with a ```getWordtable()``` call.
-   In the agent file change the capability definition to include the necessary belief mapping.

```java
@Capability(beliefmapping=@Mapping(value="wordtable"))
protected TranslationCapability capa = new TranslationCapability();
```

-   Add/move the definition of the word table belief to the agent.
-   Add an agent init method and create word pairs as well as synonyms. For example the following:

```java
@AgentCreated
public void init()
{
  wordtable.put("coffee", "Kaffee");
  wordtable.put("milk", "Milch");
  wordtable.put("cow", "Kuh");
  wordtable.put("cat", "Katze");
  wordtable.put("dog", "Hund");
  wordtable.put("puppy", "Hund");
  wordtable.put("hound", "Hund");
  wordtable.put("jack", "Katze");
  wordtable.put("crummie", "Kuh");
}
```

-   Add a plan called *findSynonyms* which iterates through the word table and collects synonyms.

```java
@Plan
protected List<String> findSynonyms(ChangeEvent ev)
{
  String eword = (String)((Object[])ev.getValue())[0];
  List<String> ret = new ArrayList<String>();
  String gword = wordtable.get(eword);
  for(String key: wordtable.keySet())
  {
    if(wordtable.get(key).equals(gword))
    {
      ret.add(key);
    }
  }
  return ret;
}
```

-   In addition to adding the Translate goal, adopt the *findSynonyms* plan directly in the agent body:

```java
@AgentBody
public void body() {
    String eword = "dog";
    String gword = (String) bdiFeature.dispatchTopLevelGoal(capability.new Translate(eword)).get();
    System.out.printf("Translating %s to %s", eword, gword);
    
    List<String> syns = (List<String>)bdiFeature.adoptPlan("findSynonyms", new Object[]{eword}).get();
    System.out.println("Found synonyms: "+eword+" "+syns);
}
```

** Starting and testing the agent **

After starting the agent you should the print out of the translated word and a list of found synonyms.