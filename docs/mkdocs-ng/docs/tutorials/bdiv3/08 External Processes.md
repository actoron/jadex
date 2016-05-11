# External Processes

One prominent application for agents is wrapping legacy systems and "agentifying" them. 
Hence, it is an important point how separate processes can interact with Jadex agents as these applications often use other means of communications such as sockets or RMI.  
A Jadex agent executes behavior sequentially and does not allow any parallel access to its internal structures due to integrity constraints.  
For this reason it is disallowed and discouraged to block the active plan thread e.g. by opening sockets and waiting for connections or simply by calling *Thread.sleep()*. 
This can cause the whole agent to hang because the agent waits for the completion of the current plan step. 
When external processes need to interact directly with the agent, they have to use methods from the so called *jadex.runtime.IExternalAccess* interface, which offers the most common agent methods.

Exercise G1 - Socket Communication
-----------------------------------------------

We create a simple translation with a plan that sets up a server socket which listens for translation requests. 
Whenever a new request is issued (e.g. from a browser) a new goal containing the client connection is created and dispatched. 
The translation plan handles this translation goal and sends back some HTML content including some text and the translated word.

-   Create a new agent class called TranslationBDI.
-   Add again the fields for the agent API and the word table.
-   Add another field called server that stores the ServerSocket.
-   Create a goal as inner class called Translate that has a field called client of type Socket. Also add a constructor with a parameter of type Socket and add getter/setter methods for it.


```java

@Goal
public class Translate
{
  protected Socket client;

  public Translate(Socket client)
  {
    this.client = client;
  }
  ...
}

```


-   Add an agent init method and first put the code for creating and initializing the word table. Afterwards we want to setup the server socket and listen for incoming client requests. To achieve this we create a Runnable that sets up the connection:


```java

Runnable run = new Runnable()
{			
  public void	run()
  {
    try
    {
      server = new ServerSocket(port);
    }
    catch(IOException e)
    {
      throw new RuntimeException(e.getMessage());
    }

```


-   If the socket could be opened we start waiting in an endless loop in a blocking fashion for incoming calls. Whenever a request is received we schedule a step on the agent and dispatch a translation goal with the new client socket. 


```java

while(true)
{
  final Socket client = server.accept();
  agent.scheduleStep(new IComponentStep<Void>()
  {
    @Classname("translate")
    public IFuture<Void> execute(IInternalAccess ia)
    {
      agent.dispatchTopLevelGoal(new Translate(client));
      return IFuture.DONE;
    }
  });
}

```


You need to put the loop code above in a try catch construct to shut down the server when a component termination exception occurs (i.e. the agent was terminated).


```java

if(server!=null)
{
  try
  {
    server.close();
  }
  catch(Exception e)
  {
  }
}

```


-   At the end of the agent init method start a new thread with the runnable using:


```java

Thread t = new Thread(run);
t.start();

```


-   What is still missing is the plan that reacts on the translation goals. We define it as a method with a corresponding trigger. In the body we process the HTTP request by parsing and reading the word from the socket and write back the translated word as HTTP response.


```java

@Plan(trigger=@Trigger(goals=Translate.class))
public void translate(Translate trans)
{
  Socket client = trans.getClient();

  try
  {
    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    String request = in.readLine();
    int slash = request.indexOf("/");
    int space = request.indexOf(" ", slash);
    String eword = request.substring(slash+1, space);
    String gword = wordtable.get(eword);
    System.out.println(request);
    PrintStream	out = new PrintStream(client.getOutputStream());
    out.print("HTTP/1.0 200 OKrn");
    out.print("Content-type: text/htmlrn");
    out.println("rn");
    out.println("<html><head><title>TranslationM1 - "+eword+"</title></head><body>");
    out.println("<p>Translated from english to german: "+eword+" = "+gword+".");
    out.println("</p></body></html>");
    out.flush();
    client.close();
  }
  catch(IOException e)
  {
    throw new RuntimeException(e.getMessage());
  }
} 

```


### Starting and testing the agent 
Start the agent and open a browser to issue translation request. This can be done by entering the server url and appending the word to translate, e.g. [http://localhost:9099/dog.](http://localhost:9099/dog.)  The result should be printed out in the returned web page.
