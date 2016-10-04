# Summary

This section shortly recaptures the lessons learned in the previous sections and provides pointer where to go next.

## Challenges Addressed in the Example Application

In the following it will be shortly sketched, how the Jadex Active Components middleware helps coping with the challenges laid out in the [introduction](../01 Introduction/).

**Challenge 1: Discovery of distributed components**

*"The time user components need to discover the available time provider components. This requires a) some form of service description, b) a way to publish service descriptions and c) a search mechanism for finding matching services in the network."*

a) In Jadex, services are described by POJO Java interfaces.

b) Publishing of services is as simple as adding a *@ProvidedServices* annotation to a Java class.
 
c) In Jadex discovery is supported by a combination of two mechanisms. First, [platform awareness](../../remote/remote/#awareness) automatically discovers all available Jadex platforms in local networks and potentially across the whole Internet. Second, the [service search](../../guides/ac/05 Services/#service-search) potentially traverses all known platforms looking for the desired service and thus potentially finds any matching service available somewhere on the Internet.

**Challenge 2: Components with internal behavior**

*"The time providers need to periodically send out time values. Unlike purely passive servers like, e.g., static web applications, the server component requires some active internal behavior. In addition, concurrency between client requests and also with respect to internal behavior needs to be handled to assure consistency of the time provider's internal state."*

In Jadex, components may have internal behavior ranging from [purely reactive components to simple task-oriented](../../guides/ac/02 Active Components/#active-components) or even [complex intelligent agents](../../guides/bdiv3/02 Concepts/). Jadex components are executed using a single-thread cooperative scheduling approach, i.e., each component step is executed until it ends or blocks and no two steps of the same component are ever executed in parallel.

E.g. the time provider component's periodic task for sending out time values is executed as a step and the addition of a new subscriber is another step. No manual synchronization is necessary, e.g. regarding the subscriptions list, because Jadex assures that these two steps will never be interleaved. 

**Challenge 3: Designing communication protocols**

*"The communication between time user and time provider needs to be defined including, on the protocol level, the allowed sequences of messages and, on the application level, the data format for the transferred information."*

In Jadex, a set of message transports assures that platforms can communicate in local networks as well as across the Internet. For the protosol level, Jadex comes with ready to use implementations for [commonly used interaction patterns](../../futures/futures/) like publish/subscribe.

On the application level, interaction may use classes and interfaces as needed for the data types. Jadex provides automatic conversion of objects to transer formats like binary, JSON and XML.

[//]: # (*todo: ref to conversion docs?*)

**Challenge 4: Handling partial failures**

*"Due to node or network failures, time user components may not always be able to correctly unsubscribe at the time providers. Thus time providers should automatically unsubscribe clients, which are no longer responding. Otherwise time providers would accumulate broken clients and quickly run into memory leaks."*

Using the available interaction pattern for publish/subscribe, Jadex will automatically detect failed clients and inform the time provider to remove the client.

**Challenge 5: Security**

*"Providing and accessing services across the Internet involves many security issues. E.g. potentially security critical services should by default be shielded from unauthorized access. On the other hand, no complicated security setup should be necessary for uncritical services like the time service."* 

In Jadex, by default only trusted platform may invoke services of each other, therefore running a Jadex platform is safe by default. More fine-grained treatment of security issues is supported by security annotations. These annotations can be placed alongside the component code and allows a clean separation between component functionality and non-functional aspects like security. E.g., the *Security.UNRESTRICTED* flag is used on the time service interface to mark the service as being safe to be called from the outside without prior authentication or authorization.

## Where To Go Next?

There are several possible paths for further exploring the features of Jadex. For practical experiences, you can continue by looking at examples provided in the *jadex-application-xyz* jars. These can be started after adding the jar to the JCC starter panel. Some examples are also available online as [webstart applications](https://www.activecomponents.org/index.html#docs/examples) or as [web applications](http://www.activecomponents.org/jadex-applications-web/).

If you prefer more reading, the [documentation overview page](https://www.activecomponents.org/index.html#/docs/overview) guides you to the available documentation. In case you would like more hands-on exercises, you can choose from a set of [tutorials](../../), which are specifically designed to introduce a specific Jadex component type.