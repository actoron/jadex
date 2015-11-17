<span>Chapter 01 - Introduction</span> 
======================================

This tutorial provides a quick start for using Jadex. It is intended for people who just like to "jump in" and quickly getting things to run. Furthermore this tutorial provides many pointers to other documentation pages that you can follow if you want to learn a bit more about a certain topic.

<span>Purpose of the Example Application</span> 
-----------------------------------------------

In this tutorial, a small distributed system will be built in which time clients can subscribe to time servers for continuously receiving the current time of the server. Therefore time user components will search for available time provider components and subscribe to all providers they can find (see Fig.1a). The time providers remember the subscribed time user components. They then periodically (e.g. every 5 seconds) send their current time to all subscribed time users components (see Fig.1b).

a\) ![01 Introduction@subscribe.png](subscribe.png) b) ![01 Introduction@publish.png](publish.png)\
*Figure 1: a) Time users subscribing to time providers, b) time providers publishing their current time to time users*

Although this application is quite simple, it contains several common challenges regarding programming distributed systems. In the following it will be shortly sketched, how the Jadex Active Components middleware helps coping with these challenges:

**Challenge 1: Discovery of distributed components**

The time user components need to discover the available time provider components. In Jadex discovery is supported by a combination of two mechanisms. First, <span class="wikiexternallink">[platform awareness](AC%20User%20Guide/07%20Platform%20Awareness)</span> automatically discovers all available Jadex platforms in local networks and potentially across the whole internet. Second, the <span class="wikiexternallink">[service search](AC%20User%20Guide/05%20Services)</span> potentially traverses all known platforms looking for the desired service and thus potentially finds any matching service available somewhere on the Internet.

**Challenge 2: Components with internal behavior**

The time providers need to periodically send out time values. In Jadex, components may have internal behavior ranging from <span class="wikiexternallink">[purely reactive components to simple task-oriented](AC%20User%20Guide/02%20Active%20Components)</span> or even <span class="wikiexternallink">[complex intelligent agents](BDI%20User%20Guide/02%20Concepts)</span>. E.g. the time provider component has a periodic task for sending out time values.

**Challenge 3: Designing communication protocols**

The communication between time user and time provider needs to be defined. Similar to discovery, in Jadex, communication is dealt with on the platform and on the component level. A set of message transports assures that platforms can communicate in local networks as well as across the Internet. On the component level, interaction typically uses object-oriented interfaces, e.g. the use of services through remote method invocation (RMI). Furthermore, Jadex comes with ready to use implementations for <span class="wikiexternallink">[commonly used interaction patterns](AC%20User%20Guide/03%20Asynchronous%20Programming)</span> like publish/subscribe.

**Challenge 4: Handling partial failures**

Due to node or network failures, time user components may not always be able to correctly unsubscribe at the time providers. Thus time providers should automatically unsubscribe clients, which are no longer responding. Otherwise time providers would accumulate broken clients and quickly run into memory leaks. Using the available interaction pattern for publish/subscribe, Jadex will automatically detect failed clients and inform the time provider to remove the client.

**Challenge 5: Security**

Providing and accessing services across the Internet involves many <span class="wikiexternallink">[security issues](AC%20User%20Guide/08%20Security)</span>. In Jadex, by default only trusted platform may invoke services of each other, therefore running a Jadex platform is safe by default. More fine-grained treatment of security issues is supported by security annotations. These annotations can be placed alongside the component code and allows a clean separation between component functionality and non-functional aspects like security.

<span>Application Architecture</span> 
-------------------------------------

The architecture of the system is shown in Fig.2 as a UML class diagram. The time service interface is the central aspect of the design. The interface is used by the time user agent and it is implemented by the time provider agent. Time user and time provider do not know each other directly and only communicate through the time service interface.\
In the following three chapters each of the three elements of the architecture will be explained in detail.

![01 Introduction@timearch.png](timearch.png)\
*Figure 2: UML class diagram of the time user / time provider system architecture*
