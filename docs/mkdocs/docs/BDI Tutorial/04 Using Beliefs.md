1 Chapter 4. Using Beliefs

An agent's beliefbase represents its knowledge about the world. The beliefbase is in some way similar to a simple data-storage, that allows the clean communication between different plans by the means of shared beliefs. Contrary to most PRS-style BDI systems, Jadex allows to store arbitrary Java objects as beliefs in its beliefbase. In Jadex between two kinds of beliefs is distinguished. On the one hand there are beliefs that allow the user to store exactly one fact and on the other hand belief sets are supported that allow to store a set of facts. The use of beliefs and belief sets as primary storage capacities for plans is strongly encouraged, because from its usage the user benefits in several ways. If it is necessary to retrieve a cut out of the stored data this is supported by a declarative OQL-like query language. Furthermore, it is possible to monitor single beliefs with respect to their state and cause an event when a corresponding condition is satisfied. This allows to trigger some action when e.g. a fact of a belief set is added or a belief is modified. It is also possible to wait for some complex expression that relates to several beliefs to become fulfilled.

1.1 Exercise C1 - Beliefs

From this point the copying and renaming of files is not explicitly stated anymore. Furthermore, from now on we use a syntax in the request format that looks like this:





\*\\&lt;action\\&gt; \\&lt;language(s)\\&gt; \\&lt;content\\&gt;\*





To translate a word we have to send a request in the form:





\*translate english\_german \\&lt;word\\&gt;\*





To add a new word pair to the database we have to send a request in the format:





\*add english\_german \\&lt;eword\\&gt; \\&lt;gword\\&gt;\*





In this first exercise we will use the beliefbase for letting more than one plan having access to the word table by using a belief for storing the word table. 





\*Modify the existing plan to support the request format and introduce a new plan for adding word pairs\*

-   Create a new EnglishGermanAddWordPlanC1 as passive plan, that handles add-new-wordpair requests. In its body method, the plan should check whether the format is correct (using a \~java.util.StringTokenizer\~). If it is ok, it should retrieve the hashtable containing the word pairs via: \~Map words = (Map)getBeliefbase().getBelief("egwords").getFact();\~ Assuming that the belief for storing the wordpairs is named "egwords". Now the plan has to check if the English word is already contained in the map (using \~words.containsKey(eword)\~) and if it is not contained, it should be added (using \~words.put(eword, gword)\~).
-   Modify the EnglishGermanTranslationPlanC1 so, that it uses the word table stored as single belief in the beliefbase. Additionally the plan has to check the newly introduced request format by using a \~java.util.StringTokenizer\~.
-   Add a static getDictionary() method to the EnglishGermanTranslationPlanC1. This method should return a hashmap with some wordpairs contained in it. Besides the static method you also need to declare a static variable for storing the dictionary:









{code:java}\
protected static Map dictionary;\
public static Map getDictionary()\
{\
  if(dictionary==null)\
  {\
    dictionary = new HashMap();\
    dictionary.put("milk", "Milch");\
    dictionary.put("cow", "Kuh");\
    dictionary.put("cat", "Katze");\
    dictionary.put("dog", "Hund");\
  }\
  return dictionary;\
}\
{code}











\*Update the ADF to incorporate the new plan and the new belief\* 





The updated version of the translation agent ADF is outlined in the following code snippet. Note that the agent now has two plans named "addword" for adding a word pair to the database and "egtrans" for translating from English to German. The belief declaration is enclosed by a beliefs tag that denotes that an arbitrary number of belief declarations may follow. The ADF defines the beliefs and belief sets of an agent, optionally with default fact(s).  The belief for storing the wordtable is named "egwords" and typed through the class attribute to \~java.util.Map\~. The tag of this element is set to \\&lt;belief\\&gt; (in contrast to \\&lt;beliefset\\&gt;) denoting that only one fact can be stored.  Further it is necessary to clarify which kinds of events trigger the plans. Therefore, the events section is extended to include a new \~request\_addword\~ event type which also matches request messages. To be able to distinguish between both kinds of events they are refined to match only messages that start with a specific content string. The match expressions use a logical AND (\\&\\&), that has to be written a little bit awkwardly with the xml entities \\&amp;\\&amp;.











