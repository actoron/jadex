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
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.NotCondition;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test operation of Rete not node with initial-fact.
 */
public class NotNodeInitialfactTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The list is filled by the action of the condition. */
	protected List	list;
		
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
		list	= new ArrayList();
		
		// Matches, if no green block exists.
		// (not (Block (color "green")))
		ObjectCondition	greenblock	= new ObjectCondition(Blocks.block_type);
		greenblock.addConstraint(new LiteralConstraint(Blocks.block_has_color, "green"));
		ICondition	cond1	= new NotCondition(greenblock);
		IRule	rule1	= new Rule("Condition 1", cond1, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				list.add("triggered");
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		rb.addRule(rule1);
		system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();

//		RetePanel.createReteFrame("Not Node Test", ((RetePatternMatcher)system.getMatcher()).getReteNode());
//		synchronized(system){system.wait();}
		
		// Add red block and red ball.
		block	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(block, Blocks.block_has_color, "red");
		ball	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(ball, Blocks.ball_has_color, "red");
	}
	
	//-------- test methods --------

	/**
	 *  Test that not-condition triggers for initial-fact.
	 */
	public void testInitialTrigger()
	{
		List	test	= Collections.singletonList("triggered");
		system.fireAllRules();
		assertEquals("The condition should trigger initially", test, list);
	}

	/**
	 *  Test that not-condition does not trigger after inserting fact.
	 */
	public void testAddNoTrigger()
	{
		// Condition should trigger initially.
		state.notifyEventListeners();
		
		// Create green block -> deactivates condition
		Object	newblock	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(newblock, Blocks.block_has_color, "green");

		// Condition should not trigger.
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("The condition should not trigger for green block", test, list);
	}

	/**
	 *  Test that not-condition triggers after inserting/removing fact.
	 */
	public void testRemoveTrigger()
	{
		// Create green block
		Object	newblock	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(newblock, Blocks.block_has_color, "green");

		// Remove green block
		state.dropObject(newblock);

		// Condition should now trigger.
		List	test	= Collections.singletonList("triggered");
		system.fireAllRules();
		assertEquals("The condition should trigger after removal", test, list);
	}
}
