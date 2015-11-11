1 Chapter 5. Using Capabilities

Different agents often need to use the same or similar functionalities that incorporate more than just plan behavior. Often private or shared beliefs and goals are part of a common functionality of one agent. These units of functionality are comparable to the module concept in the object oriented paradigm, but exhibit very different properties because of the use of mentalistic notions. For this reasons the capability concept was originally introduced \\\[Busetta et al. 2000\\\] and enhanced in \\\[Braubach et al. 2005b\\\] that allows for packaging a subset of beliefs plans and goals into an agent module and reuse this module wherever needed. The capability structure of an agent forms a tree. A superordinated (parent) capability may contain an arbitrary number of subcapabilities. All elements of a capability have per default private visibility and need to be explicitly made available for usage in a connected capability. For this purpose elements can be defined as abstract or exported enabling access from another capability.

1.1 Preparation

We use the functionality of the C2 Agent and build up a capability of its plans and beliefs. Therefore, it is necessary to copy and rename all files from C2 to D1. We slightly modify these plans to make the translation agent answer to a request with a reply message. Hence the following has to be done in both plans:\
   

-   Declare two variables at the beginning of the plans: 

{code:java}\
String reply;  * The message event type of the reply.\
String content; * The content of the reply message event.\
{code}\
      

-   Set both variables with respect to the success of the translation. In the success case set (assuming that gword and eword are variables for the English and German word respectively):\
         \
    {code:java}\
    reply = "inform";\
    content = gword;\
    {code}\
         
-   And in the failure case:\
    {code:java}\
    reply = "failure";\
    content = "Sorry, word could not be translated: "+eword;\
    {code}\
        
-   Send an answer to the caller at the end of the event processing:\
    {code:java}\
    IMessageEvent replymsg = getEventbase().createReply((IMessageEvent)getReason(), reply);\
    replymsg.getParameter(SFipa.CONTENT).setValue(content);\
    sendMessage(replymsg);\
    {code}\
         
-   Add the new message event types "inform" and "failure" to the ADF:

{code:xml}\
&lt;events&gt;\
    ...\
    &lt;messageevent name="inform" direction="send" type="fipa"&gt;\
        &lt;parameter name="performative" class="String" direction="fixed"&gt;\
            &lt;value&gt;SFipa.INFORM&lt;/value&gt;\
        &lt;/parameter&gt;\
    &lt;/messageevent&gt;

    &lt;messageevent name="failure" direction="send" type="fipa"&gt;\
        &lt;parameter name="performative" class="String" direction="fixed"&gt;\
            &lt;value&gt;SFipa.FAILURE&lt;/value&gt;\
        &lt;/parameter&gt;\
    &lt;/messageevent&gt;\
&lt;/events&gt;\
{code}\
      

-   Test the agent and verify that it answers to the request messages by sending an answer message (for correct as well as for incorrect requests).\
    \
    1.1 Exercise D1 - Creating a Capability    

In this exercise we will create a translation capability.

<div class="wikimodel-emptyline">

</div>

 \
\*Create a new Capability ADF\*

<div class="wikimodel-emptyline">

</div>

-   Create a new file TranslationD1.capability.xml with the skeleton code from the following code snippet. Now copy the definition of imports, plans, beliefs, events (including the newly defined ones from above) and expressions (in this case there are no goals) from TranslationC2.agent.xml into this file.

{code:xml}\
&lt;capability xmlns="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi</span>](http://jadex.sourceforge.net/jadex-bdi)</span>"\
            xmlns:xsi="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://www.w3.org/2001/XMLSchema-instance</span>](http://www.w3.org/2001/XMLSchema-instance)</span>"\
            xsi:schemaLocation="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi</span>](http://jadex.sourceforge.net/jadex-bdi)</span>\
                                <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi-2.0.xsd</span>](http://jadex.sourceforge.net/jadex-bdi-2.0.xsd)</span>"\
  name="TranslationD1"\
  package="jadex.bdi.tutorial"&gt;\
  ...\
&lt;/capability&gt;\
{code}

-   Modify the agent ADF (TranslationD1.agent.xml) by removing all plan and belief definitions. Instead insert a new section for using the new capability.

{code:xml}\
&lt;capabilities&gt;\
    &lt;capability name="transcap" file="TranslationD1"/&gt;\
&lt;/capabilities&gt;\
{code}\
     

-   Note that here the type name is employed, but absolute and relative paths to (the model name of) the XML file can also be used.\
         \
    \*Start and test the agent\*

Load the agent model in the RMA and start the agent. Test the agent with add word and translate requests. It should behave exactly like the Agent from C2. Use the debugger agent to view the new internal structure of the agent.\
  \
1.1 Exercise D2 - Exported Beliefs\
In this exercise we will extend the translation agent by making it capable to find synonyms for English words. Therefore we extend the agent from D1 with a new find synonyms plan which will directly be contained in the agent description. Because the plan needs to access the dictionary from the translation capability, the egwords belief will be made usable from external.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\*Create a new plan\*

   