{code:xml}\
&lt;agent xmlns="http://jadex.sourceforge.net/jadex ](http://jadex.sourceforge.net/jadex) "\
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance ](http://www.w3.org/2001/XMLSchema-instance) "\
       xsi:schemaLocation="http://jadex.sourceforge.net/jadex ](http://jadex.sourceforge.net/jadex) \
                           http://jadex.sourceforge.net/jadex-bdi-2.3.xsd ](http://jadex.sourceforge.net/jadex-bdi-2.3.xsd) "\
  name="TranslationC1"\
  package="jadex.bdi.tutorial"&gt;

  &lt;imports&gt;\
    &lt;import&gt;java.util.logging.\*&lt;/import&gt;\
    &lt;import&gt;java.util.\*&lt;/import&gt;\
    &lt;import&gt;jadex.bridge.fipa.\*&lt;/import&gt;\
  &lt;/imports&gt;

  &lt;beliefs&gt;\
    &lt;belief name="egwords" class="Map"&gt;\
      &lt;fact&gt;EnglishGermanTranslationPlanC1.getDictionary()&lt;/fact&gt;\
    &lt;/belief&gt;\
  &lt;/beliefs&gt;

  &lt;plans&gt;\
    &lt;plan name="addword"&gt;\
      &lt;body class="EnglishGermanAddWordPlanC1"/&gt;\
      &lt;trigger&gt;\
        &lt;messageevent ref="request\_addword"/&gt;\
      &lt;/trigger&gt;\
    &lt;/plan&gt;\
  \
    &lt;plan name="egtrans"&gt;\
      &lt;body class="EnglishGermanTranslationPlanC1"/&gt;\
      &lt;trigger&gt;\
        &lt;messageevent ref="request\_translation"/&gt;\
      &lt;/trigger&gt;\
    &lt;/plan&gt;\
  &lt;/plans&gt;

  &lt;events&gt;\
    &lt;messageevent name="request\_addword" direction="receive" type="fipa"&gt;\
      &lt;parameter name="performative" class="String" direction="fixed"&gt;\
        &lt;value&gt;SFipa.REQUEST&lt;/value&gt;\
      &lt;/parameter&gt;\
      &lt;match&gt;\$content instanceof String &amp;amp;&amp;amp; ((String)\$content).startsWith("add english\_german")&lt;/match&gt;\
    &lt;/messageevent&gt;\
\
    &lt;messageevent name="request\_translation" direction="receive" type="fipa"&gt;\
      &lt;parameter name="performative" class="String" direction="fixed"&gt;\
        &lt;value&gt;SFipa.REQUEST&lt;/value&gt;\
      &lt;/parameter&gt;\
      &lt;match&gt;\$content instanceof String &amp;amp;&amp;amp; ((String)\$content).startsWith("translate english\_german")&lt;/match&gt;\
    &lt;/messageevent&gt;\
  &lt;/events&gt;\
&lt;/agent&gt;\
{code}











\*Start and test the agent\* 

Send several add-word and translation requests to the agent and observe, if it behaves well. In this example the belief is already created when the agent is initialized.\
1.1 Exercise C2 - Beliefsets

Using a belief set for storing the word-pairs and employing beliefbase queries to look-up a word in the word table belief set. In this example each word pair is saved in a data structure called \~jadex.commons.Tuple\~ which is a list of entities similar to an object array. In contrast to an object array two tuples are considered to be equal when they contain the same objects. \*Of course, in belief sets arbitrary Java objects can be stored, not just Tuples.\*





\*Modify the plans\*



-   Modify the EnglishGermanTranslationPlanC2 so, that it uses a query to search the requested word in the belief set. Therefore use an expression defined in the ADF: \~this.queryword = getExpression("query\_egword");\~ (Assuming that the \~jadex.bdi.runtime.IExpression\~ queryword is declared as instance variable in the plan). To apply the query insert the following code at the corresponding place inside the plan's body method: \~String gword = (String)queryword.execute("\$eword", eword);\~



-   Modify the EnglishGermanAddWordPlanC2 so, that it also uses the same query to find out, if a word pair is already contained in the belief set. Apply the query before inserting a new word pair. When the word pair is already contained log some warning message. To add a new fact to an existing belief set you can use the method: \~getBeliefbase().getBeliefSet("egwords").addFact(new jadex.commons.Tuple(eword, gword));\~

\*Modify the ADF\* 



-   For checking if a word pair is contained in the wordtable and for retrieving a wordpair from the wordtable a query expression will be used. Insert the following code into the ADF below the events section:

{code:xml}\
&lt;expression name="query\_egword"&gt;\
  select one \$wordpair.get(1)\
  from Tuple \$wordpair in \$beliefbase.getBeliefSet("egwords").getFacts()\
  where \$wordpair.get(0).equals(\$eword)\
&lt;/expression&gt;\
{code}

-   We don't cover the details of the query construction in this tutorial. If you are interested in understanding the details of the Jadex OQL query language, please consult the \[BDI User Guide&gt;BDI User Guide.01 Introduction\].



-   Modify the ADF by defining a belief set for the wordtable. Therefore change the tag type from "belief" to "belief set" and the class from "Map" to "Tuple". Note that Tuple is a helper class that is located in jadex.commons and has to be added to the imports section if you don't specify the fully-qualified classname. Remove the old Map fact declaration and put in four new facts each surrounded by the fact tag. Put in the same values as before \~new Tuple("milk", "Milch"))\~ etc. for each fact.

\*Start and test the agent\* 

Send several add-word and translation requests to the agent and observe, if it behaves well. Verify that it behaves exactly like the agent we built in exercise C1. This exercise does not functionally modify our agent.

1.1 Exercise C3 - Belief Conditions\
In this exercise we will use a condition for triggering a passive plan that congratulates every 10th user.



 \
\*Create and modify plans\*

-   Create a new passive ThankYouPlanC3 that prints out a congratulation message and the actual number of processed requests. The number of processed requests will be stored in a belief called "transcnt" in the ADF. Retrieve the actual request number by getting the fact from the beliefbase with: \~int cnt = ((Integer)getBeliefbase().getBelief("transcnt").getFact()).intValue();\~



-   Modify the EnglishGermanTranslationPlanC3 to count the translation requests: \~int cnt = ((Integer)getBeliefbase().getBelief("transcnt").getFact()).intValue(); getBeliefbase().getBelief("transcnt").setFact(new Integer(cnt+1));\~

\*Modify the ADF\*



-   Modify the ADF by defining the new ThankYouPlanC3 as passive plan (with the name thankyou in the ADF) in the plans section. Instead of defining a triggering event for this passive plan we define a condition that activates the new ThankYouPlanC3. A condition has the purpose to monitor some state of affair of the agent. In this case we want to monitor the belief "transcnt" and get notified whenever 10 translations have been requested. Insert the code from the following snippet in the plan's trigger. This condition consists of two parts: This first transcnt\\&gt;0 makes sure that at least one translation has been done and the second part checks if transcnt modulo 10 has no rest indicating that 10\*x translations have been requested. The two parts are connected via a logical AND (&amp;&amp;), that has to be written a little bit awkwardly with the xml entities \\&amp;\\&amp;.

{code:xml}\
&lt;condition&gt;\$beliefbase.transcnt&gt;0 &amp;amp;&amp;amp; \$beliefbase.transcnt%10==0&lt;/condition&gt;\
{code}

-   Define and initialize the new belief in the ADF by introducing the following lines in the beliefs section:\
    {code:xml}\
    &lt;belief name="transcnt" class="int"&gt;\
        &lt;fact&gt;0&lt;/fact&gt;\
    &lt;/belief&gt;\
    {code}

\*Start and test the agent\*

Send some translation requests and observe if every 10th time the congratulation plan is invoked and prints out its message.

1.1 Exercise C4 - Agent Arguments\
In this exercise we will use agent arguments for the custom initialization of an agent instance.

-   Use the translation agent C3 as starting point and specify an agent argument in the ADF. Arguments are beliefs for which a value can be supplied from outside during the agent start-up. For declaring a belief being an agent argument simply mark it as argument by using the corresponding belief attribute \~argument="true"\~. In this case we want the belief "transcnt" being the argument. Note that only beliefs not belief sets can be used as arguments.



-   Use the Starter to create instances of the new agent model. The Starter automatically displays textfields for all agent arguments and also shows the default model value (if any) that will be used when the user does not supply a value. Try entering different values into the textfield: What happens if you enter e.g. a string instead of the integer value that is needed here? 



-   Start the agent with different argument values. Verfify, that the agent immediately invokes the congratulation plan if the initial number of translation requests is e.g. 10.\
    \
    1.1 Exercise C5 - Observing Agent State  \
    In this exercise we will use the Jadex BDI introspector tool agent to view the beliefs of the agent.

Start the translation agent from the last exercise. Before sending requests to the translation agent start the Jadex BDI debugger and open the "agent inspector" tab.\
     

-   Use the Conversation Center to send translation or add-word requests to the translation agent.\
         
-   Observe the belief change of the translation count, whenever a translation request is processed.\
         
-   Observe the changes of the word pair belief set, whenever an add-word request is processed.\
         
-   Use the example from C1 to see the difference in the representation of the word table as belief and belief set.\
     

