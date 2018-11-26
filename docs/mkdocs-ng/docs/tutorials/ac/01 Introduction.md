# Introduction

This tutorial provides step-by-step instructions to learn how to use the Jadex Active Components concept for developing applications. You will learn how to compose systems from components. 

The following topics are covered in the upcoming chapters:

-   [Chapter 03 Active Components ](../03 Active Components)  illustrates how to program simple components.
-   [Chapter 04 Required Services ](../04 Required Services)  describes how to fetch and use services of other components.
-   [Chapter 05 Provided Services ](../05 Provided Services)  explains how to equip a component with services.
-   [Chapter 06 Composition ](../06 Composition)  describes how to compose a component from subcomponents.
-   [Chapter 07 External Access ](../07 External Access)  describes how to attach tightly coupled functionality, e.g. for GUIs.

# Application Context

In this tutorial a simple chat application will be implemented. The chat application can be used to send messages to other users. This base functionality will be extended in the different exercises, but it is not our goal to build up a solution that combines all the extensions, because this would lead to difficulties concerning the complexity of the application. Instead this tutorial will concentrate on setting up simple components that explain the Jadex concepts step by step.

![AC Tutorial.01 Introduction@chatdesign.png](chatdesign.png)  
*Conceptual design of the Chat application*

The figure above shows the conceptual design of the chat application. On different computers, so called 'Chat' components are running, each of which provides a graphical interface to a local user. When a user enters a new chat message (e.g. in 'Chat 1'), the message gets forwarded to all chat components in the network (e.g. 'Chat 2' and 'Chat 3).

We will come back to this design in [05 Provided Services](../05 Provided Services), where we put all the pieces together that allow us building an initial working version of this chat application.


# Prerequisites
Before you can start with this tutorial, please follow the steps in our [Getting Started](../../../getting-started/getting-started/#ide-setup)) section to setup your IDE.
