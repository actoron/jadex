package ecatest;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import jadex.rules.eca.annotations.Condition;
//import jadex.rules.eca.annotations.Event;
//import jadex.rules.eca.annotations.RuleObject;
//
///**
// * 
// */
////@Agent
public class CleanerAgent
{
//	protected IEnvironment environment = Environment.getInstance();
//	
//	protected Set<Waste> wastes = new HashSet<Waste>();
//
//	protected Set<Wastebin> wastebins = new HashSet<Wastebin>();
//
//	protected Set<Chargingstation> chargingstation = new HashSet<Chargingstation>();
//	
//	protected Set<Cleaner> cleaner = new HashSet<Cleaner>();
//
//	protected List<Integer> raster = new ArrayList<Integer>(); // 10, 10
//
//	protected List<MapPoint> visitedpositions = MapPoint.getMapPointRaster(
//		$beliefbase.raster[0].intValue(), $beliefbase.raster[1].intValue(), 1, 1);
//
//	protected boolean daytime = true;
//
//	protected Location my_location = new Location(0.2, 0.2);
//	
//	protected double my_speed = 3.0;
//
//	protected double my_vision = 0.1;
//	
////	@Belief
//	protected double chargestate = 1.0;
//
//	protected Waste carriedwaste;
//
//	protected List<Location> patrolpoints; 
////<beliefset name="patrolpoints" class="Location">
////	<fact>new Location(0.1, 0.1)</fact>
////	<fact>new Location(0.1, 0.9)</fact>
////	<fact>new Location(0.3, 0.9)</fact>
////	<fact>new Location(0.3, 0.1)</fact>
////	<fact>new Location(0.5, 0.1)</fact>
////	<fact>new Location(0.5, 0.9)</fact>
////	<fact>new Location(0.7, 0.9)</fact>
////	<fact>new Location(0.7, 0.1)</fact>
////	<fact>new Location(0.9, 0.1)</fact>
////	<fact>new Location(0.9, 0.9)</fact>
////</beliefset>
//
//	protected jadex.bdi.planlib.GuiCreator gui = new jadex.bdi.planlib.GuiCreator(CleanerGui.class, new Class[]{jadex.bdi.runtime.IBDIExternalAccess.class}, 
//		new Object[]{$scope.getExternalAccess()});
//	
//	
//	protected void body()
//	{
//		
//	}
//	
////	@MaintainGoal
////	@Inhibits(ref="performlookforwaste" inhibit="when_in_process")
////	@Inhibits(ref="achievecleanup" inhibit="when_in_process")
////	@Inhibits(ref="performpatrol" inhibit="when_in_process")
//	class MaintainBatteryLoaded
//	{
////		@MaintainCondition()
//		public boolean	batteryLoadCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate < 0.2;
//		}
//		
////		@TargetCondition()
//		public boolean	batteryTargetCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate==1;
//		}
//	}
//	
////	@AchieveGoal
////	@Inhibits(ref="performlookforwaste")
////	@Inhibits(ref="achievecleanup")
////		$beliefbase.my_location.getDistance($goal.waste.getLocation())
////		&lt; $beliefbase.my_location.getDistance($ref.waste.getLocation())
////	@Unique
//	class AchieveCleanup
//	{
//		protected Waste waste;
//
////		@CreationCondition()
//		public boolean	batteryLoadCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate < 0.2;
//		}
//		
////		@MaintainCondition()
//		public boolean	batteryLoadCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate < 0.2;
//		}
//		
////		@TargetCondition()
//		public boolean	batteryTargetCondition(@Event("chargestate") double chargestate)
//		{
//			return chargestate==1;
//		}
//	}
}	
////	<!-- Clean up some waste. -->
////	<achievegoal name="achievecleanup" retry="true" exclude="when_failed">
////		<parameter name="waste" class="Waste">
////			<!-- Bind against variable from creation condition. -->
////			<!-- <bindingoptions>"?waste"</bindingoptions> -->
////			<value>$waste</value>
////		</parameter>
////		<!-- Uniqueness ensures that no two goal are adopted for the same waste. -->
////		<unique/>
////		<creationcondition language="jcl">
////			Waste $waste
////		
////			// ?rbeliefset = (beliefset (element_has_model ?mbeliefset) (beliefset_has_facts $?x ?waste $?y))
////			// ?mbeliefset = (mbeliefset (melement_has_name "wastes"))
////			// 
////			// // unique (?rcapa and ?mgoal are provided internally)
////			// (not (and
////			// 	?rparam = (parameter (parameter_has_name "waste") (parameter_has_value ?waste))
////			// 	?adopted = (goal (element_has_model ?mgoal) (parameterelement_has_parameters contains ?rparam))
////			// 	?rcapa = (capability (capability_has_goals contains ?adopted))
////			// 	// ?adopted = (goal (element_has_model ?mgoal) (parameterelement_has_parameters $?x ?rparam $?y))
////			// 	//?rparam = (parameter (parameter_has_name "waste") (parameter_has_value ?waste))
////			// ))
////		</creationcondition>
////		<!-- Suspend the goal when it is night. -->
////		<contextcondition language="jcl">
////			$beliefbase.daytime
////			
////			// ?rbelief = (belief (element_has_model ?mbelief) (belief_has_fact true))
////			// ?mbelief = (mbelief (melement_has_name "daytime"))
////			</contextcondition>
////		<!-- The goal will be dropped when
////				the agent currently walks to the waste and the waste has vanished. -->
////		<dropcondition language="jcl">
////			// $beliefbase.carriedwaste==null &amp;&amp;
////			// !$beliefbase.getBeliefSet("wastes").containsFact($goal.waste)
////			$beliefbase.carriedwaste==null &amp;&amp;
////			!Arrays.asList($beliefbase.wastes).contains($goal.waste)
////			
////			// ?rgoal and ?mgoal are provided internally
////			// ?rparam = (parameter (parameter_has_name "waste") (parameter_has_value ?waste))
////			// ?rgoal = (goal (parameterelement_has_parameters contains ?rparam))
////			//
////			// ?rbeliefset = (beliefset (element_has_model ?mbeliefset) (beliefset_has_facts excludes ?waste))
////			// ?mbeliefset = (mbeliefset (melement_has_name "wastes"))
////			// 
////			// ?rbelief = (belief (element_has_model ?mbelief) (belief_has_fact null))
////			// ?mbelief = (mbelief (melement_has_name "carriedwaste"))
////			
////			// todo: make sure that all elements are in same capa
////			</dropcondition>
////		<!-- <deliberation cardinality="1"> -->
////		<deliberation>
////			<inhibits ref="performlookforwaste"/>
////			<inhibits ref="achievecleanup" language="jcl">
////				$beliefbase.my_location.getDistance($goal.waste.getLocation())
////				&lt; $beliefbase.my_location.getDistance($ref.waste.getLocation())
////				
////				// ?rbelief = (belief (element_has_model ?mbelief) (belief_has_fact ?myloc))
////				// ?mbelief = (mbelief (melement_has_name "my_location"))
////				// 
////				// ?rparam = (parameter (parameter_has_name "waste") (parameter_has_value ?waste))
////				// ?rgoal = (goal (parameterelement_has_parameters contains ?rparam))
////				// ?waste = (Waste (location ?wasteloc))
////				// 
////				// ?refparam = (parameter (parameter_has_name "waste") (parameter_has_value ?refwaste))
////				// ?refgoal = (goal (parameterelement_has_parameters contains ?refparam))
////				// ?refwaste = (Waste (location ?refwasteloc &amp; 
////				// 	:(&lt; (MethodCallFunction "Location" "getDistance" ?myloc ?wasteloc)
////				// 		(MethodCallFunction "Location" "getDistance" ?myloc ?refwasteloc)  
////				// )))
////			</inhibits>
////		</deliberation>
////		<!-- The goal is achieved when the waste is in some wastebin. - ->
////		<targetcondition>
////			(select one Wastebin $wastebin from $beliefbase.wastebins
////			where $wastebin.contains($goal.waste))!=null
////		</targetcondition>-->
////	</achievegoal>
////
////	<!-- Look out for waste when nothing better to do, what means that
////		the agent is not cleaning, not loading and it is daytime. -->
////	<performgoal name="performlookforwaste" retry="true" exclude="never">
////		<contextcondition language="jcl">
////			$beliefbase.daytime
////			// ?rbelief = (belief (element_has_model ?mbelief) (belief_has_fact true))
////			// ?mbelief = (mbelief (melement_has_name "daytime"))
////			</contextcondition>
////		</performgoal>
////
////	<!-- Perform patrols at night when the agent is not loading. -->
////	<performgoal name="performpatrol" retry="true" exclude="never">
////		<contextcondition language="jcl">
////			!$beliefbase.daytime
////			// ?rbelief = (belief (element_has_model ?mbelief) (belief_has_fact false))
////			// ?mbelief = (mbelief (melement_has_name "daytime"))
////			</contextcondition>
////		</performgoal>
////
////	<!-- Sub-level goals -->
////
////	<!-- Pick up a piece of waste. -->
////	<achievegoal name="achievepickupwaste" retry="false">
////		<parameter name="waste" class="Waste"/>
////	</achievegoal>
////
////	<!-- Drop a piece of waste into a wastebin. -->
////	<achievegoal name="achievedropwaste" retry="true" exclude="never">
////		<parameter name="wastebin" class="Wastebin"/>
////		
////		<!-- The goal has failed when the aimed wastebin is full. - ->
////		<failurecondition>
////			(select one Wastebin $wastebin
////				from $beliefbase.wastebins
////			where $goal.wastebin.getId().equals($wastebin.getId())).isFull()
////		</failurecondition> -->
////		<dropcondition language="jcl">
////			$goal.wastebin.isFull()
////			
////			// ?rparam = (parameter (parameter_has_name "wastebin") (parameter_has_value ?wastebin))
////			// ?wastebin = (Wastebin (isFull() true)) 
////			// ?rgoal = (goal (parameterelement_has_parameters contains ?rparam))
////		</dropcondition>
////	</achievegoal>
////
////	<!-- Try to move to the specified location. -->
////	<achievegoal name="achievemoveto">
////		<parameter name="location" class="Location"/>
////		<!-- The goal has been reached when the agent's location is
////			near the target position as specified in the parameter. - ->
////		<targetcondition>
////				$beliefbase.my_location.isNear($goal.location)
////		</targetcondition> -->
////	</achievegoal>
////
////	<!-- Try to find a not full waste bin that
////		is as near as possible to the agent. -->
////	<querygoal name="querywastebin" exclude="never">
////		<parameter name="result" class="Wastebin" evaluationmode="push" direction="out">
////			<value language="jcl" variable="$wastebin">
////				Wastebin $wastebin &amp;&amp; !$wastebin.isFull()
////				&amp;&amp;
////				!(Wastebin $wastebin2 &amp;&amp; !$wastebin2.isFull()
////									  &amp;&amp; $beliefbase.my_location.getDistance($wastebin.getLocation())
////											   > $beliefbase.my_location.getDistance($wastebin2.getLocation()))
////			</value>
////		</parameter>
////	</querygoal>
////
////	<!-- Find the nearest charging station. -->
////	<querygoal name="querychargingstation" exclude="never">
////		<parameter name="result" class="Chargingstation" evaluationmode="push" direction="out">
////			<value language="jcl" variable="$chargingstation">
////				Chargingstation $chargingstation
////				&amp;&amp;
////				!(Chargingstation $chargingstation2 &amp;&amp; $beliefbase.my_location.getDistance($chargingstation.getLocation())
////															 > $beliefbase.my_location.getDistance($chargingstation2.getLocation()))
////			</value>
////		</parameter>
////	</querygoal>
////	
////	<!-- Basic environment actions modelled as goals to handle failures. -->
////
////	<!-- Get the current vision. -->
////	<achievegoal name="get_vision_action">
////		<parameter name="vision" class="Vision" direction="out"/>
////	</achievegoal>
////
////	<!-- Pick up a piece of waste. -->
////	<achievegoal name="pickup_waste_action">
////		<parameter name="waste" class="Waste"/>
////	</achievegoal>
////
////	<!-- Drop a piece of waste. -->
////	<achievegoal name="drop_waste_action">
////		<parameter name="waste" class="Waste"/>
////		<parameter name="wastebin" class="Wastebin"/>
////	</achievegoal>
////
////	<!-- Memorize the already visited positions in a raster. -->
////	<performgoal name="performmemorizepositions"/>
////
//	
////	@Plan(trigger=MaintainBatteryLoaded.class)
////	protected void load()
////	{
////		chargestate += 0.01;
////	}
//	
//	/**
//	 *  Get the chargestate.
//	 *  @return the chargestate.
//	 */
//	public double getChargeState()
//	{
//		return chargestate;
//	}
//	
//	@Event("chargestate")
//	public void decreaseChargeState()
//	{
//		chargestate -= 0.01;
////		new Event(new Double(chargestate));
//	}
//}
//
//
//// 	</goals>
////
////	<plans>
////		<!-- Walk to the least seen positions.
//// 			Position just visited have value 1. The longer the position
//// 			was not visted the lower the value. Good for seeking movable targets. -->
////		<plan name="leastseenwalk">
////			<body class="LeastSeenWalkPlan"/>
////			<trigger>
////				<goal ref="performlookforwaste"/>
////			</trigger>
////		</plan>
////
////		<!-- This plan explores the map by walking to unknown positions.
//// 			Uses the absolute quantity of visits at the map points.
//// 			Good for seeking firm targets.-->
////		<plan name="exploremap">
////			<body class="ExploreMapPlan"/>
////			<trigger>
////				<goal ref="querywastebin"/>
////				<goal ref="querychargingstation"/>
////			</trigger>
////		</plan>
////
////		<!-- Perform patrols. -->
////		<plan name="patrol">
////			<body class="PatrolPlan"/>
////			<trigger>
////				<goal ref="performpatrol"/>
////			</trigger>
////		</plan>
////
////		<!-- Clean up waste by picking it up
////			and carrying it to a waste bin. -->
////		<plan name="cleanup">
////			<parameter name="waste" class="Waste">
//// 				<goalmapping ref="achievecleanup.waste"/>
////			</parameter>
////			<body class="CleanUpWastePlan"/>
////			<trigger>
////				<goal ref="achievecleanup"/>
////			</trigger>
////		</plan>
////
////		<!-- Pick up a waste. -->
////		<plan name="pickupwaste">
////			<parameter name="waste" class="Waste">
//// 				<goalmapping ref="achievepickupwaste.waste"/>
////			</parameter>
////			<body class="PickUpWastePlan" />
////			<trigger>
////				<goal ref="achievepickupwaste"/>
////			</trigger>
////		</plan>
////
////		<!-- Drop a waste into a waste bin. -->
////		<plan name="dropwaste">
////			<parameter name="wastebin" class="Wastebin">
//// 				<goalmapping ref="achievedropwaste.wastebin"/>
////			</parameter>
////			<body class="DropWastePlan"/>
////			<trigger>
////				<goal ref="achievedropwaste"/>
////			</trigger>
////		</plan>
////
////		<!-- Load the battery. -->
////		<plan name="loadbattery">
////			<body class="LoadBatteryPlan"/>
////			<trigger>
////				<goal ref="maintainbatteryloaded"/>
////			</trigger>
////		</plan>
////
////		<!-- Move to a location. -->
////		<plan name="moveto">
////			<parameter name="location" class="Location">
////				<goalmapping ref="achievemoveto.location"/>
////			</parameter>
////			<body class="MoveToLocationPlan"/>
////			<trigger>
////				<goal ref="achievemoveto"/>
////			</trigger>
////			<!-- <contextcondition>$beliefbase.my_chargestate &gt; 0</contextcondition>-->
////		</plan>
////
////		<!-- This plan memorizes the positions. -->
////		<plan name="memorizepositions">
////			<body class="MemorizePositionsPlan"/>
////			<trigger>
////				<goal ref="performmemorizepositions"/>
////			</trigger>
////		</plan>
////
////
////		<!-- Pickup waste action. -->
////		<plan name="localpickupwasteaction" priority="1">
////			<parameter name="waste" class="Waste">
//// 				<goalmapping ref="pickup_waste_action.waste"/>
////			</parameter>
////			<body class="LocalPickUpWasteActionPlan"/>
////			<trigger>
////				<goal ref="pickup_waste_action"/>
////			</trigger>
////		</plan>
////
////		<!-- Drop waste action. -->
////		<plan name="localdropwasteaction" priority="1">
////			<parameter name="waste" class="Waste">
//// 				<goalmapping ref="drop_waste_action.waste"/>
////			</parameter>
////			<parameter name="wastebin" class="Wastebin">
//// 				<goalmapping ref="drop_waste_action.wastebin"/>
//// 			</parameter>
//// 			<body class="LocalDropWasteActionPlan"/>
////			<trigger>
////				<goal ref="drop_waste_action"/>
////			</trigger>
////		</plan>
////
////		<!-- Drop waste action. -->
////		<plan name="localgetvisionaction" priority="1">
////			<parameter name="vision" class="Vision" direction="out">
//// 				<goalmapping ref="get_vision_action.vision"/>
////			</parameter>
////			<body class="LocalGetVisionActionPlan"/>
////			<trigger>
////				<goal ref="get_vision_action"/>
////			</trigger>
////		</plan>
////	</plans>
////
////	<expressions>
////		<!-- Query all objects from the beliefs that are currently in sight.-->
////		<expression name="query_in_vision_objects">
////			select LocationObject $object
////			from SUtil.joinArbitraryArrays(new Object[]
//// 				{
//// 					$beliefbase.getBeliefSet("wastes").getFacts(),
//// 					$beliefbase.getBeliefSet("wastebins").getFacts(),
//// 					$beliefbase.getBeliefSet("chargingstations").getFacts(),
//// 					$beliefbase.getBeliefSet("cleaners").getFacts()
////				})
////			where $beliefbase.getBelief("my_location").getFact().isNear($object.getLocation(), $beliefbase.getBelief("my_vision").getFact())
////		</expression>
////
////		<!-- Query the max quantity map point. -->
////		<expression name="query_max_quantity">
////			select one MapPoint $mp
////			from $beliefbase.getBeliefSet("visited_positions").getFacts()
////			order by $mp.getQuantity() desc
////		</expression>
////
////		<!-- Query the map points ordered by their quantity
//// 			(least ones first). -->
////		<expression name="query_min_quantity">
////			select MapPoint $mp
////			from $beliefbase.getBeliefSet("visited_positions").getFacts()
////			order by $mp.getQuantity()
////		</expression>
////
////		<!-- Query the map points ordered by their seen value
//// 			(least ones first). -->
////		<expression name="query_min_seen">
////			select MapPoint $mp
////			from $beliefbase.getBeliefSet("visited_positions").getFacts()
////			order by $mp.getSeen()
////		</expression>
////	</expressions>
////
////	<properties>
////		<!--<property name="logging.level">Level.FINE</property>-->
////		<!-- <property name="debugging">true</property> -->
////		<property name="componentviewer.viewerclass">"jadex.bdi.examples.cleanerworld_classic.cleaner.CleanerViewerPanel"</property>
////	</properties>
////
////	<configurations>
////		<configuration name="default">
////			<goals>
////				<initialgoal ref="performlookforwaste"/>
////  				<initialgoal ref="performpatrol"/>
////		  		<initialgoal ref="maintainbatteryloaded"/>
////				<initialgoal ref="performmemorizepositions"/>
////			</goals>
////		</configuration>
////	</configurations>
////</agent>
//
//
//
//
////
////<beliefs>
////	<belief name="environment" class="ContinuousSpace2D">
////		<belief name="myself" class="ISpaceObject" exported="true">
////	<beliefset name="wastes" class="ISpaceObject" />
////	<beliefset name="wastebins"  class="ISpaceObject" />
////	<beliefset name="chargingstations" class="ISpaceObject" />
////	<beliefset name="cleaners" class="ISpaceObject" />
////	<beliefset name="raster" class="Integer">
////	<beliefset name="visited_positions" class="MapPoint">
////	<belief name="daytime" class="boolean" evaluationmode="pull" updaterate="1000">
////	<belief name="my_location" class="IVector2" evaluationmode="push">
////		<belief name="my_speed" class="double" evaluationmode="push">
////		<belief name="my_vision" class="double" evaluationmode="push">
////		<belief name="my_chargestate" class="double" evaluationmode="push">
////	<beliefset name="patrolpoints" class="IVector2">
////
////	<!-- Observe the battery state. -->
////	<maintaingoal name="maintainbatteryloaded">
////		<deliberation>
////			<inhibits ref="performlookforwaste" inhibit="when_in_process"/>
////			<inhibits ref="achievecleanup" inhibit="when_in_process"/>
////			<inhibits ref="performpatrol" inhibit="when_in_process"/>
////		</deliberation>
////			<maintaincondition language="jcl">
////				$beliefbase.my_chargestate > 0.2
////			</maintaincondition>
////		<targetcondition language="jcl">
////				$beliefbase.my_chargestate >= 1.0
////			</targetcondition>
////	</maintaingoal>
////
////	<!-- Clean up some waste. -->
////	<achievegoal name="achievecleanup" retry="true" exclude="never">
////		<parameter name="waste" class="ISpaceObject">
////			<value>$waste</value>
////		</parameter>
////		<unique/>
////		<creationcondition language="jcl">
////			ISpaceObject $waste &amp;&amp; $waste.getType().equals("waste")
////			&amp;&amp; $waste.position!=null
////		</creationcondition>
////		<contextcondition language="jcl">
////			$beliefbase.daytime
////			</contextcondition>
////		<dropcondition language="jcl">
////			// $beliefbase.myself.waste==null &amp;&amp;
////			// !$beliefbase.getBeliefSet("wastes").containsFact($goal.waste)
////			
////			$beliefbase.myself.waste==null &amp;&amp;
////			!Arrays.asList($beliefbase.wastes).contains($goal.waste)
////			</dropcondition>
////		<!-- <deliberation cardinality="1"> -->
////		<deliberation>
////			<inhibits ref="performlookforwaste"/>
////			<inhibits ref="achievecleanup" language="jcl">
////				($goal.waste==$beliefbase.myself.waste || 
////				$ref.waste!=$beliefbase.myself.waste &amp;&amp;
////				$goal.waste.position!=null &amp;&amp;	// Hack!!! should not be required?
////				$ref.waste.position!=null &amp;&amp;	// Hack!!! should not be required?
////				$beliefbase.my_location.getDistance((IVector2)$goal.waste.position)
////					.less($beliefbase.my_location.getDistance((IVector2)$ref.waste.position)))
////			</inhibits>
////		</deliberation>
////		<!-- <targetcondition>
////			(select one ISpaceObject $wastebin from $beliefbase.wastebins
////			where $wastebin.contains($goal.waste))!=null
////		</targetcondition>-->
////	</achievegoal>
////
////	<!-- Look out for waste when nothing better to do, what means that
////		the agent is not cleaning, not loading and it is daytime. -->
////	<performgoal name="performlookforwaste" retry="true" exclude="never">
////		<contextcondition language="jcl">
////			$beliefbase.daytime
////			</contextcondition>
////		</performgoal>
////
////	<!-- Perform patrols at night when the agent is not loading. -->
////	<performgoal name="performpatrol" retry="true" exclude="never">
////		<contextcondition language="jcl">
////			!$beliefbase.daytime
////			</contextcondition>
////		</performgoal>
////
////	<!-- Sub-level goals -->
////
////	<!-- Pick up a piece of waste. -->
////	<achievegoal name="achievepickupwaste" retry="false">
////		<parameter name="waste" class="ISpaceObject"/>
////	</achievegoal>
////
////	<!-- Drop a piece of waste into a wastebin. -->
////	<achievegoal name="achievedropwaste" retry="true" exclude="never">
////		<parameter name="wastebin" class="ISpaceObject"/>
////		<parameter name="waste" class="ISpaceObject"/>
////		
////		<!-- The goal has failed when the aimed wastebin is full. -->
////		<dropcondition language="jcl">
////			//$goal.wastebin.full
////			$goal.wastebin.wastes >= $goal.wastebin.capacity 
////			
////			// ?rparam = (parameter (parameter_has_name "wastebin") (parameter_has_value ?wastebin))
////			// ?wastebin = (Wastebin (isFull() true)) 
////			// ?rgoal = (goal (parameterelement_has_parameters contains ?rparam))
////		</dropcondition>
////	</achievegoal>
////
////	<!-- Try to move to the specified location. -->
////	<achievegoal name="achievemoveto">
////		<parameter name="location" class="IVector2"/>
////		<!-- The goal has been reached when the agent's location is
////			near the target position as specified in the parameter. - ->
////		<targetcondition>
////				$beliefbase.my_location.isNear($goal.location)
////		</targetcondition> -->
////	</achievegoal>
////
////	<!-- Try to find a not full waste bin that
////		is as near as possible to the agent. -->
////	<querygoal name="querywastebin" exclude="never">
////		<parameter name="result" class="ISpaceObject" evaluationmode="push" direction="out">
////			<value language="jcl" variable="$wastebin">
////				// ISpaceObject $wastebin &amp;&amp; $wastebin.getType().equals("wastebin") &amp;&amp; !$wastebin.full
////				// &amp;&amp;
////				// !(ISpaceObject $wastebin2 &amp;&amp; $wastebin2.getType().equals("wastebin") &amp;&amp; !$wastebin2.full
////				// 						  &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$wastebin.position, (IVector2)$wastebin2.position))
////				
////				ISpaceObject $wastebin &amp;&amp; $wastebin.getType().equals("wastebin") &amp;&amp; $wastebin.capacity > $wastebin.wastes
////				&amp;&amp;
////				!(ISpaceObject $wastebin2 &amp;&amp; $wastebin2.getType().equals("wastebin") &amp;&amp; $wastebin2.capacity > $wastebin2.wastes
////										  &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$wastebin.position, (IVector2)$wastebin2.position))
////			</value>
////		</parameter>
////	</querygoal>
////
////	<!-- Find the nearest charging station. -->
////	<querygoal name="querychargingstation" exclude="never">
////		<parameter name="result" class="ISpaceObject" evaluationmode="push" direction="out">
////			<value language="jcl" variable="$chargingstation">
////				ISpaceObject $chargingstation &amp;&amp; $chargingstation.getType().equals("chargingstation")
////				&amp;&amp;
////				!(ISpaceObject $chargingstation2 &amp;&amp; $chargingstation2.getType().equals("chargingstation")
////												 &amp;&amp; MoveTask.isGreater($beliefbase.my_location, (IVector2)$chargingstation.position, (IVector2)$chargingstation2.position))
////			</value>
////		</parameter>
////	</querygoal>
////	
////	<!-- Basic environment actions modelled as goals to handle failures. -->
////
////	<!-- Pick up a piece of waste. -->
////	<achievegoal name="pickup_waste_action">
////		<parameter name="waste" class="ISpaceObject"/>
////	</achievegoal>
////
////	<!-- Drop a piece of waste. -->
////	<achievegoal name="drop_waste_action">
////		<parameter name="waste" class="ISpaceObject"/>
////		<parameter name="wastebin" class="ISpaceObject"/>
////	</achievegoal>
////
////	<!-- Memorize the already visited positions in a raster. -->
////	<performgoal name="performmemorizepositions"/>
////
////	</goals>
////
////<plans>
////	<!-- Walk to the least seen positions.
////			Position just visited have value 1. The longer the position
////			was not visted the lower the value. Good for seeking movable targets. -->
////	<plan name="leastseenwalk">
////		<body class="LeastSeenWalkPlan"/>
////		<trigger>
////			<goal ref="performlookforwaste"/>
////		</trigger>
////	</plan>
////	
////	<!-- Walk to random positions. -->
////	<plan name="randomwalk" priority="-1">
////		<body class="RandomWalkPlan"/>
////		<trigger>
////			<goal ref="performlookforwaste"/>
////			<goal ref="querywastebin"/>
////			<goal ref="querychargingstation"/>
////		</trigger>
////	</plan>
////
////	<!-- This plan explores the map by walking to unknown positions.
////			Uses the absolute quantity of visits at the map points.
////			Good for seeking firm targets.-->
////	<plan name="exploremap">
////		<body class="ExploreMapPlan"/>
////		<trigger>
////			<goal ref="querywastebin"/>
////			<goal ref="querychargingstation"/>
////		</trigger>
////	</plan>
////
////	<!-- Perform patrols. -->
////	<plan name="patrol">
////		<body class="PatrolPlan"/>
////		<trigger>
////			<goal ref="performpatrol"/>
////		</trigger>
////	</plan>
////
////	<!-- Clean up waste by picking it up
////		and carrying it to a waste bin. -->
////	<plan name="cleanup">
////		<parameter name="waste" class="ISpaceObject">
////				<goalmapping ref="achievecleanup.waste"/>
////		</parameter>
////		<body class="CleanUpWastePlan"/>
////		<trigger>
////			<goal ref="achievecleanup"/>
////		</trigger>
////	</plan>
////
////	<!-- Pick up a waste. -->
////	<plan name="pickupwaste">
////		<parameter name="waste" class="ISpaceObject">
////				<goalmapping ref="achievepickupwaste.waste"/>
////		</parameter>
////		<body class="PickUpWastePlan" />
////		<trigger>
////			<goal ref="achievepickupwaste"/>
////		</trigger>
////	</plan>
////
////	<!-- Drop a waste into a waste bin. -->
////	<plan name="dropwaste">
////		<parameter name="wastebin" class="ISpaceObject">
////				<goalmapping ref="achievedropwaste.wastebin"/>
////		</parameter>
////		<parameter name="waste" class="ISpaceObject">
////				<goalmapping ref="achievedropwaste.waste"/>
////		</parameter>
////		<body class="DropWastePlan"/>
////		<trigger>
////			<goal ref="achievedropwaste"/>
////		</trigger>
////	</plan>
////
////	<!-- Load the battery. -->
////	<plan name="loadbattery">
////		<body class="LoadBatteryPlan"/>
////		<trigger>
////			<goal ref="maintainbatteryloaded"/>
////		</trigger>
////	</plan>
////
////	<!-- Move to a location. -->
////	<plan name="moveto">
////		<parameter name="location" class="IVector2">
////			<goalmapping ref="achievemoveto.location"/>
////		</parameter>
////		<body class="MoveToLocationPlan"/>
////		<trigger>
////			<goal ref="achievemoveto"/>
////		</trigger>
////		<!-- <contextcondition>$beliefbase.my_chargestate &gt; 0</contextcondition>-->
////	</plan>
////
////	<!-- This plan memorizes the positions. -->
////	<plan name="memorizepositions">
////		<body class="MemorizePositionsPlan"/>
////		<trigger>
////			<goal ref="performmemorizepositions"/>
////		</trigger>
////	</plan>
////</plans>
////
////<expressions>
////	<!-- Query the max quantity map point. -->
////	<expression name="query_max_quantity">
////		select one MapPoint $mp
////		from $beliefbase.getBeliefSet("visited_positions").getFacts()
////		order by $mp.getQuantity() desc
////	</expression>
////
////	<!-- Query the map points ordered by their quantity
////			(least ones first). -->
////	<expression name="query_min_quantity">
////		select MapPoint $mp
////		from $beliefbase.getBeliefSet("visited_positions").getFacts()
////		order by $mp.getQuantity()
////	</expression>
////
////	<!-- Query the map points ordered by their seen value
////			(least ones first). -->
////	<expression name="query_min_seen">
////		select MapPoint $mp
////		from $beliefbase.getBeliefSet("visited_positions").getFacts()
////		order by $mp.getSeen()
////	</expression>
////</expressions>
////
////<properties>
////	<!--<property name="logging.level">Level.FINE</property>-->
////	<!-- <property name="debugging">true</property>  -->
////	<property name="componentviewer.viewerclass">"jadex.bdi.examples.cleanerworld.cleaner.CleanerViewerPanel"</property>
////</properties>
////
////<configurations>
////	<configuration name="default">
////		<goals>
////			<initialgoal ref="performlookforwaste"/>
////				<initialgoal ref="performpatrol"/>
////	  		<initialgoal ref="maintainbatteryloaded"/>
////			<initialgoal ref="performmemorizepositions"/>
////		</goals>
////	</configuration>
////</configurations>
////</agent>

