<span>Chapter 3. Using Plans</span> 
===================================

Plans play a central role in Jadex, because they encapsulate the recipe for achieving some state of affair. Generally, a plan consists of two parts in Jadex. The plan body is a standard Java class that extends a predefined Jadex framework class (*jadex.bdi.runtime.Plan*) and has at least to implement the abstract *body()* method which is invoked after plan instantiation. The plan body is associated to a plan head in the ADF. This means that in the plan head several properties of the plan can be specified, e.g. the circumstances under which it is activated and its importance in relation to other plans.

<div class="wikimodel-emptyline">

</div>

In contrast to other well-known PRS-like systems, Jadex supports two styles of plans. A so called *service plan* is a plan that has service character in the sense that a plan instance of the plan is usually running and waits for service requests. It represents an easy way to react on service requests sequentially without the need to synchronize different plan instances for the same plan. Therefore a service plan can setup its private event waitqueue and receive events for later processing, even when it is working at the moment.

<div class="wikimodel-emptyline">

</div>

A so called *PRS-style or passive plan* is the normal version of a plan, as can be found in all other PRS-systems. This means that usually such a plan is only running, when it has a task to achieve. For this kind of plan the triggering events and goals must be specified in the agent definition file to let the agent know what kinds of events this plan can handle. When an agent receives an event, the BDI reasoning engine builds up the so called applicable plan list (that are all plans which can handle the current event or goal) and candidate(s) are selected and instantiated for execution. PRS-style plans are a good choice, when the parallel execution of one kind of task is needed or is at least not disturbing. For more detailed information about plans have a look in the <span class="wikiexternallink">[BDI User Guide](../BDI%20User%20Guide/01%20Introduction)</span>.

<div class="wikimodel-emptyline">

</div>

BEGIN MACRO: html param: clean="false" wiki="true"\
\
Often a plan does some action and then wants to wait until the action has been done before continuing (e.g. dispatching a subgoal, sending a message and waiting for the reply). Therefore a plan can use one of the various waitFor() methods, that come in quite different flavors. Coming back to the examples mentioned, e.g. the dispatchSubgoalAndWait(IGoal subgoal, long timeout) can be used to dispatch a subgoal and wait for its completion (optionally with some timeout). Similar, for sending a message and waiting for a reply the sendMessageAndWait(IMessageEvent me, long timeout) method can be used. For an extensive overview of all available methods, please refer to the <span class="wikiexternallink">[BDI User Guide](../BDI%20User%20Guide/01%20Introduction)</span> or the API docs contained in the Jadex release. &lt;!-- &lt;a href="http://vsis-www.informatik.uni-hamburg.de/projects/jadex/jadex-0.96x/kernel/index.html"&gt;API documentation&lt;/a&gt;.--&gt;\\

<span>Exercise B1 - Service Plans</span> 
----------------------------------------

In this exercise we will use a service plan for translating words from English to German. Create a new TranslationB1.agent.xml file by copying the TranslationA1.agent.xml file and modify all occurrences of "A1" to "B1".&lt;br/&gt;\
&lt;p/&gt;\
**Create a new file called EnglishGermanTranslationPlanB1.java responsible for a basic word translation with the following properties:**

-   Create the plan as extension to the jadex.bdi.runtime.Plan class:


```java

public class EnglishGermanTranslationPlanB1 extends Plan {
    // Plan attributes.

    public EnglishGermanTranslationPlanB1() {
        // Initialization code.
    }

    public void body() {
        // Plan code.
    }
}

```


-   Import the needed classes: *jadex.bridge.fipa.SFipa, jadex.bdi.runtime.IMessageEvent, jadex.bdi.runtime.Plan, java.util.HashMap, java.util.Map*
-   Let the no argument constructor print out the text "Created:"+this.
-   Implement the plan's body() method as infinite loop. At the beginning of this loop the plan should wait for translation requests using *IMessageEvent me = waitForMessageEvent("request\_translation")*
-   Instead of performing a database query let us use a simple hashmap for the word lookup. The creation and initialization of this word table with a few word pairs can already be done in the constructor. As result the plan should print *"Translating from English to German: "+eword+" - "+gword* or *"Sorry word is not in database: "+eword*. To get the content from the request-event use *me.getParameter(SFipa.CONTENT).getValue()*.\\\
    &lt;p/&gt;\
    **Add the plan to the agent by putting it into the agent definition file:**

<!-- -->

