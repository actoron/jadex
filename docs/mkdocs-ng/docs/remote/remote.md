# Remote
Applications built with Jadex Active Components can be easily distributed across platforms, meaning that [service discovery](../services/services/#service-scopes) and [accessing services](../services/services/#accessing-services) works the same way, no matter if the target service is available at a local or remote platform.

However, one additional aspect has to be addressed in the remote case: How can *platforms* discover each other to share services?

# Awareness
To solve the platform discovery problem, Jadex introduces a mechanism called *Awareness*, which is enabled by default.
The goal is the detection of all available remote platforms in order to seamlessly communicate between each other if required. 

To achieve this, several different kinds of discovery mechanisms are used. In Local Area Networks, *Broadcast* and *Multicast* are used to discover platforms, but the most important method for platforms behind a firewall is the *Relay Discovery*. By default, platforms register at one of our relay servers, so they can see each other.  
For more details, check out the Active Components User Guide about [Platform Awareness](../guides/ac/07 Platform Awareness).


## Configuration
Please refer to the [Configuration examples](../platform/platform/#configuration-examples) in the Platform chapter or visit the [Platform Awareness](../guides/ac/07 Platform Awareness/#configuration) section of the Active Components User Guide for awareness configuration. 

## Manually Connecting Platforms

TODO: how to manually add remote platforms

# Security

Thanks to mechanisms for global awareness and connectivity, in principle, any Jadex platform around the world may find and invoke any services of components on any other Jadex platform.  
Because this is usually undesired, the following security mechanisms exists. 
A full guide about security is available in the [Security Chapter](../guides/ac/08 Security/) of the Active Components Guide.

## Platform-Level Security

Platform-level security is based on passwords shared by the platforms that should be able to communicate with each other.
There are two kinds of passwords:  

### Platform passwords
By default, a Jadex platform is secured by a password which is generated at first startup and printed to the console. If a remote platform knows this password, it is able to find and invoke any services available on the local platform.  
This password-protection may be switched off by setting ```RootComponentConfiguration.setUsePass(false)``` (**NOT recommended!**)

Remote platforms can set platform passwords to access remote platforms within the [JCC Security Settings](../tools/05 Security Settings/#remote-platform-password-settings).

### Network passwords
As your application may include several platforms, it is more convenient to set-up a trusted network.
You can either configure this in the [JCC Security Settings](../tools/05 Security Settings/#network-password-settings) or by adding it to the startup configuration of every involved platform:

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
TODO

# Serialization
TODO

# Advanced Topics 

## Making the remote execution safe for different builds

When accessing the GUI remote you may have noticed the following message being printed to the console:
*Warning: Anonymous class without XML class name property (XML_CLASSNAME) / annotation (@XMLClassname): tutorial.BotGuiF3$1*.

This warning indicates a potential problem due to the Java language specification not describing a naming scheme for anonymous inner classes. Each java compiler decides for itself how to name an inner class (typically OuterClass$1, OuterClass$2, ...). This can cause incompatibilities when two platforms communicate that have been compiled using a different compiler (e.g. javac vs. eclipse). To allow proper mapping of inner classes you can specify an additional identifier using the @XMLClassname annotation:

-   For each inner class, add an *@XMLClassname("some_identifier")* annotation. Of course you should use different identifiers for each occurrence.
-   Access the GUI remotely and check if the warnings have vanished.
