
## Exercise H3 - Creating a component

Now that we have the platform access available, we can do everything programmatically that we could also do with the JCC. Therefore we can access running components and also start and stop new components as needed.

In this exercise, we use the platform access to obtain the component management service (CMS). We then use the CMS to create a chat component.

### Obtain the CMS

-   Copy the *MainH2* class to a new file *MainH3.java*.
-   Extend the main method to search for the CMS service. For this purpose, use the static helper class *SServiceProvider* from package *jadex.bridge.service.search* as follows:


```java

IComponentManagementService cms = SServiceProvider.getService(platform.getServiceProvider(),
  IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);

```


The *SServiceProvider* class provides access to the service search mechanism and further provides helper methods for dealing with required and provided services of components. Here, we use the *getService(...)* method for searching for a service. The supplied service provider represents the entry point for the search, from which the components to be searched are derived using the search scope. Here we use the service provider of the platform. The cms interface class is supplied to indicate the type of service we are interested in. Finally, the search scope *platform* states that all components and subcomponents of the platform should be searched.

The result of the *getService(...)* method is a future of the corresponding service type. The application is still running on the Java main thread, therefore it is safe to use again the thread suspendable for blocking until the search result is available.

### Create a component programatically

-   On the cms, invoke the *createComponent(...)* method.
-   Most arguments can be set to *null*, only the second argument (*model*) is required.
-   As model, supply the class name of the chat component and append *".class"*. Use the chat component from Exercise D2.
-   The result is a future of the component identifier of the newly created component.
-   Wait for the component identifier using the thread suspendably and afterwards print the id to the console.
-   The resulting code should look as follows:

```java

IComponentIdentifier cid = cms.createComponent(null, ChatD2Agent.class.getName()+".class", null, null).get(sus);
System.out.println("Started chat component: "+cid);

```


The arguments to the *createComponent(...)* method are as follows:

-   **name**: The name of the new component. When set to null, a name is automatically generated.
-   **model**: The file name of the component model. Here we derive the name from the chat component built in Exercise D2 to avoid typos. You could also supply a string directly, e.g. *"mypackage/MyAgent.class"* or *"mypackage/My.component.xml"*.
-   **info**: An object of type *jadex.bridge.service.types.cms.CreationInfo*, which can be used to supply various options like component arguments, parent, etc. Can be set to null if no special options are required.
-   **resultlistener**: A result listener that is called when the created component terminates. The result listener will be given the result values that are produced by the component.

The method returns a future result and asynchronously starts initiating the new component in the background. When the new component could be started successfully, its component identifier is provided as a result. The result is only made available after all init code of the component and its services (if any) has completed.

### Test the application

-   Start the application using a new or updated launch configuration for the *MainH3* class.
-   The chat window should appear indicating that the chat component was created.
-   Test the chat by sending some messages.
-   Close the chat window. The Java VM will exit, because as a default, the Jadex platform shuts down when no more application components are running.

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