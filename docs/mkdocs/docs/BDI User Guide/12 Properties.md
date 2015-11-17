<span>Chapter 12. Properties</span> 
===================================

This chapter contains an overview about the usage of agent and capability properties, that allow to change the behavior of the agent. In general, properties represent static expressions, i.e. they are interpreted but only once when an agent instance is loaded. Properties can be defined using the properties section of the agent (and capability) XML file and add an arbitrary number of properties.

![](jadexpropertiesadf.png)

*Propertybase xml specification* 

 \
The table below gives an overview of all available properties. The scope denotes, if the property can only be specified for the agent as a whole, or can be adjusted to different values for individual capabilities.

<div class="wikimodel-emptyline">

</div>

  Scope        Property                    Default     Possible Values
  ------------ --------------------------- ----------- -----------------------------------
  agent        max\_planstep\_time         unlimited   Positive long or 0 for unlimited
  agent        storedmessages.size         unlimited   Positive int or 0 for unlimited
  agent        debugging                   false       {true, false}
  capability   logging.level               SEVERE      java.util.logging.Level instances
  capability   logging.useParentHandlers   true        {true, false}
  capability   logging.addConsoleHandler   null        java.util.logging.Level instances
  capability   logging.level.exceptions    SEVERE      java.util.logging.Level instances

The Jadex sytem has to take care that only one plan step is executed at a time, therefore it waits until a plan step returns. With the help of the "max\_planstep\_time" property it is possible to set the maximum execution time for a single planstep in milliseconds. Per default the execution time is not limited and a plan might execute as long plan steps as it want to (note that long plan steps are not recommended, because they hinder the agent in responding to urgent events). A plan running longer than the maximum plan step time will be forcefully aborted by the system. This feature is only available for standard plans.

\
The "storedmessages.size" property can be used to restrict the number of monitored conversations. Generally, an agent has to keep track of its sent messages for being able to associate an incoming message to already sent messages. This means an agent has to know what it sent to determine if it received some reply of a previous message. When restricting the number of conversations, and a message arrives belonging to an ongoing conversation that was removed from the cache, the agent might not be able to route the message to the correct capability.

\
The "debugging" property influences the execution mode of the agent. When setting debugging to *true* the agent is halted after startup and set to single-step mode. You can then use the debugger tab of the introspector tool execute the agent step-by-step and observe its behavior.

<div class="wikimodel-emptyline">

</div>

The logging properties can be used to adjust the logging behavior according to the <span class="wikiexternallink">[Java Logging API]("http://java.sun.com/j2se/1.4/docs/guide/util/logging/overview.html")</span>. The level influences the amount of logging information produced by the agent (logging information below the level will be completely ignored). Setting "useParentHandlers" to "true" will forward logging information to the parent handler, which by Java default causes logging output up to the INFO level to be displayed on the console. If you want to direct more detailed logging output to the console use the "addConsoleHandler" property, which creates a custom logging handler for console ouput with the specified logging level. &lt;!-- More about logging settings can be found in &lt;xref linkend="JadexToolGuide"/&gt;.--&gt;

<div class="wikimodel-emptyline">

</div>

The "logging.level.exceptions" property can be used to specify the logging level for uncatched exceptions occurring in plan bodies. Using the default settings for logging (non-BDI specific) exceptions are printed out as SEVERE log messages to the console. You can adjust the level settings to suppress exception log messages from plans that you expect to throw exceptions. 

<div class="wikimodel-emptyline">

</div>

The following concrete subclasses of the abstract *jadex.bdi.runtime.BDIFailureException* may occur:

-   **jadex.bdi.runtime.GoalFailureException** A goal failure exception indicates that a goal could not successfully pursued. It is thrown by the reasoning engine when e.g. dispatchSubgoalAndWait() is called and the goal does not succeed. 
-   **jadex.runtime.PlanFailureException** Can be thrown from user code in plans for indicating that a normal plan failure has occurred. Also calling the fail() method will lead to throwing a plan failure exception.
-   **jadex.bdi.runtime.TimeoutException** Occurs, when any waitFor...() method is called with eexception of the basic waitFor(time) method, which will only block until the given time interval elpased.

The following figure shows an example property section setting logging and plan step options.


```xml

<properties> 
  <property name="logging.level">Level.WARNING</property>
  <property name="scheduler.max_planstep_time">5000</property>
</properties>

```


*Example properties section*
