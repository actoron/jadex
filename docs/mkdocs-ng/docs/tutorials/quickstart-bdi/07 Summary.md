# Summary

${SorryNotYetAvailable}

## Solutions and Answers to Questions

Here you can find solutions for code you had to write yourself and the answers to questions from different exercises.

### Exercise A3: Additional Patrol Plan Code

<div style="float:right;">
<img src="../patrol-plan2.png">
</div>

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
		actsense.moveTo(0.3, 0.3);
		actsense.moveTo(0.3, 0.7);
		actsense.moveTo(0.7, 0.7);
		actsense.moveTo(0.7, 0.3);
		actsense.moveTo(0.3, 0.3);
	}
```

<div style="float:right;">
<img src="../patrol-plan3.png">
</div>

```java	
	/**
	 *  Declare a third plan for the PerformPatrol goal.
	 */
	// Fill in @Plan annotation and method body for third patrol plan,
	// e.g. according to the figure
	@Plan(trigger=@Trigger(goals=PerformPatrol.class))
	private void	performPatrolPlan3()
	{
		// Follow a zig-zag path in the museum.
		System.out.println("Starting performPatrolPlan3()");
		actsense.moveTo(0.3, 0.3);
		actsense.moveTo(0.7, 0.7);
		actsense.moveTo(0.3, 0.7);
		actsense.moveTo(0.7, 0.3);
		actsense.moveTo(0.3, 0.3);
	}
```


### Exercise A4: Questions about Means-end Reasoning Flags

1. The recur delay only applies after all plans have been executed, the retry delay appears between the plans. 
2. With the or-success flag removed, only one plan is executed. Due to the random selection flag,
    one of the three plans is chosen randomly for each goal processing cycle.
3. The retry delay has no meaning without the or-success, because only one plan is executed and no retry happens.
    You can instead specify a recur delay, to add some waiting time before executing the next randomly selected plan.
4. With or-success set to false, the *AND* semantics is enabled meaning that the agent continues executing plans
    as long as there are plans in the APL. With the exclude mode *when-failed*, all of the patrol plans
    remain in the APL even after they have been executed, and thus can be selected again and again.
    Random selection causes the cleaner to select a random plan from the APL instead of the first. Without
    random selection, only the first plan would be executed over and over. Finally, the retry delay stops the cleaner
    after each execution of a plan.
5. All three plans get executed in parallel and try move the cleaner to different locations at once.
    One of the plan "wins" and is allowed to execute its `moveTo()` action, while the other two are stopped
    with an error message. Therefore, only one of the patrol rounds is actually performed.

### Exercise C2: A Plan for Finding a Charging Station

#### Alternative 1

Reusing the first patrol plan, you could just add the new goal to the plan trigger:

```java
	/**
	 *  Declare the same plan for the PerformPatrol and QueryChargingStation goals
	 *  by using a method with @Plan and @Trigger annotation.
	 */
	@Plan(trigger=@Trigger(goals={PerformPatrol.class, QueryChargingStation.class}))
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

### Alternative 2

A simple alternative is to plan that does just one random move.
To allow this plan being executed repeatedly, you can set `ExcludeMode.Never` on the goal.

```java
	@Goal(excludemode=ExcludeMode.Never)
	class QueryChargingStation
	{
		...
	}
	
	/**
	 *  A plan to move randomly in the environment.
	 */
	@Plan(trigger=@Trigger(goals=QueryChargingStation.class))
	private void	moveAround()
	{
		// Choose a random location and move there.
		System.out.println("Starting moveAround() plan");
		actsense.moveTo(Math.random(), Math.random());
	}
```