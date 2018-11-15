# Plans with Subgoals

This exercise shows how to build plans based on so called subgoals.
A subgoal is a goal, that exists in the context of a plan.
For the subgoal, the agent may again execute other plans,
thus forming a hierarchy of goals and plans called the *goal plan tree*.

The figure below shows an example of an agent that wants to eat pancakes.
The goal plan tree is an *AND/OR* decomposition of the problem.
To achieve a goal, *only one* of the available plans needs to work (*OR* decomposition, e.g. "throw pancake" *or* "flip with spatula").
For a plan to complete, *all* subgoals of the plan need to be successful (*AND* decomposition, e.g. "pancake mix ready" *and* "pancake flipped").

![](example-goalplantree.svg)

*Figure C.1: Example Goal Plan Tree (inspired by a [paper of Broekens et al.](https://link.springer.com/chapter/10.1007%2F978-3-642-16178-0_5))*
 
 
 
## Exercise C0: Managing Known Charging Stations in a Belief Set
 
Before we start using subgoals, we need to prepare our agent.
In the `loadBattery()` plan, the agent currently directly accesses the `actsense` object
to get the location of a known charging station. When no charging station is known,
the battery loading plan fails and the agent dies when the battery has run out.

We want to test what happens, when the agent never discovers a charging station.
Therefore remove (e.g. comment out by prepending `//`s) the `performPatrolPlan()` method.
The two remaining plans should only move the cleaner in the inner area, where no charging station is available.

Execute the program and watch what happens. When the battery drops below 20%, the following error should occur,
because we try to fetch a charging station object from an empty set:


```
WARNING: Plan 'loadBattery' threw exception: java.util.NoSuchElementException
	at java.util.LinkedHashMap$LinkedHashIterator.nextNode(LinkedHashMap.java:721)
	at java.util.LinkedHashMap$LinkedKeyIterator.next(LinkedHashMap.java:742)
	at quickstart.cleanerworld.single.CleanerBDIAgent.loadBattery(CleanerBDIAgent.java:170)
```

We now want to make sure that we always know some charging station by using a subgoal
in the battery loading plan. This subgoal should be based on the current beliefs of the agent.
Thus we want to define a belief for the known charging stations, next to the `self` belief:

```java
	/** Set of the known charging stations. Managed by SensorActuator object. */
	@Belief
	private Set<IChargingstation>	stations	= new LinkedHashSet<>();
```

Also, we need to tell the `SensorActuator` object to keep this belief up to date.
We can do this in the `exampleBehavior()` method:

```java
		// Tell the sensor to update the belief sets
		actsense.manageChargingstationsIn(stations);
```

To test the new belief set, we can change the battery loading plan as follows:

```java
//		IChargingstation	chargingstation	= actsense.getChargingstations().iterator().next();	// old
		IChargingstation	chargingstation	= stations.iterator().next();	// new
		
		// Print class of stations object to show that the LinkedHashSet has been wrapped.
		System.out.println("Class of the belief set is: "+stations.getClass());
```

### The `stations` Belief Set

One important aspect of the Jadex framework is that fields and the referenced objects of field
marked with `@Belief` are monitored for changes (cf. Exercise B1). The `LinkedHashSet` as well as
other Java collection classes do not directly support monitoring their contents. For that reason,
Jadex wraps the object into a collection that supports monitoring. Jadex can do this for lists (`java.util.List`),
sets (`java.util.Set`) and maps (`java.util.Map`). We added a `println()` in the loading plan,
so you can see that the `stations` set is wrapped in an object of type `jadex.bdiv3.runtime.wrappers.SetWrapper`.

Try changing the belief declaration as follows:

```java
	/** Set of the known charging stations. Managed by SensorActuator object. */
	@Belief
//	private Set<IChargingstation>	stations	= new LinkedHashSet<>();
	private LinkedHashSet<IChargingstation>	stations	= new LinkedHashSet<>();
```

Instead of the interface `Set`, now we use the implementation class `LinkedHashSet`.
The code compiles fine, but if you run the program, the following error will occur:

*java.lang.IllegalArgumentException: Can not set java.util.LinkedHashSet field jadex.quickstart.cleanerworld.single.CleanerBDIAgent.stations to jadex.bdiv3.runtime.wrappers.SetWrapper*

To avoid this error, remember using the interface types (`Collection`, `Set`, `List` or `Map`) for the field,
when you want to use Java collection classes as beliefs, because Jadex needs to wrap the collections.

### The `manageChargingstationsIn()` Method

The `SensorActuator` object provides this method to simplify using belief sets for the perceptions from the environment.
Whenever the sensor perceives a previously unknown charging station, it is added to the set that you provided as a parameter in the method.

Similar methods are available for waste, waste bins, and other cleaners. The sensor also updates the belief sets on the
disappearance of objects: E.g. when the the cleaner is in vision range of a location where previously a waste object was detected,
but the waste object is no longer there, the sensor will remove the waste object from the set that has been provided
by the `manageWastesIn()` method.

Note that the beliefs are not immediately updated when an object changes that is not currently in vision range of the cleaner. 



## Exercise C1: A Subgoal for Knowing Charging Stations

Now we can add a goal that checks if a charging station is known:

```java
	/**
	 *  A goal to know a charging station.
	 */
	@Goal
	class QueryChargingStation
	{
		// Remember the station when found
		IChargingstation	station;
		
		// Check if there is a station in the beliefs
		@GoalTargetCondition
		boolean isStationKnown()
		{
			station	= stations.isEmpty() ? null : stations.iterator().next();
			return station!=null;
		}
	}
```

We also have to alter our load battery plan to use this new goal:

```java
	/**
	 *  Move to charging station and load battery.
	 */
	@Plan(trigger=@Trigger(goals=MaintainBatteryLoaded.class))
	private void loadBattery(IPlan plan)
	{
		System.out.println("Starting loadBattery() plan");
		
//		// Move to first known charging station -> fails when no charging station known.
//		IChargingstation	chargingstation	= actsense.getChargingstations().iterator().next();	// from Exercise B1
//		IChargingstation	chargingstation	= stations.iterator().next();	// from Exercise C0
		
		// Dispatch a subgoal to find a charging station (from Exercise C1)
		QueryChargingStation	querygoal	= new QueryChargingStation();
		plan.dispatchSubgoal(querygoal).get();
		IChargingstation	chargingstation	= querygoal.station;
		
		// Move to charging station as provided by subgoal
		actsense.moveTo(chargingstation.getLocation());
		
		// Load until 100% (never reached, but plan is aborted when goal succeeds).
		actsense.recharge(chargingstation, 1.0);
	}
```

Execute the program and check what happens. Again, an error occurs, but a different one:

```
WARNING: Plan 'loadBattery' threw exception: jadex.bdiv3.runtime.impl.GoalFailureException: No more candidates: quickstart.cleanerworld.single.CleanerBDIAgent$QueryChargingStation...
```

Now the agent has the goal to find a charging station, but there are no plans for this goal.
Therefore the agent prints the error message: *No more candidates*. We can now fix this error
by just adding a plan for the goal. Before we do that, lets quickly discuss the new code in this exercise.
 

### The `QueryChargingStation` Goal

Unlike our previous goals, which were empty (`PerformPatrol`) or only included methods (`MaintainBatteryLoaded`),
the `QueryChargingStation` also defines a field for remembering a charging station, once it has been found.
As a goal is still just a Java class, you can use all the Java features you like, e.g. add fields and constructors,
extend other classes or implement interfaces etc.

With regard to Jadex features, this goal only uses features that we already encountered: The target condition
as implemented in the method `isStationKnown()` depends on our new `stations` belief, so it will be re-checked,
whenever our sensor adds a charging station to this belief set. When the set is not empty, we remember the first station.
Once we remembered a station, the method will return `true` and the goal will be completed.


### The `IPlan` Parameter and the `dispatchSubgoal()` Method
 
Note, that we have changed the signature of the `loadBattery()` method by adding the `IPlan plan` parameter.
When present in a plan's method signature, the parameter gives access to the
[plan API of Jadex](${URLJavaDoc}/index.html?jadex/bdiv3/runtime/IPlan.html),
which provides methods related to the currently executing plan. Here we use the
`dispatchSubgoal()` method to attach a newly created `QueryChargingStation` object
as a subgoal to the plan.

By default, Jadex would process the subgoals of a plan in parallel to the plan itself.
The result of the `dispatchSubgoal()` call is therefore a
[future](${URLJavaDoc}//index.html?jadex/commons/future/IFuture.html) object that allows
various ways of synchronously and asynchronously waiting for the subgoal being processed.
Our load battery plan needs a charging station before it can continue, so we wait
synchronously by blocking the plan until the subgoal completes by using the `get()` method
of the future.

After the subgoal is done, we can just access the field from the goal object (`querygoal.station`)
and continue with the remainder of the loading plan as before. 


## Exercise C2: A Plan for Finding a Charging Station

Implement a plan that handles the `QueryChargingStation` goal. Add a method named, e.g., `moveAround()`
with a corresponding `@Plan` annotation and write code to move around in search for a charging station.
You should be able to devise such a plan yourself. *Hint: you could just reuse one of the patrol plans...*


### Agent Behavior After Adding the Plan

After adding the plan, the agent should exhibit the following behavior:

* The agent performs patrol rounds as long as there is enough battery.
* When the battery is below the threshold in the maintain condition (i.e. the `isBatteryLoaded()` method), the agent will stop its patrol plan and 
    * move to a charging station if known (as implemented in the `loadBattery()` plan method),
    * execute a plan to find a charging station if not known (in reaction to the subgoal posted in the `loadBattery()` plan method).

Note, that the plan for finding a charging station will not be executed, when the agent already knows a charging station,
because the target condition of the `QueryChargingStation` goal will be immediately succeeded. You can test this,
by letting the cleaner run for a while and observing the output. Note that the `moveAround()` plan
only appears the first time that the agent runs the `loadBattery()` plan:

```
Starting performPatrolPlan2()
Starting performPatrolPlan3()
Starting performPatrolPlan2()
Starting loadBattery() plan
Starting moveAround() plan
Starting performPatrolPlan2()
Starting performPatrolPlan3()
Starting performPatrolPlan2()
Starting performPatrolPlan3()
Starting loadBattery() plan
Starting performPatrolPlan2()
Starting performPatrolPlan3()
Starting performPatrolPlan2()
Starting performPatrolPlan3()
Starting loadBattery() plan
Starting performPatrolPlan2()
...
```
