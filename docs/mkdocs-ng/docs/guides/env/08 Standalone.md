# Standalone Platform with EnvSupport

## Exercise 1 - Starting Components from Java
In many cases, you may want to configure components via Java directly instead of via an .application.xml, for example if their initial values are manually read from an XML file. In this case, the platform needs to be started on its own, the parametrisation should take place and only after this parametrisation the agents should start their real behaviour. 
In order to create agents like this, one first needs to start the platform from Java. This can be done calling the Starter from Java with the parameters that may be used from command line as well. 
Afterwards, the ComponentManagementService, which is capable of creating agents, needs to be retrieved.

### Platform Startup
Create a class *Startup* containing the following code in it's ```public static void main()``` method:
		
```java
PlatformConfiguration config = PlatformConfiguration.getDefault();
config.setGui(false);
config.setWelcome(false);
config.setCli(false);
config.setPrintPass(false);
IFuture<IExternalAccess> platfut = Starter.createPlatform(config);
IExternalAccess platform = platfut.get();
IComponentManagementService cms = SServiceProvider.searchService(platform, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
```

### Creating Agent and Service
With the ComponentManagementService, the agents can now be created. As you are trying to create them with Java, you usually want to set values to them. This is done by an Service, which is defined in a separate Service Definition as pure Java Interface (see [Chapter 5: Provided Services](05 Provided Services):

```java
public interface IInitialisationService {
	void start();
	void setMyColor(int color);
	void setPosition(Vector2Double position);
}
```

The agent afterwards needs to provide this service (via an annotation) and implement it to save the received values into fields.
Create a class *ExampleAgent*:

```java
@Agent
@Service
@ProvidedServices(@ProvidedService(type = IInitialisationService.class))
public class ExampleAgent implements IInitialisationService {
```

Don't forget to implement the Service interface and add a field to inject the agent access:
```java
    @Agent
    private IInternalAccess agent;
```

### Agent Startup and Configuration
If this preconditions are met, the agent can be created and parameterized in Java. This is done via a call of the service at the newly created agent and would look like this (inside the Startup.main() method):

```java	
String exampleModel = ExampleAgent.class.getName() + ".class";
Random random = new Random();
final ITuple2Future<IComponentIdentifier, Map<String, Object>> componentFuture = cms.createComponent("ExampleAgent", exampleModel, null);
IComponentIdentifier component = componentFuture.getFirstResult();
IExternalAccess external = cms.getExternalAccess(component).get();
IInitialisationService serviceSQ = SServiceProvider.getDeclaredService(external, IInitialisationService.class).get();
serviceSQ.setPosition(new Vector2Double(1, 1));
serviceSQ.setMyColor(Math.abs(random.nextInt() % 254));
```

Afterwards, the agent behaviour needs to be started. As the service call can only be executed with an agent who has finished his ```@AgentBody```, the start of the agent behaviour can not be done by in the body. Instead, the ```IInitialisationService``` has the ```start()```-method which should now trigger the normal agent behaviour.

## Exercise 2 - Creating Space and Space Objects from Java
In order to get both a spatial representation of the agents and a graphical interface at once with minimal effort, the Jadex Environment Space was created. A detailed description can be found in [Chapter 1](01 Introduction).

In order to use the space with standalone started components, the space needs to be created manually. For describing the space, a separate .application.xml should be created. It should define at least the objects in space and the mapping between the agents and their avatars in the space.

### The Application XML ###
In this Example, the application XML is named *jadex/example/Example.application.xml* and contains the following code:

```xml
<applicationtype xmlns="http://www.activecomponents.org/jadex-application" xmlns:env="http://www.activecomponents.org/jadex-envspace"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://www.activecomponents.org/jadex-application
	                    http://www.activecomponents.org/jadex-application-3.0.0-RC30.xsd
	                    http://www.activecomponents.org/jadex-envspace
	                    http://www.activecomponents.org/jadex-envspace-3.0.0-RC30.xsd"
                 name="Example" package="jadex.example">

    <imports>
        <import>jadex.extension.envsupport.environment.space2d.action.*</import>
        <import>jadex.extension.envsupport.environment.space2d.*</import>
        <import>jadex.extension.envsupport.environment.*</import>
        <import>jadex.extension.envsupport.math.*</import>
        <import>jadex.extension.envsupport.dataview.*</import>
        <import>jadex.extension.envsupport.observer.perspective.*</import>
        <import>jadex.extension.envsupport.evaluation.*</import>
        <import>jadex.commons.*</import>
        <import>jadex.commons.future.*</import>
        <import>jadex.extension.envsupport.observer.gui.plugin.*</import>
        <import>jadex.example.*</import>
        <import>java.awt.*</import>
    </imports>

    <extensiontypes>
        <env:envspacetype name="gc2dspace" width="20" height="20" class="ContinuousSpace2D">
            <env:objecttypes>
                <env:objecttype name="ExampleAvatar">
                    <env:property name="happy">false</env:property>
                </env:objecttype>
            </env:objecttypes>

            <env:dataviews>
                <env:dataview name="view_all" class="GeneralDataView2D" />
            </env:dataviews>

            <env:avatarmappings>
                <env:avatarmapping componenttype="ExampleAgent" objecttype="ExampleAvatar" />
            </env:avatarmappings>

            <env:perspectives>
                <env:perspective name="abstract" class="Perspective2D" objectplacement="center">
                    <env:drawable objecttype="ExampleAvatar" width="1.0" height="1.0">
                        <env:property name="color" dynamic="true">new Color($object.happy ? 0 : 255, $object.happy ? 255 : 0, 0)</env:property>
                        <env:rectangle color="color" width="0.8" height="0.8" />
                    </env:drawable>
                    <env:prelayers>
                        <env:colorlayer color="black" />
                    </env:prelayers>
                </env:perspective>
            </env:perspectives>

            <env:spaceexecutor class="RoundBasedExecutor">
                <env:property name="space">$space</env:property>
            </env:spaceexecutor>
        </env:envspacetype>
    </extensiontypes>

    <componenttypes>
        <componenttype name="ExampleAgent" filename="jadex/example/ExampleAgent.class" />
    </componenttypes>

    <configurations>
        <configuration name="default">
            <extensions>
                <env:envspace name="mygc2dspace" type="gc2dspace" width="25" height="25">
                    <env:observers>
                        <env:observer name="world" dataview="view_all" perspective="main" />
                    </env:observers>
                </env:envspace>
            </extensions>
        </configuration>

    </configurations>

</applicationtype>
```

### Creating the space ###
This Application XML can be loaded by starting it with the previously acquired ComponentManagementService:

```java
final ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent = cms.createComponent("jadex.example.Example.application.xml", null);
createComponent.getFirstResult(); // Wait for space creation
final IEnvironmentService spaceService = SServiceProvider.searchService(platform, new ServiceQuery<>( IEnvironmentService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
ContinuousSpace2D mySpace = (ContinuousSpace2D) spaceService.getSpace("gc2dspace").get();
```

The creation of the avatar has do be done manually. Therefore the description of the concrete agent (for defining the owner of the object) and the space are needed. In this example, this is achieved by retrieving the space in the ```start()```-method of the agent and creating the corresponding space object there:

```java
@Override
	public void start() {
        final HashMap<Object, Object> properties = new HashMap<>();
        properties.put(Space2D.PROPERTY_POSITION, position);
        properties.put("myColor", myColor);
        properties.put(ISpaceObject.PROPERTY_OWNER, agent.getComponentDescription());

        final IEnvironmentService spaceService = agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IEnvironmentService.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
        ContinuousSpace2D space = (ContinuousSpace2D) spaceService.getSpace("mygc2dspace").get();
        avatar = space.createSpaceObject("ExampleAvatar", properties, new LinkedList<>());
        avatar.setProperty(Space2D.PROPERTY_POSITION, position);
	}
```

The last step is calling the start() method from the Startup.main() method, after the space was created:

```java
serviceSQ.start();
```

<!--All in all, this example shows a line of green rectangles, which where parameterized from with random values for their green-value. -->
This example can be extended for any use case where agents are parameterized from Java with custom values and a space.