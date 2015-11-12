1 Component Interaction

In this section the interaction concepts between components and space objects are described. The presented elements are part of the space environment type xml part as introduced in section \[Domain Model&gt;03 Domain Model\]. For convenience the relevant cutout of the xml schema is shown again below.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

![](interaction.png)

\~Interaction xml schema part of the environment space type\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

1.1.1 Avatar Mapping

![](avatarmapping.png)

![](avatarmappingattributes.png)

\~Avatar mappings xml schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The avatar mapping is used to define the relation between space objects (avatars) and components (i.e. agents). This relationship determines what happens when a new space object of a specific type is created/deleted and on the other side what happens when a component is created/deleted. The connection between both can be establisched to mimic the behaviour from one side on the other side, e.g. create also an avatar (a space object) when a new component is created. The default values for the four flags \~createavatar\~, \~killavatar\~, \~createcomponent\~, \~killcomponent\~ are defined in a way that the component side dominates the simulation world, i.e. whatever happens to a component is also done with its avatar in the simulation. If the creation and deletion of space objects should also lead to the creation and deletion of components this has to be explicitly set with the corresponding flags.

The code snippet below shows a fictitious robot example, which creates robot agents whenever a new robot space object is created. Also, for each newly created robot agent an avatar is initiated automatically.

{code:xml}\
&lt;env:avatarmapping componenttype="Robot" objecttype="robot" createcomponent="true"/&gt;\
{code}\
\~Avatar mapping example\~

1.1.1 Percept Types

![](percepttypes.png)

\~Percept types xml schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Percepts are meaningful events for components, i.e. a percept can be seen as some kind of environment event that is adressed towards a specific kind of component. This means that different kinds of components may receive completely different percepts for the same observed events. Hence, a percept is a concept that connects a component with the environment. For this reason percept types include several aspects in EnvSupport. On the one hand, the basic kinds of percepts can be defined (inner percept types tag). On the other hand percept generators and processors need to be specified. A percept generator is responsible for creating percepts (based on the available percept types) and thus attached to the environment. These percepts are then consumed by components using percepts processors, which may depend on the concrete component type. E.g. a bdi agent percept processor can directly store newly arriving percepts in fitting beliefs or beliefsets.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

A percept type is described using a \~name\~ attribute for percept type identification. Using the \~objecttype\~ attribute (or tags for multiple types) the underlying meaning of the percept can be defined. The \~componenttype\~ attribute (or tags for multiple types) is useful for specifying the component types that can generally perceive such kind of percept. Property tags can be employed for parametrization as for most other elements. 

1.1.1.1 Percept Generators

A percept generator is defined using \~name\~ and implementation \~class\~ attributes. An implemeting class has to extend the \~IPerceptGenerator\~ interface. Currently, with the \~DefaultVisionGenerator\~ (package jadex.application.space.envsupport.environment.space2d) a quite powerful default implementation for a percept generator, capable of creating vision range dependent percepts, exists. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{code:java}\
&lt;env:perceptgenerator name="visiongen" class="DefaultVisionGenerator"&gt;\
  &lt;env:property name="range"&gt;0.1&lt;/env:property&gt;\
  &lt;env:property name="range\_property"&gt;"vision\_range"&lt;/env:property&gt;\
  &lt;env:property name="percepttypes"&gt;\
    new Object\[\]\
    {\
      new String\[\]{"cleaner\_moved", "moved"},\
      new String\[\]{"waste\_appeared", "appeared", "created"},\
      new String\[\]{"waste\_disappeared", "destroyed"},\
      new String\[\]{"wastebin\_appeared", "appeared", "created"},\
      new String\[\]{"wastebin\_disappeared", "destroyed"},\
      new String\[\]{"chargingstation\_appeared", "appeared", "created"},\
      new String\[\]{"chargingstation\_disappeared", "destroyed"}\
    }\
  &lt;/env:property&gt;\
