1 Chapter 13. Configurations

Configurations represent both the initial and/or end states of an agent type. Initial instance elements can be declared that are created when the agent (resp. the capability) is started. This means that initial elements such as goals or plans are created immediately when an agent is born. On the conatrary, end elements can be used to declare instance elements such as goals or plans that will be created when an agent is going to be terminated. After an agent has been urged to terminate (e.g. by calling \~killAgent()\~ from within a plan or by an CMS \~cms\_destroy\_component\~ goal), all normal goals and plans will be aborted (except plans that perform their cleanup code, i.e. execute one of the \~passed()\~, \~failed()\~ or \~aborted()\~ methods) and the declared end elements will be created and executed.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\
Instance and end elements always have to refer to some original element via the "ref" attribute. &lt;!~~ Additionally, an optional instance name can be provided via the "name" attribute. This can be useful if the element should be accessible later on via this name.~~&gt; Besides the reference also bindings can be used in combination with initial/end elements. If (at least one) binding parameter is declared instance elements will be created for all possible bindings.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\
It is possible to declare any number of configurations for a single agent or capability. When starting an agent or including a capability you can choose among the available configurations In the XML portion for specifying configurations is depicted. Each configuration must have a name for identification purposes. The default configuration can be set up by using the \~default\~ attribute of the \\&lt;configurations\\&gt; base tag. If no explicit default configuration is specified, the first one declared in the ADF is used.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{![](jadexconfigurationsadf.png})

\~The Jadex configurations XML schema part\~

A configuration allows to specify various properties. Generally, the configuration allows two different kinds of adaptations. The first one is the creation of instance elements for declared types, e.g., initial resp. end goals or plans. The second one is the configuration of instance elements such as beliefs or capabilities at start time. In the following, the possible settings will be discussed.

1.1 Capabilities

The \\&lt;capabilities\\&gt; tag allows to configure included capabilities. For this purpose a reference to an included\
\\&lt;initialcapability\\&gt; must be declared. The reference to the capability is established by setting the \~ref\~ attribute to the symbolic name of the capability specified within the \\&lt;capabilities\\&gt; section of the agent/capability (i.e., not the type name but the instance name). The configuration to be used by the included capability can be set by using the \~configuration\~ attribute of the initial capability tag.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{![](jadexconfigurationscapabilitiesadf.png})

\~The Jadex initial capabilities XML schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the figure below an example is shown how the initial state can be used to declare two different initial states. In state "one" the included capability "mycap" is configured to use its initial state "a", while in state "two" "b" is used. Per default the agent would start using initial state "two" as it is declared as default.

<div class="wikimodel-emptyline">

</div>

  

{code:xml}\
&lt;agent ...&gt;\
  ...\
  &lt;capabilities&gt;\
    &lt;capability name="mycap" file="SomeCapability"/&gt;\
  &lt;/capabilities&gt;\
  ...\
  &lt;configurations default="two"&gt;\
    &lt;configuration name="one"&gt;\
      &lt;capabilities&gt;\
        &lt;initialcapability ref="mycap" configuration="a"/&gt;\
      &lt;/capabilities&gt;\
    &lt;/configuration&gt;\
    &lt;configuration name="two"&gt;\
      &lt;capabilities&gt;\
        &lt;initialcapability ref="mycap" configuration="b"/&gt;\
      &lt;/capabilities&gt;\
    &lt;/configuration&gt;\
  &lt;/configurations&gt;\
&lt;/agent&gt;\
{code}\
\~Initial capability configuration\~

1.1 Beliefs

In the \\&lt;beliefs\\&gt; section the initial facts of beliefs and belief sets can be altered or newly introduced. In order to set the initial fact(s) of a belief or belief set an \\&lt;initialbelief\\&gt; resp. an \\&lt;initialbeliefset\\&gt; tag should be used. The connection to the "real" belief is again established via the \~ref\~ attribute and the facts can be declared in the same way as default values of beliefs and belief sets. The initial state does not distinguish between original beliefs and references to beliefs from other capabilities, therefore the same tags can also be used to change initial facts of belief references and belief set references as well.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\
{![](jadexconfigurationsbeliefsadf.png})

\~The Jadex initial beliefs XML schema part\~

The example below shows how a configuration can be used to change belief facts. Belief "name" has a default value of "Jim" which is overridden by the initial belief fact "John". The belief set "names" has no default values. In the initial state it is filled with some data from a database. This means that for all results that the method \~DB.queryNames()\~ produces, a new fact is added to the belief set.

