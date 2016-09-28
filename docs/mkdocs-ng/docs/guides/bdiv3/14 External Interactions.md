# External Interactions

In this chapter it is explained how the interaction of Jadex agents with other system components that are not necessarily agents can be done. For this purpose it is shown how agent internals can be accessed from other (non-agent) threads and additionally how agent listeners can be employed to get notified whenever changes within the agent happen (cf. the following two sections respectively).

## External Processes

A Jadex agent is synchronized in the sense, that only one plan step at a time is executed (or none, if the agent is busy performing internal reasoning processes). Sometimes one may want to access agent internals from external threads. A good example is when your agent provides a graphical user interface (GUI) to accept user input. When the user clicks a button your Java AWT/Swing event handler method is called, which is executed on the Java AWT-Thread (there is one AWT Thread for each Java virtual machine instance). To force that such external threads are properly synchronized with the internal agent execution, every invocation on a BDI API component (the flyweights implementing e.g. IBelief, IGoal, etc.) is checked. If the agent calls these methods from its own thread the code is directly executed. Otherwise, if an external thread is the caller, the call is redirected to the agent thread and the external thread has to wait until the agent call returns. Please note that this call scheme can lead to deadlocks when agents try to invoke methods on other agents forming a cycle in the callgraph. To gain access to agents and call methods on them from an external thread the external access interface should be used. It can be fetched from the component management service (currently only for local agents) via the *getExternalAccess(IComponentIdentifier cid, IResultListener listener)* method. An alternative from within a plan is to use the method *getExternalAccess()* provided by the *jadex.bdi.runtime.AbstractPlan* class.

The external access object for BDI agents implements the *IBDIExternalAccess* and *ICapability* interfaces. Together, they provide access to all important features of a capability (beliefbase, goalbase, etc.) as well as plan methods for directly creating and dispatching goals, message and internal events. The following code presents an example where a belief is changed when the user presses a button.
  
```java
public void body()
{
  ...
  JButton button = new JButton("Click Me");
  button.addActionListener(new ActionListener()
  {
    public void actionPerformed(ActionEvent e)
    {
      // This code is executed on the AWT thread (not on the plan thread!)
      IBeliefbase bb = getExternalAccess().getBeliefbase();
      bb.getBelief("button_pressed").setFact(new Boolean(true));
    }
  });
  ...
}
```
*External process example*


## Agent Listeners

Agent listeners can be used to get informed whenever agent state changes happen. Normally, listeners will be employed in agent external components such as a GUI for getting information about declared elements. A GUI e.g. could use a listener to update its view with respect to belief changes in the agent. Generally, for all important agent attitudes such as belief, plans and goals as well as the agent itself different listener types exist (cf. the listener table below).


Depending on the listener type different callback methods are provided that are automatically invoked when relevant changes happen. Whenever a callback method is invoked a so called *AgentEvent* is passed and contains relevant information about the change that happened. It basically offers the two methods *getSource()* and *getValue()*. The source here is the originating element of the change event. For belief and beliefset changes, the agent event additionally contains the changed fact object, accessible by *getValue()*.

The invocation of listener methods can happen either on the agent thread or on a separate thread. The notification is performed on the agent thread so that it is not directly possible to use blocking calls such as *dispatchTopLevelGoalAndWait()* in the listener implementation code. If you want to use these methods you have to redirect the call to an agent external thread.


The addition and removal of listeners can be done either on the instance elements themselves (e.g. a goal) or on the bases (e.g. the goalbase). In case the listener shall be added on an instance element it is only necessary to pass the listener object itself as parameter of the call (e.g. *addBeliefListener(IBeliefListener listener)*). In case a type-based listener shall be used e.g. for getting informed about new goal instances in addition to the parameters aforementioned also the type needs to be declared (e.g. *addGoalListener(String type, IGoalListener listener)*).

  

In the listener example below it is shown how a belief listener can be directly added to a "name" belief via the external access interface. It is used to update the value of a textfeld whenever the belief value changes.
  

```java
IExternalAccess agent = ...
agent.getBeliefbase().getBelief("name").addBeliefListener(new IBeliefListener()
{
  public void beliefChanged(AgentEvent ae)
  {
    textfield.setText("Name: ["+ae.getValue()+"]");
  }
});
```
*Agent listener example*
    

| Listener               | Element                  | Listener Methods                          |
|------------------------|--------------------------|-------------------------------------------|
| IAgentListener         | ICapability              | agentTerminating() agentTerminated()      |
| IBeliefListener        | IBelief                  | beliefChanged()                           |
| IBeliefSetListener     | IBeliefSet               | factAdded() factRemoved() factChanged()   |
| IConditionListener     | ICondition               | conditionTriggered()                      |
| IGoalListener          | IGoalbase IGoal          | goalAdded(), goalFinished()               |
| IInternalEventListener | IEventbase               | internalEventOccurred()                   |
| IMessageEventListener  | IMessageEvent IEventbase | messageEventReceived() messageEventSent() |
| IPlanListener          | IPlan IPlanbase          | planAdded() planFinished()                |

*Available listeners*
     