&lt;/env:perceptgenerator&gt;\
{code}\
\~Default vision generator example from cleanerworld example\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the code snippet above an example for a vision generator specification is shown. The configuration of the default vision generator in done using the \~range\~, \~range\_property\~ and \~percepttypes\~ properties. The range determines the radius of the vision the avatar can perceive (all elements within the radius can be seen). In order to find out what range to use the following steps are performed: 1) try to get the range as property from the avatar using the range\_property (if not specified "range" ist tried) 2) if no range value could be obtained the default range is used by fetching the \~range\~ property of the vision generator. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The percept types are defined using String arrays of the form of a perceptname and an arbitrary number of actionnames. In order to find the correct percept type for a specific event consisting of \~componenttype\~, \~objecttype\~ and \~actiontype\~ the percepttype defined in the space is fetched and it is checked if the componenttype and objecttype of the event fit to those of the percepttype. Thereafter, the actiontype of the event is compared with those declared in the vision processor. Please note the the default vision processor supports the following action types:

-   \*created:\* A space object has been created.
-   \*destroyed:\* A space object has been destroyed.
-   \*appeared:\* A space object came into the vision range and was not seen before.
-   \*disappeared:\* A space object moved out the vision range and was seen before.
-   \*moved:\* A space object changed its position. 

1.1.1.1 Percept Processors

A percept processor is specified using \~componentype\~ and implementation \~class\~ attributes. The first is used to define in component types the percepts shall be injected. The latter has to extend the \~IPerceptProcessor\~ interface shown below.

{code:java}\
package jadex.application.space.envsupport.environment;

public interface IPerceptProcessor extends IPropertyObject\
{\
  public void processPercept(IEnvironmentSpace space, String type,\
    Object percept, IComponentIdentifier component, ISpaceObject avatar);\
}\
{code}\
\~Percept processor interface\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

For BDI agents a default implementation called \~DefaultBDIVisionProcessor\~ exists.

{code:java}\
&lt;env:perceptprocessor componenttype="Cleaner" class="DefaultBDIVisionProcessor" &gt;\
  &lt;env:property name="percepttypes"&gt;\
    new Object\[\]\
    {\
      new String\[\]{"cleaner\_moved", "remove\_outdated", "wastes"},\
      new String\[\]{"waste\_appeared", "add", "wastes"},\
      new String\[\]{"waste\_disappeared", "remove", "wastes"},\
      new String\[\]{"wastebin\_appeared", "add", "wastebins"},\
      new String\[\]{"wastebin\_disappeared", "remove", "wastebins"},\
      new String\[\]{"chargingstation\_appeared", "add", "chargingstations"},\
      new String\[\]{"chargingstation\_disappeared", "remove", "chargingstations"}\
    }\
  &lt;/env:property&gt;\
&lt;/env:perceptprocessor&gt;\
{code}\
\~Default vision processor example from cleanerworld example\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the code snippet above an example for a vision processor specification is shown. The configuration of the default vision processor is done using the \~range\~, \~range\_property\~ and \~percepttypes\~ properties. The first two range properties are only of importance for the "remove\_outdated" action as here the objects that are not seen any longer need to be determined. The concrete range is determined in the same way as explained in the text of vision generators. The syntax for specifying the percepttypes is \~perceptname, action, belief(set)name, conditionname\~. It means that the action will be executed when the named percept occurs and the condition evaluates to true. The condition itself has to be spercified as named property of the vision processor. The available actions are shown below.

-   \*add:\* Add a percept to a beliefset.
-   \*remove:\* Remove a percept to a beliefset.
-   \*remove\_outdated:\* The remove\_outdated action checks all entries in the belief set, if they should be seen, but are no longer there.
-   \*set:\* Set the percept as fact of a belief.
-   \*unset:\* Set a belief to null.

1.1.1 Action Type

![](actiontype.png)

\~Action type xml schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Action types are used for specifying which kinds of action can be issued on an environment. It is also uniquely defined by a type \~name\~ and additionally an action \~class\~. This action class has to implement the \~ISpaceAction\~ interface (package 'jadex.application.space.envsupport.environment') and is itself an extension of the \~IPropertyObject\~ (package 'jadex.commons') interface. The interface ISpaceAction only contains one method that has to be implemented by all action types. It is named \~perform(Map parameters, perform(Map parameters, IEnvironmentSpace space)\~ and should contain the procedural code for the action. It has access to the space itself via the \~IEnvironmentSpace\~ interface (you can cast if you need a concrete subclass) and to a map of action type specific parameters (name - value pairs). The \~IPropertyObject\~ interface requires the action type to have getter and setter methods for properties. This allows a very flexible way of action type configuration from the xml. The declared property values in the xml can directly be accessed via the \~getProperty(String name)\~ method.