-   Therefore, a new plans section is introduced, in which all plans for the agent have to be declared. In this simple example only one plan named here "egtrans" is added. As part of the plan the Java class name for the plan body is stated. Additionally, the plan's waitqueue is declared to handle all message events of type "request\_translation". This means that the plan has its own event waitqueue in which all matching events are dispatched, even when the plan is busy and currently waits for other events. These events are collected in its queue till it calls a suitable *waitFor()* matching one of the collected events. In this case this collected event is directly dispatched to the plan.
-   The plan should be started when the agent is born. For this purpose a configuration has to be declared within the ADF. It is sufficient in this case to define one configuration (named "default") with an initial plan. The initial plan simply references the plan for which an instance should be created.
-   Besides the introduction of the new plan we also need to make explicit what exactly a request\_translation event means. For this purpose a new events section is introduced. In this section the request\_translation event is declared being a message event with one parameter. This parameter specifies that its performative has the fixed value 'request'. Whenever the agent receives a message it will search its declared events for the best matching event type. In this case all messages with performative request it will be treated as request\_translation events.


```xml

<agent xmlns="http://jadex.sourceforge.net/jadex-bdi"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://jadex.sourceforge.net/jadex-bdi
                      http://jadex.sourceforge.net/jadex-bdi-2.0.xsd"
  name="TranslationB1"
  package="jadex.bdi.tutorial">

  <plans>
    <plan name="egtrans">
      <body class="EnglishGermanTranslationPlanB1"/>
      <waitqueue>
        <messageevent ref="request_translation"/>
      </waitqueue>
    </plan>
  </plans>

  <events>
    <messageevent name="request_translation" direction="receive" type="fipa">
      <parameter name="performative" class="String" direction="fixed">
        <value>jadex.bridge.fipa.SFipa.REQUEST</value>
      </parameter>
    </messageevent>
  </events>

  <properties>
    <property name="debugging">false</property>
  </properties>

  <configurations>
    <configuration name="default">
      <plans>
        <initialplan ref="egtrans"/>
      </plans>
    </configuration>
  </configurations>
</agent>

```


**Start and test the agent**\\\
&lt;p/&gt;\
Create a translation agent via the Control Center and observe the standard output, if the initial plan is created at startup. Use the Conversation Center (in new Jadex versions the conversation center needs to be activated via popup menu on the toolbar, see <span class="wikiexternallink">[Tool Guide](../AC%20Tool%20Guide/02%20JCC%20Overview)</span>) to send a translation request to the TranslationAgent by setting the performative to *request* and the content to some word to translate. Observe the TranslationAgent's output on the console when it receives the request.

<span>Exercise B2 - Passive Plans</span> 
----------------------------------------

In constrast to the last exercise we will now use a passive plan to react on translation requests. To show the difference between the two forms of plans we now modify the service plan slightly to become a passive plan. Create the files EnglishGermanTranslationPlanB2.java and TranslationB2.agent.xml by copying the files from exercise B1.&lt;br/&gt;\
  \
**Modify the copied file TranslationPlanB2.java:**\\

-   Replace all occurrences of "B1" in the Plan with "B2"
-   In contrast to the initial plan, the passive plan's body method is only invoked, when an event matches the plan's trigger. So use the method *getReason()* to retrieve the event that caused the execution. Because we know that only certain messages activate the plan the event can directly be cast to type *jadex.bdi.runtime.IMessageEvent* and the content can be retrieved. The infinite loop in the body should be discarded, because for each event a new plan instance is created, which only handles a single message.\\\
    &lt;p/&gt;\
    **Modify the copied file TranslationB2.agent.xml**\\

<!-- -->

-   Replace all occurrences of "B1" in the ADF file with "B2"
-   Modify the plan declaration in the ADF by removing the configurations section. Additionally a passive plan needs a trigger, that specifies under what circumstances a new plan instance is created. Therefore remove the waitqueue statement and add a new statement for the plan trigger:


```xml

<trigger>
  <messageevent ref="request_translation"/>
</trigger>

```


**Start and test the agent**\\\
   \
Start the agent as explained in the preceding exercise. Observe that a new instance of the translation plan is created everytime an appropriate event arrives. The passive plan is instantiated and each instance processes a different message event. Many different plan instances may remain active while processing their triggers.

<span>Exercise B3 - Plan Parameters</span> 
------------------------------------------

