Reuasability is a key aspect of software engineering as it allows for applying a once developed solution at several places. Regarding BDI agents reusability can be achieved by two different approaches. First, in BDI V3 it is possible to exploit the existing Java inheritance mechanism and design a base agent class that contains common functionality and is extended by different application agent classes. Of course, this mechanism suffers from the fact that in Java no multi-inheritance is possible. Hence, in case you want to reuse different functionalities these can be encapsulated in so called BDI capabilities. A BDI capability represents a module that may contains belief, goals and plans like a normal agent. Capabilities realize a hierarchical (de)composition concept meaning that it is possible to include any number of subcapabilities that may again represent composite entities. 

A module has to provide an explicit boundary which allows for connecting it with an agent or with another module. In contrast to BDI V2, in which it had to be explicitly declared which beliefs, goals and plans are exported and thus visible to the outside of a module, in BDI V3 these specifications have been pushed to the Java level. This means that the visibility modifiers you use in Java determines also if beliefs, goals and plans are visible. There is basically one additional feature that goes beyond these rules. In order to allow the specification of abstract beliefs, which should be available in the module but are made concrete and are assigned at the level of the outer, i.e. including module, unimplemented beliefs can be specified. Such unimplemented beliefs are represented as native getter/setter pairs without method body. In the outer capability an explicit belief mapping has to be stated which describes the connection of a local and the abstract belief of the submodule. 

In Jadex V3 a capability is typically represented as a:

-   **Class**: As a capability should enable reuse it is the normal case to use a separate class file for the module. The module is declared and instantiated as normal field in the agent with corresponding meta information in terms of an annotation. 

<span>E1 - Creating a Capability</span> 
---------------------------------------

In this first exercise we just create a capability that encapsultaes the translation agent behaviour. The agent itself in reduced to use the capability and dispatch a translation goal from the capability.

-   Create a new class file called TranslationCapability and add the @Capability annotation above the class definition.

<!-- -->

-   Add a belief named wordtable of type Map&lt;String, String&gt;  and create an instance of it.

<!-- -->

-   Create a goal as inner class named Translate and add a goal parameter named eword of type String and a goal result named gword of type String. Also add a constructor which takes the eword as parameter and assigns it to the goal parameter.

<!-- -->

-   Add a method plan that reacts to the translate goal. It should take the eword as parameter and return the translated word using the normal lookup in the word table.

<!-- -->

-   Finally, add an empty constructor which adds some word pairs to the word table.


```java

@Capability
public class TranslationCapability
{
  ...
}

```


-   Create a empty agent file called TranslationBDI with the corresponding annotation.

<!-- -->

-   Add a field of type BDIAgent called agent and add the @Agent annotation.

<!-- -->

-   Add a field called capability of type TranslationCapability and assign an instance of it.


```java

@Capability
protected TranslationCapability capa = new TranslationCapability();

```


-   Create an agent body which creates and dispatches a translation goal from the included capability. Wait for the goal to be finished and print out the result of the translation.

**Start and test the agent**

After starting the agent you should see again the print out of the translated word.

<span>E2 - Using an Abstract Belief</span> 
------------------------------------------

In this exercise we will show how an abstract belief can be used. The application idea here is that the agent itself manages the word table (instead of the capability) and provide another plan to search for synonyms in that table. (In this simple case an alternative solution would have been making the word table accessible via public getter/setter methods. But the design here suggests that if more functionalities share a data structure that none of them really owns exclusively that it is better to move it to the agent level).

-   Copy both files from the last lecture and perform the following modifications.

<!-- -->

-   Delete the wordtable belief and the constructor from the capability definitions.

<!-- -->

-   Add a native getter/setter pair, i.e. an abstract belief named get- and setWordtable.


```java

@Belief
public native Map<String, String> getWordtable();
	
@Belief
public native void setWordtable(Map<String, String> wordtable);

```


-   In the translate plan body replace the direct field access with a getWordtable call.

<!-- -->

-   In the agent file change the capability definition to include the necessary belief mapping.


```java

@Capability(beliefmapping=@Mapping(value="wordtable"))
protected TranslationCapability capa = new TranslationCapability();

```


-   Add/move the definition of the word table belief to the agent.

<!-- -->

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


-   Add a plan called findSynonyms which iterates through the word table and collects synonyms.


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

