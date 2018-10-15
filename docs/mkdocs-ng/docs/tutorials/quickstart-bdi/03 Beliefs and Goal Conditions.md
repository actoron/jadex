# Beliefs and Goal Conditions

This exercise introduces beliefs that let an agent automatically perceive and react to changes.
It also shows how to use goal conditions to control goal processing behavior based on belief values and belief changes.

## Exercise B1: Using a Belief to Control a Declarative Goal

Up to now, our cleaner only *acted* in the environment, but did not *perceive* anything.
In this exercise we extend the cleaner to monitor its charge state. The charge state is updated by the environment
as a result of the cleaner moving around. Capturing this in a belief, causes the Jadex framework to monitor the value 
and let the cleaner know, if anything important changes. Add the following code to the agent data fields section.
(Remember to make a backup of your last solution as `CleanerBDIAgentA4.java` before making any changes).

```java
	/** Knowledge of the cleaner about itself (e.g. location and charge state). */
	@Belief
	private ICleaner	self	= actsense.getSelf();
```

We want the agent to recharge its battery before it gets too low. We can specify this as a goal.
Using a goal condition, we can directly refer to the charge state value. Add the following code to the goals section:

```java
	/**
	 *  A goal to recharge whenever the battery is low.
	 */
	@Goal(recur=true, recurdelay=3000)
	class MaintainBatteryLoaded
	{
		@GoalMaintainCondition	// The cleaner aims to maintain the following expression, i.e. act to restore the condition, whenever it changes to false.
		boolean isBatteryLoaded()
		{
			return self.getChargestate()>=0.2; // Everything is fine as long as the charge state is above 20%, otherwise the cleaner needs to recharge.
		}
	}
```

Don't forget to instantiate the goal in the agent startup code next to the perform patrol goal like this:

```java
		// Create and dispatch agent goals.
		bdi.dispatchTopLevelGoal(new PerformPatrol());
		bdi.dispatchTopLevelGoal(new MaintainBatteryLoaded());
```

Now all we need is a plan to handle the new goal. Add it at the end of the agent class:

```java
	/**
	 *  Move to charging station and load battery.
	 */
	@Plan(trigger=@Trigger(goals=MaintainBatteryLoaded.class))
	private void loadBattery()
	{
		System.out.println("Starting loadBattery() plan");
		
		// Move to first known charging station -> fails when no charging station known.
		IChargingstation	chargingstation	= actsense.getChargingstations().iterator().next();
		actsense.moveTo(chargingstation.getLocation());
		
		// Load until 100% (never reached, but plan is aborted when goal succeeds).
		actsense.recharge(chargingstation, 1.0);
	}
```

What do you expect to happen when you start the agent? When will the new code be executed and what will happen then?
Try to answer these questions to yourself before starting the program and reading on.

Have your executed the agent and observed its behavior? Watch the console output and try to understand what went *wrong*,
but also what already went *right*. In the following subsections, we will discuss the code somewhat backwards, starting with the plan.

### The `loadBattery()` Plan

This plan is quite straight-forward. The plan handles the new `MaintainBatteryLoaded` goal as stated in the trigger.
Self-check: would you have known how to write the `@Plan` and `@Trigger` annotations?
The implementation is somewhat naive: it assumes a charging station to be known (`actsense.getChargingstations()`)
and just moves to the location of the first charging station, where it starts recharging.

One interesting thing to note is that it tries to load to 100%, although this value will never actually be reached.
So this plan cannot complete. It will *hang* forever at the `recharge()` operation.
For now, keep that in mind. We will come back to that.

So what went wrong in our current cleaner implementation? When you run the program, you will eventually see an error like the following:

```
Starting loadBattery() plan
WARNUNG: Plan 'loadBattery' threw exception: java.lang.IllegalStateException: Cannot move to multiple targets simultaneously. Target exists: Location(x=0.7, y=0.7)
```

Focus on the bright side: our new plan actually got executed! The cleaner moved around for some time and when the charge state 
droppen below 20%, the cleaner tried to start the battery loading plan. The only problem was, that it was still performing a
patrol round thus trying to move to two directions at once. This caused the above error. In the next exercise we will fix that.

But first, we want to understand how the cleaner decided, when to start reloading.

### The `@GoalMaintainCondition` and Declarative Goals

Unlike the empty class of the `PerformPatrol` goal, our new `MaintainBatteryLoaded` class had a method `isBatteryLoaded()`
to check, if there is still enough battery. The `@GoalMaintainCondition` tells Jadex to monitor the result of this method.
*Maintain* means that the agent wants to keep the result value at `true` and thus starts executing plans for the goal, when
the value changes to `false`. The expression `self.getChargestate()>=0.2` only evaluates to false when the charge state drops below 20%.
This is why the agent didn't start executing the `loadBattery()` plan right from the beginning, but only after doing some patrol rounds.

Thanks to the maintain condition, the goal becomes *declarative*, which means that the agent can check
(with the boolean expression implemented in the method) if the goal is currently succeeded or not.
This is quite different to the perform patrol goal, which is only *procedural* meaning that the agent will always execute plans
for the perform patrol goal and consider it succeeded after successful plan execution.

Declarative goals capture the desired state already in their specification and are thus more decoupled from their plans.
E.g. when the condition is `true`, no plan needs to be executed, whereas a procedural goal will always lead to plan execution.
Also, when the condition is `false` and stays `false` even after plan execution, the agent will look for other plans to pursue the goal,
whereas a procedural goal is always considered to have succeeded after plans (i.e. procedures) are completed.

### The `@Belief` Annotation

So why did we need the `@Belief` annotation? Lets find out: remove just the annotation (i.e. keep the `self` field) and restart the program.
Uh, oh. Not good. Scrolling down the output finally reveals the following source of error:

```
Found condition without triggering events (will never trigger): maintain_boolean jadex.quickstart.cleanerworld.single.CleanerBDIAgentB1$MaintainBatteryLoaded.isBatteryLoaded()
```

What does that mean? The Jadex framework does not check every goal condition for every step of every agent all the time
(this would lead to really poor perfomance). Instead it waits for interesting events to happen and only then selectively
checks the affected conditions of the affected goals of the affected agents. Thanks to this, you can run Jadex programs
with a lot of agents that can have a lot of goals with a lot of conditions without the need for a supercomputer.

The drawback is, that Jadex needs to know for which events to look. Thankfully, Jadex analyzes your code and is able
to deduce many events automatically as long as your condition code refers to field marked as `@Belief`.
Therefore having the annotation at the `self` field allows Jadex to see that the maintain condition, that also refers
to this field, should be re-checked whenever the `self` belief value changes.

So thats all there is to know, right? Well, almost. Actually the value of the `self` field does not change,
because the value is of type `ICleaner`...