<div class="wikimodel-emptyline">

</div>

{code:xml}\
&lt;agent ...&gt;\
  ...\
  &lt;beliefs&gt;\
    &lt;belief name="name" class="String"&gt;\
      &lt;fact&gt;"Jim"&lt;/fact&gt;\
    &lt;/belief&gt;\
    &lt;beliefset name="names" class="String"/&gt;\
  &lt;/beliefs&gt;\
  ...\
  &lt;configurations&gt;\
    &lt;configuration name="one"&gt;\
      &lt;beliefs&gt;\
        &lt;initialbelief ref="name"&gt;\
          &lt;fact&gt;"John"&lt;/fact&gt;\
        &lt;/initialbelief&gt;\
        &lt;initialbelief set ref="names"&gt;\
          &lt;facts&gt;DB.queryNames()&lt;/facts&gt;\
        &lt;/initialbelief set&gt;\
      &lt;/beliefs&gt;\
    &lt;/configuration&gt;\
  &lt;/configurations&gt;\
&lt;/agent&gt;\
{code}\
\~Initial belief configuration\~

1.1 Goals

In the \\&lt;goals\\&gt; section  initial and end goals can be specified. Initial goals will be instantiated when an agent is born whereas end goals are created when an agent is beginning the termination phase. This means that a new goal instance is created for each declared initial resp. end goal at the mentioned points in time. The specification of an \\&lt;initialgoal\\&gt; and an \\&lt;endgoal\\&gt; requires the connection to the underlying goal template which is used for instantiation. For this purpose the \~ref\~ attribute is used. Optionally, further parameter(set) values can be declared by using the corresponding \\&lt;parameter\\&gt; and \\&lt;parameterset\\&gt; tags.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{![](jadexconfigurationsgoalsadf.png})

\~The Jadex initial and end goals XML schema part\~

<div class="wikimodel-emptyline">

</div>

    

In the example below is depicted how an initial and end goal can be created. Both, the initial and end goal refer to the declared "play\_song" perform goal of the agent and provides a new parameter value for the song parameter. When the agent is started in this initial state it creates the initial goal and pursues it. So, given that the agent has some plan to play an mp3 file, it will play a welcome song in this example. On the other hand the agent will also play a good bye jingle when it is terminated by creating the corresponding end goal.\
  \
{code:xml}\
&lt;agent ...&gt;\
  ...\
  &lt;goals&gt;\
    &lt;performgoal name="play\_song"&gt;\
      &lt;parameter name="song" class="URL"/&gt;\
    &lt;/performgoal&gt;\
  &lt;/goals&gt;\
  ...\
  &lt;configurations&gt;\
    &lt;configuration name="one"&gt;\
      &lt;goals&gt;\
        &lt;initialgoal name="welcome" ref="play\_song"&gt;\
          &lt;parameter ref="song"&gt;\
            &lt;value&gt;new URL("<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://someserver/welcome.mp3</span>](http://someserver/welcome.mp3)</span>")&lt;/value&gt;\
          &lt;/parameter&gt;\
        &lt;/initialgoal&gt;\
        &lt;endgoal name="goodbye" ref="play\_song"&gt;\
          &lt;parameter ref="song"&gt;\
            &lt;value&gt;new URL("<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://someserver/goodbye.mp3</span>](http://someserver/goodbye.mp3)</span>")&lt;/value&gt;\
          &lt;/parameter&gt;\
        &lt;/endgoal&gt;\
      &lt;/goals&gt;\
    &lt;/configuration&gt;\
  &lt;/configurations&gt;\
&lt;/agent&gt;\
{code}\
\~Initial and end goals\~

1.1 Plans

In the \\&lt;plans\\&gt; section initial and end plans can be specified. This means that a new plan instance is created for each declared initial and end plan. The specification of an \\&lt;initialplan\\&gt; and \\&lt;endplan\\&gt; requires the connection to the underlying plan template which is used for instantiation. For this purpose the \~ref\~ attribute is used. Optionally, further parameter(set) values can be declared by using the corresponding \\&lt;parameter\\&gt; and \\&lt;parameterset\\&gt; tags.

{![](jadexconfigurationsplansadf.png})\
\~The Jadex initial and end plans XML schema part\~

