# Remote
Applications built with Jadex Active Components can be easily distributed across platforms, meaning that [service discovery](../services/services/#service-scopes) and [accessing services](../services/services/#accessing-services) works the same way, no matter if the target service is available at a local or remote platform.

However, one additional aspect has to be addressed in the remote case: How can *platforms* discover each other to share services?

# Awareness
To solve the platform discovery problem, Jadex introduces a mechanism called *Awareness*.
The goal is the detection of all available remote platforms in order to seamlessly communicate between each other if required. 

To achieve this, several different kinds of discovery mechanisms are used:

-   **Broadcast discovery** (enabled per default, local network): Uses IP broadcast to announce awareness infos. It uses the default port '55670'. As IP broadcast is only availble in IPV4 networks this mechanism will not work in pure IPV6 environments.
-   **Multicast discovery** (enabled per default, local network): This mechanism uses IP multicasts to find other platforms. Per default it uses multicast address 224.0.0.0 and port 55667. As multicast requires receivers to register at the multicast address this mode does not send packets to other no Jadex nodes in the network.
-   **IP-Scanner discovery** (disabled per default, local network): The scanner tries to find out the network type and send awareness infos to IP addresses within the network. The default port the scanner uses is 55668. Please note that ip scanning might conflict with the policies of your network administrator. First, the scanner floods the network with messages and second sending out messages to a bunch of network ips is\
    sometimes considered to be an indication for virus behavior. For these reasons the scanner is deactivated per default.
-   **Registry discovery** (disabled per default, global network): The registry mechanism allows for using a dedicated registry platform at which all other platforms register at runtime (using the address argument). The registry distributes its entries to all platforms. If you want to use the registry you should provide it with a unique platform id (otherwise it will use some fallback that is not guaranteed to be online).
-   **Message discovery** (enabled per default, global network): Message discovery is based on message receipt of other platforms. Whenever a message is received the message service will forward it to this discovery agent which subsequently announces a new platform. Using message discovery is especially beneficial in asymmetric network settings, in which one partner can find the other but not vice versa. This e.g. occurss with broadcast or multicast in virtual networks using NAT (e.g. VirtualBox or Android emulator).
-   **Relay discovery** (enabled per default, global network): The relay discovery is based on a web server that is used as a common rendezvouz point for the platforms, i.e. the web server distributes awareness infos among the currently connected nodes. Per default the relay uses a server at http://jadex.informatik.uni-hamburg.de/relay/ ](http://jadex.informatik.uni-hamburg.de/relay/) ](http://jadex.informatik.uni-hamburg.de/relay/) , but further servers are planned to be used. It is also possible to setup an own server using the relay WAR file from the downloads section, which allows for building up private platform networks.

TODO : more info link



Configuration
--------------------------

Awareness can be turned on/off with the argument: 





**-awareness true/false**\
\
This will start the platform with or without the awareness component. If you want to disable awareness at runtime it is sufficient to kill the 'awa' component. On the other hand it is also possible to enable awareness at runtime by starting the awareness component (*jadex.base.service.awareness.management.AwarenessManagementAgent*) contained in the module *jadex-platform-base*. In addition to the global awareness setting it is also possible to determine the awareness mechanisms that should be used. This can be done by using the argument: 





**-awamechanisms new String\[\]{"Broadcast", "Multicast", "Message", "Relay"}**





At runtime the currently active awareness mechanisms can be seen by looking at the subcomponents of the awareness management component. To deactivate or activate mechanisms at runtime again subcomponents can be started or stopped. Furthermore, the delay between awareness announcements can be configured using the **-awadelay** argument, which per default is 20 seconds. This delay is propagted down to all awareness mechanisms at startup. At runtime the awareness settings can be further customized. For this puropose the awareness settings JCC plugin is available. 

# Manual Connect

TODO: how to manually add remote platforms

# Security

Full-guide: [AC User guide](../guides/ac/08 Security/)

Thanks to mechanisms for global awareness and connectivity, in principle, any Jadex platform around the world may find and invoke any services of components on any other Jadex platform. In practice, of course, access to platforms and provided services needs to be restricted to appropriate groups of users. In this chapter you will learn how to specify or relax the default security restrictions of Jadex services in your application code. Furthermore you will learn how to configure your platforms to enable restricted access to other platforms.

## Exercise G1 - Making the Chat Publicly Available

The security lecvel of services and their methods can be adjusted by the *@Security* annotation (package *jadex.bridge.service.annotation*). Currently, two levels are supported (other more fine-grained levels my be added later). The default level for all services is *PASSWORD* and allows access only to platforms, which have some sort of security credentials for the invoked platform. The other level is *UNRESTRICTED* and allows access to any platform.

### Starting two different Platforms

Currently, your chat service has the default security level *PASSWORD* and therefore cannot be accessed from other platforms. To verify this behavior, start two platforms with different names. E.g. in eclipse, duplicate your launch configuration (cf. [Installation](./02 Installation) ) and add the following in the programm arguments section: *-platformname platform2_*.
Start the chat component on each platform (e.g. *ChatD2*) and check that chat messages are not sent between the platforms.

### Changing the security level of the service

Edit the *IChatService.java* and add a corresponding security annotation.

```java

@Security(Security.UNRESTRICTED)
public interface IChatService 
{
...
}

```

Restart the two platform and verify that that messages are exchanged. Note, that the annotation can be added to the service as a whole, but also seperately to the service methods. The annotation of a method takes precedence over the annotation of the service as a whole. E.g. when the service itself is unrestricted but the method has an additional annotation with level *PASSWORD*, the service can be found but the method can not be accessed from outside platforms.

## Exercise G2 - Accessing Restricted Services

In the Jadex Control Center, you can edit the security settings of a platform. This allows e.g. to set the password of the platform, but also to add known passwords of other platforms. Furthermore, you can setup trusted network zones. Using these security settings you can enable service calls between components, even when the services are restricted.

For this exercise, remove the security annotations from the chat service interface. Start two platforms as above and verify that the platforms do not communicate. In the following, different techniques are described to enable the restricted access.



### Platform passwords

At startup, each Jadex platform prints out the platform as stored in its *.settings.xml*. Find the password of the second platform in the console. Go to the starter panel in the JCC of the first platform, unfold the *platforms* node and right-klick on the node of the second platform (see below).

![](set_password.png)  
*Entering a password for a remote platform*

Enter the password of the second platform in the appearing dialog. The first platform should now be able to access all services of the second platform. As a result, the node of the second platform will change its color to green. Repeat the process by entering the first platform's password in the second platform's JCC. Now the two chat components should be able to communicate with each other.

### Setting up a trusted network

In the following an alternative setup for allowing restricted access is described. To be able to test if it works, reset the password settings by deleting the *platform2.settings.xml* file. When you now start the second platform it will generate a different password and also no longers knows about the first platform's password. Therefore chat communication between the two platforms will be disabled.

Now open the security panel in the first platforms JCC, which is identified by a lock symbol. In the text fields at the bottom, enter a network name of your choice and click 'Add'. Add the same network name in the second platform. Instead of having separate passwords for each platform, the security network settings allow establishing a group of platforms that allow access to each other. While not strictly necessary, you can also add a password for the network. You can also add multiple networks for platforms that should be present in more than one group. As long as two platforms share a at least one network name with the same password (or no password for both), they will allow restricted communication.

Start a chat component on each platform and verify if it works. Further details about security issues and settings can be found in the [security settings chapter in the tool guide](../AC%20Tool%20Guide/05%20Security%20Settings%20) .


# Transports

# Serialization

### Making the remote execution safe for different builds

When accessing the GUI remote you may have noticed the following message being printed to the console:
*Warning: Anonymous class without XML class name property (XML_CLASSNAME) / annotation (@XMLClassname): tutorial.BotGuiF3$1*.

This warning indicates a potential problem due to the Java language specification not describing a naming scheme for anonymous inner classes. Each java compiler decides for itself how to name an inner class (typically OuterClass$1, OuterClass$2, ...). This can cause incompatibilities when two platforms communicate that have been compiled using a different compiler (e.g. javac vs. eclipse). To allow proper mapping of inner classes you can specify an additional identifier using the @XMLClassname annotation:

-   Copy the F3 files into new F4 files, changing the all occurrences of F3 to F4 accordingly.
-   For each inner *IComponentStep* class, add an *@XMLClassname("some_identifier")* annotation. Of course you should use different identifiers for each occurrence (three in total).
-   Access the GUI remotely and check if the warnings have vanished.

