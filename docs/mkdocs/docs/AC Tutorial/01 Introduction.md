<span>Chapter 1 - Introduction</span> 
=====================================

The Active Components project aims at providing programming and execution facilities for distributed and concurrent systems. The general idea is to consider systems to be composed of components acting as service providers and consumers. Hence, it is very similar to the Service Component Architecture (SCA) approach and extends it in the direction of agents. In contrast to SCA, components are always active entities, i.e. they posses autonomy with respect to what they do and when they perform actions making them akin to agents. In contrast to agents communication is preferably done using service invocations. 

<div class="wikimodel-emptyline">

</div>

<div class="wikimodel-emptyline">

</div>

This tutorial provides step-by-step instructions to learn how to use the Jadex active components infrastructure. You will learn how to install and use the infrastructure (i.e. the Jadex platform) to execute components and how to compose systems from components. In particular, the following topics are covered in the upcoming chapters:

-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">02 Installation</span>](02%20Installation)</span> describes the steps necessary to install and run the Jadex Active Components runtime.
-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">03 Active Components</span>](03%20Active%20Components)</span> illustrates how to program simple components.
-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">04 Required Services</span>](04%20Required%20Services)</span> describes how to fetch and use services of other components.
-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">05 Provided Services</span>](05%20Provided%20Services)</span> explains how to equip a component with services.
-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">06 Composition</span>](06%20Composition)</span> describes how to compose a component from subcomponents.
-   Chapter <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">07 External Access</span>](07%20External%20Access)</span> describes how to attach tightly coupled functionality, e.g. for GUIs.

<span>Application Context</span> 
--------------------------------

In this tutorial a simple chat application will be implemented. The chat application can be used to send messages to other users. This base functionality will be extended in the different exercises, but it is not our goal to build up a solution that combines all the extensions, because this would lead to difficulties concerning the complexity of the application. Instead this tutorial will concentrate on setting up simple components that explain the Jadex concepts step by step.

<div class="wikimodel-emptyline">

</div>

![AC Tutorial.01 Introduction@chatdesign.png](chatdesign.png)  
*Conceptual design of the Chat application*

<div class="wikimodel-emptyline">

</div>

The figure above shows the conceptual design of the chat application. On different computers, so called 'Chat' components are running, each of which provides a graphical interface to a local user. When a user enters a new chat message (e.g. in 'Chat 1'), the message gets forwarded to all chat components in the network (e.g. 'Chat 2' and 'Chat 3).

We will come back to this design in <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">05 Provided Services</span>](05%20Provided%20Services)</span>, where we put all the pieces together that allow us building an initial working version of this chat application.