In the example is depicted how an initial and end plan can be used. In this case an initial "print\_hello" plan is declared which refers to the "print\_hello" plan template of the agent. As result the agent will print "Hello World!" to the console on start-up. On the contrary it will also print "Goodbye World" when the agent gets terminated by creating the corresponding end plan.

{code:xml}\
&lt;agent ...&gt;\
  ...\
  &lt;plans&gt;\
    &lt;plan name="print\_plan"&gt;\
      &lt;parameter name="text" class="String"/&gt;\
      &lt;body class="PrintOnConsolePlan" /&gt;\
    &lt;/plan&gt;\
  &lt;/plans&gt;\
  ...\
  &lt;configurations&gt;\
    &lt;configuration name="one"&gt;\
      &lt;plans&gt;\
        &lt;initialplan ref="print\_hello"&gt;\
          &lt;parameter name="text"&gt;"Hello World!"&lt;/parameter&gt;\
        &lt;/initialplan&gt;\
        &lt;endplan ref="print\_goodbye"&gt;\
          &lt;paramter name="text"&gt;"Goodbye World!"&lt;/parameter&gt;\
        &lt;/endplan&gt;\
      &lt;/plans&gt;\
    &lt;/configuration&gt;\
  &lt;/configurations&gt;\
&lt;/agent&gt;\
{code}\
\~Initial and end plans\~

1.1 Events

Finally, in the \\&lt;events\\&gt; section initial and end events can be specified. This means that a new event instance is created for each declared initial event after startup of the agent. Additionally, new event instances are created for all declared end events whenever the agent is shutdowned. It is possible to define initial/end internal and initial/end message events (goal events are not necessary as initial goals can be declared). The specification of an \\&lt;initialinternalevent\\&gt; resp. an \\&lt;endinternalevent\\&gt; or an \\&lt;initialmessageevent\\&gt; resp. an \\&lt;endmessageevent\\&gt; requires the connection to the underlying event template which is used for instantiation. For this purpose the \~ref\~ attribute is used. Optionally, further parameter(set) values can be declared by using the \\&lt;parameter\\&gt; and \\&lt;parameterset\\&gt; tags.

<div class="wikimodel-emptyline">

</div>

  

{![](jadexconfigurationseventsadf.png})

\~The Jadex initial and end events XML schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the example below it is shown how an initial and end message event can be created. The intial/end message events refer to the underlying message event template "inform\_state" and set the parameter values for the content as well as for the receiver accordingly. When an agent named "Harry" is started, it sends an initial message event with the content "Harry is born" to an agent named "Uncle" on the same platform. Likewise it sends the message "Harry is terminating" to "Uncle" when the agent shuts down.\
   \
{code:xml}\
&lt;events&gt;\
  &lt;messageevent name="inform\_state" type="fipa" direction="send"&gt;\
    &lt;parameter name="performative" class="String" direction="fixed"&gt;\
      &lt;value&gt;SFipa.INFORM&lt;/value&gt;\
    &lt;/parameter&gt;\
  &lt;/messageevent&gt;\
&lt;/events&gt;\
...\
&lt;configurations&gt;\
  &lt;configuration name="one"&gt;\
    &lt;events&gt;\
      &lt;initialmessageevent ref="inform\_state"&gt;\
        &lt;parameter ref="content"&gt;\
          &lt;value&gt;\$scope.getAgentName()+" is born."&lt;/value&gt;\
        &lt;/parameter&gt;\
        &lt;parameterset ref="receivers"&gt;\
          &lt;value&gt;\$scope.getEventbase().createComponentIdentifier("Uncle")&lt;/value&gt;\
        &lt;/parameterset&gt;\
      &lt;/initialmessageevent&gt;\
      &lt;endmessageevent ref="inform\_state"&gt;\
        &lt;parameter ref="content"&gt;\
          &lt;value&gt;\$scope.getAgentName()+" is terminating."&lt;/value&gt;\
        &lt;/parameter&gt;\
        &lt;parameterset ref="receivers"&gt;\
          &lt;value&gt;\$scope.getEventbase().createComponentIdentifier("Uncle")&lt;/value&gt;\
        &lt;/parameterset&gt;\
      &lt;/endmessageevent&gt;\
    &lt;/events&gt;\
  &lt;/configuration&gt;\
&lt;/configurations&gt;\
{code}\
\~Initial events\~
