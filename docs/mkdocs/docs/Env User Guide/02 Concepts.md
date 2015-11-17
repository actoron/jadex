\$xwiki.ssx.use("XWiki.Lightbox")\
\$xwiki.jsx.use("XWiki.Lightbox")\
&lt;a href="\$doc.getAttachmentURL('envsupportconcepts.png')" rel="lightbox" title="Environment Concepts"&gt;\
![](envsupportconceptssmall.png)

&lt;/a&gt;\
\~Figure 1: Main conceptual building blocks of EnvSupport\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

In the figure above the conceptual parts of EnvSupport and their interplay is shown. It consists of the parts \~spaces\~, \~agents\~, \~observers\~ and \~evaluation\~. The environment space mainly contains the \~domain model\~ and the \~interaction\~ specification. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

1.1 Space

In the domain model the constituents of the space are declared. In EnvSupport these constituents are so called \~space objects\~, which may represent arbitrary artifacts of the described world. This may include passive as well as active objects, whereby the activity can be exerted on the objects internally as well as externally. Internally space objects can be modified by \~tasks\~ and \~processes\~. A task is directly associated to a space object and encapsulates the object manipulation logic. In contrast, a process is not directly connected to a specific space object, but has instead a global scope and access to all objects of the space. A typical example for a task is a move function, which continuously changes the object's location based on the elapsed time and the current speed and direction. Considering processes, an example is heat dissipation, which distributes the heat on the landscape according to some physical formula. Besides the space objects, tasks and processes also \~data views\~ can be specified. A data view is a definable cutout of the model world and hence similar to a database view in relation to the database. Currently, it is used to transfer a specific view of the world to dedicated observers, which can use these data for presentation purposes. Examples for data views are the built-in complete view representation the whole world and local avatar view, which contains only object in the vision range of the object.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The interaction part serves for the specification of how the interrelationship between agents and space objects is. In EnvSupport space objects which are representatives for agents in the virtual world are called \~avatars\~. The connection between such avatars and agents are specified in so called \~avatar mappings\~. It defines what happens when an agent or an avatar is created or destroyed. Both sides of an agent avatar association can be tied together, so that the creation of an agent automatically leads to the creation of its avatar. The same applies for the other direction, which means that the creation of an avatar also can initiate the creation of an associated agent. Such kind of connections can also be set-up for the destruction of agents or avatars. The communication between agents and the environment is organized via \~percepts\~ and \~actions\~. A percept is a meaningful event created in the environment and directed towards specific kinds of agents. In this way information is passed on a semantic level to the agents. The creation of percepts is performed by \~percept generators\~, which can react on basic environment events, like the movement of a space object, and transform them to percepts. These percepts are then fed into the fitting \~percept processors\~, which have the task to interact with the agent and inform it about the new percept. On the other hand agents can influence the environment by executing \~actions\~ on it. These actions are predefined in the environment space and may modify arbitrary environmental properties and objects. The execution can directly be performed by an agent on the space.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

As described until now, a space represents a passive entity. In order to allow also active environments containing tasks and processes, a \~space executor\~ is used. Such a space executor is typically coupled to the clock of the platform and performs some kind of "execution cycle" at each time step. One aspect of this cycle is the processing of percepts and actions and executing the tasks and processes. Hence, a space executor encapsultes the environment execution semantics and can e.g. implement a round-based or a continuous execution scheme. 

1.1 Observers

So far the internals of an environment have been considered. Observers represent user interfaces for watching an environment. It typically allows for viewing the current state of the environment and its objects. The definition of observers include two main concepts: \~data views\~ and \~perspectives\~. A \~data view\~ is a cutout of the environment, which represents the view of a specific entity. One example is the local view of an object, which is e.g. defined by the radius of its sight. Another kind of view is the global data view encompoassing the complete environment data model. Besides the selection of the data that should be presented also the way of its presentation is of importance. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The presentation of the data can be defined using \~perspective\~. A perspective is composed of \~drawables\~, which describe the way a space object will be rendered. Typical types of drawables include geometrical shapes like triangle, rectangle, circle and also image icons. The drawables are organized in a simplified version of a \~scenegraph\~. This means that in a drawable multiple shapes can be combined in arbitrary flavors. In addition to the relative placement and sizes of these drawable parts \~drawconditions\~ can be used to determine if a part is currently visible. This allows to visualize special parts of a drawable depending on its current environmental conditions (e.g. using different icons for different movement directions). A perspective is organized in layers to which drawables can be assigned. The layers determine the order in which the elements are painted, so that earlier painted drawables may be partially or completely hidden by elements of a higher layer. In addition to drawables so called \~pre- and post-layers\~ can be defined. These can be used to e.g. show background or foreground elements that are not necessarily backed by space objects.  

1.1 Evaluation

In addition to the space itself and its visualization, an optional evaluation can be set up. An evaluation allows for collecting specific data of the space and processing it further. The collection of data is specified via \~data providers\~, which make use of a table based data format similar to relational databases. For this purpose data sources and data elements can be specified. The sources are typically space objects or collections of space objects. These sources are joined (the cartesian product is calculated) and for each combination of source objects a new data row is produced. The row consits of the defined data elements (e.g. attributes of objects). For each relevant time point the data provider can generate such a data table. Data providers are typically connected to data processors, which use them for fetching the input data. Data processors can come in very different flavors, from a simple file writer to graphical output generators for charts or histograms. The specification of data providers heaily depends on their concrete type so that the main commonality is the data source given by the data provider. 
