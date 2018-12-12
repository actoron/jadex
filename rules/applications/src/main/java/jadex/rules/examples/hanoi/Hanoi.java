package jadex.rules.examples.hanoi;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jadex.commons.gui.SGUI;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;


/**
 *  OAV test doing towers of Hanoi.
 */
public class Hanoi
{
	//-------- OAV metamodel --------
	
	/** The type model. */
	public static final OAVTypeModel hanoi_type_model;
	
	/** The java OAV atribute type. */
	public static final OAVObjectType java_oavattribute_type;

	
	/** The agent type. */
	public static final OAVObjectType	agent_type;
	
	/** Agent has tower A. */
	public static final OAVAttributeType	agent_has_tower_a;

	/** Agent has tower B. */
	public static final OAVAttributeType	agent_has_tower_b;

	/** Agent has tower C. */
	public static final OAVAttributeType	agent_has_tower_c;
	
	/** Agent has move goals. */
	public static final OAVAttributeType	agent_has_movegoals;

	
	/** The disc type. */
	public static final OAVObjectType	disc_type;
	
	/** Disc has size. */
	public static final OAVAttributeType	disc_has_size;
	
	
	/** The move goal type. */
	public static final OAVObjectType	movegoal_type;
	
	/** Move goal is executing. */
	public static final OAVAttributeType	movegoal_is_executing;

	/** Move goal has precondition. */
	public static final OAVAttributeType	movegoal_has_precodition;

	/** Move goal has postcondition. */
	public static final OAVAttributeType	movegoal_has_postcodition;
	
	/** Move goal has from. */
	public static final OAVAttributeType	movegoal_has_from ;

	/** Move goal has to. */
	public static final OAVAttributeType	movegoal_has_to;

	/** Move goal has temp. */
	public static final OAVAttributeType	movegoal_has_temp;
	
	/** Move goal has number. */
	public static final OAVAttributeType	movegoal_has_number;

	static
	{
		hanoi_type_model = new OAVTypeModel("hanoi_type_model");
		hanoi_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		java_oavattribute_type = hanoi_type_model.createJavaType(OAVAttributeType.class, OAVJavaType.KIND_VALUE);

		disc_type	= hanoi_type_model.createType("disc");
		disc_has_size = disc_type.createAttributeType("disc_has_size", OAVJavaType.java_integer_type);
		
		movegoal_type	= hanoi_type_model.createType("movegoal");
		movegoal_is_executing	= movegoal_type.createAttributeType("movegoal_is_executing",  OAVJavaType.java_boolean_type, OAVAttributeType.NONE, Boolean.FALSE);
		movegoal_has_precodition	= movegoal_type.createAttributeType("movegoal_has_precondition", movegoal_type);
		movegoal_has_postcodition	= movegoal_type.createAttributeType("movegoal_has_postcondition", movegoal_type);
		movegoal_has_from	= movegoal_type.createAttributeType("movegoal_has_from", java_oavattribute_type);
		movegoal_has_to	= movegoal_type.createAttributeType("movegoal_has_to", java_oavattribute_type);
		movegoal_has_temp	= movegoal_type.createAttributeType("movegoal_has_temp", java_oavattribute_type);
		movegoal_has_number	= movegoal_type.createAttributeType("movegoal_has_number",  OAVJavaType.java_integer_type);
		
		agent_type	= hanoi_type_model.createType("agent");
		agent_has_tower_a	= agent_type.createAttributeType("agent_has_tower_a", disc_type, OAVAttributeType.LIST);
		agent_has_tower_b	= agent_type.createAttributeType("agent_has_tower_b", disc_type, OAVAttributeType.LIST);
		agent_has_tower_c	= agent_type.createAttributeType("agent_has_tower_c", disc_type, OAVAttributeType.LIST);
		agent_has_movegoals	= agent_type.createAttributeType("agent_has_movegoals", movegoal_type, OAVAttributeType.SET);
	}
	
