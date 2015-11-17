1 Environment Instance

This section describes how an environment space instance can be created based on an environment space type definition. Space instance definitions are part of the application instance section in the application descriptor. Basically, in the instance part arbitrary many elements of the defined types can be created. In addition, it is possible to set-up evaluations, which can be used to collect and process data from the environment. In the Figure below the corresponding cutout of the XML schema is shown.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

![](envspace.png)

\~The environment instance schema definition\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

An environment space has two mandatory attributes. The \~name\~ is used for identifying the space instance and the \~type\~ relates the environment space type that is the basis for the space instance. Additionally, the original \~width\~, \~height\~ and \~depth\~ attributes of the space type can be overridden at the instance level via attributes. Furthermore, space \~property\~ elements can be defined for storing arbitrary user data. The additional elements - objects, processes, data providers, data comsumers and observers - are described in the following sections.

1.1 Objects

![](object.png)

\~Object schema definition\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

An object in an instance of a space object type. It is defined using the \~type\~ attribute that must relate to an existing object type. For convenience, it is possible to specify the \~number\~ attribute, which has the effect that moew than one object instances are instantiated with a single object declaration. If necessary \~property\~ tags can be used as usual for further object customization.

1.1 Processes

![](process.png)

\~Process schema definition\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

A process instance is defined very similar to an object. In the same way the \~type\~ attribute has to used to declare the underlying process type. Also properties can again be used for passing further parameter values to the process instance.

1.1 Observers

![](observer.png)

\~Observer schema definition\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

An observer represents the user interface part of the simulation environment. It is configured using mandatory attributes for the \~name\~, \~dataview\~ and \~perspective\~. Please note that the dataview and perspective attributes only represent the default settings of the observer window. The user can change these at runtime as she likes. Additionally, the \~killonexit\~ flag can be used to determine, if the application should be killed when the gui is closed. The observer offers an extension mechanism that allows the user interface being modified by adding new plugins. A plugin is represented with its own button on the top left of the observer. When activated the plugin can display its own view on the control area (lower left). A plugin is defined using \~name\~ and \~class\~ attributes. Plugin classes have to implement the \~jadex.application.space.envsupport.observer.gui.plugin.IObserverCenterPlugin\~.\
The interface is shown below and basically has methods for start and shutdown as well as fetching information such as name, icon and the view to display. 

{code:java}\
package jadex.application.space.envsupport.observer.gui.plugin;

public interface IObserverCenterPlugin\
{\
  public void start(ObserverCenter main);\
  \
  public void shutdown();\
  \
  public String getName();\
\
  public String getIconPath();\
\
  public Component getView();\
\
  public void refresh();\
}\
{code}\
\~IObserverCenterPlugin interface\~ 

The evaluation of environment data is done by specifying data providers and data consumers. A data provider can be used to define which data should be collected and a data consumer then can operate on this collected data in order to process it in different ways. 

1.1 Data Providers

![](dataproviders.png)

\~Data provider schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

A data provider is similar to a database query, which is executed once every step the spaceexecutor performs. This means a data provider does not store a history of entries but only provides the data that has been collected at the current point in time. In order to use a data provider from data consumers it is necessary to equip it with a specific \~name\~ attribute. The query itself is specified using arbitrary many \~source\~ and \~data\~ elements. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

A named \~source\~ element determines which space objects are of general interest for the query (corresponds to the 'from' part of an SQL query). It requires to state, which space objects of an underlying \~objecttype\~ should be considered. As possibly not all objects of a given type should be included an \~includecondition\~ can be employed to explicitly state what dynamic property an object has to posses in order to be considerd. If not the object itself is of primary interest for the query but only one of its properties this can be expressed using an expression inside the tag. Using the \~aggregate\~ flag it is possible to determine if the source data is interpreted as multiple elements (for each included object) or as one element (list of objects). The result data is calculated via a join over all sources, i.e. the cartesian product of all source elements (similar to the 'select' of an SQL query). An aggregated source provides always only one element to the join.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Finally, the concrete data model is specified using columns similar to a relational table model. With each \~data\~ tag one named column is defined (\~name\~ attribute). In the tag content an arbitrary Java expression can be specified for stating the desired property that should be recorded. The objects from the different sources are available via predefined variables directly using the source name. In addition, the predefined variables '\$time' and '\$tick' are available as time series represent an often occurring use case.

1.1 Data Consumers

![](dataconsumers.png)

\~Data consumers schema part\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Data consumers can be used to process data in various ways. As data consumers may be very different, the configuration is kept very general. A data consumer is specified using the \~name\~ and \~class\~ attributes. The class has to extend the interface \~jadex.application.space.envsupport.evaluation.ITableDataConsumer\~, which is shown below. 

{code:java}\
package jadex.application.space.envsupport.evaluation;

public interface ITableDataConsumer extends IPropertyObject\
{\
  public void consumeData(long time, double tick);\
}\
{code}\
\~Interface ITableDataConsumer\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

The interface contains the method \~consumeData()\~, which is automatically called in each execution step (determined by the space executor). The method then typically has the purpose to fetch the data from a (or more) connected data provider(s) and process it in a specific way, e.g. visualize it as chart. The connection to a data provider as well as other characteristics are defined using \~property\~ tags. Predefined data consumers available in the distribution are TimeChartDataConsumer, XYChartDataConsumer, HistogramDataConsumer, CategoryChartDataConsumer and a CSVFileDataConsumer. In the following the XYChartDataConsumer, the HistogramDataConsumer and the CSVFileDataConsumer will be described in more details. The other consumers are very similar to the XYChartDataConsumer and only differ in the way the x-axis is interpreted.

1.1.1 XYChartDataConsumer

The xy chart comsumer can be used to display arbitrary many chart series in an area. The class of this consumer is \~jadex.application.space.envsupport.evaluation.XYChartDataConsumer\~. The following table shows the properties that can be specified for a xy chart data consumer. It is based on the JFreeChart class xy line chart.

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{table}\
name| class| description\
dataprovider| String| The name of the data provider to use.\
title| String| The title of the chart consumer\
labelx| String| The label for the x-axis\
labely| String| The label for the y-axis\
bgimage| String| The filname of a background image.\
maxitemcount| int| The maximum number of items kept. If the max is reached the oldest entries are removed.\
autorepaint| boolean| True for automatic repaint on changes (possibly slow if case of frequent changes).\
{table}\
\~Basic chart properties\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

{table}\
name| class| description\
seriesid| String| The name of the id property. If an id is used it is a multi series definition.\
seriesname| String| The name of the series.\
valuex| String or IExpression (then dynamic must be true)| The property name for x value fetching or an expression for evaluation.\
valuey| String or IExpression (then dynamic must be true)| The property name for y value fetching or an expression for evaluation.\
{table}\
\~Series chart properties\~

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

Please note that it is possible to define a \~normal series\~ and a \~multi series\~. A normal series is identified by a series name, whereas a multi series consists of a multitude of series that are identified by the 'id' property. In case of a multi series for each 'id' value (fetched by the id property name) a separate chart curve is drawn.\
If several normal series should be used they should follow a name scheme with appended '\_\\&lt;no\\&gt;', e.g. seriesname\_0, valuex\_0 and valuey\_0, seriesname\_1 etc.

1.1.1 HistogramDataConsumer

1.1.1 CSVFileDataConsumer
