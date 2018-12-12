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
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test equal join pattern matching (i.e. block.color==ball.color).
 */
public class EqualJoinTest extends TestCase
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
		
		// ?block <- (Block (color ?color))
		// (Ball (color ?color))
		
		// Matches a block with a color
		ObjectCondition	cblock	= new ObjectCondition(Blocks.block_type);
		cblock.addConstraint(new BoundConstraint(null, new Variable("block", Blocks.block_type)));
		cblock.addConstraint(new BoundConstraint(Blocks.block_has_color, new Variable("color", OAVJavaType.java_string_type)));

		// Matches a ball with the same color
		ObjectCondition	samecolor	= new ObjectCondition(Blocks.ball_type);
		samecolor.addConstraint(new BoundConstraint(Blocks.ball_has_color, new Variable("color", OAVJavaType.java_string_type)));

		// Add block of triggered condition to list.
		ICondition	cond	= new AndCondition(new ICondition[]{cblock, samecolor});
		IRule rule = new Rule("block", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				blocks.add(assigments.getVariableValue("block"));
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		system	= new RuleSystem(state,rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
		/*RetePanel.createReteFrame("Equal Join Test",
			((RetePatternMatcherFunctionality)system.getMatcherFunctionality()).getReteNode(),
			((RetePatternMatcherState)system.getMatcherState()).getReteMemory(),
			system.getAgenda(), new Object());
		synchronized(system){system.wait();}*/

		// Add red block and green ball.
		block = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(block, Blocks.block_has_color, "red");
		ball = state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(ball, Blocks.ball_has_color, "green");
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that no condition triggers initially. 
	 */
	public void testNoInitialTrigger()
	{
//		ReteMemory rm = ((RetePatternMatcher)system.getMatcher()).getReteMemory();
//		System.out.println(rm);
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("No condition should trigger initially", test, blocks);
	}
		
	/**
	 *  Test left addition, which triggers condition. 
	 */
	public void testLeftAddTrigger()
	{
//		ReteMemory rm = ((RetePatternMatcher)system.getMatcher()).getReteMemory();
//		System.out.println(rm);	

		// Create new ball without color.
		state.createRootObject(Blocks.ball_type);
		system.fireAllRules();
		assertEquals("Condition should not trigger for new ball", Collections.EMPTY_LIST, blocks);

		// Create new block without color -> should trigger condition.
		Object	newblock	= state.createRootObject(Blocks.block_type);
		List	test	= Collections.singletonList(newblock);
		
//		System.out.println(rm);	
		
		system.fireAllRules();
		assertEquals("Condition should trigger for new block", test, blocks);
	}
		
	/**
	 *  Test right addition, which triggers condition. 
	 */
	public void testRightAddTrigger()
	{
		// Create new block without color.
		Object	newblock	= state.createRootObject(Blocks.block_type);

		// Create new ball without color -> should trigger condition.
		state.createRootObject(Blocks.ball_type);
		List	test	= Collections.singletonList(newblock);
		system.fireAllRules();
		assertEquals("Condition should trigger for new block", test, blocks);
	}
	
	/**
	 *  Test left addition, which does not trigger condition. 
	 */
	public void testLeftAddNoTrigger()
	{
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
		// Create new ball without color -> should not trigger condition.
		state.createRootObject(Blocks.ball_type);
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for new block", test, blocks);
	}

	/**
	 *  Test left removal, which does not trigger condition. 
	 */
	public void testLeftRemoveNoTrigger()
	{
		// Change color of ball -> should activate condition.
		state.setAttributeValue(ball, Blocks.ball_has_color, "red");
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
		// Change color of block -> should activate condition.
		state.setAttributeValue(block, Blocks.block_has_color, "green");
		state.notifyEventListeners();

		// Remove ball -> should retract activated condition.
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
		// Create new red ball -> should activate condition.
		Object	newball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(newball, Blocks.ball_has_color, "red");
		state.notifyEventListeners();

		// Change color of block -> should retract activated condition.
		state.setAttributeValue(block, Blocks.block_has_color, "blue");
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
		state.setAttributeValue(ball, Blocks.ball_has_color, "red");
		List	test	= Collections.singletonList(block);
		system.fireAllRules();
		assertEquals("Condition should trigger for changed ball", test, blocks);
	}

	
	/**
	 *  Test right modification, which does not trigger condition. 
	 */
	public void testRightModifyNoTrigger()
	{
		// Create new green block -> should activate condition.
		Object	newblock	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(newblock, Blocks.block_has_color, "green");
		state.notifyEventListeners();

		// Change color of ball -> should retract activated condition.
		state.setAttributeValue(ball, Blocks.ball_has_color, "blue");
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("Condition should not trigger for changed ball", test, blocks);
	}
}
