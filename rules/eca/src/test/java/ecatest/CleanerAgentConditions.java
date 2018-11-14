package ecatest;

import jadex.rules.eca.RuleSystem;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.Event;

public class CleanerAgentConditions
{
	protected double chargestate = 1.0;
	
	/**
	 *  Get the chargestate.
	 *  @return the chargestate.
	 */
	public double getChargeState()
	{
		return chargestate;
	}

	@Event("chargestate")
	public void decreaseChargeState()
	{
		chargestate -= 0.01;
//		new Event(new Double(chargestate));
	}
	
	@Condition("battery")
	public boolean	batteryLoadCondition(@Event("chargestate") double chargestate)
	{
		return chargestate < 0.2;
	}
	
	@Action("battery")
	public void	loadBatteryAction()
	{
		System.out.println("loading");
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		RuleSystem rs = new RuleSystem(null);
		CleanerAgentConditions cac = new CleanerAgentConditions();
		cac = (CleanerAgentConditions)rs.observeObject(cac, true, true, null);
				
		for(int i=0; i<100; i++)
		{
			cac.decreaseChargeState();
			System.out.println("Current charge state: "+cac.getChargeState());
			rs.processAllEvents();
		}
		
		rs.unobserveObject(cac, null);
	}
	
//	@MaintainGoal
//	class MaintainBatteryLoadedGoal
//	{
//		@MaintainCondition
//		public boolean	batteryLoadedTarget(@Event double chargestate)
//		{
//			return chargestate > 0.2;
//		}
//		
//		@TargetCondition
//		public boolean	batteryLoadedTarget(@Event double chargestate)
//		{
//			return chargestate >= 1.0;
//		}
//	}
}

