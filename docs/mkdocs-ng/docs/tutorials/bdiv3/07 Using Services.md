# Using Services

So far we have explored how BDI can be used to define the internal behaviour of an agent. 
In this part we move on towards multi-agent scenarios and show how a BDI agents can be designed to interact with each other. The typical way for realizing interactions with active components is using [services](../../services/services/).
 
A service is defined by an interface that determines the available methods and a service implementation that can be either a separate class of just part of the agent itself.  
For further details please have a look at the  [services section](../../services/services/).

# F1 - Creating a Service

In the first exercise we will equip the translation agent with a corresponding service. 
We will additionally create a user agent that opens a small user interface.  
The user interface allows for entering English words that will be translated on request. 
Internally, the user agent searches for a translation service and delegates the request to it. 

-   First create a new Java interface called *ITranslationService*. Add a method called *translateEnglishGerman* to it. The method should take a String parameter called *eword* and return a [futurized](../../futures/futures/) String (```IFuture<String>```):

```java
public interface ITranslationService
{
  public IFuture<String> translateEnglishGerman(String eword);
}
```

-   Create a Java class called *TranslationBDI* that implements the translation interface. 
-   Add the ```@Agent``` and ```@Service``` annotations to the class, as this agent will represent the service implementation. 
-   Furthermore, add a new provided service using the ```@ProvidedServices``` annotation. Set the type of the provided service to *ITranslationService*:

```java
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITranslationService.class))
public class TranslationBDI implements ITranslationService
{
  ...
}
```

-   Add a *wordtable* field to the agent. As in previous lectures declare it with the type ```Map<String, String>```.  
-   Add an agent init method using the ```@AgentCreated``` annotation. Initialize the wordtable and add some word pairs to it.
-   Implement the interface method by just looking up the word in the map and returning it via a new future:

```java
public IFuture<String> translateEnglishGerman(String eword)
{
  String gword = wordtable.get(eword);
  return new Future<String>(gword);
}
```

-   Create a new Java class called *UserBDI*, annotated with ```@Agent```.
-   Add a field *agent* for the Agent API, annotated with ```@Agent```. 
-   Add an agent body method (```@AgentBody```) that creates the user interface. To simplify this task the corresponding code is shown below.  

```java
@Agent
public class UserBDI
{
    @Agent
    protected IInternalAccess agent;
	
    @AgentBody
    public void body()
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame();
                PropertiesPanel pp = new PropertiesPanel();
                final JTextField tfe = pp.createTextField("English Word", "dog", true);
                final JTextField tfg = pp.createTextField("German Word");
                JButton bt = pp.createButton("Initiate", "Translate");
                f.add(pp, BorderLayout.CENTER);
                f.pack();
                f.setLocation(SGUI.calculateMiddlePosition(f));
                f.setVisible(true);
            }
        });
    }
```

Inside of the body method first a thread switch to the Swing thread is performed (using ```SwingUtilities.invokeLater```). Within the runnable that is executed by Swing first a JFrame is created. Two textfields and one button are added. The rest of the code is in charge of displaying the gui at the center of the screen. 

<x-hint title="Swing Thread">
It is a general Swing requirement that all gui related actions should always be performed only on the Swing thread. Otherwise you might encounter strange behavior due to race conditions.
</x-hint>

-   One last part is missing. If a user enters a word that should be translated and presses the Translate button a service invocation has to be created.  
For this purpose add an inline action listener to the button and in its ```actionPerformed``` method look up a translation service.  
If found, invoke the translation method and display the result  (or the error that occurred) in the other textfield:

```java
bt.addActionListener(new ActionListener()
{
    public void actionPerformed(ActionEvent e)
    {
        SServiceProvider.getServices(agent, ITranslationService.class, ServiceScope.PLATFORM)
            .addResultListener(new IntermediateDefaultResultListener<ITranslationService>()
        {
            public void intermediateResultAvailable(ITranslationService ts)
            {
                ts.translateEnglishGerman(tfe.getText())
                    .addResultListener(new SwingResultListener<String>(new IResultListener<String>()
                {
                    public void resultAvailable(String gword) 
                    {
                        tfg.setText(gword);
                    }
                    
                    public void exceptionOccurred(Exception exception)
                    {
                        exception.printStackTrace();
                        tfg.setText(exception.getMessage());
                    }
                }));
            }
        });
    }
});
```