In this exercise we will use plan parameters to supply the plan with arguments. Plan parameters can directly be accessed from within the plan body via the getParameter("paramname") and getParameterSet("paramsetname") methods. Generally parameters can have the directions *in*, *out* and *inout* describing parameters that are used for supplying values or resp. gathering return values from the plan. Plan parameters can be supplied with fixed values via the &lt;value&gt; or &lt;values&gt; tags. More interestingly parameter values can be mapped from and to the triggers by using parameter mappings. If a plan could be activated by more than one trigger (e.g. two different messages, or a message and a goal, etc.) multiple goal mappings (one for each trigger type) have to be used to unify the plans view on its arguments.&lt;br/&gt;\
   \
Create the files EnglishGermanTranslationPlanB3.java and TranslationB3.agent.xml by copying the files from exercise B2. Apply the same replacements B2-&gt;B3 as in the previous exercise.&lt;br/&gt;\
&lt;p/&gt;\
**Modify the EnglishGermanTranslationPlanB3.java**

-   Instead of using the getReason() method to retrieve the English word, we use the the statement: String eword = (String)getParameter("eword").getValue();\\\
    &lt;p/&gt;\
    **Modify the copied file TranslationB3.agent.xml to include the new plan parameter**\\

<!-- -->

-   Add the new plan parameter with a message event mapping to the ADF:


```xml

<plan name="egtrans">
  <parameter name="eword" class="String">
    <messageeventmapping ref="request_translation.content"/>
  </parameter>
  <body class="EnglishGermanTranslationPlanB3"/>
  <trigger>
    <messageevent ref="request_translation"/>
  </trigger>
</plan>

```


**Start and test the agent**\\\
Test and verify that the agent behavior is the same as in the last exercise.

<span>Exercise B4 - Plan Selection</span> 
-----------------------------------------

In this exercise we will use plan priorities to establish a plan selection order. Create the files EnglishGermanTranslationPlanB4.java and TranslationB4.agent.xml by copying the files from exercise B2. Apply the same replacements B2-&gt;B4 as in the previous exercise.&lt;br/&gt;\
  \
**Create a new plan file named SearchTranslationOnlineB4.java**

-   This plan should be used when the agent cannot find the word in its (currently very small) dictionary. In this case the online search plan will try to connect to a web dictionary and report the found translations. The address of a simple English-German dictionary is <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://wolfram.schneider.org/dict/dict.cgi</span>](http://wolfram.schneider.org/dict/dict.cgi)</span> (you may use any other dictionary for this purpose if you are not afraid of parsing the result HTML page). To issue a query against this online database you need to create a URL and read the data from there as outlined below. You will have to add code for fetching the "eword" that should be translated and a try/catch block when creating the URL, i.e. if the dictionary is not available. 


```java

URL dict = new URL("http://wolfram.schneider.org/dict/dict.cgi?query="+eword);
BufferedReader in = new BufferedReader(new InputStreamReader(dict.openStream()));
String inline;
while((inline = in.readLine())!=null) 
{
  if(inline.indexOf("<td>")!=-1 &amp;&amp; inline.indexOf(eword)!=-1) 
  {
    try 
    {
     int start = inline.indexOf("<td>")+4;
     int end = inline.indexOf("</td", start);
     String worda = inline.substring(start, end);
     start = inline.indexOf("<td", start);
     start = inline.indexOf(">", start);
     end = inline.indexOf("</td", start);
     String wordb = inline.substring(start, end==-1? inline.length()-1: end);
     wordb = wordb.replaceAll("<b>", "");
     wordb = wordb.replaceAll("</b>", "");
     System.out.println(worda+" - "+wordb);
    }
    catch(Exception e) 
    {
      System.out.println(inline);
    }
  }
}
in.close();

```


**Modify the EnglishGermanTranslationPlanB4 having a static dictionary**

-   Make the variable for the dictionary static and initialize it in a static block instead of in the constructor:


```java

static {
  wordtable = new HashMap();
  wordtable.put("coffee", "Kaffee");
  wordtable.put("milk", "Milch");
  wordtable.put("cow", "Kuh");
  wordtable.put("cat", "Katze");
  wordtable.put("dog", "Hund");
}

```


-   Provide a public static method for testing if a word is contained in the dictionary:


```java

public static boolean containsWord(String name) {
  return wordtable.containsKey(name);
}

```


**Modify the copied file TranslationB4.agent.xml to include the new plan**

-   Add the new online search plan to the plan declarations using a low priority:


```xml

<plan name="searchonline" priority="-1">
  <body class="SearchTranslationOnlineB4"/>
  <trigger>
    <messageevent ref="request_translation"/>
  </trigger>
</plan>

```


