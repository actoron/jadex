# Components

With Jadex, the behaviour of a software is defined by the interaction between components, each of them providing a clearly defined functionality.
 
When you create a new component, you have to choose between different component types. For now, we will focus on *Micro Agents*, the most basic type of component. For other component types, please refer to [Component Types](../component-types/component-types).

For a more complete guide into Active Components, take a look at the [AC User Guide](../guides/ac/01 Introduction).

# Implementation
Micro Agents are defined by plain java classes. In order for a java class to represent a Micro Agent, two requirements have to be met:

 - The name of the class has to end with "Agent" (e.g. ```MyAgent```, ```ChatAgent```, ...)
 - The class has to be annotated with the ```@Agent``` Annotation
  
Optionally, it can provide a description using the ```@Description``` Annotation. The value is then shown inside the [JCC](../tools/02 JCC Overview).

This leads to the following code for a basic micro agent:

```java
package tutorial;

@Agent
public class MyAgent
{
}
```

In order for you Agent to do something once it is started, you can provide an *Agent Body* method. This is an arbitrary named public method without parameters annotated with ```@AgentBody```:

```java
@AgentBody
public IFuture<Void> body()
{
    System.out.println("Hello World!");
}
```

<x-hint title="Futures">
Instead of the return type ```IFuture<Void>```, you can also use ```void```.
Using a *futurized* return type allows you to perform work asynchronously, which is handled in chapter [Futures](../futures/futures).
</x-hint>

# Startup

Starting of components is done by the Platform's ```ComponentManagementService``` (CMS). 
Service instances in general can be retrieved using the static methods of the [SServiceProvider](${URLJavaDoc}/jadex/bridge/service/search/SServiceProvider.html) class.

## Obtaining the CMS
Remember the ```IExternalAccess platform``` object that you got when starting a platform? It is now required to retrieve the CMS:

```java
IExternalAccess platform = Starter...
IFuture<> fut = SServiceProvider.getService(platform, IComponentManagementService.class);

IComponentManagementService cms = fut.get();
```

<x-hint title="Service interfaces">
Notice how we use the **interface** of the service we want to retrieve?
In Jadex, Java interfaces are used for the interaction with services, so the implementation can remain hidden.
</x-hint>

## Starting the component

Once you get a reference to the CMS, you can use the ```createComponent()``` methods to start your components (See API documentation of [IComponentManagementService](${URLJavaDoc}/jadex/bridge/service/types/cms/IComponentManagementService.html)).

The preferred method to start a component has the following signature:
```java 
ITuple2Future<...> createComponent(String name, String model, CreationInfo info);
```

