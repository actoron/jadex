So far we have explored how BDI can be used to define the internal behaviour of an agent. In this part we move on towards multi-agent scenarios and show how a BDI agents can be made to interact with each other. The typical way for realizing interactions with active components is using services. A service is defined by an interface that determines to available methods and a service implmentation that can be either a separate class of just part of the agent itself. If you are unfamiliar with services please have a look at the active components <span class="wikiexternallink">[tutorial](../AC%20Tutorial/01%20Introduction)</span> and <span class="wikiexternallink">[user guide](../AC%20User%20Guide/01%20Introduction)</span>.

<span>F1 - Creating a Service</span> 
------------------------------------

In the first exercise we will equip the translation agent with a corresponding service. We will additionally create a user agent that opens a small user interface. The user interface allows for entering English words that will be translated on request. Internally, the user agent searches for a translation service and delegates the request to it. 

-   First create a new Java interface called ITranslationService. Add a method called translateEnglishGerman to it. The method should take a String parameter called eword and return a futurized String (IFuture&lt;String&gt;).


```java

public interface ITranslationService
{
  public IFuture<String> translateEnglishGerman(String eword);
}

```


-   Create a Java class called TranslationBDI that implements the translation interface. Add the @Agent and @Service annotations to the class. Furthermore, add a new provided service using the @ProvidedServices and in it the @ProvidedService annotation. Set the type of the provided service to ITranslationService.


```java

@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITranslationService.class))
public class TranslationBDI implements ITranslationService
{
  ...
}

```


-   Add two fields to the agent. First, we need the agent API that should be injected to a field called agent and that is of type BDIAgent. Second, we need the wordtable. As in previous lectures declare it with name wordtable and type Map&lt;String, String&gt;.  

<!-- -->

-   Add an agent init method using the @AgentCreated annotation. Create the word table in it and add some word pairs to it.

<!-- -->

-   Implement the interface method by just looking up the word in the map and returning it via a new future.


```java

public IFuture<String> translateEnglishGerman(String eword)
{
  String gword = wordtable.get(eword);
  return new Future<String>(gword);
}

```


-   Create a new Java class called UserAgent. The user agent should declare also a field called agent for the agent API. Additionally, it should add an agent body method (@AgentBody) that creates the user interface. To simplify this task the corresponding code is displayed below. Inside of the body method first a thread switch to the Swing thread is performed (using SwingUtilities.invokeLater). It is a general Swing requirement that all gui related actions should always be performed only on the Swing thread. Otherwise you might encounter strange behavior due to race conditions that might occur sometimes. Within the runnable that is executed by Swing first a jframe is created. Two textfields and one button are added. The rest of the code is in charge of displaying the gui at the center of the screen. 


```java

@Agent
public class UserBDI
{
  @Agent
  protected BDIAgent agent;
	
  @AgentBody
  public void body()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        JFrame f = new JFrame();
        PropertiesPanel pp = new PropertiesPanel();
        final JTextField tfe = pp.createTextField("English Word", "dog", true);
        final JTextField tfg = pp.createTextField("German Word");
        JButton bt = pp.createButton("Initiate", "Translate");			
        f.add(pp, BorderLayout.CENTER);
        f.pack();
        f.setLocation(SGUI.calculateMiddlePosition(f));
        f.setVisible(true);

        ...

```


-   One last part is missing. If a user enters a word that should be translated and presses the Translate button s service invocation has to be created. For this purpose add an inline action listener to the button and in its actionPerformed method search for a translation service. If found, invoke the translation method and display the result in the other textfield (or the error that occurred). As starting point the code for searching the service is outlined below:


```java

SServiceProvider.getServices(agent.getServiceProvider(), ITranslationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
  .addResultListener(new IntermediateDefaultResultListener<ITranslationService>()
{
  public void intermediateResultAvailable(ITranslationService ts)
  {
    ...

```


**Start and test the agents**

From the JCC, start both agents. The user interface should appear after the user agent has been started. Enter a word and press the Translate button. You should see the translated word appearing immediately in the text field below. 

<span>F2 - Mapping a Service to Plans</span> 
--------------------------------------------

