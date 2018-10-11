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

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.gui.SensorGui;

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
conf.addComponent("quickstart/cleanerworld/CleanerBDIAgent.class");`
```

The relevant aspects of the agent file are explained in the following subsections.

### @Agent Annotation

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

Now we want to make the agent do something useful like performing patrol rounds. Create a copy of the
`SimpleCleanerAgent.java` and name it `SimpleCleanerAgentA0.java` to keep the result of
the last exercise for future reference. Do this from now on for every new exercise such that you will
be able to go back to a previous solution in case you messed up. This way you can keep editing the
`SimpleCleanerAgent.java`, but you won't lose any previously working version.

Now add a goal and a plan specification to the agent as shown below:

```java
	//-------- inner classes that represent agent goals --------
	
	/**
	 *  A goal to patrol around in the museum.
	 */
	@Goal	// The goal annotation allows instances of a Java class to be dispatched as goals of the agent. 
	class PerformPatrol {}
	
	//-------- methods that represent plans (i.e. predefined recipes for working on certain goals) --------
	
	/**
	 *  Declare a plan using a method with @Plan and @Trigger annotation.
	 */
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))	// The plan annotation makes a method or class a plan. The trigger states, when the plan should considered for execution.
	private void	performPatrolPlan()
	{
		// Follow a simple path around the four corners of the museum.
		actsense.moveTo(0.1, 0.1);
		actsense.moveTo(0.1, 0.9);
		actsense.moveTo(0.9, 0.9);
		actsense.moveTo(0.9, 0.1);
	}
```

You should add the code *before* the last closing brace (`}`), such that the `PerformPatrol` class
becomes an *inner* class and `performPatrolPlan()` a method of the `CleanerBDIAgent` class.

Execute the agent by starting the `Main` class and observe its behavior.