-   Create the file FindEnglishSynonymsPlanD2.java as a passive plan which reacts on messages with performative type request and starts with "find\_synonyms english". Therefore, you need to introduce the new message event type "find\_synonyms" that fits for request messages that start with "find\_synonyms english".\
         
-   Create one query (called query\_translate here) in the constructor for translating an English word (the query expression can be copied from the EnglishGermanTranslationPlanD2). Create another query (called query\_find here) with the purpose to find all English words that match exactly a German word and are unequal to the given English word.\
          \
    {code:java}\
    String find = "select \$wordpair.get(0) "\
     +"from Tuple \$wordpair in \$beliefbase.egwords "\
     +"where \$wordpair.get(1).equals(\$gword) &amp;&amp; !\$wordpair.get(0).equals(\$eword)";\
    this.queryfind = createExpression(find, new String\[\]{"\$gword", "\$eword"}, new Class\[\]{String.class, String.class});\
    {code}

<!-- -->

-   In the body method, search for synonyms when the message format is correct, what means that the request has exactly three tokens. Use a \~StringTokenizer\~ to parse the request and apply the translation query on the the given English word. When a translation was found, use the result to apply the query find for searching for synonyms. Create a reply and send back the found synonyms as an inform message in the success case and a failure message with a failure reason in the error case. The following code snippet outlines how the second query can be realized (eword is the English word for which synonyms are searched, gword is the German translation of the given English word):

{code:java}\
List syns = (List)queryfind.execute(new String\[\]{"\$gword", "\$eword"},  new Object\[\]{gword, eword});\
{code}

\*Create a new Capability ADF\*\
   

-   Create a new file TranslationD2.capability.xml by copying the capability from exercise D1.\
         
-   Modify the belief set declaration of "egwords" by setting the belief set \~type="exported"\~. Add some facts to the belief "egtrans" to have some synonyms present.

{code:xml}\
&lt;beliefset name="egwords" class="Tuple" exported="true"&gt;\
    &lt;fact&gt;new Tuple("milk", "Milch")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("cow", "Kuh")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("cat", "Katze")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("dog", "Hund")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("puppy", "Hund")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("hound", "Hund")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("jack", "Katze")&lt;/fact&gt;\
    &lt;fact&gt;new Tuple("crummie", "Kuh")&lt;/fact&gt;\
&lt;/beliefset&gt;\
{code}\
     

-   Also use the exported attribute to make the "inform" and "failure" messages accessible, as we want to use these in the new synomyms plan.\
         \
    \*Create a new TranslationD2 Agent ADF\*\
       
-   Create a new file TranslationD2.agent.xml by copying the file from D1. Extend this definition by adding the new plan to the plans section and adding a referenced belief, that relates to the egwords belief from the capability. Note that the name of the referenced belief can be chosen arbitrarily (in this case we name it egwords, too). Additionally change the capability reference to the newly created TranslationCapabilityD2 and add the new message event type request\_findsynonyms.

{code:xml}\
&lt;beliefs&gt;\
    &lt;beliefsetref name="egwords"&gt;\
        &lt;concrete ref="transcap.egwords" /&gt;\
    &lt;/beliefsetref&gt;\
&lt;/beliefs&gt;

&lt;plans&gt;\
    &lt;plan name="find\_synonyms"&gt;\
        &lt;body class="FindEnglishSynonymsPlanD2"/&gt;\
        &lt;trigger&gt;\
            &lt;messageevent ref="request\_findsynonyms"/&gt;\
        &lt;/trigger&gt;\
    &lt;/plan&gt;\
&lt;/plans&gt;

&lt;events&gt;\
    &lt;messageevent name="request\_findsynonyms" direction="receive" type="fipa"&gt;\
        &lt;parameter name="performative" class="String" direction="fixed"&gt;\
            &lt;value&gt;SFipa.REQUEST&lt;/value&gt;\
        &lt;/parameter&gt;\
        &lt;parameter name="content-start" class="String" direction="fixed"&gt;\
            &lt;value&gt;"find\_synonyms english"&lt;/value&gt;\
        &lt;/parameter&gt;\
    &lt;/messageevent&gt;

    &lt;messageeventref name="inform"&gt;\
        &lt;concrete ref="transcap.inform"/&gt;\
    &lt;/messageeventref&gt;

    &lt;messageeventref name="failure"&gt;\
        &lt;concrete ref="transcap.failure"/&gt;\
    &lt;/messageeventref&gt;\
&lt;/events&gt;\
{code}

\*Start and test the agent\*

Start the agent and send it some find synonyms requests, e.g. "find\_synonyms english dog". When your agent works ok, you should be notified that the synonyms for dog are hound and puppy. Use the bdi debugger to understand what the belief set reference means.
