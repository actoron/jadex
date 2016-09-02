# Services
In order to interact between each other, components can provide services and use services provided by other components.
A Jadex Service is generally represented by an arbitrary Java Interface and has to be declared on the providing and/or requiring component.
The service interface can be implemented as a Java class.
 Inside the component, access to services is then granted via Component Features or Injections.

For services provided for general use by the Jadex Active Components Platform, please refer to [Platform Services](../platform/platform/#platform-services).

# Implementation

To implement a Jadex Service, two things are required: A Java Interface and an implementation of this interface. 
 
## Java Interface
The interface can be a plain Java interface like this:
```java
public interface ISumService {
    public IFuture<Integer> addValues(int a, int b);
}
```

<x-hint title="Futures">
Again, using the *futurized* return type allows you to perform work asynchronously while a service call is executing. You can refer to the chapter [Futures](../futures/futures) for more information.
</x-hint>

## Java Implementation
To provide an implementation for the specified service, just create a new class implementing the Java interface and mark it with the ```@Service``` annotation:
```java
@Service
public class SumService implements ISumService {
    public IFuture<Integer> addValues(int a, int b) {
        int sum = a + b;
        return new Future<Integer>(sum);
    }
}
```

# Providing Services
A service can be provided by any component. Just add the following Annotation to you component's code to make it provide the *SumService* declared above:
 
```java
@ProvidedServices({
    @ProvidedService(name="sum", type=ISumService.class, implementation=@Implementation(SumService.class))
})
@Agent
public class SumAgent {...
```

To provide multiple services, just add them to the list of ```@ProvidedServices``` (comma-separated).

** @ProvidedService **  
Inside the ```@ProvidedService``` annotation, the following parameters can be specified:

|Parameter|Description|
|---------|-----------|
|*name*| The name of the provided service, used for referencing the service.|
|*type*| The type (interface) of the service|
|*implementation*| The ```@Implementation``` of the service (see below)
|*scope* | The *Scope* of the provided service, see [Service Scopes](#service-scopes). Defaults to *global*.|
|*publish* | A ```@Publish``` annotation (see [Publishing](#publishing))|
|*properties* | Properties (```NameValue``` array) (see [Properties](#properties)) |

** @Implementation **  
The implementation is usually given by specifiying a class that implements the service type. This class should usually have an empty constructor.
Additionally, the following parameters can be specified:

|Parameter|Description|
|---------|-----------|
|*value*  |A class implementing the service|
|*expression*| Java expression to be executed for instantiation of the service. Can be used instead of *value* to pass constructor arguments. TODO: link to expression guide|
|*proxytype* | The [type](#proxy-types) of the service proxy that is created.|
|*interceptors* | (not documented) |
|*binding* | (not documented) |

## Service Scopes
Whether a service is visible for another component depends on it's scope as shown in the figure below.

![Service Scopes](scopes.png)  
Service Scopes

Possible scopes are:

| Scope | Description |
|-------|-------------|
| *none*  | No search will be performed / Nobody will see the service
| *local* | Only visible inside the local component |
| *component* | Visible inside the local component and sub-components |
| *application* | Visible inside the whole application (defined by an application component |
| *parent* | Visible inside the parent component |
| *platform* | Visible inside the local platform |
| *global* | Globally visible |

See the [RequiredServiceInfo](${URLJavaDoc}/jadex/bridge/service/RequiredServiceInfo.html) class for matching String constants.

Service Scopes are respected in two cases: When providing a service and during service search. This means a *locally* provided service cannot be found by other components, even if the search scope is set to *global*.

<!--## Service Tagging-->
<!--TODO-->

# Using Services

Depending on the defined scope, a service can be used by the local component only, by related components or by remote components, too.
To use a service, it has to be declared as Required Service inside the using component:

```java
@RequiredServices({
    @RequiredService(name="sumservice", type=ISumService.class, 
    binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
@Agent
public class UsingAgent {...
```

To require multiple services, just add them to the list of ```@RequiredServices``` (comma-separated).

** @RequiredService **  
Inside the ```@RequiredService``` annotation, the following parameters can be specified:

|Parameter|Description|
|---------|-----------|
|*name*| The name of the required service, used for referencing the service.|
|*type*| The type (interface) of the service|
|*multiple*| Set to true if multiple instances of the service should be used, see [Advanced Service Topics](#using-multiple-services)|
|*binding*| A ```@Binding``` annotation (see below)|
|*multiplextype*| (not documented)|
|*nfprops*| TODO nfprops|

** @Binding **  
The ```@Binding``` annotation defines parameters of the service binding that Jadex will establish between providing and requiring components. Most notably, it defines the [search scope](#service-scopes). Other parameters are:

|Parameter|Description|
|---------|-----------|
|*proxytype*| TODO |
|*name*| TODO|
|*componentname*|TODO|
|*componenttype*|TODO|
|*scope*| The search scope to find the required service |
|*dynamic*| If set to true, a new search will be initiated every time the required service is accessed |
|*create*| Set to true if the service should be instantiated by the local component |
|*creationInfo*| ```@CreationInfo``` annotation, see [Advanced Service Topics](#auto-instantiation-of-required-services) |
|*recover*| TODO|
|*interceptors*| (not documented)|


# Accessing Services

## Using Injection
Usually, you want to retrieve the instance of a required service and perform operations on it inside the component. You can use the following Annotation to inject the service instance into a field:
```java
@AgentService
private ISumService sumService
```

The service can then be used inside the component after it is started (```@AgentCreated``` is called). 

<x-hint title="Method Injections">
Some injection annotations can also be applied to methods. In this case, the service instance can be passed as method parameter when it is found:  
@AgentService  
public void setSumService(ISumService sum) { ...
</x-hint>

Instead of injecting the service instance, you can directly inject values of the service:
TODO: @AgentServiceValue

## Using Component Features
You can also access the required services of a component by using the RequiredServicesFeature. Inject the feature inside the code of your component and call *getRequiredService()*:

```java
@AgentFeature
private IRequiredServiceFeature reqFeat;
...
{
    ISumService sum = reqFeat.getRequiredService("sum").get();
}
```

Note that the name specified must match the required service declaration.
By using the ```IProvidedServiceFeature```, you can also get access to services *provided* by your component.

## The IService interface
TODO

# Accessing the Component

Sometimes it is necessary to access component features, the InternalAccess or even the component's POJO object itself from inside the service.
This can be done using the ```@ServiceComponent``` annotation:

```java
@ServiceComponent
private IExecutionFeature exeFeat;

@ServiceComponent
private IInternalAccess agentAccess;

@ServiceComponent
private MyAgent agent;
```

This annotation will also inject Agent Capabilities (see [BDI Capabilities](../bdiv3/06 Using Capabilities/)) and other instances that can be guessed by the [Parameter Guesser](../components/components/#parameter-guesser).

# Service Lifecycle

Just as components, services have their own lifecycle. For each step in the cycle there is an annotation which can be used on methods to perform actions during the lifecycle step.

|Annotation|Description|
|----------|-----------|
|**@ServiceStart**|A method marked with this annotation will be called upon creation of the service. Injected fields will be available at this point.|
|**@ServiceShutdown**|A method marked with this annotation will be called just before the service is terminated.|

# Advanced Topics

This section discusses some of the more advanced topics regarding services.

## More Annotations
The most important annotations were already discussed. The following is an uncomplete list of other potentially useful annotations. 
For a full reference, have a look at the [jadex.bridge.service.annotation](${URLJavaDoc}/jadex/bridge/service/annotation/package-summary.html) package.

|Annotation|Description|
|----------|-----------|
|**@ServiceIdentifier**| Can be used on fields to inject the [ServiceIdentifier](${URLJavaDoc}/jadex/bridge/service/annotation)|
|**@Excluded**| Can be used on methods or classes that should not be available from remote. Will throw an UnsupportedOperationException when called.|

## Accessing non-declared Services

The *SServiceProvider* helper class provides means to obtain services from any component without having to declare them as required services.
The method ```getService(provider, cid, type)``` allows fetching a declared service of a specific component directly:

```java
ISumService sum = SServiceProvider.getService(platformAccess, providerCid, ISumService.class).get();
```

Note that we use the platform as search entry point, but we specify the cid of the component providing the service. Thus we state that we want to search for the *ISumService* only in this specific component.

You can also use the method ```getService(provider, type)``` to initiate a search on all components instead.
The other ```getService()``` methods allow to specify search filters or scopes.
Using ```getServices()```, you can also find multiple instances of the service, if available.

If you want to avoid calling other platforms during search and only want to lookup local components, use the ```getLocalServices()``` instead.

 For further information, please have a look into to API documentation of [SServiceProvider](${URLJavaDoc}/jadex/bridge/service/search/SServiceProvider.html).

## Embedding services
You can also embed the service logic directly in your component, which might be a better choice in some cases.
To do so, just add the ```@Service``` Annotation to your component class and let it implement the service interface:
 
```java
@Agent
@Service
@ProvidedServices({
	@ProvidedService(type=ISumService.class)
})
public class SumAgent implements ISumService {...
```

Using ```@Agent(autoprovide=true)```, you can also leave out the ```@ProvidedServices``` Declaration:
```java
@Agent(autoprovide=true)
@Service
public class SumAgent implements ISumService {...
```

## Auto-Instantiation of Required Services
** @CreationInfo **  
Most of the time, this annotation is not needed.
If you want a component trigger auto-instantiation of the required service components, set **create** to true in the ```@Binding``` annotation and specify the type of the providing component:

```java
@RequiredServices({@RequiredService(name="sumservice", type=ISumService.class, 
    binding=@Binding(create=true, creationInfo=@CreationInfo(type="sum")))
})
@Agent
public class UsingAgent {...
```

In this case, the classpath will be searched for a component of type *sum* (e.g., *SumAgent*), which will be instantiated when *UsingAgent* is started. 

## Using Multiple services

## Proxy types

## Properties

## Publishing

| **@Publish** | TODO |