You may provide a [CreationInfo](#creation-info) object (e.g., to pass parameters at startup) or a *name* for the created component instance, but most importantly, you have to provide a *model*, which in this case is simply the **fully qualified name** of the component class (or XML-file for other component types):

```java
ITuple2Future<...> fut = cms.createComponent("myAgent1", "tutorial.MyAgent.class", null);
IComponentIdentifier cid = fut.getFirstResult();
System.out.println("Started component: " + cid);
```

<x-hint title="Future types">
Notice how you get a different Future object this time?
A ```Tuple2Future``` represents a promise that two different results are going to be available. In this case, the first is the ```ComponentIdentifier```, which is used to identify the instantiated component. The second result is a Map that can be filled by the component and is only returned upon termination of the component. Take a look at [Futures](../futures/futures/#future-types) for different future types.
</x-hint>

Now that you know how to start your own components, you can read more about [Services](../services/services), as they provide a way for components to interact with each other.

## Destroying the component

To destroy a component, the CMS has to be used again. Call ```destroyComponent(cid)``` and pass the Component Identifier returned on component startup:
```java
Map<String,Object> results = cms.destroyComponent(cid).get();
```
If the component has any results, they are contained in the returned map.

## Component Arguments

### Declaring Arguments
Components can declare arguments that can be passed during creation.
To declare arguments, use the ```@Arguments``` Annotation:
```java
@Arguments(@Argument(name="myName", description = "Name of this agent", clazz=String.class, defaultvalue = "Hugo"))
public class MyAgent ...
```

To access this argument from inside the agent, inject it into a field using the ```@AgentArgument``` annotation:
```java
@AgentArgument
protected String myName;
```

<x-hint title="Agent Argument names">
Note that the field and argument name must match. If you want your field to have another name, specify the argument name as parameter when using the annotation:
```@AgentArgument("myName")```.
</x-hint>
Another way to access the arguments of an agent is by using the [IArgumentsResultsFeature](#component-features).

### Passing Arguments
When you created a component as explained above, the last parameter of```createComponent``` was null.
Instead, you can create your own ```CreationInfo``` object containing your component's arguments and pass it in *createComponent*:
```java
CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"myName"}, new Object[]{"Harald"}))
```
	
<!--TODO: Component results doc-->
<!--## Component Results-->
<!--### Passing Results-->
<!--### Receiving Results-->

# Component Features

All component functionalities are available via *features*.
By default, all components have a certain set of features, which can be injected into fields by using an annotation:
```java
@AgentFeature
IExecutionFeature exeFeat;
```

<!--final long delay = ((Number)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("delay")).longValue();-->

Below is a list of important features commonly available for all components.
For features specific to a component-type, take a look at [component types](../component-types/component-types).

| Feature Name | Description |
|--------------|-------------|
|IArgumentsResultsFeature| Provides access to agent arguments and can take agent results. |
|IExecutionFeature| Provides access to the execution model to schedule component steps and provide wait functions. |
|IMessageFeature| Handles sending and reception of messages between components. |
|IMonitoringComponentFeature| Components can publish monitoring events with this feature. |
|IRequiredServicesFeature | See [Services](../services/services#accessing-services) |
|IProvidedServicesFeature | See [Services](../services/services#accessing-services) |

<!-- TODO: defining custom component features -->
<!--You can even define new component features. Please refer to [TODO](TODO) to see how.-->

# Component Lifecycle
The Jadex Active Components Platform and the CMS implement a specific lifecycle for components. 
For each step in the cycle there is an annotation which can be used on methods to perform actions during the lifecycle step.
These annotations can be used on methods, like this:
```java
@AgentCreated
public IFuture<Void> agentCreated() {...
```

<x-hint title="Parameters">
All annotations also allow for methods with parameters, see [Parameter Guesser](#parameter-guesser).
</x-hint>

|Annotation | Description|
|-----------|------------|
| **@AgentCreated** | A method marked with this annotation will be called upon creation of the agent. This means services, injected fields etc. are not initialized at this point. |
|**@AgentBody** | A method marked with this annotation will be called after creation of the agent is complete. At this point, all fields and required services are available and can be used.|
|**@AgentKilled** | A method marked with this annotation will be called just before the component is removed from the platform.|  

# Advanced Topics

This section discusses some of the more advanced topics regarding components.

## More Annotations
The most important annotations common to all components were already discussed.
For a full reference, have a look at the [jadex.micro.annotation](${URLJavaDoc}/jadex/micro/annotation/package-summary.html) package.

<!--InternalAccess?-->
<!--| **@Agent** | fields | Injects the ```IExternalAccess``` of the component.|-->
<!--| **@Parent** | fields | TODO |-->

<!--## Messaging-->
<!--TODO: Messaging-->

<!--|Annotation|Description|Method declaration|-->
<!--|----------|-----------|------------------|-->
<!--| **@AgentMessageArrived** | Methods annotated with this will be called when the component receives messages.| void messageArrived(Map<String, Object> msg, MessageType mt)-->
<!--| **@AgentStreamArrived** | Methods annotated with this will be called when the component receives a new message stream. | TODO method header?-->

<!--## Composition-->
<!--TODO: Component Composition!-->

<!-- TODO: Parameter Guesser-->
<!--## Parameter Guesser-->
<!--What can be guessed: -->

<!--- component features-->
<!--- agent capabilities-->
<!--- IInternalAccess-->
<!--- IExternalAccess-->
<!--- Agent Pojo-->