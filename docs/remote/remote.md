${SorryOutdatedv3}

# Remote Communication
Applications built with Jadex Active Components can be easily distributed across platforms, meaning that [service discovery](../../services/services/#service-scopes) and [accessing services](../../services/services/#accessing-services) works the same way, no matter if the target service is available at a local or remote platform.

However, one additional aspect has to be addressed in the remote case: How can *platforms* discover each other to share services?

# Awareness
To solve the platform discovery problem, Jadex introduces a mechanism called *Awareness*, which is enabled by default.
The goal is the detection of all available remote platforms in order to seamlessly communicate between each other if required. 

To achieve this, several different kinds of discovery mechanisms are used. In Local Area Networks, *Broadcast* and *Multicast* are used to discover platforms, but the most important method for platforms behind a firewall is the *Relay Discovery*. By default, platforms register at one of our relay servers, so they can see each other.  
For more details, check out the Active Components User Guide about [Platform Awareness](../../guides/ac/07 Platform Awareness).


## Configuration
Please refer to the [Configuration examples](../../platform/platform/#configuration-examples) in the Platform chapter or visit the [Platform Awareness](../../guides/ac/07 Platform Awareness/#configuration) section of the Active Components User Guide for awareness configuration. 

## Manually Connecting Platforms
${SorryNotYetAvailable}
<!--TODO: how to manually add remote platforms-->

# Security

Thanks to mechanisms for global awareness and connectivity, in principle, any Jadex platform around the world may find and invoke any services of components on any other Jadex platform.  
Because this is usually undesired, the following security mechanisms exists. 
A full guide about security is available in the [Security Chapter](../../guides/ac/08 Security/) of the Active Components Guide.

## Platform-Level Security

Platform-level security is based on passwords shared by the platforms that should be able to communicate with each other.
There are two kinds of passwords:  

### Platform passwords
By default, a Jadex platform is secured by a password which is generated at first startup and printed to the console. If a remote platform knows this password, it is able to find and invoke any services available on the local platform.  
This password-protection may be switched off by setting ```PlatformConfiguration.setUsePass(false)``` (**NOT recommended!**)  
Remote platforms can set platform passwords to access remote platforms within the [JCC Security Settings](../../tools/05 Security Settings/#remote-platform-password-settings) or by accessing the [ISecurityService](${URLJavaDoc/jadex/bridge/service/types/security/ISecurityService.html}) programmatically.

### Network passwords
As your application may include several platforms, it is more convenient to set-up a trusted network.
You can either configure this in the [JCC Security Settings](../../tools/05 Security Settings/#network-password-settings) or by adding it to the startup configuration of every involved platform:

```java
rootConfig.setNetworkName(<myName>);
rootConfig.setNetworkPass(<myPass>);
```

Every platform may invoke services on every other platform that shares the same network name/password combination.
You may also enable *Trusted LAN* mode, which will allow connections within a LAN without specifying a password:

```java
rootConfig.setTrustedLan(true);
```

## Application-Level Security

In addition to the password-protected access to platform services, a service implementation can also use the ```@Security``` annotation for methods or the whole type, which allows to enable unrestricted access (without platform-level password) to the service:

```java
@Security(Security.UNRESTRICTED)
public interface IPublicService {...
```

By default, the security level for all services is ```Security.PASSWORD``` and requires a shared platform-level password.

# Transports
After discovering remote platforms using awareness, Jadex Active Components needs a way to communicate with those platforms. This is where the *transports* come in, which provide the means to communicate with other platforms.

Since all forms of communication (such as TCP/IP) may not be available or may be impeded by firewalls or similar, a number of transports are available of which the following are enabled by default:

* The **LocalTransport** enables quick communication between components on the same platform. This is only relevant if raw messages are exchanged manually. It is not used for platform-scope service calls.
* **NIOTCPTransport** implements communication based on TCP/IP streams using the java.nio.* API.
* **HttpRelayTransport** uses the same external relay servers as the relay discovery to communicate with other platforms. Since this transport uses HTTP, it works under most circumstances.

In addition, there are some additional transports that are *not enabled by default*:

* **TCPTransport**: uses TCP/IP like the NIOTCPTransport but uses the java.io.* API. Since this approach requires more threads compared to NIOTCPTransport, it is not used by default
* **SSLTCPTransport**: This transport offers authentication/encryption support using TLS/SSL. It is only included in the commercial 'pro' packages of Jadex Active Components.

Jadex Active Components will use all available transports to ensure that a message gets delivered. The message is offered to all transports and one is chosen to actually transmit the message based on order of priority and if the transport is currently working.  
If a transport fails to transmit a message, the other transports are tried before transmission is aborted. Therefore as long as at least one working transport is available to another platform, communication is maintained for the applications.

# Serialization
When a remote service is called, the data of the call must be transmitted to the remote machine using a network connection. Critically, this data includes the method parameters of the call as well as the return values once the call is complete.

Java objects like those in the parameters exist in the local computers memory. In order to send them over to the remote machine, they must be converted into a form that can be send over networks. This process is called *serialization*, which turns Java objects into binary or text representations that can be transmitted.

Jadex Active Components includes a number of serialization approaches, the default being a compact binary format. However, not all objects can be sensible serialized, for example, it makes no sense to serialize a *java.lang.Thread* object, since it represents an execution thread only valid on the local machine. Therefore, in order for serialization to work, the classes you use in service calls must one of the following:

* A number of classes, mostly standard Java library classes, are directly supported by Jadex Active Components. This includes primitive value (int, long, ...), Strings, Java collection classes (Lists, Sets, Maps, ...) as well as certain useful standard classes like Exceptions, Optionals, Date, Image and URI/URL.
* Classes that loosely follow the *JavaBean* conventions (see below).

## Bean-conform Classes
The latter option allows you to easily implement your own classes that can be transmitted. The classes do not have to follow the full JavaBean specification, only two things are required:

* The class must offer a "default constructor", which means it includes a (public or private) constructor that has no arguments.
* JavaBean-conforming accessor methods for each field you want transmitted.

For example, the following example would conform to this:

```java
public class Customer {

	private int id;
	private String name;
	
	public Customer() {
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return id;
	}
	
	public void setName(int name) {
		this.name = name;
	}
```

As long as your classes conform to this pattern or are one of the directly supported types, they can be transmitted. You can also nest (use one of them as a field in another class) at will and even include (self-) references to other parts of the object graph and the object will be correctly recreated on the remote computer.

<x-hint title="java.io.Serializable">
Notice that the class does not implement the java.io.Serializable interface. It is not necessary because Jadex Active Components does not use the Java built-in serialization by default.  
While the build-in Java serialization works and is quick, it has some serious drawbacks that makes it inflexible such as requiring implementing the java.io.Serialization marker interface in all classes nested in an object as well as lack of support for partially-matching class versions.
</x-hint>

## Annotation-based

If you don't want to add bean-conform getters/setters to your classes, you may also use the [@IncludeFields](${URLJavaDoc}/jadex/commons/transformation/annotations/IncludeFields.html) annotation:

```java
@IncludeFields
public class Customer {

	public int id; // included
	public String name; // included

	public Customer() {
	}
```

You may also use an explicit [@Include](${URLJavaDoc}/jadex/commons/transformation/annotations/Include.html) annotation on every field to include it.

```java
public class Customer {

    @Include
    public int id;
    @Include
    public String name;

    private String hidden;  // field hidden is exluded
}
```

This annotation-based method also requires a default constructor.
### Private fields

Since Jadex 3.0.80, it is also possible to include private fields in serialization, using ```@IncludeFields(includePrivate=true)```. Specific fields can also be excluded, as shown below:

```java
@IncludeFields(includePrivate=true)
public class Customer {

    private int id; // included
    private String name; // included

    @Exclude
    private String hidden;
}
```

As with public fields, you may also use an explicit [@Include](${URLJavaDoc}/jadex/commons/transformation/annotations/Include.html) annotation on every field to include it.

```java
public class Customer {

    @Include
    private int id;
    @Include
    private String name;

    private String hidden;  // field hidden is exluded
}
```

# Advanced Topics 

## Making the remote execution safe for different builds

When accessing the GUI remote you may have noticed the following message being printed to the console:
*Warning: Anonymous class without XML class name property (XML_CLASSNAME) / annotation (@XMLClassname): tutorial.BotGuiF3$1*.

This warning indicates a potential problem due to the Java language specification not describing a naming scheme for anonymous inner classes. Each java compiler decides for itself how to name an inner class (typically OuterClass$1, OuterClass$2, ...). This can cause incompatibilities when two platforms communicate that have been compiled using a different compiler (e.g. javac vs. eclipse). To allow proper mapping of inner classes you can specify an additional identifier using the @XMLClassname annotation:

-   For each inner class, add an *@XMLClassname("some_identifier")* annotation. Of course you should use different identifiers for each occurrence.
-   Access the GUI remotely and check if the warnings have vanished.
