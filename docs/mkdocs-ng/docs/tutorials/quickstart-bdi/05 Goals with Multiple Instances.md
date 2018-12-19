# Goals with Multiple Instances

In this example, we will handle goals using the concepts *type* and *instance*
that you may know from object-orientation. In the previous exercises, our
agents always used a single instance of each goal that was created at agent
startup with `bdi.dispatchTopLevelGoal(new <goalclass>())`, e.g.
`bdi.dispatchTopLevelGoal(new PerformPatrol())`. This makes perfect
sense for the *PerformPatrol* and *MaintainBatteryLoaded* goals. When
considering the goal to pick up any known piece of waste, we can follow a
different approach: instead of a single global goal to pick up *all* pieces
of waste, we can create individual goal instances for each specific piece of
waste.

This *one goal for one piece of waste* approach might look a bit more complex
at first. Yet, it has an important advantage as it makes the choices of the
agent more explicit. If we would combine all waste pieces in a global
*PickUpAllWaste* goal, we would implement the decision logic (which piece
to pick up first?) into procedural plans. Having a separate goal instance for
each piece of waste allows us to describe the choice using goal deliberation
and keep our procedural plans simple.


## Exercise D1: A Goal Instance for each Piece of Waste

To keep track of waste objects, first add a corresponding belief set to the agent:

```java
	/** Set of the known waste items. Managed by SensorActuator object. */
	@Belief
	private Set<IWaste>	wastes	= new LinkedHashSet<>();
```

Don't forget to ask the *SensorActuator* to manage the belief set's contents
in the `exampleBehavior()` method:

```java
		actsense.manageWastesIn(wastes);
```

Now we can make the agent react to changes in the belief set by creating
corresponding goals. Therefore define a new goal type *AchieveCleanupWaste*
as follows:

```java
	/**
	 *  A goal to cleanup waste.
	 */
	@Goal
	class AchieveCleanupWaste
	{
		// Remember the waste item to clean up
		IWaste	waste;
		
		// Create a new goal instance for each new waste item
		@GoalCreationCondition(factadded="wastes")
		public AchieveCleanupWaste(IWaste waste)
		{
			System.out.println("Created achieve cleanup goal for "+waste);
			this.waste = waste;
		}
	}
```

Execute the agent and observe the console output. Whenever the agent discovers a
previously unseen piece of waste, it will create a new goal instance as shown
by the *Created achieve cleanup goal for ...* output:

```
Starting performPatrolPlan2()
Created achieve cleanup goal for Waste(id=Waste_#3, location=Location(x=0.3, y=0.5))
...
```

You can place additional waste objects in the *Welcome to Cleaner World*
window with the left mouse button and check, if the agent detects them,
when they come in vision range.