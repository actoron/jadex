# Components

**Outdated Documentation**: This page is yet to be updated to the latest Jadex version. The documentation is still valid and the explanations still apply. Yet, due to some API changes, not all code examples in this document may be used as such. When in doubt, check the example sources in the *applications* modules available on GitHub, e.g. for [Micro](https://github.com/actoron/jadex/tree/master/applications/micro/src/main/java/jadex/micro) and [BDI](https://github.com/actoron/jadex/tree/master/applications/bdiv3/src/main/java/jadex/bdiv3) agents.

With Jadex, the behaviour of a software is defined by the interaction between components, each of them providing a clearly defined functionality.

When you create a new component, you have to choose between different component types. For now, we will focus on *Micro Agents*, the most basic type of component. For other component types, please refer to [Component Types](../component-types/component-types.md).

For a more complete guide into Active Components, take a look at the [AC User Guide](../guides/ac/01%20Introduction.md).

# Implementation

Micro Agents are defined by plain Java classes. In order for a Java class to represent a Micro Agent, two requirements have to be met:

 - The name of the class has to end with "Agent" (e.g. ```MyAgent```, ```ChatAgent```, ...)
 - The class has to be annotated with the ```@Agent``` Annotation

Optionally, it can provide a description using the ```@Description``` Annotation. The value is then shown inside the [JCC](../tools/02%20JCC%20Overview.md).

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

While implementing your component, keep in mind that components in Jadex are always **Single-Threaded**! Concurrency only takes place between different components, which will spare you a lot of headaches. Please don't start your own threads, as this will lead to errors later on.

<x-hint title="Futures">
Instead of the return type ```IFuture<Void>```, you can also use ```void```.
Using a *futurized* return type allows you to perform work asynchronously, which is handled in chapter [Futures](../futures/futures).
</x-hint>

# Startup

Starting of components is done by the Platform's ```ComponentManagementService``` (CMS).
Service instances in general can be retrieved using the static methods of the [SServiceProvider](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/bridge/service/search/SServiceProvider.html) class.

## Obtaining the CMS

Remember the ```IExternalAccess platform``` object that you got when starting a platform? It is now required to retrieve the CMS:

```java
IExternalAccess platform = Starter...
IFuture<IComponentManagementService> fut = SServiceProvider.searchService(platform, new ServiceQuery<>( IComponentManagementService.class));

IComponentManagementService cms = fut.get();
```

<x-hint title="Service interfaces">
Notice how we use the **interface** of the service we want to retrieve?
In Jadex, Java interfaces are used for the interaction with services, so the implementation can remain hidden.
</x-hint>

## Starting the component

Once you get a reference to the CMS, you can use the ```createComponent()``` methods to start your components (See API documentation of [IComponentManagementService](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/bridge/service/types/cms/IComponentManagementService.html)).

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
A ```Tuple2Future``` represents a promise that two different results are going to be available. In this case, the first is the ```ComponentIdentifier```, which is used to identify the instantiated component. The second result is a Map that can be filled with results by the component and is only returned upon termination of the component. You can use ```fut.getSecondResult()``` to block until the component is terminated and receive the results. Take a look at [Futures](../../futures/futures/#future-types) for different future types.
</x-hint>

Now that you know how to start your own components, you can read more about [Services](../services/services.md), as they provide a way for components to interact with each other.

## Destroying the component

To destroy a component, the CMS has to be used again. Call ```destroyComponent(cid)``` and pass the Component Identifier returned on component startup:

```java
Map<String,Object> results = cms.destroyComponent(cid).get();
```

If the component has any results, they are contained in the returned map. This is the same Map that is provided by the ```ITuple2Future``` received upon starting the component.

## Component Arguments

### Declaring Arguments

Components can declare arguments that can be passed during creation.
To declare arguments, use the ```@Arguments``` Annotation:

```java
@Arguments(@Argument(name="myName", description = "Name of this agent", clazz=String.class, defaultvalue = "\"Hugo\""))
public class MyAgent ...
```

Because the defaultvalue is parsed, Strings have to be quoted. You can also use other (Java) expressions that are executed to determine the default value.
To access this argument from inside the agent, inject it into a field using the ```@AgentArgument``` annotation:

```java
@AgentArgument
protected String myName;
```

<x-hint title="Agent Argument names">
Note that the field and argument name must match. If you want your field to have another name, specify the argument name as parameter when using the annotation: ```@AgentArgument("myName")```.
</x-hint>
Another way to access the arguments of an agent is by using the [IArgumentsResultsFeature](#component-features).

### Passing Arguments

When you created a component as explained above, the last parameter of```createComponent``` was null.
Instead, you can create your own [CreationInfo](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/micro/annotation/CreationInfo.html) object containing your component's arguments and pass it in *createComponent*:

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
For features specific to a component-type, take a look at [component types](/../component-types/component-types).

| Feature Name | Description |
|--------------|-------------|
|IArgumentsResultsFeature| Provides access to agent arguments and can take agent results. |
|IExecutionFeature| Provides access to the execution model to schedule component steps and provide wait functions. |
|IMessageFeature| Handles sending and reception of messages between components. |
|IMonitoringComponentFeature| Components can publish monitoring events with this feature. |
|IRequiredServicesFeature | See [Services](/services/services#accessing-services) |
|IProvidedServicesFeature | See [Services](/services/services#accessing-services) |

<!-- TODO: describe all default features -->
<!--PropertiesComponentFeature-->
<!--ISubcomponentsFeature-->
<!--NFPropertyComponentFeature-->
<!--ComponentLifecycleFeature-->

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

<!-- TODO: keepalive=true, damit agent nach body nicht beendet wird. Agent wird beendet, wenn body ein future returned - nicht bei void. -->

# Advanced Topics

This section discusses some of the more advanced topics regarding components.

## Composition

Components can be in a hierarchy to express compositional relationship.
To declare subcomponents, you may use the ```@ComponentTypes``` annotation and then create a ```@Configuration``` that includes an instance of the desired subcomponent like this:

```java
@Configurations(@Configuration(name = "default", components =  {@Component(type = "MyChildAgent")}))
@ComponentTypes(@ComponentType(name="MyChildAgent", clazz=ChildAgent.class))
public class ParentAgent { …
```

Any services provided by subcomponents using the scope [ServiceScope.COMPONENT](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/bridge/service/RequiredServiceInfo.html) can then be accessed using the same scope in the parent component or any other subcomponents.
Please refer to the [AC Tutorial](../tutorials/ac/06%20Composition.md) for a more complete example.

## More Annotations

The most important annotations common to all components were already discussed.
For a full reference, have a look at the [jadex.micro.annotation](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/micro/annotation/package-summary.html) package.

<!--InternalAccess?-->
<!--| **@Agent** | fields | Injects the ```IExternalAccess``` of the component.|-->
<!--| **@Parent** | fields | |-->

<!--## Messaging-->
<!--TODO: Messaging-->

<!--|Annotation|Description|Method declaration|-->
<!--|----------|-----------|------------------|-->
<!--| **@AgentMessageArrived** | Methods annotated with this will be called when the component receives messages.| void messageArrived(Map<String, Object> msg, MessageType mt)-->
<!--| **@AgentStreamArrived** | Methods annotated with this will be called when the component receives a new message stream. | method header?-->

<!--## Composition-->
<!--TODO: Component Composition!-->

## Parameter Guesser

Each Jadex Active Component has a *Parameter Guesser* that is used for annotation-based injections, e.g. when using ```@ServiceComponent``` inside Services or ```@AgentFeature``` inside Components.
When using one of these annotations on methods or fields, fields and method parameters declared with the following types are filled with values, if possible:

- IInternalAccess
- IExternalAccess
- Subtypes of IComponentFeature (see [Component Features](#component-features))
- Subtypes of ICapability (for bdiv3 components)
- Type of your Component - to inject the Component POJO

<!-- TODO: for which annotations does the parameter guesser work? -->

## Scheduling steps

The [concurrency model](../guides/ac/05%20Services.md#concurrency) of Jadex Active Components is based on single-threaded components.
If you want to execute your code on a component's thread from outside, you can call [scheduleStep](https://download.actoron.com/docs/nightlies/latest/javadoc/jadex/bridge/IExternalAccess.html#scheduleStep-jadex.bridge.IComponentStep-) on the IExternalAccess of a component:

```java
extAcc.scheduleStep(iAccess -> {
    // now you are on the component's thread
    return Future.DONE;
});
```

<x-hint title="Component Steps and Instance Methods">
If you schedule a step on a remote component, Jadex will send the step instance to the remote platform, where they are re-instantiated. While normal inner classes have access to their surrounding instance, component steps **do not**! Be sure to only call static methods from inside a component step.
</x-hint>