For more information about service invocation, visit the [Services](../../services/services/#using-services) chapter.

** Starting and testing the agents **

Start both agents. The user interface should appear after the user agent has been started. Enter a word and press the Translate button. You should see the translated word appearing immediately in the text field below. 

# F2 - Mapping a Service to Plans

<!--One of the strength of BDI is that it provides a flexible runtime execution by selecting suitable plans at runtime. -->
<!--This concept cannot only be used with goals but also directly with plans.-->
In Jadex BDIV3, we can create plans and define that these plans realize a service call.
In this case an incoming service call is automatically delegated to a suitable plan.  
Let's try this:

-   The service interface and the user agent need no changes. Just copy them from the last exercise. Also copy the TranslationBDI class file, but change it as follows:
-  Remove the existing ```@ProvidedServices``` annotation.
-  Now we need to state that we want the BDI agent to implement the translation interface via plans.   
This is done by declaring the implementation of the provided service to be the BDIAgent:
```java
@ProvidedServices(@ProvidedService(name="transser", type=ITranslationService.class, 
  implementation=@Implementation(IBDIAgent.class)))
```

-   Additionally remove the ```extends ITranslationService``` part of the class definition. The interface is now only implemented indirectly via plans. Hence, also remove the ```translateEnglishGerman``` method from the agent completely.

-   Add a new plan that uses the dictionary to translate words. We want to execute this plan only if the word is contained in the dictionary. Thus, we use an inner class as plan and add a precondition method:

```java
public class TranslatePlan
{
    @PlanPrecondition
    public boolean checkPrecondition(Object[] params)
    {
        return wordtable.containsKey(params[0]);
    }
}
```

-   The Plan has to declare a ```@ServiceTrigger``` that links the service interface to the Plan. If your service interface has more than one method, you can pass a *method* argument to this annotation.

```java
@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
```

-   Additionally, we add a plan body that takes as argument an object array representing the parameters of the service call. We need to fetch the first parameter, cast it to String and look it up in the dictionary:

```java
@PlanBody
public String body(Object[] params)
{
    String eword = (String)params[0];
    String gword = wordtable.get(eword);
    System.out.println("Translated with internal dictionary dictionary: "+eword+" - "+gword);
    return gword;
}
```

-   We add a second plan that will allow us to translate words *not* contained in the internal dictionary. 
Instead we will use an online dictionary and look up the word. The result is retrieved as html page which needs to be parsed to extract the translation.  
This plan uses the same trigger as the other plan, but has no precondition as it can translate any word.
The parsing code is included below:

```java
@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
public String internetTranslate(Object[] params)
{
  String eword = (String)params[0];
  String ret = null;
  try
  {
    URL dict = new URL("http://wolfram.schneider.org/dict/dict.cgi?query="+eword);
    System.out.println("Following translations were found online at: "+dict);
    BufferedReader in = new BufferedReader(new InputStreamReader(dict.openStream()));
    String inline;
    while((inline = in.readLine())!=null)
    {
      if(inline.indexOf("<td>")!=-1 && inline.indexOf(eword)!=-1)
      {
        try
        {
          int start = inline.indexOf("<td>")+4;
          int end = inline.indexOf("</td", start);
          String worda = inline.substring(start, end);
          start = inline.indexOf("<td", start);
          start = inline.indexOf(">", start);
          end = inline.indexOf("</td", start);
          String wordb = inline.substring(start, end==-1? inline.length()-1: end);
          wordb = wordb.replaceAll("<b>", "");
          wordb = wordb.replaceAll("</b>", "");
	  ret = worda;
          System.out.println("Translated with internet dictionary: "+worda+" - "+wordb);
        }
	catch(Exception e)
	{
          System.out.println(inline);
        }
      }
    }
    in.close();
  }
  catch(Exception e)
  {
    e.printStackTrace();
    throw new PlanFailureException(e.getMessage());
  }
  return ret;
}
```

** Starting and testing the agents **

Again, start both agents. Now try out if internal as well as internet translations are displayed when entering translation requests in the gui.

**Note:** If no translations show up, make sure that your TranslationBDI agent as well as your UserBDI agent **are referencing the same service interface** ("f2.ITranslationService") in their imports!

<x-hint title="Plan-service-mapping restrictions">
Please note that when mapping plan to a service interface only one plan is executed and no retries are performed.
If this is required, we need to map the service to a goal and not a plan as described in the next exercise.
</x-hint>

# F3 - Goal Delegation

Sometimes, mapping a service call to goal is more appropriate than a plan. This is the case if the BDI means-end reasong should be used for executing the service call.   
Another advantage of a service to goal mapping is that it allows for goal delegation between different agents. 
This means we can just create a translation goal in the user agent and dispatch it.  
The goal will automatically be forwarded (as service call) to the translation agent which will reify the call to a goal and try to achieve it.

-   Copy the *ITranslationService* interface, the *UserBDI* and *TranslationBDI* classes from the previous exercise.
-   Create a new class called *TranslationGoal* representating the shared goal between both agents. For this reason we do not want to define it as inner class of one of the agents.  
-   Use the ```@Goal``` annotation to mark it as goal. 
-   Add two fields of type String: one called *gword* and one called *eword*. Make *eword* become a goal parameter (```@GoalParameter```) and *gword* become the goal result (```@GoalResult```). 
-   Add a constructor taking *eword* as parameter and generate getter/setter methods for both fields.

```java
@Goal
public class TranslationGoal
{
  @GoalResult
  protected String gword;

  @GoalParameter
  protected String eword;

  public TranslationGoal(String eword)
  {
    this.eword = eword;
  }
}
```

-   In the user agent, we need to inject the execution feature and the BDI agent feature:
```java
@AgentFeature
protected IExecutionFeature execFeature;

@AgentFeature
protected IBDIAgentFeature bdiFeature;
```
-   Now we need to change the user agent to use a translation goal and that it has delegation capabilities. The idea is to allow for defining a plan that is represented by a required service. Such a mapping is defined using the ```@ServicePlan``` annotation. It refers to the name of a previously defined required service (*transser*). 

```java
@RequiredServices(@RequiredService(name="transser", type=ITranslationService.class, 
  binding=@Binding(scope=ServiceScope.PLATFORM)))
@Goals(@Goal(clazz=TranslationGoal.class))
@Plans(@Plan(trigger=@Trigger(goals=TranslationGoal.class), body=@Body(service=@ServicePlan(name="transser"))))
```

The code in the action listener of the translate button has to be changed to create a translation goal instead of a service call:

-  As we need to dispatch a goal on the agent thread (and not on the Swing thread which is active when the button is pressed) first a thread switch has to be applied. This is done using a component step which [is executed on the agent](../../components/components/#scheduling-steps). 

-  Inside the component step, just create and dispatch the goal and use ```get()``` to wait for the result of the future.  

-  Afterwards set the result in the textfield on the swing thread. Also catch exceptions and display errors in case they occur.

```java
execFeature.scheduleStep(new IComponentStep<Void>()
{
    public IFuture<Void> execute(IInternalAccess ia)
    {
        try
        {
            final String gword = (String)bdiFeature.dispatchTopLevelGoal(new TranslationGoal(tfe.getText())).get();
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    tfg.setText(gword);
                }
            });
        }
        catch(final Exception e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    tfg.setText(e.getMessage());
                }
            });
        }
        
        return IFuture.DONE;
    }
});
```

Now, in the Translation agent, we have to publish the translation goal as service, i.e. when the service is called a new goal is created and after processing the result will be set as result of the call:

-  Change the annotation part to publish the translation goal as a service. As in this case our interface only has one service method it is sufficient to state the service interface. Otherwise the method would also have to be defined.

```java
@Agent
@Service
@Goals(@Goal(clazz=TranslationGoal.class, publish=@Publish(type=ITranslationService.class)))
public class TranslationBDI 
{
  ...
}
```

-   Keep the field definitions and the agent init method.
-   Delete both plans and instead add a new simple method plan that reacts on the translation goal. It just looks up the word and return the translation. 

```java
@Plan(trigger=@Trigger(goals=TranslationGoal.class))
public String translatePlan(String eword)
{
  return  wordtable.get(eword);
}
```


** Starting and testing the agents **

Start both agents and verify that translation requests get executed.

**Note:** Again, if no translations show up, make sure that your TranslationBDI agent as well as your UserBDI agent **are referencing the same service interface** ("f3.ITranslationService") in their imports!