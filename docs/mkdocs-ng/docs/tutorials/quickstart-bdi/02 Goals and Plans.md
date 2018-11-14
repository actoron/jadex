# Goals and Plans

This exercise introduces the BDI model and shows how to generate agent actions by using the
goal and plan concepts of Jadex. Specifically, you will learn how add goals and plans to an agent
and how to control the plan selection and plan execution processes with goal flags.



## Quick Introduction to the BDI Model

The belief-desire-intention (BDI) model of agency is based on Stanford philosopher
[Michael E. Bratman](https://philosophy.stanford.edu/people/michael-e-bratman)'s seminal work
[Intention, Plans, and Practical Reason](https://www.press.uchicago.edu/ucp/books/book/distributed/I/bo3629095.html).
An (overly simplified) description of one main idea is that humans are resource-bounded agents in the sense
that they don't calculate the utility of every possible course of action in every second of their existence.
Instead, it is rational for agents, to stick to a once-chosen plan, without questioning its actions in every step.
Only when a problem occurs with a chosen plan, the agent will rethink its choice and maybe form another plan.


The so called *means-end reasoning* process is at the heart of this model. For every desire,
the agent will form intentions how to satisfy it. The selection of suitable desires is in turn based
on the current beliefs of an agent. In concrete software implementations of the BDI model (such as Jadex),
desires and intentions are replaced by more concrete notions of goals and plans.
Goals can be stated, e.g., as a boolean expression that represents a word state to be achieved.
Plans are procedural recipes of actions (e.g. code to be executed for achieving a goal).
Instead of abstract desires, a software agent in Jadex has a dynamic set of concrete goals to be pursued.
Instead of forming arbitrary intentions, a Jadex agent selects existing plans from its so called *plan library*
in response to the currently active goals. The means-end reasoning process is then the decision logic
for finding an appropriate plan (*means*) for a given goal (*end*). 



## Exercise A0: Structure of a BDI Agent

```java
package quickstart.cleanerworld;

import java.util.*;
import jadex.bdiv3.annotation.*;
import jadex.bdiv3.features.*;
import jadex.bdiv3.runtime.*;
import jadex.bdiv3.model.MProcessableElement.ExcludeMode;
import jadex.micro.annotation.*;
import jadex.quickstart.cleanerworld.environment.*;
import jadex.quickstart.cleanerworld.gui.*;

/**
 *  BDI agent template.
 */
@Agent(type="bdi")	// This annotation makes the java class and agent and enabled BDI features
public class CleanerBDIAgent
{
	//-------- fields holding agent data --------
	
	/** The sensor/actuator object gives access to the environment of the cleaner robot. */
	private SensorActuator	actsense	= new SensorActuator();
	
	//... add more field here
	
	//-------- setup code --------
	
	/**
	 *  The body is executed when the agent is started.
	 *  @param bdifeature	Provides access to bdi specific methods
	 */
	@AgentBody	// This annotation informs the Jadex platform to call this method once the agent is started
	private void	exampleBehavior(IBDIAgentFeature bdi)
	{
		// Open a window showing the agent's perceptions
		new SensorGui(actsense).setVisible(true);
		
		//... add more setup code here
		actsense.moveTo(Math.random(), Math.random());	// Dummy call so that the cleaner moves a little.
	}

	//-------- additional BDI agent code --------
	
	//... BDI goals and plans will be added here
}
```


*Figure 1: Starting point for a BDI cleaner agent*

Paste the above code snipped into your Eclipse project (e.g. mark the text, hit ctrl-c,
right-click the `src/main/java` folder and choose *Paste*). A file `CleanerBDIAgent.java`
will be created in the package `quickstart.cleanerworld`.

To Start the agent, open the file `Main.java`, search for the line

```java
conf.addComponent("quickstart/cleanerworld/SimpleCleanerAgent.class");
```

 and change it to

```java
conf.addComponent("quickstart/cleanerworld/CleanerBDIAgent.class");
```

The relevant aspects of the agent file are explained in the following subsections.

### `@Agent` Annotation

If you compare the `CleanerBDIAgent.java` and the `SimpleCleanerAgent.java` you will notice
a difference in the `@Agent` annotation. In both cases, the annotation tells Jadex, that the class
can be started as an agent. In addition, the `CleanerBDIAgent.java` states that the agent is a
BDI agent (`type="bdi"`). The second part is necessary to enable BDI features, such as the
automatic processing of goals and selection and execution of plans.

### Agent Setup

You can notice another difference to the `SimpleCleanerAgent.java` in the setup code.
As in the simple cleaner, we annotated a method with `@AgentBody` to have it executed after agent startup.

Unlike the simple cleaner, we stored the `actsense` object in a field instead of a local variable.
The reason is that we will add more methods and inner classes later for goals and plans and want to access
the `actsense` object from all of these.

Another difference is the `IBDIAgentFeature bdi` parameter of the method. It provides access to the
BDI features of Jadex. We will use it later, e.g. to add goals to the agent. You can find the corresponding Javadoc
[here](${URLJavaDoc}/index.html?jadex/bdiv3/features/IBDIAgentFeature.html).

We added a single `moveTo()` call as a place-holder, so that the agent would move a little, when it is started.
The following sections show how to add more useful behavior using goals and plans.



## Exercise A1: A Goal and a Plan

Now we want to make the agent do something useful like performing patrol rounds. But before you continue:
keep the result of the last exercise for future reference by creating a copy of the `CleanerBDIAgent.java`
and naming it `CleanerBDIAgentA0.java` (for the last exercise A0). Do this from now on after every completed exercise
such that you can go back to a previous solution in case you messed up. This way you can keep editing the
`CleanerBDIAgent.java`, but you won't lose any previously working version.

Now add a goal and a plan specification to the agent (i.e. to `CleanerBDIAgent.java`) as shown below:

```java
	//-------- inner classes that represent agent goals --------
	
	/**
	 *  A goal to patrol around in the museum.
	 */
	@Goal	// The goal annotation allows instances of a Java class to be dispatched as goals of the agent. 
	class PerformPatrol {}
	
	//-------- methods that represent plans (i.e. predefined recipes for working on certain goals) --------
	
	/**
	 *  Declare a plan for the PerformPatrol goal by using a method with @Plan and @Trigger annotation.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))	// The plan annotation makes a method or class a plan. The trigger states, when the plan should considered for execution.
	private void	performPatrolPlan()
	{
		// Follow a simple path around the four corners of the museum and back to the first corner.
		System.out.println("Starting performPatrolPlan()");
		actsense.moveTo(0.1, 0.1);
		actsense.moveTo(0.1, 0.9);
		actsense.moveTo(0.9, 0.9);
		actsense.moveTo(0.9, 0.1);
		actsense.moveTo(0.1, 0.1);
	}
```

You should add the code *before* the last closing brace (`}`), such that the `PerformPatrol` class
becomes an *inner* class and `performPatrolPlan()` a method of the `CleanerBDIAgent` class.

Execute the agent by starting the `Main` class and observe its behavior. OK, the new code didn't really change anything.
The agent still only executes the random `moveTo(...)` from the `exampleBehavior()` method and ignores
the new goal and plan. Why?

The reason is that the `PerformPatrol` class is only a template for a goal, just like any Java class is only a 
template for an object. We need to instatiate the goal class and tell the agent to pursue this newly created goal object.
We can do that in the `exampleBehavior()` method, that is executed directly after the agent is started.
Find the line

```
		//... add more setup code here
		actsense.moveTo(Math.random(), Math.random());	// Dummy call so that the cleaner moves a little.
```

and replace it with

```
		// Create and dispatch a goal.
		bdi.dispatchTopLevelGoal(new PerformPatrol());
```

Execute the agent again by starting the `Main` class and observe it performing a patrol round as specified in the
`performPatrolPlan()` method.

### The `@Goal` Annotation and the `PerformPatrol` Class

We wanted our agent to have a goal to perform patrol rounds. Therefore we added the `PerformPatrol` class to represent the goal object.
Having Java classes for goals allows treating them as instances like any other object in Java. Thus, goals can be created,
passed to the agent for processing, and the agent can keep track of its goals as a collection of objects.

The Jadex framework requires some additional internal management of goal objects. Therefore we need to tell the framework that
the `PerformPatrol` class should be able to represent goals. This is done by placing the `@Goal` annotation before the class.

Try removing the `@Goal` annotation. You will notice that the code still compiles without errors.
Try executing the agent without the `@Goal` annotation by starting the `Main`class. An error occurs while starting the agent,
because the Jadex framework cannot use `PerformPatrol` as a goal class.

### The `@Plan` Annotation and the `performPatrolPlan()` Method

According to the BDI model as implemented in Jadex, the agent should pursue its goals by selecting appropriate plans
from its *plan library*.  Plans are procedural recipes that can be naturally specified as simple Java methods.
To add a method to the plan library you have to tell Jadex to treat this method as a plan. This is done by the `@Plan` annotation.

In addition to marking a method with `@Plan` you also need to tell Jadex the situations, when this plan should be selected.
Most prominently, you have to state some triggering event or goal for which the agent should consider the method as a suitable plan.

### The `@Trigger` Annotation

So we need to tell the agent to consider the `performPatrolPlan()` method as an applicable plan for the `PerformPatrol` goal.
This is, where the  `@Trigger` annotation comes into play for establishing the connection between or goal class an plan method.
Any single plan can have not one but many different triggers. Therefore the `@Trigger` annotation allows stating a single goal as in
`goals=PerformPatrol.class`, but also multiple goals (e.g., `goals={PerformPatrol.class, ...}`) as well as other kinds
of triggering events. You can find more details about the annotation in its
[Javadoc documentation](${URLJavaDoc}/index.html?jadex/bdiv3/annotation/Trigger.html). 

### The `IBDIAgentFeature` and the `dispatchTopLevelGoal()` Method

Do you remember that the agent only reacted to the goal after we added it as an object to the agent?
The `IBDIAgentFeature` class provides methods to access the BDI functionality of a Jadex agent.
We had an instance of this class automatically fed into our `exampleBehavior()` method
(cf. Exercise A0) in the `bdi` parameter. We can use it to add a goal to the agent with the
 `dispatchTopLevelGoal()` method. All we need is an object to represent the goal.

Like any other Java class, we can create instances of the `PerformPatrol` class with the `new` keyword.
The result is a Java object that can be used as an agent goal. We just need to tell the agent to pursue
this object as a goal, like we did in the  `exampleBehavior()` method.



## Exercise A2: Execute the Goal Periodically

The result of the previous exercise is a cleaner agent that performs a single patrol round and then stops.
Create a copy of the `CleanerBDIAgent.java` named `CleanerBDIAgentA1.java` to keep your code
of the previous exercise.

Now we want to make the agent start over with a new patrol round whenever a patrol round is finished.
This is very easy in Jadex. Just change the `@Goal` annotation to `@Goal(recur=true)`.
Execute the `Main` class and observe that the agent restarts to patrol after having completed a patrol round.

### The `recur` Flag for Goals

By default, a goal is considered to be finished after it has been processed by the agent. As a result
the goal is removed from the agent and does not trigger new plans (cf. Exercise A1).
This behavior can be changed with the `recur` flag. When `recur` is set to true, the goal processing
restarts after all available plans have been executed for the goal. As a result, in our revised cleaner
agent the process is as follows:

1. We create the goal and add it with `dispatchTopLevelGoal()`.
2. The Jadex framework selects the `exampleBehavior()` as a suitable plan and executes the method.
3. Our plan implementation in that method causes the agent to move to the specified locations on after another
    and finally the plan method returns.
4. The Jadex framework notices that the plan is finished and considers the goal processing to be complete.
5. In Exercise A1 the goal would now be dropped because processing is finished. Due to the `recur=true` flag,
    in this exercise, the goal processing restarts at step 2.



## Exercise A3: Multiple Plans for a Goal

Backup you current solution as `CleanerBDIAgentA2.java`.

<div style="float:right;">
	<img src="../patrol-plan2.svg">
	<br/>
	<img src="../patrol-plan3.svg">
</div>

One advantage of the BDI model is the clean separation between *what* an agent should achieve (goals)
and *how* it can achieve it (plans). Often, there a many different ways to achieve the same result.
In BDI agents this is naturally reflected by the possibility to have many plans that all are suitable candidates
for pursuing the same goal.

In this exercise, we want to specify alternative patrol rounds (i.e. sequences of locations), that the agent
should choose from for pursuing the `PerformPatrol` goal. We can just add more methods with a corresponding
`@Plan` annotation as shown below.

```java
	/**
	 *  Declare a second plan for the PerformPatrol goal.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))
	private void	performPatrolPlan2()
	{
		// Follow another path around the middle of the museum.
		System.out.println("Starting performPatrolPlan2()");
		
		// Fill in moveTo() commands, e.g. according to the figure
	}
	
	/**
	 *  Declare a third plan for the PerformPatrol goal.
	 */
	// Fill in @Plan annotation and method body for third patrol plan,
	// e.g. according to the figure
	
```

Add the methods above into your `CleanerBDIAgent.java`. Execute the agent by starting the `Main` class.
Do you notice a difference?

### The `orsuccess` Flag for Goals

Probably you haven't noticed any difference. The agent still only executes the first plan repeatedly.
The reason is that the agent starts with the first plan and continues executing plans until it
considers the goal to be achieved. As the first plan completes without errors (i.e. not throwing a Java exception)
it is considered a success (cf. step 4 in the process described in Exercise A2) and the goal processing
restarts with an empty slate (cf. step 2).

One option for telling the agent to look for other plans after a plan is completed is stating that not only *one*,
but *all* available plans should be executed. This can be done with the `orsuccess` flag,
that allows changing the processing semantics from *OR* (= only one of many plans needs to be executed)
to *AND* (= all of the available plans need to be executed). The *OR* semantics (flag is `true`) is the
default behavior that our agent currently exhibits. Setting the flag to `false` enables the *AND* semantics
and lets the agent execute our additional patrol plans.

Thus change the `@Goal` annotation of the `PerformPatrol` goal to

```java
	@Goal(recur=true, orsuccess=false)
```

Execute the agent by starting the `Main` class and check that now all three plans are executed after each other.



## Exercise A4: Means-end Reasoning Flags

In the last exercise (which you should now backup as `CleanerBDIAgentA3.java`, btw) we already introduced one
flag for controlling the means-end reasoning (a.k.a. goal processing, a.k.a. plan selection) behavior of a BDI agent.
You can find many more flags in the Javadoc of the
[@Goal annotation](${URLJavaDoc}/index.html?jadex/bdiv3/annotation/Goal.html).

Lets say we want the cleaner to stop between patrol rounds. We find two flags that seem to support this functionality:
`recurdelay` and `retrydelay`. Both represent a waiting time in milliseconds. Add a `retrydelay` of 3000 (i.e. three seconds) to the goal like so:

```java
	@Goal(recur=true, orsuccess=false, retrydelay=3000)	// Goal flags: variation 1
```

Execute the agent by starting the `Main` class and observe the behavior. What happens if you change 
`retrydelay` to `recurdelay` (1)? Try experimenting with other flags. Here are some suggestions:

* Replace `orsuccess=false` with `randomselection=true`. What is the difference (2)?
   What happens to the retry delay and why (3)?
* Instead of restarting goal processing with recur, you can also tell the agent to consider the same plan
   multiple times in the same goal processing cycle. Try the following settings. Can you explain what these flags do (4)?

```java
	@Goal(orsuccess=false, excludemode=ExcludeMode.WhenFailed, randomselection=true, retrydelay=3000) // Goal flags variation 3
```

* What happens if you use the `posttoall` flag for the `PerformPatrol` goal and why (5)?
