1 Chapter 16. Using Predefined Capabilities

The documentation of the predefined capabilities is not yet finished.\
Please also take a look at the \[BDI Tutorial&gt;BDI Tutorial.07 Using Events\] (Exercise F4)\
and at the \[legacy documentation of Jadex 0.96&gt;<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.informatik.uni-hamburg.de/docs/jadex-0.96x/userguide/predef\_cap.html</span>](http://jadex.informatik.uni-hamburg.de/docs/jadex-0.96x/userguide/predef_cap.html)</span>\].

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Jadex uses capabilities for the modularization of agents (see \[Chapter 5. Capabilities&gt;05 Capabilities\]), whereby\
capabilities contain ready to use functionalities. The Jadex distribution contains several\
ready-to-use predefined capabilities for different purposes. Besides the basic management\
capabilties for using the CMS (component management service, see \[CMSCapability&gt;\#HTheComponentManagementService28CMS29Capability\])\
and the DF (see \[DFCapability&gt;\#HTheDirectoryFacilitator28DF29Capability\])\
also a \[Protocols Capability&gt;\#HTheProtocolsCapability\] is available for the efficient\
usage of some predefined FIPA interaction protocols. The interface of a capability mainly consists of a set of exported\
goals which is similar to an object-oriented method-based interface description.\
This chapter aims at depicting their usage by offering the application programmer\
an overview and explanation of their functionalities and additionally a selection of short code snippets\
that can directly be used in your applications. 

<div class="wikimodel-emptyline">

</div>

The test capability for writing agent-based unit test is explained in the\
\~Jadex Tool Guide\~, which also illustrates the usage of the corresponding\
Test Center user interface.\
 

&lt;! ~~** CMS ** ~~&gt;

1.1 The Component Management Service (CMS) Capability

<div class="wikimodel-emptyline">

</div>

The Component Management Service (CMS) capability provides goals, that allow the application programmer\
to use functionalties of the local or some remote CMS. Basically the CMS is responsible for\
managing the component lifecycle and for interacting with the platform. Concretely this means the CMS\
capability can be used for:

<div class="wikimodel-emptyline">

</div>

-   \[Creating Components&gt;\#HCreatingComponents\]
-   \[Destroying Components&gt;\#HDestroying Components\]
-   \[Suspending Components&gt;\#HSuspendingComponents\]
-   \[Resuming Components&gt;\#HResumingComponents\]
-   \[Searching Components&gt;\#HSearchingComponents\]
-   \[Shutting Down the Platform&gt;\#HShuttingDownthePlatform\]\
      

&lt;! ~~** CMS: CREATE COMPONENTS ** ~~&gt;

1.1.1 Creating Components

<div class="wikimodel-emptyline">

</div>

The goal \~cms\_create\_component\~ creates a new component via the CMS on the platform.\
This goal has the following parameters:

<div class="wikimodel-emptyline">

</div>

{table}\
Name            | Type                 | Description\
type            | String               | The component type (name/path of component model).\
name\\\*          | String               | The name of the instance to create. If no name is specified, a name will be generated automatically.\
configuration\\\* | String               | The initial component configuration to use. If no configuration is specified, the default configuration will be used.\
arguments\\\*     | Map                  | The arguments as name-value pairs for the new component. Depending on the platform, Java objects (for Jadex Standalone or local JADE requests) or string expressions (for remote JADE requests) have to be supplied for the argument values.\
cms\\\*           | IComponentIdentifier | The component identifier of the CMS (only required for remote requests)\
start\\\*         | boolean              | True, when the component should be directly started after creation (default). Note that some platforms will not support decoupling of component creation and starting (e.g. for remote requests in JADE).\
componentidentifier \\\[out\\\] | IComponentIdentifier | Output parameter containing the component identifier of the created component.\
{table}\
\~Parameters for cms\_create\_component goal (\* denotes optional parameters)\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

To use the "cms\_create\_component"-goal, you must first of all include the CMS-capability in your ADF (if not\
yet done in order to use other goals of the CMS-capability) and set a reference to the goal as described below. The name of the goal reference can be arbitrarily chosen, but it will be assumed here for convenience that the same as the original name will be used.

<div class="wikimodel-emptyline">

</div>

{code:xml}\
...\
&lt;capabilities&gt;\
  &lt;capability name="cmscap" file="jadex.bdi.planlib.cms.CMS" /&gt;\
  ...\
&lt;/capabilities&gt;\
...\
&lt;goals&gt;\
  &lt;achievegoalref name="cms\_create\_component"&gt;\
    &lt;concrete ref="cmscap.cms\_create\_component" /&gt;\
  &lt;/achievegoalref&gt;\
  ...\
&lt;/goals&gt;\
...\
{code}\
\~Including the CMS capability and the cms\_create\_component-goal\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Now you can use this goal to create a component in your plan:

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  ...\
  IGoal cc = createGoal("cms\_create\_component");\
  cc.getParameter("type").setValue("mypackage.MyComponent");\
  dispatchSubgoalAndWait(cc);\
  IComponentIdentifier createdcomponent =\
    (IComponentIdentifier)cc.getParameter("componentidentifier").getValue();\
  ...\
}\
{code}\
\~Creating a component on the local platform\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the above listing - in order to create a component - you instantiate a new goal using the \~createGoal()\~-method with the paramter "cms\_create\_component". Then you set its parameters to the desired values, dispatch the subgoal and wait. After the goal has succeeded, you can fetch the \~IComponentIdentifier\~ of the created component by calling the \~getValue()\~-method on the parameter "componentidentifier".

<div class="wikimodel-emptyline">

</div>

The same goal is used for remote creation of a component:

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  IComponentManagementService cms = IComponentManagementService)getScope()\
    .getServiceContainer().getService(IComponentManagementService.class);\
  IComponentIdentifier remote\_cms\_id = cms.createComponentIdentifier("cms@remoteplatform",\
    false, new String\[\]{"<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">nio-mtp://134.100.11.232:5678</span>](nio-mtp://134.100.11/232:5678)</span>"});

  IGoal cc = createGoal("cms\_create\_component");\
  cc.getParameter("type").setValue("mypackage.MyComponent");\
  cc.getParameter("cms").setValue(remote\_cms\_id);\
  dispatchSubgoalAndWait(cc);\
  IComponentIdentifier createdcomponent =\
    (IComponentIdentifier)cc.getParameter("componentidentifier").getValue();\
  ...\
}\
{code}\
\~Creating a component on a remote platform\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the above listing you can see how to create a component on a remote platform\
using its remote CMS. In order to do so, it's of course crucial that you know at least one address of the remote CMS.\
Moreover, the corresponding transport must be available on the local platform. The transport used by the other\
platform can be recognized by the prefix of the address (ending with the :*). In this case the prefix\
is \~<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">nio-mtp://\~\~</span>](nio-mtp://~~)</span>, which represents the transport \~jadex.adapter.standalone.transport.niotcpmtp.NIOTCPTransport\~.*

<div class="wikimodel-emptyline">

</div>

If you know the address of the remote CMS and you're sure that the local platform supports its transport, you must\
create an \~IComponentIdentifier\~ using the local component management service and set its name and address to that of the CMS that should create the new component.

<div class="wikimodel-emptyline">

</div>

Thereafter you can instantiate a new goal using the \~createGoal()\~-method with the\
paramter "cms\_create\_component". Then you set its parameters to the desired values, dispatch the subgoal\
and wait. After the goal has succeeded, you can fetch the \~IComponentIdentifier\~ of the created component by calling the \~getValue()\~-method on the parameter "componentidentifier".

<div class="wikimodel-emptyline">

</div>

&lt;! ~~** CMS: DESTROY COMPONENTS ** ~~&gt;

1.1.1 Destroying Components

The CMS capability offers the goal \~cms\_destroy\_component\~ to give the application programmer the possibility to destroy\
components, both on a local as well as on remote platforms.\
    

The goal has the following parameters:

<div class="wikimodel-emptyline">

</div>

{table}\
Name                | Type                 | Description\
componentidentifier | IComponentIdentifier | Identifier of the component to be destroyed.\
cms\\\*               | IComponentIdentifier | The component identifier of the CMS (only required for remote requests)\
{table}\
\~Parameters for cms\_destroy\_component (\* denotes optional parameters)\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

To use the \~cms\_destroy\_component\~-goal, you must first of all include the CMS-capability in your ADF (if\
not yet done in order to use other goals of the CMS-capability) and set a reference to the goal as described below:

{code:xml}\
...\
&lt;capabilities&gt;\
  &lt;capability name="cmscap" file="jadex.bdi.planlib.cms.CMS" /&gt;\
  ...\
&lt;/capabilities&gt;\
...\
&lt;goals&gt;\
  &lt;achievegoalref name="cms\_destroy\_component"&gt;\
     &lt;concrete ref="cmscap.cms\_destroy\_component" /&gt;\
  &lt;/achievegoalref&gt;\
  ...\
&lt;/goals&gt;\
...\
{code}\
\~Including the CMS capability and the cms\_destroy\_component-goal\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Thus you can destroy a component in your plan:

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  IGoal dc = createGoal("cms\_destroy\_component");\
  dc.getParameter("componentidentifier").setValue(createdcomponent);\
  * dc.getParameter("cms").setValue(cms);  * Set cms in case of remote platform\
  dispatchSubgoalAndWait(dc);\
  ...\
}\
{code}\
\~Destroying a component on a local/remote platform\~

In the listing above - in order to destroy a component - you instantiate a\
new goal using the \~createGoal()\~-method with the parameter\
"cms\_destroy\_component". Then you set its componentidentifier-parameter to the desired value, dispatch the\
subgoal and wait for success. The same goal is used to destroy a remote component. In this case you only\
have to additionally supply the remote CMS component identifier.\
  

&lt;! ~~** CMS: SUSPENDING COMPONENTS **\* ~~&gt;\
  \
1.1.1 Suspending Components

The CMS offers the goals "cms\_suspend\_component" and "cms\_resume\_component" in order to suspend\
the execution of a component and later resume it. When a component gets suspended the platform will not process\
any actions of this component. Nevertheless, the component is able to receive messages from other components and will\
process them when its execution is resumed.\
    

The "cms\_suspend\_component"-goal has the following parameters:

   \
{table}\
Name                | Type                 | Description\
componentidentifier | IComponentIdentifier | Identifier of the component to be suspended.\
cms\\\*               | IComponentIdentifier | The component identifier of the CMS (only required for remote requests)\
componentdescription \\\[out\\\] | ICMSComponentDescription | This output parameter contains the possibly changed CMSComponentDescription of the suspended component.\
{table}\
\~Parameters for cms\_suspend\_component (\* denotes optional parameters)\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

To use the "cms\_suspend\_component"-goal, you must first of all include the CMS-capability in your ADF (if\
not yet done in order to use other goals of the CMS-capability) and set a reference to the goal as described below:

<div class="wikimodel-emptyline">

</div>

{code:xml}\
...\
&lt;capabilities&gt;\
  &lt;capability name="cmscap" file="jadex.bdi.planlib.cms.CMS" /&gt;\
  ...\
&lt;/capabilities&gt;\
...\
&lt;goals&gt;\
  &lt;achievegoalref name="cms\_suspend\_component"&gt;\
    &lt;concrete ref="cmscap.cms\_suspend\_component" /&gt;\
  &lt;/achievegoalref&gt;\
  ...\
&lt;/goals&gt;\
...\
{code}\
\~Including the CMS capability and the cms\_suspend\_component-goal\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Thus you can suspend a component in your plan:

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  IComponentIdentifier component;  * The component to suspend\
  ...\
  IGoal sc = createGoal("cms\_suspend\_component");\
  sc.getParameter("componentidentifier").setValue(component);\
  * sc.getParameter("cms").setValue(cms);  * Set cms in case of remote platform\
  dispatchSubgoalAndWait(sc);\
  ...\
}\
{code}\
\~Suspending a component on a local/remote platform\~*

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the listing above - in order to suspend a component - you instantiate a\
new goal using the \~createGoal()\~-method with the paramter\
"cms\_suspend\_component". Then you set its componentidentifier-parameter to the desired value, dispatch the\
subgoal and wait for success. As result the goal returns a possibly modified CMS component description of\
the suspended component. The same goal is used to suspend a remote component. In this case you only\
have to additionally supply the remote CMS component identifier.\
   

&lt;! ~~** CMS: RESUMING COMPONENTS **\* ~~&gt;\
  \
1.1.1 Resuming Components\
   \
If you want to resume a suspended component you can use the goal "cms\_resume\_component". It offers the following parameters:

{table}\
Name                | Type                 | Description\
componentidentifier | IComponentIdentifier | Identifier of the component to be resumed.\
cms\\\*               | IComponentIdentifier | The component identifier of the CMS (only required for remote requests)\
componentdescription \\\[out\\\] | ICMSComponentDescription | This output parameter contains the possibly changed CMSComponentDescription of the resumed component.\
{table}\
\~Parameters for cms\_resume\_component (\* denotes optional parameters)\~

<div class="wikimodel-emptyline">

</div>

  \
To use the "cms\_resume\_component"-goal, you must first of all include the CMS-capability in your ADF (if\
not yet done in order to use other goals of the CMS-capability) and set a reference to the goal as described below:

<div class="wikimodel-emptyline">

</div>

{code:xml}\
...\
&lt;capabilities&gt;\
  &lt;capability name="cmscap" file="jadex.bdi.planlib.cms.CMS" /&gt;\
  ...\
&lt;/capabilities&gt;\
...\
&lt;goals&gt;\
  &lt;achievegoalref name="cms\_resume\_component"&gt;\
    &lt;concrete ref="cmscap.cms\_resume\_component" /&gt;\
  &lt;/achievegoalref&gt;\
  ...\
&lt;/goals&gt;\
...\
{code}\
\~Including the CMS capability and the cms\_resume\_component-goal\~\
   

Thus you can resume a component in your plan:

{code:java}\
public void body()\
{\
  ComponentIdentifier component;  * The component to resume\
  ...\
  IGoal rc = createGoal("cms\_resume\_component");\
  rc.getParameter("componentidentifier").setValue(component);\
  * rc.getParameter("cms").setValue(cms);  * Set cms in case of remote platform\
  dispatchSubgoalAndWait(rc);\
  ...\
}\
{code}\
\~Resuming a component on a local/remote platform\~*

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the above listing - in order to resume a component - you instantiate a\
new goal using the \~createGoal()\~-method with the paramter\
"cms\_resume\_component". Then you set its componentidentifier-parameter to the desired value, dispatch the\
subgoal and wait for success. As result the goal returns a possibly modified CMS component description of\
the resumed component. The same goal is used to resume a remote component. In this case you only\
have to additionally supply the remote CMS component identifier.\
   \
&lt;! ~~** CMS: SEARCHING COMPONENTS ** ~~&gt;

1.1.1 Searching for Components

The goal "cms\_search\_components" allows you to search for components, both on\
the local platform and on remote platforms, thereby determining if the component\
is available at all and learning about its state (e.g. active or suspended).

<div class="wikimodel-emptyline">

</div>

The goal has the following parameters:

<div class="wikimodel-emptyline">

</div>

{table}\
Name                | Type                     | Description\
description         | ICMSComponentDescription | The template description to search for matching components.\
cms\\\*               | IComponentIdentifier     | The component identifier of the CMS (only required for remote requests)\
constraints\\\*       | ISearchConstraints       | Representation of a set of constraints to limit the search process. As a default, only one matching result is returned. You can set the max-results setting of the search constraints to -1 for unlimited number of search results. See \[FIPA Agent Management Specification&gt;<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://www.fipa.org/specs/fipa00023/XC00023H.html\#\_Toc526742642</span>](http://www.fipa.org/specs/fipa00023/XC00023H.html#_Toc526742642)</span>\].\
result \\\[set\\\]\\\[out\\\] | ICMSComponentDescription | This output parameter set contains the component descriptions that have been found.\
{table}\
\~Parameters for cms\_search\_components (\* denotes optional parameters)\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

To use the "cms\_search\_components"-goal, you must first of all include the CMS-capability in your ADF (if\
not yet done in order to use other goals of the CMS-capability) and set a reference to the goal as described below.

<div class="wikimodel-emptyline">

</div>

{code:xml}\
...\
&lt;capabilities&gt;\
  &lt;capability name="cmscap" file="jadex.bdi.planlib.cms.CMS" /&gt;\
  ...\
&lt;/capabilities&gt;\
...\
&lt;goals&gt;\
  &lt;achievegoalref name="cms\_search\_components"&gt;\
    &lt;concrete ref="cmscap.cms\_search\_components" /&gt;\
  &lt;/achievegoalref&gt;\
  ...\
&lt;/goals&gt;\
...\
{code}\
\~Including the CMS capability and the cms\_search\_components-goal\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

To search for components in your plan use the goal in the following manner:

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  CMSComponentDescription desc = new CMSComponentDescription(new ComponentIdentifier("a1", true));\
  IGoal search = createGoal("cms\_search\_components");\
  search.getParameter("description").setValue(desc);\
  * search.getParameter("cms").setValue(cms);  * Set cms in case of remote platform\
  dispatchSubgoalAndWait(search);\
  CMSComponentDescription\[\] result = (CMSComponentDescription\[\])\
  search.getParameterSet("result").getValues();\
  ...\
}\
{code}\
\~Searching a component on a local/remote platform\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the listing above - in order to search for a component - you instantiate a\
new goal using the \~createGoal()\~-method with the paramter\
"cms\_search\_components". The search is constrained by an CMS component description that need to be\
provided. You could e.g. create an \~CMSComponentDescription\~ with a\
new \~ComponentIdentifier\~ and the\
boolean \~true\~ for a local component as parameter, that is defined only by its name.\
Then you set its description-parameter to that just created \~CMSComponentDescription\~,\
dispatch the subgoal and wait for success. Supplying an empty component description\
with component identifier of null allows to perform an unconstrained search, i.e.\
returning all components on the platform.\
In case of a remote request you have to set the component identifier\
of the remote CMS well.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The documentation of the predefined capabilities is not yet finished.\
Please also take a look at the \[BDI Tutorial&gt;BDI Tutorial.07 Using Events\] (Exercise F4)\
and at the \[legacy documentation of Jadex 0.96&gt;<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.informatik.uni-hamburg.de/docs/jadex-0.96x/userguide/predef\_cap.html</span>](http://jadex.informatik.uni-hamburg.de/docs/jadex-0.96x/userguide/predef_cap.html)</span>\].

<div class="wikimodel-emptyline">

</div>

&lt;!~~\
The same goal is used to search for remote components:~~

<div class="wikimodel-emptyline">

</div>

{code:java}\
public void body()\
{\
  ...\
  IComponentIdentifier cms = ...\
  CMSComponentDescription desc = new CMSComponentDescription(new ComponentIdentifier("my\_component@myplatform"));\
  IGoal search = createGoal("cms\_search\_components");\
  search.getParameter("description").setValue(desc);\
  search.getParameter("cms").setValue(cms);\
  dispatchSubgoalAndWait(search);\
  CMSComponentDescription\[\] result = (CMSComponentDescription\[\])\
    search.getParameterSet("result").getValues();\
  ...\
}\
{code}\
\~Searching for a component on a remote platform\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the above listing a component with the name "my\_component" is sought-after.\
Assuming that the remote CMS was created as per description in section \[Creating Components&gt;\#HCreatingComponents\], you have to\
create the \~CMSComponentDescription\~ with a new \~ComponentIdentifier\~ as parameter, that is defined only by its name. Afterwards,\
you must instantiate the "cms\_destroy\_component"-goal by using the\
\~createGoal()\~-method with the parameter "cms\_search\_components".

<div class="wikimodel-emptyline">

</div>

After dispatching the goal and waiting for success, you can fetch the result by calling\
\~getParameterSet("result").getValues()\~ on the goal and casting to an array of\
CMS-component-descriptions. If no matching components were found, the resulting array will be empty.\
~~&gt;~~