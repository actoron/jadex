# Services TODO

- provide a way for interaction between components


## Exercise H4 - Accessing a Component

When integrating your components into some application, you often need some form of coordination and/or data exchange between the components and the remaining application code outside of Jadex. The natural way for realizing this is using services of your components. Sometimes, you may be able to reuse the services that are already present in your components. Otherwise, you need to devise new service interfaces that capture the interaction requirements between a component and outside code.

In this exercise we obtain the chat service of the chat component to print a welcome message in the GUI.

### Obtain the Chat Service

-   Copy the *MainH2* class to a new file *MainH3.java*.
-   Use the *SServiceProvider* helper class to obtain a service from the created chat component. The method *getService(provider, cid, type)* allows fecthing a declared service of a specific component directly.
-   Call the *message(...)* method of the chat service to display a startup message.
-   The required code looks as follows:


```java

IChatService chat = SServiceProvider.getService(platform.getServiceProvider(), cid, IChatService.class).get(sus);
chat.message("Main", "Chat started.");

```


Note that we use the service provider of the platform as search entry point, but we specify the cid of the chat component. Thus we state that we want to search for the *IChatService* only in this specific chat component. As before, the suspendable is used to wait and fetch the future result value. The obtained chat service object can be used to invoke the methods of the chat service object.


## Accessing services
The *SServiceProvider* class provides access to the service search mechanism and further provides helper methods for dealing with required and provided services of components. Here, we use the *getService(...)* method for searching for a service. The supplied service provider represents the entry point for the search, from which the components to be searched are derived using the search scope. Here we use the service provider of the platform. The cms interface class is supplied to indicate the type of service we are interested in. Finally, the search scope *platform* states that all components and subcomponents of the platform should be searched.

The result of the *getService(...)* method is a future of the corresponding service type. The application is still running on the Java main thread, therefore it is safe to use again the thread suspendable for blocking until the search result is available.