<span>Chapter 3 - Active Components</span> 
==========================================

The active component approach brings together agents, services and components in order to build a worldview that is helpful for modelling and programming various classes of distributed systems. Recently, with the service component architecture (<span class="wikiexternallink">[SCA](http://www.osoa.org/display/Main/Service+Component+Architecture+Home)</span>) a new software engineering approach has been proposed by several major industry vendors including IBM, Oracle and TIBCO. SCA combines in a natural way the service oriented architecture (SOA) with component orientation by introducing SCA components communicating via services. Active components build on SCA and extend it in the direction of sofware agents. The general idea is to transform passive SCA components into autonomously acting service providers and consumers in order to better reflect real world scenarios which are composed of various active stakeholders. In the figure below an overview of the synthesis of SCA and agents to active components is shown.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

![03 Active Components@ac.png](ac.png)

*Active Component Structure*

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The figure presents on the right hand side the structure of an active component. It yields from conceptually merging an agent with an SCA component (shown at the left hand side). An agent is considered here as an autonomous entity that is perceiving its environment using sensors and can influence it by its effectors. The behavior of the agent depends on its internal reasoning capabilities ranging from rather simple reflex to intelligent goal-directed decision procedures. The underlying reasoning mechanism of an agent is described as an agent architecture and determines also the way an agent is programmed. On the other side an SCA component is a passive entity that has clearly defined dependencies with its environment. Similar to other component models these dependencies are described using required and provided services, i.e. services that a component needs to consume from other components for its functioning and services that it provides to others. Furthermore, the SCA component model is hierarchical meaning that a component can be composed of an arbitrary number of subcomponents. Connections between subcomponents and a parent component are established by service relationships, i.e. connection their required and provided service ports. Configuration of SCA components is done using so called properties, which allow values being provided at startup of components for predefined component attributes. The synthesis of both conceptual approaches is done by keeping all of the aforementioned key characteristics of agents and SCA components. On the one\
hand, from an agent-oriented point of view the new SCA properties lead to enhanced software engineering capabilities as hierarchical agent composition and service based interactions become possible. On the other hand, from an SCA perspective internal agent architectures enhance the way how component functionality can be described and allow reactive as well as proactive behavior.

BEGIN MACRO: toc param: start="2" depth="2" END MACRO: toc

<span>Exercise B1 - XML Component Definition</span> 
---------------------------------------------------

The figure above has highlighted the important properties of an active component from a black-box perspective, i.e. for now we do not care abouts its internal behavior definition. Instead, in this lecture we will learn how a component can be defined and started in Jadex. For this purpose we resort to a component xml description, which follows the XML-schema definition for active components. It basically contains the elements shown above, but as first step we will only use the most basic aspects:


```xml

<?xml version="1.0" encoding="UTF-8"?>
<!-- Chat component. -->
<componenttype xmlns="http://jadex.sourceforge.net/jadex"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://jadex.sourceforge.net/jadex
                      http://jadex.sourceforge.net/jadex-component-2.1.xsd"
  name="ChatB1" package="tutorial">

</componenttype>

```


*Basic chat component definition*

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Open a source code editor or an IDE of your choice and create a new component definition file called *ChatB1.component.xml* (cf. figure above). We recommend using eclipse for Java EE for editing files or some other advanced XML-Editor. In this file all important startup properties are defined in a way that complies to the Jadex schema specification. First property of the component is its type name which must be the same as the file name (similar to Java class files), in this case it is set to *ChatB1.component.xml*. Additionally you can specify a package attribute, which has a similar meaning as in Java programs and serves for grouping purposes only (you will need to alter the package name with respect to your actually used directory structure). All Java classes from the component's package are automatically known to it and need not to be imported via an import tag.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

**Start your first active component**\
Start the Jadex platform. In the Jadex Control Center (JCC) use the "Add Path" button explained above to add the root directory of your example package (typically named bin or classes). Then open the folder until you can see your file "ChatB1.component.xml". The effect of selecting the input file is that the component model is loaded. The filename extension *component.xml* is used by the Jadex platform to determine which kind of component it is loading. When the model contains no errors, the description of the model, taken from the XML comment above the componenttype tag, is shown in the description view. In case there are errors in the model, correct the errors shown in the description view and press reload. Below the file name, the component name and its default configuration are shown. After pressing the start button the new component should appear in the component tree (at the bottom left). It is also possible to start a component simply by double-clicking it in the model tree. *Please note that when you use a double-click on the model name in the left tree view to start a component, the settings on the right will be ignored.*

<span>Exercise B2 - Java Component Definition</span> 
----------------------------------------------------

There are various Jadex active component types such as applications, BPMN workflows, micro and BDI agents. Most of these component types are XML-based. These types all share the same XML descriptor format introduced above and extend it in certain directions. In constrast, micro agents are defined using Java only. In order to equip such Java based components with active component specifics the *Java annotation mechanism* can be used. Annotations are meta-information placed in to Java source code starting with '*@*'. The meta-information can be used by tools or frameworks (like Jadex) for dealing with the Java objects in a special way.


```java
package tutorial;
import ...

/**
 *  Chat micro agent. 
 */
@Description("This agent offers a chat service.")
@Agent
public class ChatB2Agent
{
}
```
\
*Basic chat micro agent definition*

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Use your IDE to create a Java class called *ChatB2Agent.java* and add the annotations as shown in the figure above. In this case the component only posses a description that will be displayed in the JCC and the marker annotation @Agent. The Java comment cannot be used directly as Jadex operates on class files, in which the Java comments are not retained. Please further note that it is also required to follow a naming convention which requires that all micro agent files end with *Agent.java*. Start the micro agent following the same steps as explained in excercise B1 and verify that it appears at the component tree at the lower left panel in the Starter. You can kill a component by rightclicking on it to activate a popup menu and choosing *Kill component*.

 
