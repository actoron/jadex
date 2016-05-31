# Components

With Jadex, the behaviour of a software is defined by the interaction between components, each of them providing a clearly defined functionality.
 
When you create a new component, you have to choose between different component types. For now, we will focus on *Micro Agents*, the most basic type of component. For other component types, please refer to [Component Types](component-types/component-types).

# Implementation
Micro Agents are defined by plain java classes. In order for a java class to represent a Micro Agent, two requirements have to be met:

 - The name of the class has to end with "Agent" (e.g. ```MyAgent```, ```ChatAgent```, ...)
 - The class has to be annotated with the ```@Agent``` Annotation
  
Optionally, it can provide a description using the ```@Description``` Annotation. For other possible annotations, see [annotations](#annotations).

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
Using a *futurized* return type allows you to perform work asynchronously, which is handled in chapter [Futures](futures/futures).
</x-hint>

# Startup

Starting of components is done by the Platform's ```ComponentManagementService``` (CMS). 
Service instances in general can be retrieved using the static methods of the [SServiceProvider](../../javadoc/jadex/bridge/service/search/SServiceProvider.html) class.

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

Once you get a reference to the CMS, you can use the ```createComponent()``` methods to start your components (See API documentation of [IComponentManagementService](../../javadoc/jadex/bridge/service/types/cms/IComponentManagementService.html)).

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
A ```Tuple2Future``` represents a promise that two different results are going to be available. In this case, the first is the ```ComponentIdentifier```, which is used to identify the instantiated component. The second result is a Map that can be filled by the component (TODO: correct?) and is only returned upon termination of the component. Take a look at [Futures](../futures/futures) for different future types.
</x-hint>

Now that you know how to start your own components, you can read more about [Services](../services/services), as they provide a way for components to interact with each other.

## Creation Info
TODO

# Component Features

All component functionalities are available via *features*.
By default, all components have a certain set of features, which can be injected into fields by using an annotation:
```java
@AgentFeature
IExecutionFeature exeFeat;
```

Below is a list of important features commonly available for all components.
For features specific to a component-type, take a look at [component types](../component-types/component-types).

| Feature Name | Description |
|--------------|-------------|
|IArgumentsResultsFeature| TODO |
|IExecutionFeature| TODO |
|IMessageFeature| TODO |
|IMonitoringComponentFeature| TODO |

You can even define new component features. Please refer to [TODO](TODO) to see how.

# Annotations
The most important annotations common to all components are listed below.
For a more complete guide, take a look at the [AC User Guide](../guides/ac/01 Introduction).
For a full reference, have a look at the [jadex.micro.annotation](../../javadoc/jadex/micro/annotation/package-summary.html) package.

## Lifecycle (Method) Annotations
The Jadex Active Components Platform and the CMS implement a specific lifecycle for components. 
For each step in the cycle there is an annotation which can be used on methods to perform actions during the lifecycle step.
These annotations can be used on methods, like this:
```java
@AgentCreated
public IFuture<Void> agentCreated() {...
```

TODO: Guessed parameters?

|Annotation | Description|
|-----------|------------|
| **@AgentCreated** | A method marked with this annotation will be called upon creation of the agent. This means services, injected fields etc. are not initialized at this point. |
|**@AgentBody** | A method marked with this annotation will be called after creation of the agent is complete. At this point, all fields and required services are available and can be used.|
|**@AgentKilled** | A method marked with this annotation will be called just before the component is removed from the platform.|  

## Type Annotations
These annotations can be applied to the component type definition, like this:
```java
@Agent
public class MyAgent {...
```

|Annotation | Description|
|-----------|------------|
|**@Agent** | Marks the class as micro agent.|
|**@Description** | Add a description to your component. |

## Injection Annotations

|Annotation | Applicable | Description|
|-----------|------------|------------|
| **@AgentFeature** | fields | Injects features of components.|
| **@Agent** | fields | Injects the ```IExternalAccess``` of the component.|
| **@AgentArgument** | fields | TODO |
| **@AgentService** | methods/fields | TODO |
| **@AgentServiceValue** | methods/fields | TODO |
| **@Parent** | fields | TODO |


## Messaging Annotations
|Annotation|Description|Method declaration|
|----------|-----------|------------------|
| **@AgentMessageArrived** | Methods annotated with this will be called when the component receives messages.| void messageArrived(Map<String, Object> msg, MessageType mt)
| **@AgentStreamArrived** | Methods annotated with this will be called when the component receives a new message stream. | TODO method header?