	//-------- main --------
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		int discs	= 15;
		
		// Implementation to use: 1=Rete, 2=Goals, 3=State, 4=Lists
		int	impl	= 1;
		boolean show_towers	= true;
		boolean show_rete	= true;
		test(discs, impl, show_towers, show_rete);
		
		// Run benchmark comparing all implementations.
//		benchmark(discs);
	}
	
	/**
	 *  Test method. Calls one of the implementations once.
	 */
	protected static void test(int discs, int impl, boolean showtowers, boolean showrete)
	{
		switch(impl)
		{
			// Rete implementation.
			case 1:
			{
				IOAVState state = createState();
				RuleSystem	rete	= initializeRete(state, showrete);
				Object agent = initState(discs, state, showtowers);
				moveWithRete(state, agent, agent_has_tower_a, agent_has_tower_c, agent_has_tower_b, discs, rete, showrete);
				break;
			}
			// State with goals but without Rete.
			case 2:
			{
				IOAVState state = createState();
				Object agent = initState(discs, state, showtowers);
				moveWithoutRete(state, agent, agent_has_tower_a, agent_has_tower_c, agent_has_tower_b, discs);
				break;
			}
			// Simple state based implementation
			case 3:
			{
				IOAVState state = createState();
				Object agent = initState(discs, state, showtowers);
				moveWithState(state, agent, agent_has_tower_a, agent_has_tower_c, agent_has_tower_b, discs);
				break;
			}
			// Simple list based implementation (no state involved).
			case 4:
			{
				IOAVState state = createState();
				Object agent = initState(discs, state, showtowers);
				List	from	= new ArrayList();
				List	to	= new ArrayList();
				List	temp	= new ArrayList();
				from.addAll(state.getAttributeValues(agent, agent_has_tower_a));
				moveWithoutState(from, to, temp, discs);
				break;
			}
		}
	}

	/**
	 *  Create an OAV state.
	 */
	protected static IOAVState createState()
	{
		return OAVStateFactory.createOAVState(hanoi_type_model);
//		return new JenaOAVState();
	}
	
	/**
	 *  Initialize the state with the given number of discs.
	 */
	protected static Object initState(int discs, IOAVState state, boolean showtowers)
	{
		// Setup
		Object	agent	= state.createRootObject(agent_type);
		for(int i=discs; i>0; i--)
		{
			Object	disc	= state.createObject(disc_type);
			state.setAttributeValue(disc, disc_has_size, Integer.valueOf(i));
			state.addAttributeValue(agent, agent_has_tower_a, disc);
		}
		
		// Show Hanoi towers in JFrame.
		if(showtowers)
			showFrame(state, agent);
		
		return agent;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	benchmark(int maxdiscs)
	{
		int	times	= (int)Math.pow(2, maxdiscs);
		int	discs	= 1;
		
		while(times>1)
		{
			// Simple list based implementation (no state involved).
			IOAVState	state	= createState();
			Object	agent	= initState(discs, state, false);
			List	from	= new ArrayList();
			List	to	= new ArrayList();
			List	temp	= new ArrayList();
			from.addAll(state.getAttributeValues(agent, agent_has_tower_a));
			long	nostatestart	= System.currentTimeMillis();
			for(int i=0; i<(times*3); i++)
			{
				if(i%2==0)
				{
					moveWithoutState(from, to, temp, discs);
				}
				else
				{
					moveWithoutState(to, from, temp, discs);
				}
			}
			double	nostatetime	= calcTime(nostatestart, times*3, discs);

			// Simple state based implementation
			state	= createState();
			agent	= initState(discs, state, false);
			long	statestart	= System.currentTimeMillis();
			for(int i=0; i<times; i++)
			{
				if(i%2==0)
				{
					moveWithState(state, agent, agent_has_tower_a, agent_has_tower_b, agent_has_tower_c, discs);
				}
				else
				{
					moveWithState(state, agent, agent_has_tower_b, agent_has_tower_a, agent_has_tower_c, discs);
				}
			}
			double	statetime	= calcTime(statestart, times, discs);
			
			// Goal based implementation.
			state	= createState();
			agent	= initState(discs, state, false);
			long	noretestart	= System.currentTimeMillis();
			for(int i=0; i<Math.ceil(times/5.0); i++)
			{
				if(i%2==0)
				{
					moveWithoutRete(state, agent, agent_has_tower_a, agent_has_tower_b, agent_has_tower_c, discs);
				}
				else
				{
					moveWithoutRete(state, agent, agent_has_tower_b, agent_has_tower_a, agent_has_tower_c, discs);
				}
			}
			double	noretetime	= calcTime(noretestart, (int)Math.ceil(times/5.0), discs);

			// Rete based implementation.
			state = createState();
			RuleSystem	rete	= initializeRete(state, false);
			agent = initState(discs, state, false);
			long	retestart	= System.currentTimeMillis();
			for(int i=0; i<Math.ceil(times/25.0); i++)
			{
				if(i%2==0)
				{
					moveWithRete(state, agent, agent_has_tower_a, agent_has_tower_b, agent_has_tower_c, discs, rete, false);
				}
				else
				{
					moveWithRete(state, agent, agent_has_tower_b, agent_has_tower_a, agent_has_tower_c, discs, rete, false);
				}
				//System.out.println("Rete memory size: "+((RetePatternMatcher)rete.getMatcher()).getReteMemory().getTotalMemorySize());
				//System.out.println("State size: "+state.getSize());
			}
			double	retetime	=  calcTime(retestart, (int)Math.ceil(times/25.0), discs);

			System.out.println("Discs:"+discs
				+" \tnostate:"+(int)nostatetime
				+" \tstate:"+(int)statetime
				+" \toverhead(%):"+calcOverhead(nostatetime, statetime)

				+" \tnorete:"+(int)noretetime	//+" overhead(%):"+calcOverhead(nostatetime, noretetime)
				+" \trete:"+(int)retetime
				+" \toverhead(%):"+calcOverhead(noretetime, retetime));
			
			times	/= 2;
			discs++;
		}
	}

	/**
	 *  calculates time per move in nanoseconds.
	 */
	protected static double calcTime(long start, int times, int discs)
	{
		long	elapsed	= System.currentTimeMillis() - start;
//		System.out.println("elapsed: "+elapsed);
		return 1000000*elapsed/(double)times/Math.pow(2, discs);
	}

	/**
	 *  calculates overhead of second value in percent.
	 */
	protected static String calcOverhead(double benchmark, double value)
	{
		return benchmark!=0 ? ""+(int)((value*100/benchmark)-100) : "n/a";
	}

	/**
	 *  Solve the Hanoi puzzle using goals in state.
	 */
	protected static void moveWithoutRete(IOAVState state, Object agent, OAVAttributeType from, OAVAttributeType to, OAVAttributeType temp, int num)
	{
		// Goal based implementation.
		Object	rootgoal;
		synchronized(state)
		{
			rootgoal	= state.createObject(movegoal_type);
			state.setAttributeValue(rootgoal, movegoal_has_from, from);
			state.setAttributeValue(rootgoal, movegoal_has_to, to);
			state.setAttributeValue(rootgoal, movegoal_has_temp, temp);
			state.setAttributeValue(rootgoal, movegoal_has_number, Integer.valueOf(num));
			state.addAttributeValue(agent, agent_has_movegoals, rootgoal);
		}

		// Implementation based on linear search.
//		while(state.getAttributeValues(agent, agent_has_movegoals)!=null)
//		{
//			// Find a goal to execute.
//			Object	goal	= null;
//			Iterator	goals	= state.getAttributeValues(agent, agent_has_movegoals).iterator();
//			while(goal==null && goals.hasNext())
//			{
//				goal	= goals.next();
//				boolean	executing	= ((Boolean)state.getAttributeValue(goal, movegoal_is_executing)).booleanValue();
//				Object	precondition	= state.getAttributeValue(goal, movegoal_has_precodition);
//				if(executing || precondition!=null && state.containsObject(precondition))
//				{
//					// If already executing or precondition not fulfilled continue search.
//					goal	= null;
//				}
//			}
//			
//			if(goal==null)
//				throw new RuntimeException("No goal to execute.");
//			
//			performMoveGoal(state, agent, goal);
//			Thread.yield();
//		}
		
		// Recursive implementation.
		performMoveGoalRecursive(state, agent, rootgoal);
	}
	
	/**
	 *  Recursively perform ove goals.
	 */
	protected static void	performMoveGoalRecursive(IOAVState state, Object agent, Object goal)
	{
		Object[]	subgoals	= performMoveGoal(state, agent, goal);
		if(subgoals!=null)
		{
			performMoveGoalRecursive(state, agent, subgoals[0]);
			performMoveGoalRecursive(state, agent, subgoals[1]);
			performMoveGoalRecursive(state, agent, subgoals[2]);
		}
	}

	/**
	 *  Perform a single move goal.
	 */
	protected static Object[]	performMoveGoal(IOAVState state, Object agent, Object goal)
	{
		synchronized(state)
		{
			Object[]	ret	= null;
			OAVAttributeType from;
			OAVAttributeType to;
			OAVAttributeType temp;
			state.setAttributeValue(goal, movegoal_is_executing, Boolean.TRUE);
			
			from	= (OAVAttributeType)state.getAttributeValue(goal, movegoal_has_from);
			to	= (OAVAttributeType)state.getAttributeValue(goal, movegoal_has_to);
			temp	= (OAVAttributeType)state.getAttributeValue(goal, movegoal_has_temp);
			int	number	= ((Integer)state.getAttributeValue(goal, movegoal_has_number)).intValue();
			
//			System.out.println(goal+": Moving "+number+" discs from "+from+" to "+to+" with "+temp);
			
			// Execute atomic goal.
			if(number==1)
			{
				// Avoid race conditions with GUI thread.
				List	fromlist	= (List)state.getAttributeValues(agent, from);
				Object	disc	= fromlist.get(fromlist.size()-1);
				state.addAttributeValue(agent, to, disc);
				state.removeAttributeValue(agent, from, disc);
			
				// Drop goal and postconditions.
				while(goal!=null)
				{
					Object	postcondition	= state.getAttributeValue(goal, movegoal_has_postcodition);
//					System.out.println("Finished: "+goal);
					state.dropObject(goal);
					goal	= postcondition;
				}
			}
			
			// Decompose complex goal.
			else
			{
				Object	subgoal1	= state.createObject(movegoal_type);
				state.setAttributeValue(subgoal1, movegoal_has_from, from);
				state.setAttributeValue(subgoal1, movegoal_has_to, temp);
				state.setAttributeValue(subgoal1, movegoal_has_temp, to);
				state.setAttributeValue(subgoal1, movegoal_has_number, Integer.valueOf(number-1));
				state.addAttributeValue(agent, agent_has_movegoals, subgoal1);
	
				Object	subgoal2	= state.createObject(movegoal_type);
				state.setAttributeValue(subgoal2, movegoal_has_from, from);
				state.setAttributeValue(subgoal2, movegoal_has_to, to);
				state.setAttributeValue(subgoal2, movegoal_has_temp, temp);
				state.setAttributeValue(subgoal2, movegoal_has_number, Integer.valueOf(1));
				state.setAttributeValue(subgoal2, movegoal_has_precodition, subgoal1);
				state.addAttributeValue(agent, agent_has_movegoals, subgoal2);
	
				Object	subgoal3	= state.createObject(movegoal_type);
				state.setAttributeValue(subgoal3, movegoal_has_from, temp);
				state.setAttributeValue(subgoal3, movegoal_has_to, to);
				state.setAttributeValue(subgoal3, movegoal_has_temp, from);
				state.setAttributeValue(subgoal3, movegoal_has_number, Integer.valueOf(number-1));
				state.setAttributeValue(subgoal3, movegoal_has_precodition, subgoal2);
				state.setAttributeValue(subgoal3, movegoal_has_postcodition, goal);
				state.addAttributeValue(agent, agent_has_movegoals, subgoal3);
				
				ret	= new Object[]{subgoal1, subgoal2, subgoal3};
			}
			
			return ret;
		}
	}

	/**
	 *  Open a JFrame displaying the towers.
	 */
	protected static void showFrame(final IOAVState state, Object agent)
	{
		JFrame	frame	= new JFrame("Towers of Hanoi");
		final JComponent	comp	= new HanoiComponent(state, agent);
		frame.getContentPane().add(comp);
		frame.setSize(800, 250);
		frame.setLocation(SGUI.calculateMiddlePosition(frame).x, 0);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		state.addStateListener(new IOAVStateListener()
		{
			/**
			 *  Notification when an attribute value of an object has been set.
			 *  @param id The object id.
			 *  @param attr The attribute type.
			 *  @param oldvalue The oldvalue.
			 *  @param newvalue The newvalue.
			 */
			public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
			{
				comp.repaint();
				Thread.yield();
			}
			
			/**
			 *  Notification when an object has been added to the state.
			 *  @param id The object id.
			 *  @param type The object type.
			 */
			public void objectAdded(Object id, OAVObjectType type, boolean root)
			{
				comp.repaint();
				Thread.yield();
			}
			
			/**
			 *  Notification when an object has been removed from state.
			 *  @param id The object id.
			 *  @param type The object type.
			 */
			public void objectRemoved(Object id, OAVObjectType type)
			{
				comp.repaint();
				Thread.yield();
			}	
		}, true);
	}

	/**
	 *  Move some discs.
	 *  @param state	The state.
	 *  @param agent	The agent.
	 *  @param from		The tower to move from.
	 *  @param to	The tower to move to.
	 *  @param temp	The tower for temporarily holding discs.
	 *  @param num	The number of discs to move.
	 */
	protected static void	moveWithState(IOAVState state, Object agent, OAVAttributeType from, OAVAttributeType to, OAVAttributeType temp, int num)
	{
		if(num==1)
		{
			// Avoid race conditions with GUI thread.
			synchronized(state)
			{
				List	fromlist	= (List)state.getAttributeValues(agent, from);
				Object	disc	= fromlist.get(fromlist.size()-1);
				state.removeAttributeValue(agent, from, disc);
				state.addAttributeValue(agent, to, disc);
			}
			Thread.yield();
		}
		else
		{
			moveWithState(state, agent, from, temp, to, num-1);
			moveWithState(state, agent, from, to, temp, 1);
			moveWithState(state, agent, temp, to, from, num-1);
		}
	}
	
	/**
	 *  Move some discs.
	 *  @param from		The tower to move from.
	 *  @param to	The tower to move to.
	 *  @param temp	The tower for temporarily holding discs.
	 *  @param num	The number of discs to move.
	 */
	protected static void	moveWithoutState(List from, List to, List temp, int num)
	{
		if(num==1)
		{
			to.add(from.remove(from.size()-1));
			Thread.yield();
		}
		else
		{
			moveWithoutState(from, temp, to, num-1);
			moveWithoutState(from, to, temp, 1);
			moveWithoutState(temp, to, from, num-1);
		}
	}
	
	/**
	 *  Initialize Rete system.
	 *  @param state	The state.
	 *  @param showrete	Show the rete structure in a JFrame.
	 */
	protected static RuleSystem	initializeRete(final IOAVState state, boolean showrete)
	{
		// Condition for goal (using "executing" flag). 
		ObjectCondition oc1a = new ObjectCondition(movegoal_type);
		oc1a.addConstraint(new LiteralConstraint(movegoal_is_executing, Boolean.FALSE));
		oc1a.addConstraint(new LiteralConstraint(movegoal_has_precodition, null));
		oc1a.addConstraint(new BoundConstraint(null, new Variable("goal", movegoal_type)));
		
		// Condition for goal (for using not instead of"executing" flag). 
		ObjectCondition oc1b = new ObjectCondition(movegoal_type);
		oc1b.addConstraint(new LiteralConstraint(movegoal_has_precodition, null));
		oc1b.addConstraint(new BoundConstraint(null, new Variable("goal", movegoal_type)));
		
		// Condition for checking that no goal exists having 'goal' as precondition. 
		ObjectCondition temp = new ObjectCondition(movegoal_type);
//		temp.addConstraint(new LiteralConstraint(movegoal_has_postcodition, null, IOperator.NOTEQUAL));	// Not necessary, but improves performance when indexing is not used
		temp.addConstraint(new BoundConstraint(movegoal_has_postcodition, new Variable("goal", movegoal_type)));
		NotCondition	nc1	= new NotCondition(temp);
		
		// Condition for agent without multi-pattern.
		ObjectCondition oc2b = new ObjectCondition(agent_type);
		oc2b.addConstraint(new BoundConstraint(agent_has_movegoals, new Variable("goal", movegoal_type), IOperator.CONTAINS));
		oc2b.addConstraint(new BoundConstraint(null, new Variable("agent", agent_type)));

		// Condition for agent with multi-pattern.
		ObjectCondition oc2c = new ObjectCondition(agent_type);
		List	vars	= new ArrayList();
		vars.add(new Variable("$?g1", movegoal_type, true, false));
		vars.add(new Variable("goal", movegoal_type));
		vars.add(new Variable("$?g2", movegoal_type, true, false));
		oc2c.addConstraint(new BoundConstraint(agent_has_movegoals, vars, IOperator.CONTAINS));
		oc2c.addConstraint(new BoundConstraint(null, new Variable("agent", agent_type)));

		Rulebase rb = new Rulebase();
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object	agent	= assigments.getVariableValue("agent");
				Object	goal	= assigments.getVariableValue("goal");
				//System.out.println("Executing action: "+agent+" "+goal);
				performMoveGoal(state, agent, goal);
//				Thread.yield();
//				OAVTreeModel.createOAVFrame("Towers of Hanoi", state, agent).setVisible(true);
			}
		};
		
		// Choose rule: 0) without not, 1,2,3) with not.
