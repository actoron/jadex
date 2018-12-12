package jadex.rules.examples.blocksworld;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IPatternMatcherFunctionality;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.RetePatternMatcherState;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.IConstraint;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.OrConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  The blocks world meta model.
 */
public class Blocksworld
{
	//-------- OAV type definitions --------
	
	/** The blocksworld type model. */
	public static final OAVTypeModel blocksworld_type_model;
	
	/** The java block type. */
	public static final OAVJavaType java_block_type;

	/** The block type. */
	public static final OAVObjectType block_type;
	
	/** A block has a name. */
	public static final OAVAttributeType block_has_name;
	
	/** A block has a color. */
	public static final OAVAttributeType block_has_color;
	
	/** A block is on another block. */
	public static final OAVAttributeType block_has_on;
	
	/** A block is left from another block. */
	public static final OAVAttributeType block_has_left;
	
//	/** A block has other blocks as friends. */
//	public static final OAVAttributeType block_has_friends;
	
	
	/** The ball type. */
	public static final OAVObjectType ball_type;
	
	/** A ball has a color. */
	public static final OAVAttributeType ball_has_color;
	
	
	/** The Java blocks container. */
	public static final OAVObjectType blockcontainer_type;
	
	/** A block container can store blocks. */
	public static final OAVAttributeType blockcontainer_has_blocks;