//
//	<beliefs>
//		<belief name="environment" class="ContinuousSpace2D">
// 		<belief name="myself" class="ISpaceObject" exported="true">
//		<beliefset name="wastes" class="ISpaceObject" />
//		<beliefset name="wastebins"  class="ISpaceObject" />
//		<beliefset name="chargingstations" class="ISpaceObject" />
//		<beliefset name="cleaners" class="ISpaceObject" />
//		<beliefset name="raster" class="Integer">
//		<beliefset name="visited_positions" class="MapPoint">
//		<belief name="daytime" class="boolean" evaluationmode="pull" updaterate="1000">
//		<belief name="my_location" class="IVector2" evaluationmode="push">
// 		<belief name="my_speed" class="double" evaluationmode="push">
// 		<belief name="my_vision" class="double" evaluationmode="push">
// 		<belief name="my_chargestate" class="double" evaluationmode="push">
//		<beliefset name="patrolpoints" class="IVector2">
//
//		<!-- Observe the battery state. -->
//		<maintaingoal name="maintainbatteryloaded">
//			<deliberation>
//				<inhibits ref="performlookforwaste" inhibit="when_in_process"/>
//				<inhibits ref="achievecleanup" inhibit="when_in_process"/>
//				<inhibits ref="performpatrol" inhibit="when_in_process"/>
//			</deliberation>
// 			<maintaincondition language="jcl">
// 				$beliefbase.my_chargestate > 0.2
// 			</maintaincondition>
//			<targetcondition language="jcl">
// 				$beliefbase.my_chargestate >= 1.0
// 			</targetcondition>
//		</maintaingoal>
//
//		<!-- Clean up some waste. -->
//		<achievegoal name="achievecleanup" retry="true" exclude="never">
//			<parameter name="waste" class="ISpaceObject">
//				<value>$waste</value>
//			</parameter>
//			<unique/>
//			<creationcondition language="jcl">
//				ISpaceObject $waste &amp;&amp; $waste.getType().equals("waste")
//				&amp;&amp; $waste.position!=null
//			</creationcondition>
//			<contextcondition language="jcl">
//				$beliefbase.daytime
// 			</contextcondition>
//			<dropcondition language="jcl">
//				// $beliefbase.myself.waste==null &amp;&amp;
//				// !$beliefbase.getBeliefSet("wastes").containsFact($goal.waste)
//				
//				$beliefbase.myself.waste==null &amp;&amp;
//				!Arrays.asList($beliefbase.wastes).contains($goal.waste)
// 			</dropcondition>
//			<!-- <deliberation cardinality="1"> -->
//			<deliberation>
//				<inhibits ref="performlookforwaste"/>
//				<inhibits ref="achievecleanup" language="jcl">
//					($goal.waste==$beliefbase.myself.waste || 
//					$ref.waste!=$beliefbase.myself.waste &amp;&amp;
//					$goal.waste.position!=null &amp;&amp;	// Hack!!! should not be required?
//					$ref.waste.position!=null &amp;&amp;	// Hack!!! should not be required?
//					$beliefbase.my_location.getDistance((IVector2)$goal.waste.position)
//						.less($beliefbase.my_location.getDistance((IVector2)$ref.waste.position)))
//				</inhibits>
//			</deliberation>
//			<!-- <targetcondition>
//				(select one ISpaceObject $wastebin from $beliefbase.wastebins
//				where $wastebin.contains($goal.waste))!=null
//			</targetcondition>-->
//		</achievegoal>
//
//		<!-- Look out for waste when nothing better to do, what means that
//			the agent is not cleaning, not loading and it is daytime. -->
//		<performgoal name="performlookforwaste" retry="true" exclude="never">
//			<contextcondition language="jcl">
//				$beliefbase.daytime
// 			</contextcondition>
// 		</performgoal>
//
//		<!-- Perform patrols at night when the agent is not loading. -->
//		<performgoal name="performpatrol" retry="true" exclude="never">
//			<contextcondition language="jcl">
//				!$beliefbase.daytime
// 			</contextcondition>
// 		</performgoal>
//
//		<!-- Sub-level goals -->
//
//		<!-- Pick up a piece of waste. -->
//		<achievegoal name="achievepickupwaste" retry="false">
//			<parameter name="waste" class="ISpaceObject"/>
//		</achievegoal>
//
//		<!-- Drop a piece of waste into a wastebin. -->
//		<achievegoal name="achievedropwaste" retry="true" exclude="never">
//			<parameter name="wastebin" class="ISpaceObject"/>
//			<parameter name="waste" class="ISpaceObject"/>
//			
//			<!-- The goal has failed when the aimed wastebin is full. -->
//			<dropcondition language="jcl">
//				//$goal.wastebin.full
//				$goal.wastebin.wastes >= $goal.wastebin.capacity 
//				
//				// ?rparam = (parameter (parameter_has_name "wastebin") (parameter_has_value ?wastebin))
//				// ?wastebin = (Wastebin (isFull() true)) 
//				// ?rgoal = (goal (parameterelement_has_parameters contains ?rparam))
//			</dropcondition>
//		</achievegoal>
//
//		<!-- Try to move to the specified location. -->
//		<achievegoal name="achievemoveto">
//			<parameter name="location" class="IVector2"/>
//			<!-- The goal has been reached when the agent's location is
//				near the target position as specified in the parameter. - ->
//			<targetcondition>
// 				$beliefbase.my_location.isNear($goal.location)
//			</targetcondition> -->
//		</achievegoal>
//
//		<!-- Try to find a not full waste bin that
//			is as near as possible to the agent. -->
//		<querygoal name="querywastebin" exclude="never">
//			<parameter name="result" class="ISpaceObject" evaluationmode="push" direction="out">
//				<value language="jcl" variable="$wastebin">
//					// ISpaceObject $wastebin &amp;&amp; $wastebin.getType().equals("wastebin") &amp;&amp; !$wastebin.full
//					// &amp;&amp;
//					// !(ISpaceObject $wastebin2 &amp;&amp; $wastebin2.getType().equals("wastebin") &amp;&amp; !$wastebin2.full
//					// 						  &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$wastebin.position, (IVector2)$wastebin2.position))
//					
//					ISpaceObject $wastebin &amp;&amp; $wastebin.getType().equals("wastebin") &amp;&amp; $wastebin.capacity > $wastebin.wastes
//					&amp;&amp;
//					!(ISpaceObject $wastebin2 &amp;&amp; $wastebin2.getType().equals("wastebin") &amp;&amp; $wastebin2.capacity > $wastebin2.wastes
//											  &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$wastebin.position, (IVector2)$wastebin2.position))
//				</value>
//			</parameter>
//		</querygoal>
//
//		<!-- Find the nearest charging station. -->
//		<querygoal name="querychargingstation" exclude="never">
//			<parameter name="result" class="ISpaceObject" evaluationmode="push" direction="out">
//				<value language="jcl" variable="$chargingstation">
//					ISpaceObject $chargingstation &amp;&amp; $chargingstation.getType().equals("chargingstation")
//					&amp;&amp;
//					!(ISpaceObject $chargingstation2 &amp;&amp; $chargingstation2.getType().equals("chargingstation")
//													 &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$chargingstation.position, (IVector2)$chargingstation2.position))
//				</value>
//			</parameter>
//		</querygoal>
//		
//		<!-- Basic environment actions modelled as goals to handle failures. -->
//
//		<!-- Pick up a piece of waste. -->
//		<achievegoal name="pickup_waste_action">
//			<parameter name="waste" class="ISpaceObject"/>
//		</achievegoal>
//
//		<!-- Drop a piece of waste. -->
//		<achievegoal name="drop_waste_action">
//			<parameter name="waste" class="ISpaceObject"/>
//			<parameter name="wastebin" class="ISpaceObject"/>
//		</achievegoal>
//
//		<!-- Memorize the already visited positions in a raster. -->
//		<performgoal name="performmemorizepositions"/>
//
// 	</goals>
//
//	<plans>
//		<!-- Walk to the least seen positions.
// 			Position just visited have value 1. The longer the position
// 			was not visted the lower the value. Good for seeking movable targets. -->
//		<plan name="leastseenwalk">
//			<body class="LeastSeenWalkPlan"/>
//			<trigger>
//				<goal ref="performlookforwaste"/>
//			</trigger>
//		</plan>
//		
//		<!-- Walk to random positions. -->
//		<plan name="randomwalk" priority="-1">
//			<body class="RandomWalkPlan"/>
//			<trigger>
//				<goal ref="performlookforwaste"/>
//				<goal ref="querywastebin"/>
//				<goal ref="querychargingstation"/>
//			</trigger>
//		</plan>
//
//		<!-- This plan explores the map by walking to unknown positions.
// 			Uses the absolute quantity of visits at the map points.
// 			Good for seeking firm targets.-->
//		<plan name="exploremap">
//			<body class="ExploreMapPlan"/>
//			<trigger>
//				<goal ref="querywastebin"/>
//				<goal ref="querychargingstation"/>
//			</trigger>
//		</plan>
//
//		<!-- Perform patrols. -->
//		<plan name="patrol">
//			<body class="PatrolPlan"/>
//			<trigger>
//				<goal ref="performpatrol"/>
//			</trigger>
//		</plan>
//
//		<!-- Clean up waste by picking it up
//			and carrying it to a waste bin. -->
//		<plan name="cleanup">
//			<parameter name="waste" class="ISpaceObject">
// 				<goalmapping ref="achievecleanup.waste"/>
//			</parameter>
//			<body class="CleanUpWastePlan"/>
//			<trigger>
//				<goal ref="achievecleanup"/>
//			</trigger>
//		</plan>
//
//		<!-- Pick up a waste. -->
//		<plan name="pickupwaste">
//			<parameter name="waste" class="ISpaceObject">
// 				<goalmapping ref="achievepickupwaste.waste"/>
//			</parameter>
//			<body class="PickUpWastePlan" />
//			<trigger>
//				<goal ref="achievepickupwaste"/>
//			</trigger>
//		</plan>
//
//		<!-- Drop a waste into a waste bin. -->
//		<plan name="dropwaste">
//			<parameter name="wastebin" class="ISpaceObject">
// 				<goalmapping ref="achievedropwaste.wastebin"/>
//			</parameter>
//			<parameter name="waste" class="ISpaceObject">
// 				<goalmapping ref="achievedropwaste.waste"/>
//			</parameter>
//			<body class="DropWastePlan"/>
//			<trigger>
//				<goal ref="achievedropwaste"/>
//			</trigger>
//		</plan>
//
//		<!-- Load the battery. -->
//		<plan name="loadbattery">
//			<body class="LoadBatteryPlan"/>
//			<trigger>
//				<goal ref="maintainbatteryloaded"/>
//			</trigger>
//		</plan>
//
//		<!-- Move to a location. -->
//		<plan name="moveto">
//			<parameter name="location" class="IVector2">
//				<goalmapping ref="achievemoveto.location"/>
//			</parameter>
//			<body class="MoveToLocationPlan"/>
//			<trigger>
//				<goal ref="achievemoveto"/>
//			</trigger>
//			<!-- <contextcondition>$beliefbase.my_chargestate &gt; 0</contextcondition>-->
//		</plan>
//
//		<!-- This plan memorizes the positions. -->
//		<plan name="memorizepositions">
//			<body class="MemorizePositionsPlan"/>
//			<trigger>
//				<goal ref="performmemorizepositions"/>
//			</trigger>
//		</plan>
//	</plans>
//
//	<expressions>
//		<!-- Query the max quantity map point. -->
//		<expression name="query_max_quantity">
//			select one MapPoint $mp
//			from $beliefbase.getBeliefSet("visited_positions").getFacts()
//			order by $mp.getQuantity() desc
//		</expression>
//
//		<!-- Query the map points ordered by their quantity
// 			(least ones first). -->
//		<expression name="query_min_quantity">
//			select MapPoint $mp
//			from $beliefbase.getBeliefSet("visited_positions").getFacts()
//			order by $mp.getQuantity()
//		</expression>
//
//		<!-- Query the map points ordered by their seen value
// 			(least ones first). -->
//		<expression name="query_min_seen">
//			select MapPoint $mp
//			from $beliefbase.getBeliefSet("visited_positions").getFacts()
//			order by $mp.getSeen()
//		</expression>
//	</expressions>
//
//	<properties>
//		<!--<property name="logging.level">Level.FINE</property>-->
//		<!-- <property name="debugging">true</property>  -->
//		<property name="componentviewer.viewerclass">"jadex.bdi.examples.cleanerworld.cleaner.CleanerViewerPanel"</property>
//	</properties>
//
//	<configurations>
//		<configuration name="default">
//			<goals>
//				<initialgoal ref="performlookforwaste"/>
//  				<initialgoal ref="performpatrol"/>
//		  		<initialgoal ref="maintainbatteryloaded"/>
//				<initialgoal ref="performmemorizepositions"/>
//			</goals>
//		</configuration>
//	</configurations>
//</agent>

