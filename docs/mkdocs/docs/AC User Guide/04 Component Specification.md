Chapter 4 - Component Specification
================================================

In Jadex all component types (e.g. micro agents, BDI agents, BPMN workflows) share the same active component characteristics. Depending on the type of the component the definition is based on Java annotations or XML elements. The following explanations make extensive use of XML-based structure diagrams, but they are valid for annotations as well.  \
As can be seen in the figure below an active component specification is composed of *imports*, *arguments*, *componenttypes*, *services*, *properties*, *configurations* and *extensiontypes*. These elements will be explained in detail in the next subsections.

![04 Component Specification@componenttype.png](componenttype.png)  
*Component type*





### Imports

The imports can be used in the same way as in Java to specify the classes and packages that should be used for class and resource loading. In addition, these imports are helpful if Java expressions are used because the expressions are evaluated taking into account the defined imports. It has to be noted that normal Java imports cannot be used for expression evaluation as the import statements are not preserved within the class file. For this reason the annotation *@Imports* can be employed. As usual, single classes are defined per name and packages using the \*-notation. 





![04 Component Specification@imports.png](imports.png)  
*Imports*





In the examples below the package *java.util.\** and the class *java.net.URL* are imported. 


```xml

<imports>
  <import>java.util.*<import>
  <import>java.net.URL<import>
</imports>

```



```java

@Imports(
{
  "java.util.*",
  "java.net.URL"
})

```


### Arguments

Active components can have arguments and results. The arguments are supplied at startup of the component and the results can be fetched after the component has terminated. This allows to use components in a functional way, i.e. a component can be started and given argument values. The functional component computes something, sets its result values and terminates itself. The component creator is notified that the component has ended and can read the results for further processing. In order to use arguments and results at runtime it is necessary in the component type to declare the allowed argument and result types. For each argument and result type the following details can be specified:

-   *name*: The name of the argument or result.
-   *class*: The Java type of the argument or result. 
-   *defaultvalue*: (Optional) expression for the argument or result value if nothing else is supplied. The default value is defined as Java expression that is evaluated once.

![04 Component Specification@arguments.png](arguments.png)  
*Arguments*





The example code snippets shows how two arguments with the names "number" and "obj" and one result called "res". The type of the first is *Integer* and the type of the latter is *Customer* (if Customer has a package it has to be declared either fully qualified or it has to be imported). Both arguments have default values, which are *10* and *new Customer("Sparky")* respectively. The result is of type *boolean* and has no default value.    


```xml

<arguments>
  <argument name="number" class="Integer">10</argument>
  <argument name="obj" class="Customer">new Customer("Sparky")</argument>
  <result name="res" class="boolean"/>
</arguments>

```



```java

@Arguments(
{
  @Argument(name="number", clazz=Integer.class, defaultvalue="10"),
  @Argument(name="obj", clazz=Customer.class, defaultvalue="new Customer(\"Sparky\")")
})
@Results(@Result(name="res", clazz=boolean.class))

```


### Component Types

Active components allow for hierarchical decomposition, i.e. an active component may consist of an arbitrary number of subcomponents. The types of potentially created subcomponents should be declared within the component types section.\
The declaration of a subcomponent type is done using the following parameters:

-   *name*: The local name of the component type. This name can be used at other places to refer to the component type. Especially in the components section to create component instances of a given type.
-   *filename*: The filename of the referenced active component type. The filename can either include the package structure or contain only the name itself if the package is imported.  

![04 Component Specification@componenttypes.png](componenttypes.png)  
*Component types*





As example the declaration of a heatbug as subcomponent type is illustrated. It has the name *Heatbug* and refers to the file *jadex/micro/examples/heatbugs/HeatbugAgent.class*.


```xml

<componenttypes>
  <componenttype name="Heatbug" filename="jadex/micro/examples/heatbugs/HeatbugAgent.class"/>
<componenttypes/>

```



```java

@ComponentTypes(@ComponentType(name="Heatbug", filename="jadex/micro/examples/heatbugs/HeatbugAgent.class"))

```


