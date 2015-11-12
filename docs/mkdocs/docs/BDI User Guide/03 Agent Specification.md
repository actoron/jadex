The BDI programmer's guide is a reference to the concepts and constructs available, when programming Jadex agents. It is not meant as a step-by-step introduction to the programming of Jadex agents. For a step-by-step introduction consider working through the \[BDI Tutorial&gt;BDI Tutorial.01 Introduction\]\
\
1.1 Overview

To develop applications with Jadex, the programmer has to create two types of files: XML agent definition files (ADF) and Java classes for the plan implementations. The ADF can be seen as a type specification for a class of instantiated agents. For example Buyer agents (from the booktrading example) are defined by the Buyer.agent.xml file, and use plans implemented, e.g. in the file PurchaseBookPlan.java. The user guide describes both aspects of agent programming, the XML based ADF declaration and the plan programming Java API, and highlights the interrelations between them. Detailed reference documentation for the XML definition as well as the plan programming API is also separately available in form of the generated XML schema documentation and the generated Javadocs. Figure 1 depicts how XML and Java files together define the functionality of an agent. To start an agent, first the ADF is loaded, and the agent is initialized with beliefs, goals, and plans as specified.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

![](jadexagent.png)

\~Figure 1: Jadex BDI agent components\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Please note that different languages are used for the agent specification in the ADF. For all \~values\~ that should be created the \~Jadex expression language\~ is used. This language is very similar to Java and extends it with a small set of OQL statements. These basically allow you to use \~select\~ statements for fetching specific data in a declarative way. On the other hand there is the \~Jadex condition language\~, which is used for most of the conditions (e.g. a target condition of an achieve goal). This language is also very Java like and introduces also some small extensions. Note that it is (in most cases) not used for retrieving a value, but for signalling a specific situation. There are some elements called conditions, which are not real conditions but checked only once. These elements require the usage of the Jadex expression language (precondition of a plan). In most cases the differences between both should not be really apparent due to their Java similarity.

1.1 Structure of Agent Definition Files (ADFs)

{code:xml}\
&lt;agent xmlns="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi</span>](http://jadex.sourceforge.net/jadex-bdi)</span>"\
       xmlns:xsi="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://www.w3.org/2001/XMLSchema-instance</span>](http://www.w3.org/2001/XMLSchema-instance)</span>"\
       xsi:schemaLocation="<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi</span>](http://jadex.sourceforge.net/jadex-bdi)</span>\
                           <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex.sourceforge.net/jadex-bdi-2.0.xsd</span>](http://jadex.sourceforge.net/jadex-bdi-2.0.xsd)</span>"\
  name="Buyer" package="jadex.bdi.examples.booktrading.buyer"&gt;\
  ...\
&lt;/agent&gt;\
{code}\
\~Figure 2: Header of an agent definition file\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The head of an ADF looks like shown in Figure 2. First, the agent tag specifies that the XML document follows the jadex-2.0.xsd schema definition which allows to verify that the document is not only well formed XML butalso a valid ADF. The name of the agent type is specified in the name attribute of the agent tag, which should match the file name without suffix (&lt;filename&gt;.agent.xml&lt;/filename&gt;). It is also used as default name for new agent instances, when the ADF is loaded in the starter panel of the Jadex Control Center. The package declaration specifies where the agent first searches for required classes (e.g., for plans or beliefs) and should correspond to the directory, the XML file is located in. Additionally required packages can be specified using the \\&lt;imports\\&gt; tag. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

 \
![](jadexagentadf.png)

\~Figure 3: Jadex top level ADF elements\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

\
Figure 3 above shows which elements can be specified inside an agent definition file (please refer also to the commented schema documentation generated from the schema itself in \[BDI Schema Documentation&gt;<span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://jadex-agents.informatik.uni-hamburg.de/docs/jadex-2.0x/kernel-bdi/schema/jadex-bdi-2.0.html</span>](http://jadex-agents.informatik.uni-hamburg.de/docs/jadex-2.0x/kernel-bdi/schema/jadex-bdi-2.0.html)</span>\]. The \\&lt;imports\\&gt; tag is used to\
specify, which classes and packages can be used by expressions throughout the ADF. To modularize agent functionality, agents can be decomposed into so called capabilities. The capability specifications used by an agent are referenced in the \\&lt;capabilities\\&gt; tag. The core part of the agent specification regards the definition of the beliefs, goals, and plans of the agent, which are placed in the \\&lt;beliefs\\&gt;, \\&lt;goals\\&gt;, and \\&lt;plans\\&gt; tag, respectively. The events known by the agent are defined in the \\&lt;events\\&gt; section. The \\&lt;expressions\\&gt; tag allows to specify expressions and conditions, which can be used as predefined queries from plans. The \\&lt;properties&gt;\\ tag is used for custom settings such as debugging and logging options. Finally, in the \\&lt;configurations\\&gt; section, predefined configurations containing, e.g., initial beliefs, goals, and plans, as well as end goals and plans are specified.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

It should be noted that, unless otherwise stated, the order of occurrence of the elements is prescribed by the underlying XML Schema. Therefore, you should not, e.g., declare plans before beliefs. Throughout this user guide figure like above will always denote the correct order of element appearence (from top to bottom). Of course, it is possible to omit those elements, which are not required for your agent.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

When an ADF is loaded, a model is created for the agent containing (e.g., beliefs, goals, plans) defined in the ADF. These model elements are kept hidden from the agent programmer who has access to the runtime elements only. In Jadex V2 the model is assumed to be unchangeable so that accessing model data should not be necessary (in case information is needed from the model the corresponding runtime elements should provide accessor methods to the needed info). When the agent is executed, instances of the model elements are created; so called runtime elements (package jadex.bdi.runtime, e.g., IBelief, IGoal, IPlan). This ensures that for modelled elements at runtime several instances (IGoal objects) can be created. For example, the buyer agent will instantiate new purchase book goal (IGoal) for each book to be bought, based on the goal specification in the ADF. Think of the relation between model elements and runtime elements as corresponding to the relation between java.lang.Class and java.lang.Object. 
