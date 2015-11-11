<span>Chapter 8. External Processes</span> 
==========================================

One prominent application for agents is wrapping legacy systems and "agentifying" them. Hence, it is an important point how separate processes can interact with Jadex agents as these applications often use other means of communications such as sockets or RMI. A Jadex agent executes behavior sequentially and does not allow any parallel access to its internal structures due to integrity constraints. For this reason it is disallowed and discouraged to block the active plan thread e.g. by opening sockets and waiting for connections or simply by calling *Thread.sleep()*. This can cause the whole agent to hang because the agent waits for the completion of the current plan step. It will possibly abort the plan when the maximum plan step execution time has been exceeded. When external processes need to interact directly with the agent, they have to use methods from the so called *jadex.runtime.IExternalAccess* interface, which offers the most common agent methods.

<span>Exercise G1 - Socket Communication</span> 
-----------------------------------------------

We extend the simple translation agent from exercise C2 with a plan that sets up a server socket which listens for translation requests. Whenever a new request is issued (e.g. from a browser) a new goal containing the client connection is created and dispatched. The translation plan handles this translation goal and sends back some HTML content including some text and the translated word.

<div class="wikimodel-emptyline">

</div>

**Create a new file for the ServerPlanG1**

-   Declare the ServerSocket as attribute within the plan 


```java

protected ServerSocket server;

```


 

-   Create a constructor which takes the server port as argument and creates the server within it:


```java

try 
{
  this.server = new ServerSocket(port);
}
catch(IOException e) 
{
  throw new RuntimeException(e.getMessage());
}
getLogger().info("Created: "+server);

```


-   Additionally create a close method that can be used for shutting down the server socket:


```java

public void close() 
{
  try 
  {
    getExternalAccess().getLogger().info("Closing: "+server);
    server.close();
  }
  catch(IOException e) 
  {
    e.printStackTrace();
  }
}

```


 

-   In the body simply start a new thread that will handle client request in the run method. Additionally add an agent listener that gets invoked when the agent will be terminating. In this case the server is shut down:


```xml

new Thread(this).start();
getScope().addAgentListener(new IAgentListener() 
{
  public void agentTerminating(AgentEvent ae) 
  {
    close();
  }
  public void agentTerminated(AgentEvent ae) 
  {
  }
});

```


 

-   In the threads run method create and dispatch goals for every incoming request.

The external access's scheduleStep() method assures that modifications to the agent's goal base happen on the component step:


```java

while(true)
{
  final Socket client = server.accept();
  getExternalAccess().scheduleStep(new IComponentStep()
  {
    public Object execute(IInternalAccess ia)
    {
      IBDIInternalAccess scope = (IBDIInternalAccess)ia;
      IGoal goal = scope.getGoalbase().createGoal("translate");
      goal.getParameter("client").setValue(client);
      scope.getGoalbase().dispatchTopLevelGoal(goal);
      return null;
    }
  });
}

```


**Modify the EnglishGermanTranslationPlanG1 to handle translation goals**

-   Extract the socket from the goal and read the English word:


```java

Socket client = (Socket)getParameter("client").getValue();
BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
String request = in.readLine();
// Read the word to translate from the input string

```


 

-   Translate the word as usual by using the query
-   Send back answer to the client:


```java

PrintStream out = new PrintStream(client.getOutputStream());
out.print("HTTP/1.0 200 OK\r\n");
out.print("Content-type: text/html\r\n"); out.println("\r\n");
out.println("<html><head><title>TranslationG1 - "+eword+"</title></head><body>");
out.println("<p>Translated from english to german: "+eword+" = "+gword+".");
out.println("</p></body></html>");
client.close();

```


**Create a file TranslationG1.agent.xml by copying TranslationC2.agent.xml**

-   The addword plan and event declarations are not used and can be removed for clarity.
-   Introduce the translation goal type:


```xml

<achievegoal name="translate">
    <parameter name="client" class="java.net.Socket"/>
</achievegoal>

```


-   Introduce the new plan for setting up the server and start the plan initially:


```xml

<plan name="server">
  <parameter name="port" class="int">
    <value>9099</value>
  </parameter>
  <body class="ServerPlanG1"/>
</plan>
...
<configurations>
  <configuration name="default">
    <plans>
      <initialplan ref="server"/>
    </plans>
  </configuration>
</configurations>

```


 

-   Modify the trigger of the translation plan to react on translation goals and add a parameter for the client:


```xml

<plan name="egtrans">
  <parameter name="client" class="Socket">
    <goalmapping ref="translate.client"/>
  </parameter>
  <body class="EnglishGermanTranslationPlanG1"/>
  <trigger>
    <goal ref="translate"/>
  </trigger>
</plan>

```


**Start and test the agent**\\ Start the agent and open a browser to issue translation request. This can be done by entering the server url and appending the word to translate, e.g. <span class="wikiexternallink">[<span class="wikigeneratedlinkcontent">http://localhost:9099/dog.</span>](http://localhost:9099/dog.)</span> The result should be printed out in the returned web page.