### Services

Active components realize component orientation in the same way as traditional component approaches. In order to ensure a high degree of self-containedness of components each component type has explicitly to declare which functionalities it uses and needs. These aspects are defined in terms of *required* and *provided* services.





#### Required Services

![04 Component Specification@requiredservicetype.png](requiredservicetype.png)  
*Required service type*

A required service represents a needed functionality of another component. The specfication of a required service is composed of two aspects. First the service definition describes what kind of service is needed and secondly the binding describes how a service can be found. 

A required service is defined using the following properties:

-   *name*: The name that can be used to fetch the service programmatically. A required service can be fetched using the *service container* of a component. 
-   *class*: The type of the required service defined by an interface. 
-   *multiple*: If multiple is set to true, not one but all services of the given interface are searched and will be returned when requested.

The optional service *binding* has the following options:

![05 Services@scopes.png](scopes.png)  
*Search scopes*

-   *scope*: The scope of the search (cf. also the figure above). Static constants for search scopes are available via the class RequiredServiceInfo. Currently several predefined scopes are available (*application* being the default):
    - *local*: Consider only service within the component itself (the one that issues the search).
    - *component*: Includes services of the component itself and all subcomponents. 
    - *application*: It is assumed that applications are directly started on the platform (not as subcomponents). The application scope includes services of all components up to the uppermost application component (the platform is excluded). Please note that currently application scope does not support distributed applications. If a distributed search is needed scope global has to used.
    - *platform*: This scope includes all components of the platform the component is running on.
    - *global*: Global scope extends platform scope towards connected remote platforms. These connected remote platforms are represented as proxy compoents on the platform. (If platform awareness is enabled these proxies are automatically created and delete as platforms are discovered or leave. Without awareness such proxy objects can be created programmatically or via the connect button in the Starter of the JCC).
    - *parent*: (not shown in figure) Parent scope refers to services of the parent component only. (This scope is also used for static component service connections, e.g having a component A with two subcomponents B and C, in A it should be defined (or overriden) that B uses a service of C. Then B can define a binding with parent scope and component name C to state that the service should be picked from C of its parent. An example is shown in jadex.micro.testcases.semiautomatic.compositeservice). 
    - *upwards*: The upwards only searches upwards from the component towards the platform, i.e. root node.
-   *dynamic*: If declared as dynamic, static is default, each call to a *getRequiredService(s)* method of the service container will cause a new search. If the binding is not dynamic, the result from the first search will be cached and returned to subsequent invocations, too. In order to issue a new search for a static binding *getRequiredService(s)* can be called with the flag *rebind* set to true.
-   *proxytype*: The proxytype defines how calls are handled on the caller side with respect to interceptors. The possible values are *raw* for direct call routing and no interceptors and *decoupled* (default) for including the decoupling interceptor. If *direct* is specififed the interceptor chain is built but the decoupling interceptor is not used. Decoupling means that a service call result is shifted back to the component thread that called the service.
-   *create*: If enabled a component is created when no service of the given type could be found. The create flag requires the *componenttype* property to be specified in oder to know what kind of component need to be started. 
-   *recover*(experimental): If enabled each service invocation is tried to be recovered in specific error cases. Currently, the *ComponentTerminatedException* and *ServiceInvalidException* are catched and transparently a new search is performed. The call is automatically tried again on the new service. This feature is experimental because there are some known issues with it. For example, the search currently can return the failed service again.
-   *componentname*: The component name is used to directly reference a service of a known component, e.g. a subcomponent. Using the componentname and parent or local scope it is possible to link to services of peer or subcomponents. 
-   *componenttype*: The component type is currently used for two purposes (will be changed). First, it is used as search type in the same way as the component name. This allows for type level binding to peer or subcomponents. Furthermore, it is currently used as type for component creation if the create property is enabled. 
-   *interceptors*: The interceptors are used for performing actions before and after service calls. The provider as well as the consumer side may have their own interceptor chains. If custom interceptors are defined they are positioned after the default interceptors, which are the decoupling (turn off via proxytype=direct) and receover (turn on via recover=true) interceptors.