//		IRule rule = new Rule("Hanoi", new AndCondition(new ICondition[]{oc1a, oc2b}), action);
//		IRule rule = new Rule("Hanoi", new AndCondition(new ICondition[]{oc2c, oc1a}), action);
		IRule rule = new Rule("Hanoi", new AndCondition(new ICondition[]{oc1b, nc1, oc2b}), action);
//		IRule rule = new Rule("Hanoi", new AndCondition(new ICondition[]{oc1b, oc2b, nc1}), action);
//		IRule rule = new Rule("Hanoi", new AndCondition(new ICondition[]{oc2c, oc1b, nc1}), action);

//		System.out.println("Rule: "+rule);
		rb.addRule(rule);
		
		RetePatternMatcherFunctionality pf = new RetePatternMatcherFunctionality(rb);
		RuleSystem rete = new RuleSystem(state, rb, pf);
		rete.init();

//		if(showrete)
//			RetePanel.createReteFrame("Hanoi Rete Structure", ((RetePatternMatcher)rete.getMatcher()).getReteNode());
		
		return rete;
	}

	/**
	 *  Move some discs with Rete algorithm.
	 *  @param state	The state.
	 *  @param agent	The agent.
	 *  @param from		The tower to move from.
	 *  @param to	The tower to move to.
	 *  @param temp	The tower for temporarily holding discs.
	 *  @param num	The number of discs to move.
	 */
	protected static void	moveWithRete(IOAVState state, Object agent, OAVAttributeType from, 
		OAVAttributeType to, OAVAttributeType temp, int num, RuleSystem rete, boolean showrete)
	{
		Object	rootgoal	= state.createObject(movegoal_type);
		state.setAttributeValue(rootgoal, movegoal_has_from, from);
		state.setAttributeValue(rootgoal, movegoal_has_to, to);
		state.setAttributeValue(rootgoal, movegoal_has_temp, temp);
		state.setAttributeValue(rootgoal, movegoal_has_number, Integer.valueOf(num));
		state.addAttributeValue(agent, agent_has_movegoals, rootgoal);
		state.notifyEventListeners();
		
		if(showrete)
		{
			RuleSystemExecutor	exe	= new RuleSystemExecutor(rete, true);
			RuleEnginePanel.createRuleEngineFrame(exe, "Hanoi Rete Structure");
		}
		else
		{
			while(!rete.getAgenda().isEmpty())
			{
				rete.getAgenda().fireRule();
				state.expungeStaleObjects();
				state.notifyEventListeners();
			}
		}
	}

	//-------- helper classes --------
	
	/**
	 *  Component for displaying the towers.
	 */
	protected static class HanoiComponent	extends JComponent
	{
		//-------- attributes --------
		
		/** The state. */
		protected IOAVState	state;
		
		/** The agent. */
		protected Object	agent;
		
		//-------- constructors --------
		
		/**
		 *  Create a new Hanoi component.
		 */
		public HanoiComponent(IOAVState state, Object agent)
		{
			this.state	= state;
			this.agent	= agent;
		}
		
		//-------- methods --------
		
		/**
		 *  Paint the towers.
		 */
		protected void paintComponent(Graphics g)
		{
			synchronized(state)
			{
				List	a	= (List)state.getAttributeValues(agent, agent_has_tower_a);
				List	b	= (List)state.getAttributeValues(agent, agent_has_tower_b);
				List	c	= (List)state.getAttributeValues(agent, agent_has_tower_c);
				int	total	= (a!=null ? a.size() : 0)
							+ (b!=null ? b.size() : 0)
							+ (c!=null ? c.size() : 0);
				
				Rectangle	bounds	= getBounds();
				Insets	insets	= getInsets();
				bounds.x	= insets.left;
				bounds.y	= insets.top;
				bounds.width	-= insets.left + insets.right;
				bounds.height	-= insets.top + insets.bottom;
				
				int	towerwidth	= bounds.width / 3;
				int	discheight	= bounds.height / (total+1) - 2;
				
				for(int i=0; a!=null && i<a.size(); i++)
				{
					Object	disc	= a.get(i);
					int	size	= ((Integer)state.getAttributeValue(disc, disc_has_size)).intValue();
					int	width	= towerwidth * size / (total+1);
					g.fillRect(bounds.x + (towerwidth-width)/2, bounds.height - (i+1)*(discheight+1), width, discheight);
				}
	
				for(int i=0; b!=null && i<b.size(); i++)
				{
					Object	disc	= b.get(i);
					int	size	= ((Integer)state.getAttributeValue(disc, disc_has_size)).intValue();
					int	width	= towerwidth * size / (total+1);
					g.fillRect(bounds.x + towerwidth + (towerwidth-width)/2, bounds.height - (i+1)*(discheight+1), width, discheight);
				}
	
				for(int i=0; c!=null && i<c.size(); i++)
				{
					Object	disc	= c.get(i);
					int	size	= ((Integer)state.getAttributeValue(disc, disc_has_size)).intValue();
					int	width	= towerwidth * size / (total+1);
					g.fillRect(bounds.x + towerwidth*2 + (towerwidth-width)/2, bounds.height - (i+1)*(discheight+1), width, discheight);
				}
			}
		}
	}
}