One of the strength of BDI is that it provides a flexible runtime execution by selecting suitable plans at runtime. This concept cannot only be used with goals but also directly with plans. This means we can created plans and just state that these plans realize a service call. In this case an incoming service call is automatically delegated to a suitable plan (checking the pre- and context conditions of the plans. Please note that only one plan is executed and no retries are performed (is this is necessary we need to map the service to a goal and not a plan as described in the next exercise).

-   The interface and the user agent need no changes. Just copy them from the last exercise.

<!-- -->

-   In the translation agent we first need to state that we want the bdi agent to implement the translation interface via plans. This is done by declaring the implementation of the provided service to be the BDIAgent. Additionally remove the 'extends ITranslationService' part of the class definition. The interface is now only implemented indirectly via plans. Hence, also remove the translateEnglishGerman method from the agent completely.


```java

@ProvidedServices(@ProvidedService(name="transser", type=ITranslationService.class, 
  implementation=@Implementation(BDIAgent.class)))

```


-   Add a new plan that uses the dictionary to translate words. We want to execute this plan only is the word is contained in the dictionary. Thus, we use an inner class as plan and add a precondition method. Additionally, we add a plan body that takes as argument an object array representing the parameters of the service call. We need to fetch the first parameter, cast it to String and look it up in the dictionary.


```java

@Plan(trigger=@Trigger(service=@ServiceTrigger(type=ITranslationService.class)))
public class TranslatePlan
{
  @PlanPrecondition
  public boolean checkPrecondition(Object[] params)
  {
    return wordtable.containsKey(params[0]);
  }
		
  @PlanBody
  public String body(Object[] params)
  {
    String eword = (String)params[0];
    String gword = wordtable.get(eword);
    System.out.println("Translated with internal dictionary dictionary: "+eword+" - "+gword);
    return gword;
  }
}

```


-   We add a second plan that will allow us to translate words not contained in the internal dictionary. Instead we will use an online dictionary and look up the word. The result is retrieved as html page which needs to be parsed to extract the translation. The parsing code is presented below. Just copy the snippet and make it to a method plan of the agent using the @Plan annotion. It should have the same trigger as the other plan.


```java

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


**Start and test the agents**

Again, start both agents from the JCC. Now try out if internal as well as internet translations are displayed when entering translation requests in the gui.

<span>F3 - Goal Delegation</span> 
---------------------------------

Sometimes, mapping a service call to goal is more appropriate than a plan. This is the case if the BDI means-end reasong should be used for executing the service call. Another advantage of a service to goal mapping is that it allows for goal delegation between different agents. This means we can just create a translation goal in the user agent and dispatch it. The goal will automatically to forwarded (as service call) to the translation agent which will reify the call to a goal and try to achieve it.

-   Copy the unchanged ITranslationService interface.

<!-- -->

-   Create a new class called TranslationGoal representating the shared goal between both agents. For this reason we do not want to define it as inner class of one of the agents. Use the @Goal annotation to make it become a goal. Moreover, add two fields of type String: one called gword and one called eword. Make eword become a goal parameter (@GoalParameter) and gword become the goal result (@GoalResult). Add a constructor taking eword as parameter and generate getter/setter methods for both fields.


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
  ...
}

```


-   In the user agent we need to change that a translation goal is used and that it has delegation capabilities. The idea is to allow for defining a plan that is represented by a required service. Such a mapping is defined using the @ServicePlan annotation. It refers to the name of a previously defined required service (here 'transser'). 


```java

@RequiredServices(@RequiredService(name="transser", type=ITranslationService.class, 
  binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Goals(@Goal(clazz=TranslationGoal.class))
@Plans(@Plan(trigger=@Trigger(goals=TranslationGoal.class), body=@Body(service=@ServicePlan(name="transser"))))

```


-   The code in the action listener of the translate button has to be changed to create a translation goal instead of a service call. As we need to dispatch a goal on the agent thread (and not on the Swing thread which is active when the button is pressed) first a thread switch has to be applied. This is done using a component step which is executed on the agent. Then just create and dispatch the goal and use get() to wait for the result of the future. Afterwards set the result in the textfield on the swing thread. Also catch exceptions and display errors in case they occur.


```java

agent.scheduleStep(new IComponentStep<Void>()
{
  public IFuture<Void> execute(IInternalAccess ia)
  {
    try
    {
      final String gword = (String)agent.dispatchTopLevelGoal(new TranslationGoal(tfe.getText())).get();
      // set word in textfield on swing thread
    }
    catch(Exception e)
    {
      // set the exception message in textfield on swing thread
    }
  }
});

```


-   Our translation agent is very simple in this lecture. We change the annotation part to publish the translation goal as a service, i.e. when the service is called a new goal is created and after processing the result will be set as result of the call. As in this case our interface only has one service method it is sufficient to state the service interface. Otherwise the method would also have to be defined.


```java

@Agent
@Service
@Goals(@Goal(clazz=TranslationGoal.class, publish=@Publish(type=ITranslationService.class)))
public class TranslationBDI 
{
  ...
}

```


-   Keep the two field definitions and the agent init method.

<!-- -->

-   Delete both plans and instead add a new simple method plan that reacts on the translation goal. It just looks up the word and return the translation. 


```java

@Plan(trigger=@Trigger(goals=TranslationGoal.class))
public String translatePlan(String eword)
{
  return  wordtable.get(eword);
}

```


**Start and test the agents**

Start both agents from the JCC and verify that translation requests get executed.