#### Provided Services

![04 Component Specification@providedservicetype.png](providedservicetype.png)  
*Provided service type*





Provided services describe the functionalities offered by a component. The specification of a provided service consists of a service definition and an implementation definition. 

A provided service is defined using the following properties:

-   *name*: The optional name can be used to refer to the provided service e.g. to override its implementation in a configuration.
-   *type*: The interface type of the service.

In most cases a service definition should include an implementation because otherwise the component cannot create and provide the service at startup. It is possible to omit the implementation and supply it within a configuration. Instead of a direct implementation a provided service can also delegate the implementation to another component by using a binding. The implementation has the these properties:

-   *class*: The implementation class. Needs to have an empty constructor.
-   *expression*: Can be used if the implementation class cannot have an empty constructor, e.g. because it needs arguments. The creation expression can be an arbitrary Java expression.
-   *proxytype*: The proxytype of the service. It has the same meaning as in the binding explained above. The possible values are *decoupled*, *direct* and *raw*. The default interceptors are the *DecouplingInterceptor* to execute the service call on the client component, the *ValidationInterceptor* to check if the service is initialized (returns a *ServiceInvalidException* otherwise), the *ResolveInterceptor* to route service calls if it is a Pojo service and the *MethodInvocationInterceptor* to finally perform the call. 

### Properties

![04 Component Specification@properties.png](properties.png)  
*Properties*





Properties can be used to define specific aspects of components. A property is only evaluated once at startup of the component. If the type of a property is *IFuture* the component will evaluate it to the underlying value, i.e. the component will wait for the property to be initialized.

A property has the following attributes:

-   *name*: The property name.
-   *class*: The property type. Please note the special handling of future properties described above.
-   *expression*: The property value Java expression.

### Configurations

![04 Component Specification@configurations.png](configurations.png)  
*Configurations*





Configurations can be used to define different runtime settings of a component (a component may define an arbitrary number of different configurations). Each configuration has a name and at startup this name can be used to start the component in the underlying configurations. Configurations may differ in all runtime aspects from the type specification as can be seen also in the figure below.

The properties of a configuration are as follows:

-   *name*: The configuration name that can be used to refer to this specific setting. This name needs to be provided at startup of a component to activate the given configuration.
-   *suspend*: If enabled the component will be started in suspended mode. This is e.g. helpful for debugging purposes.
-   *master*: Starts the component as master. If a master component is terminated this causes the parent of the master also to terminate. 
-   *daemon*: The daemon setting is used in combination with autoshutdown. If a component is started as daemon it does not prevent the parent from being terminated after the last non-daemon subcomponent has terminated.
-   *autoshutdown*: In enabled, automatically terminates the component when the last child (non-daemon) has been killed.

#### Arguments

-   *arguments*: The arguments section allow for defining new values for arguments and results that possibly override the default values specified at the type level. If arguments are passed from the outside to the component these values always have precedence over configuration or type level values. An argument or result type is referenced via its name.

#### Components

![04 Component Specification@configcomponents.png](configcomponents.png)  
*Configuration components*

-   *components*: The components section allows for creating component instances of specified subcomponent types. These component instances can be customized by the follow properties:
    - *type*: The local component type that is one of the names of the declared subcomponent types.
    - *name*: The optional instance name for the component. The name can be an expression. If a number of agents is created (see next property) using *\$n* can be used as predefined expression variable that contains the current number of the instance. 
    - *number*: The number of components that should be started. The number is allowed to be a Java expression.
    - *configuration*: The configuration name the subcomponent will be started with. 
    - *suspend*: If set to true the subcomponent will be started in suspended mode.
    - *master*: If set to true the subcomponent will be started as master. If a master subcomponent is terminated the parent will be terminated as well.
    - *daemon*: If set to true the subcomponent will be started as daemon. A daemon subcomponent will not hinder the autoshutdown of the parent component (if the parent has autoshutdown set to true).
    - *shutdown*: If autoshutdown is true the component counts the number of subcomponents. If the last subcomponent is terminated the parent component will be shutdowned.
    - *arguments*: The arguments for the subcomponent can be defined.
    - *required services*: Using the name of the required services the corresponding binding can be overridden. The is e.g. helpful to statically link subcomponent services with parent or peer services.

