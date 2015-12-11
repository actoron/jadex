package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test operation of Rete not node, when used in join.
 */
public class NotNodeJoinTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The list of triggered blocks. */
	protected List	blocks;
	
	/** Red block. */
	protected Object	block;
	
	/** Red ball. */
	protected Object	ball;
	
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		state	= OAVStateFactory.createOAVState(Blocks.blocksworld_type_model);
		blocks	= new ArrayList();
		
		// Matches a block with a color: ?block <- (Block (color ?color))
		ObjectCondition	cblock	= new ObjectCondition(Blocks.block_type);
		cblock.addConstraint(new BoundConstraint(null, new Variable("block", Blocks.block_type)));
		cblock.addConstraint(new BoundConstraint(Blocks.block_has_color, new Variable("color", OAVJavaType.java_string_type)));

		// Matches if no ball with the same color exists: (not (Ball (color ?color)))
		ObjectCondition	samecolor	= new ObjectCondition(Blocks.ball_type);
		samecolor.addConstraint(new BoundConstraint(Blocks.ball_has_color, new Variable("color", OAVJavaType.java_string_type)));
		NotCondition	notsamecolor	= new NotCondition(samecolor);

		// Add block of triggered condition to list.
		ICondition	cond	= new AndCondition(new ICondition[]{cblock, notsamecolor});
		IRule	rule	= new Rule("block", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				blocks.add(assigments.getVariableValue("block"));
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();

//		RetePatternMatcherState rs = ((RetePatternMatcherState)system.getMatcherState());
//		RetePanel.createReteFrame("Not Node Test", rs.getReteNode(), rs.getReteMemory(), new Object());
	
//		synchronized(system){system.wait();}
		
		// Add red block and red ball.
		block	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(block, Blocks.block_has_color, "red");
		ball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(ball, Blocks.ball_has_color, "red");
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that no condition triggers initially. 
	 */
	public void testNoInitialTrigger()
	{
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("No condition should trigger initially", test, blocks);
	}
		
	/**
	 *  Test left addition, which triggers condition. 
	 */
	public void testLeftAddTrigger()
	{
		// Create new block without color -> should trigger condition.
		Object	newblock	= state.createRootObject(Blocks.block_type);
		List	test	= Collections.singletonList(newblock);
		system.fireAllRules();
		
		assertEquals("Condition should trigger for new block", test, blocks);
	}

	/**
	 *  Test left addition, which does not trigger condition. 
	 */
	public void testLeftAddNoTrigger()
	{
		// Create new ball without color.
		state.createRootObject(Blocks.ball_type);

		// Create new block without color -> should not trigger condition.
		state.createRootObject(Blocks.block_type);
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for new block", test, blocks);
	}

	/**
	 *  Test right addition, which does not trigger condition. 
	 */
	public void testRightAddNoTrigger()
	{
		// Create new block without color -> should activate condition.
		state.createRootObject(Blocks.block_type);
		state.notifyEventListeners();

		// Create new ball without color -> should retract activated condition.
		state.createRootObject(Blocks.ball_type);
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for new block", test, blocks);
	}

	/**
	 *  Test right removal, which triggers condition. 
	 */
	public void testRightRemoveTrigger()
	{
		// Remove red ball -> should trigger condition.
		state.dropObject(ball);
		List	test	= Collections.singletonList(block);
		system.fireAllRules();
		assertEquals("Condition should trigger for removed ball", test, blocks);
	}

	/**
	 *  Test left removal, which does not trigger condition. 
	 */
	public void testLeftRemoveNoTrigger()
	{
		// Remove red ball -> should activate condition.
		state.dropObject(ball);
		state.notifyEventListeners();

		// Remove red block -> should retract activated condition.
		state.dropObject(block);
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for removed block", test, blocks);
	}

	/**
	 *  Test right removal, which does not trigger condition. 
	 */
	public void testRightRemoveNoTrigger()
	{
		// Create 2nd red ball
		Object	newball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(newball, Blocks.ball_has_color, "red");
		state.notifyEventListeners();

		// Remove 1st red ball -> should not trigger condition.
		state.dropObject(ball);
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for removed ball", test, blocks);
	}
	
	/**
	 *  Test left modification, which triggers condition. 
	 */
	public void testLeftModifyTrigger()
	{
		// Change color of block -> should trigger condition.
		state.setAttributeValue(block, Blocks.block_has_color, "green");
		List	test	= Collections.singletonList(block);
		system.fireAllRules();
		assertEquals("Condition should trigger for changed block", test, blocks);
	}
	
	
	/**
	 *  Test left modification, which does not trigger condition. 
	 */
	public void testLeftModifyNoTrigger()
	{
		// Create green ball.
		Object	newball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(newball, Blocks.ball_has_color, "green");
		state.notifyEventListeners();
		
		// Change color of block -> should not trigger condition.
		state.setAttributeValue(block, Blocks.block_has_color, "green");
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for changed block", test, blocks);
	}
	
	/**
	 *  Test right modification, which triggers condition. 
	 */
	public void testRightModifyTrigger()
	{
		// Change color of ball -> should trigger condition.
		state.setAttributeValue(ball, Blocks.ball_has_color, "green");
		List	test	= Collections.singletonList(block);
		system.fireAllRules();
		assertEquals("Condition should trigger for changed ball", test, blocks);
	}

	
	/**
	 *  Test right modification, which does not trigger condition. 
	 */
	public void testRightModifyNoTrigger()
	{
		// Create 2nd red ball
		Object	newball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(newball, Blocks.ball_has_color, "red");
		state.notifyEventListeners();

		// Change color of ball -> should not trigger condition.
		state.setAttributeValue(ball, Blocks.ball_has_color, "green");
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for changed ball", test, blocks);
	}
}