-   Add an imports section and the import statement for the jadex.bridge.fipa classes to the imports section:


```xml

<imports>
  <import>jadex.bridge.fipa.*</import>
</imports>

```


-   Modify the applicability of the translation plan by introducing a precondition:


```xml

<plan name="egtrans">
  <body class="EnglishGermanTranslationPlanB4"/>
  <trigger>
    <messageevent ref="request_translation"/>
  </trigger>
  <precondition>
    EnglishGermanTranslationPlanB4.containsWord((String)$event.getParameter(SFipa.CONTENT).getValue())
  </precondition>
</plan>

```


**Start and test the agent**\\\
&lt;p/&gt;\
When the agent receives translation request it searches applicable plans to handle this request. If the word is contained in the dictionary both plans are applicable and the one with the higher priority is chosen (in this case it is the egtrans plan because the standard priority is 0). When the word is not contained in the dictionary only the searchonline plan is applicable and will be used.

<span>Exercise B5 - BDI Debugger</span> 
---------------------------------------

The Jadex debugging perspective is conceived to support you in the debugging of agents and helps you to understand what happens inside an agent. You could use it for the agents from the previous excercises, e.g. to grasp the differences between B1 and B2.&lt;br/&gt;\
&lt;p/&gt;\
**Using the Jadex debugger tool to control the execution of an agent**

-   Choose the agent model you want to debug in the starter (e.g. B4) and check the checkbox "start suspended". You will notice after starting that the agent symbol in the agent tree (at the bottom left) shows the agent in suspended state (zzzz icon). The agent will only process events when the execution is manually requested in the debugger tool. Note that you can also freeze and resume the execution of the translation agent by setting execution mode to "step" and "run" in the tool.  
-   As an alternative you can prepare the agent debugging by setting the debugging flag in a new properties section (at the end of the file) of the ADF to true. 


```xml

<properties>
  <property name="debugging">true</property>
</properties>

```


-   Start the translation agent of the last exercise from the Control Center.
-   Switch to the debugger perspective in the Control Center and activate a debugger view for the translation agent by double clicking the agent in the agent tree on the left hand side.
-   Use the Conversation Center to send some translation requests to the translation agent (as in B4).
-   The information panel of the debugger on the right hand side has two tabs. Use the "agent inspector" tab to view the internal state of a bdi agent and the "rule engine" to see the agent logic in form of rules. The "rule engine" tab also provides a view for the execution history (already executed activations).
-   Press the "step" button several times in the debugger and observe in the rule engine tab how an activation (rule) from the agenda is executed. 
-   The debugger also offers a breakpoint view (on the left). Try out working with breakpoints by selecting some of them and pressing "run". In case the selected breakpoint (rule) is activated the debugger will automatically set the agent to step mode so that it can be easily inspected and executed step by step. 

<span>Exercise B6 - Log-Outputs</span> 
--------------------------------------

In this exercise we will use log-outputs instead of directly printing console outputs. The log outputs will typically be printed to the System.err stream, which is displayed in red color in eclipse. Create the files EnglishGermanTranslationPlanB6.java and TranslationB6.agent.xml by copying the files from exercise B2.&lt;br/&gt;\
  \
**Modify the copied file TranslationPlanB6.java**

-   Replace all occurrences of System.out.println(..) to getLogger().info(..).\
    &lt;p/&gt;\
    **Modify the copied file TranslationB3.agent.xml**

<!-- -->

-   Add an imports section and the import statement for the java.logging classes to the imports section:


```xml

<imports>
  <import>java.util.logging.*</import>
</imports>

```


-   Introduce a properties section at the bottom of the ADF to specify the logging behavior. Insert the following code:


```xml

<properties>
  <property name="logging.level">Level.INFO</property>
  <property name="logging.useParentHandlers">true</property>
</properties>

```


These properties can be used to control the agent logging. The log-level decides what kind of log-outputs shall be considered for logging, according to the java.util.logging level hierarchy. Increasing the level value, e.g. to warning means, that only log-outputs at this or a higher level are conisdered by the logger. The useParentHandlers property can be used to turn on or off the standard console logging handler (per default it is set to true).&lt;br/&gt;\
&lt;p/&gt;\
**Start and test the agent**\\\
Start the translation agent. Send a translation request to the translation agent and watch the console and logger output. To turn off the console output simply set the property useParentHandlers in the ADF to false.

 END MACRO: html