#### Services

![04 Component Specification@configservices.png](configservices.png)  
*Configuration Services*





In the configuration of a component also the service details can be changed. With respect to provided services the implementation and with regard to required services the binding can be changed. The specifications require that a required or provided service is identified via its type name. The implementation or binding details can be redefined for the referenced service.

#### Extensions

![04 Component Specification@configextensions.png](configextensions.png)  
*Extensions*





Extensions can be used to add extension instance elements to an active component. As can be seen in the figure the concrete extension elements depend on the extension type used. Examples for extensions that make use of the extension mechanism are the virtual environment EnvSupport used in many example applications and AGR (the agent-group-role model). Please also refer to the Extension Types section below for further explanations. 



(Current limitation: extensions cannot be used in annotation based Java components)

#### Steps

![04 Component Specification@configsteps.png](configsteps.png)  
*Steps*





Steps can be used to execute custom behavior at component creation or termination. The first can be achieved using *initial steps* and the latter using *end steps*. 

Each step is defined via:

-   *class*: The class is a reference to a Java class that has to implement the generic *jadex.bridge.IComponentStep* interface. Each component has to realize a method called *execute* that should contain the corresponding domain logic. In order to access the component internals, the *IInternalAccess* interface of the component is passed as parameter value to the execute method. The internal access allows unprotected access to the internals. This is possible because it is ensured by the runtime infrastructure that steps are always executed on the component thread itself. The method must return a future that indicates when the step has been executed. The generic type of the step class can be used to adjust its concrete return type. 


```java

public interface IComponentStep<T>
{
  public IFuture<T> execute(IInternalAccess ia);
}

```


(Current difference: Micro agents have lifecycle methods instead of steps to execute behavior at creation and deletion time)

### Extension Types

![04 Component Specification@extensiontypes.png](extensiontypes.png)  
*Extension types*





Extensions can be used to add new functionality to an active component. An extension consists of the following aspects:

-   An loader extension for the component factory that is provided by the *IComponentFactoryExtensionService* interface from package *jadex.bridge.service.types.factory*.
-   An extension model instance elements that implement the *IExtensionInfo* interface from package *jadex.bridge.modelinfo*.
-   The extension logic as class that implements the IExtensionInstance interface from package *jadex.bridge.modelinfo*. 

A component factory searches for all component factory extension services and asks them for factory dependent loader information. This information (in case of XML-based components typically a set of TypeInfo Objects for the Jadex XML reader) that is used by the factory to load the extension specific parts of the model. When creating a component instance the component interpreter expects that the instance elements defined in the configurations implement the interface *IExtensionInfo*. The interpreter use the method *createInstance()* on the objects to asynchronously create an extension instance of type *IExtensionInstance*. This instance is managed by the interpreter under the name defined in the extension info, i.e. *getExtension()* can be called on the interpreter to fetch an extension instance per name. The interface of an extension instance only allows to terminate the running extension.


```java

public interface IComponentFactoryExtensionService
{
  public IFuture getExtension(String componenttype);	
}

```
\
The component factory extension service is used by component factories to fetch extension loader functionality. The *getExtension()* method is called by a component factory to retrieve loader functionality for the extension. The comonent type parameter describes the type of component the factory is resposible for. To activate an extension mechanism a component instance has to be created that provides the extension service.


```java

public interface IExtensionInfo
{
  public String	getName();
  public IFuture<IExtensionInstance> createInstance(IExternalAccess access, IValueFetcher fetcher);
}

```
\
The extension info interface allows for creating an extension instance of a given name.


```java

public interface IExtensionInstance
{
  public IFuture<Void> terminate();
}

```
\
An extension instance has a method that terminates the extension.