	static
	{
		blocksworld_type_model = new OAVTypeModel("blocksworld_type_model");
		blocksworld_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		// java block
		java_block_type	= blocksworld_type_model.createJavaType(Block.class, OAVJavaType.KIND_BEAN);
		
		// block
		block_type = blocksworld_type_model.createType("block");
		block_has_name = block_type.createAttributeType("block_has_name", OAVJavaType.java_string_type);
		block_has_color = block_type.createAttributeType("block_has_color", OAVJavaType.java_string_type);
		block_has_on = block_type.createAttributeType("block_has_on", block_type, OAVAttributeType.LIST);
		block_has_left = block_type.createAttributeType("block_has_left", block_type, OAVAttributeType.LIST);

		// ball
		ball_type = blocksworld_type_model.createType("ball");
		ball_has_color = ball_type.createAttributeType("ball_has_color", OAVJavaType.java_string_type);
	
		// java block container
		blockcontainer_type = blocksworld_type_model.createType("blockcontainer");
		blockcontainer_has_blocks = blockcontainer_type.createAttributeType("blockcontainer_has_blocks", 
			OAVJavaType.java_object_type, OAVAttributeType.LIST, null);
	}
	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
		performBlocksWorldStackTest();
	}
	
	/**
	 *  Perform the blocksworld stack test.
	 */
	public static void performBlocksWorldStackTest()
	{
		RuleSystem system = createReteSystem();
		RuleSystemExecutor	exe	= new RuleSystemExecutor(system, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "Blocksworld Test");
		IOAVState state = system.getState();
		
		Object b1 = state.createRootObject(block_type);
		Object b2 = state.createRootObject(block_type);
		Object b3 = state.createRootObject(block_type);
		Object b4 = state.createRootObject(block_type);
		Object b5 = state.createRootObject(block_type);
		
		/*System.out.println("B1: "+b1);
		System.out.println("B2: "+b2);
		System.out.println("B3: "+b3);
		System.out.println("B4: "+b4);
		System.out.println("B5: "+b5);*/
					
		state.setAttributeValue(b1, block_has_name, "B1");
		state.setAttributeValue(b1, block_has_color, "red");
		state.addAttributeValue(b1, block_has_on, b2);
		state.addAttributeValue(b1, block_has_on, b3);
		state.addAttributeValue(b1, block_has_on, b4);
		state.addAttributeValue(b1, block_has_left, b5);
		
		state.setAttributeValue(b2, block_has_name, "B2");
		state.setAttributeValue(b2, block_has_color, "green");
		state.addAttributeValue(b2, block_has_on, b3);
		state.addAttributeValue(b2, block_has_on, b4);
		state.addAttributeValue(b2, block_has_left, b5);
		
		state.setAttributeValue(b3, block_has_name, "B3");
		state.setAttributeValue(b3, block_has_color, "blue");
		state.addAttributeValue(b3, block_has_on, b4);
		state.addAttributeValue(b3, block_has_left, b5);
		
		state.setAttributeValue(b4, block_has_name, "B4");
		state.setAttributeValue(b4, block_has_color, "yellow");
		state.addAttributeValue(b4, block_has_left, b5);
		
		state.setAttributeValue(b5, block_has_name, "B5");
		state.setAttributeValue(b5, block_has_color, "red");
		
		/*state.setAttributeValue(b1, block_has_name, "B1");
		System.out.println("\n------------------------------------");		
		state.setAttributeValue(b1, block_has_color, "red");
		System.out.println("\n------------------------------------");
		state.setAttributeValue(b1, block_has_on, b2);
		System.out.println("\n------------------------------------");		
		state.setAttributeValue(b1, block_has_on, b3);
		System.out.println("\n------------------------------------");
		
		state.setAttributeValue(b2, block_has_name, "B2");
		System.out.println("\n------------------------------------");
		state.setAttributeValue(b2, block_has_left, b3);
		System.out.println("\n------------------------------------");
		state.setAttributeValue(b2, block_has_color, "blue");
		System.out.println("\n------------------------------------");
		
		state.setAttributeValue(b3, block_has_name, "B3");
		System.out.println("\n------------------------------------");
		state.setAttributeValue(b3, block_has_left, b4);
		System.out.println("\n------------------------------------");
		state.setAttributeValue(b3, block_has_color, "red");
		System.out.println("\n------------------------------------");*/
	
		state.notifyEventListeners();
		System.out.println("Rete memory: "+((RetePatternMatcherState)system.getMatcherState()).getReteMemory());
	}
	
	/**
	 *  Create the blocksworld rete.
	 *  @return The rete system.
	 */
	public static RuleSystem createReteSystem()
	{	
//		(defrule test_rule
//		  ?z <- (Block (color "red"))
//		  ?y <- (Block (left ?z))
//		  ?x <- (Block (on ?y))
//		=> (printout "match"))
	
//		(defrule test_rule
//		  ?z <- (Block (color ?c & ~"red"))
//		  ?y <- (Block (left ?z))
//		  ?x <- (Block (on ?y) (material "wood") (color ~?c))
//		=>)
		
//		(defrule test_rule
//		  ?z <- (Block (color "red" | "green"))
//		  ?y <- (Block (left ?z))
//		  ?x <- (Block (on ?y))
//		=> (printout "match"))
		
		/*OldCondition c1 = new OldCondition(new Variable("x", block_type), block_has_on, new Variable("y", block_type));
		OldCondition c2 = new OldCondition(new Variable("y", block_type), block_has_left, new Variable("z", block_type));
		OldCondition c3 = new OldCondition(new Variable("z", block_type), block_has_color, "red");
		ComplexCondition c = new ComplexCondition(new ICondition[]{c1, c2, c3}, ComplexCondition.AND);*/
		
		ObjectCondition zc = new ObjectCondition(block_type);
		IConstraint cc = new OrConstraint(new LiteralConstraint(block_has_color, "red"), 
			new LiteralConstraint(block_has_color, "green"));
		zc.addConstraint(cc);
		zc.addConstraint(new BoundConstraint(null, new Variable("z", block_type)));
		
		ObjectCondition yc = new ObjectCondition(block_type);
		yc.addConstraint(new BoundConstraint(block_has_left, new Variable("z", block_type), IOperator.CONTAINS));
		yc.addConstraint(new BoundConstraint(null, new Variable("y", block_type)));
		
		ObjectCondition xc = new ObjectCondition(block_type);
		xc.addConstraint(new BoundConstraint(block_has_on, new Variable("y", block_type), IOperator.CONTAINS));
		xc.addConstraint(new BoundConstraint(null, new Variable("x", block_type)));
		
		
		/*ObjectCondition zc = new ObjectCondition(block_type);
		zc.addConstraint(new LiteralConstraint(block_has_color, "red", IOperator.NOTEQUAL));
		zc.addConstraint(new AttributeBoundConstraint(block_has_color, new Variable("c", block_type), true));
		zc.addConstraint(new AttributeBoundConstraint(null, new Variable("z", block_type), true));
		
		ObjectCondition yc = new ObjectCondition(block_type);
		yc.addConstraint(new AttributeBoundConstraint(block_has_left, new Variable("z", block_type), false));
		yc.addConstraint(new AttributeBoundConstraint(null, new Variable("y", block_type), true));
		
		ObjectCondition xc = new ObjectCondition(block_type);
		xc.addConstraint(new AttributeBoundConstraint(block_has_on, new Variable("y", block_type), false));
		xc.addConstraint(new AttributeBoundConstraint(block_has_color, new Variable("c", block_type), false, IOperator.NOTEQUAL));
		*/
		
		AndCondition cond = new AndCondition(new ICondition[]{zc, yc, xc});
		
		System.out.println(cond);
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				System.out.println("TRIGGERED: x="+assigments.getVariableValue("x")
					+", y="+assigments.getVariableValue("y")
					+", z="+assigments.getVariableValue("z"));
			}
		};
		
		IRule rule = new Rule("block", cond, action);
		Rulebase rb = new Rulebase();
		IPatternMatcherFunctionality pf = new RetePatternMatcherFunctionality(rb);
		
		RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(blocksworld_type_model), rb,  pf);
		system.init();
//		ReteNode node = system.getReteNode();
	
		// test rule removal
		//System.out.println("The rete network with no rules has nodes: "+node.getNodeCount());
		system.getRulebase().addRule(rule);
//		System.out.println("The rete network with one rule has nodes: "+node.getNodeCount());
//		system.getRulebase().removeRule(rule);
//		System.out.println("The rete network with no rule has nodes: "+node.getNodeCount());
		
		return system;
	}
}
