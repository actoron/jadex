# Jadex Web API

**Outdated Documentation**: This page is yet to be updated to the latest Jadex version. The documentation is still valid and the explanations still apply. Yet, due to some API changes, not all code examples in this document may be used as such. When in doubt, check the example sources in the *applications* modules available on GitHub, e.g. for [Micro](https://github.com/actoron/jadex/tree/master/applications/micro/src/main/java/jadex/micro) and [BDI](https://github.com/actoron/jadex/tree/master/applications/bdiv3/src/main/java/jadex/bdiv3) agents.

A goal of Jadex Active Components is to integrate easily into Web environments.
This is approached by providing easy REST/Web Service integration as well as a JavaScript API to directly communicate with Jadex services via HTML5 Websockets.

## Jadex REST Service integration

Jadex provides an integrated approach to interact with Web Services such as SOAP and REST.
Fortunately, Jadex and REST/SOAP have one thing in common: they use services in order to provide functionality.
In consequence, Jadex services can be published as REST Services and REST Services can be used just as any other service in Jadex after they are setup.

Full Guide:
[REST Web Services](../guides/ac/06%20Web%20Service%20Integration.md#rest-web-services)

### Providing REST Services

To publish a [service](/services/services) as REST Service, the ```publish``` property has to be added to the ```@ProvidedService``` annotation:

```java
@ProvidedServices({
    @ProvidedService(name="userservice",
    type=IUserService.class,
    implementation=@Implementation(UserService.class),
    publish=@Publish(publishtype=IPublishService.PUBLISH_RS, publishid="http://localhost:8081/userservice")
})
@Agent
public class UserAgent {...
```

Inside the *@Publish* annotation, we have two parameters:

 - the publishtype defines whether to publish as REST (PUBLISH_RS) or SOAP (PUBLISH_WS) Webservice
 - the publishid determines the base location for this service

Additionally, the service interface (*IUserService*) has to declare Metadata of the methods to publish:

```java
@GET
@Produces(MediaType.TEXT_PLAIN)
String sayHello();
```

This method can than be called by issuing a GET request to URL ```http://localhost:8081/userservice/sayHello```.

To retrieve and parse input, the ```javax.ws.rs.QueryParam``` annotation can be used:

```java
@POST
@Consumes(MediaType.TEXT_PLAIN)
@Path("users")
IFuture<Boolean> addUser(@QueryParam("name") String name);

@GET
@Produces(MediaType.TEXT_XML)
@Path("users")
IFuture<String> getUsers();
```

If you would like to use XML output as in the example above, you may use the included ```jadex.xml.bean.JavaWriter```:

```java
private List<User> users = new ArrayList<User>();

public IFuture<String> getUsers() {
    return new Future(JavaWriter.objectToXML(users, this.getClass().getClassLoader()));
}
```

### Consuming REST Services

## JavaScript / HTML5 websockets (commercial license only)

This feature is currently in development stage. Documentation will be available soon.
