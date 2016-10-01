# Summary

how challenges are met in concrete example

## Challenges Addressed in the Example Application

In the following it will be shortly sketched, how the Jadex Active Components middleware helps coping with the challenges laid out in the [introduction](../01 Introduction/).

**Challenge 1: Discovery of distributed components**

The time user components need to discover the available time provider components. In Jadex discovery is supported by a combination of two mechanisms. First, [platform awareness](../../remote/remote/#awareness) automatically discovers all available Jadex platforms in local networks and potentially across the whole Internet. Second, the [service search](../../guides/ac/05 Services/#service-search) potentially traverses all known platforms looking for the desired service and thus potentially finds any matching service available somewhere on the Internet.

**Challenge 2: Components with internal behavior**

The time providers need to periodically send out time values. In Jadex, components may have internal behavior ranging from [purely reactive components to simple task-oriented](../../guides/ac/02 Active Components/#active-components) or even [complex intelligent agents](../../guides/bdiv3/02 Concepts/). E.g. the time provider component has a periodic task for sending out time values.

**Challenge 3: Designing communication protocols**

The communication between time user and time provider needs to be defined. Similar to discovery, in Jadex, communication is dealt with on the platform and on the component level. A set of message transports assures that platforms can communicate in local networks as well as across the Internet. On the component level, interaction typically uses object-oriented interfaces, e.g. the use of services through remote method invocation (RMI). Furthermore, Jadex comes with ready to use implementations for [commonly used interaction patterns](../../futures/futures/) like publish/subscribe.

**Challenge 4: Handling partial failures**

Due to node or network failures, time user components may not always be able to correctly unsubscribe at the time providers. Thus time providers should automatically unsubscribe clients, which are no longer responding. Otherwise time providers would accumulate broken clients and quickly run into memory leaks. Using the available interaction pattern for publish/subscribe, Jadex will automatically detect failed clients and inform the time provider to remove the client.

**Challenge 5: Security**

Providing and accessing services across the Internet involves many [security issues](../../remote/remote/#security). In Jadex, by default only trusted platform may invoke services of each other, therefore running a Jadex platform is safe by default. More fine-grained treatment of security issues is supported by security annotations. These annotations can be placed alongside the component code and allows a clean separation between component functionality and non-functional aspects like security.

##

where to go from here
