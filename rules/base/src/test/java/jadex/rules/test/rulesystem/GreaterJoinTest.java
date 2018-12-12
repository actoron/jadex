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
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test joins with a non-commutative operator (i.e. '>').
 */
public class GreaterJoinTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The list of triggered blocks. */
	protected List	blocks;
	
	/** Small block. */
	protected Object	small;
		
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		state	= OAVStateFactory.createOAVState(Blocks.blocksworld_type_model);
		blocks	= new ArrayList();
		
		// ?block <- (Block (size ?size) )
		// (Block (size > ?size) )
		
		// Matches a block if there is a larger block.
		ObjectCondition	cblock	= new ObjectCondition(Blocks.block_type);
		cblock.addConstraint(new BoundConstraint(null, new Variable("block", Blocks.block_type)));
		cblock.addConstraint(new BoundConstraint(Blocks.block_has_size, new Variable("size", OAVJavaType.java_integer_type)));

		ObjectCondition	cblock2	= new ObjectCondition(Blocks.block_type);
		cblock2.addConstraint(new BoundConstraint(Blocks.block_has_size, new Variable("size", OAVJavaType.java_integer_type), IOperator.GREATER));

		// Add block of triggered condition to list.
		IRule rule = new Rule("block", new AndCondition(new ICondition[]{cblock, cblock2}), new IAction()
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
		
		// Add a small block.
		small = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(small, Blocks.block_has_size, Integer.valueOf(1));		
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that no condition triggers initially. 
	 */
	public void testInitialNoTrigger()
	{
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("No condition should trigger initially", test, blocks);
	}
		
	/**
	 *  Test left addition, which triggers condition. 
	 * /
	public void testLeftAddTrigger()
	{
	}
		
	/**
	 *  Test right addition, which triggers condition. 
	 */
	public void testRightAddTrigger()
	{
		// Create big block -> should trigger condition for small block.
		Object	big	= state.createRootObject(Blocks.block_type);
		state.setAttributeValue(big, Blocks.block_has_size, Integer.valueOf(5));

		List	test	= Collections.singletonList(small);
		system.fireAllRules();
		assertEquals("Condition should trigger for small block", test, blocks);
	}
	
	/**
	 *  Test left addition, which does not trigger condition. 
	 * /
	public void testLeftAddNoTrigger()
	{
	}

	/**
	 *  Test right addition, which does not trigger condition. 
	 * /
	public void testRightAddNoTrigger()
	{
	}

	/**
	 *  Test left removal, which does not trigger condition. 
	 * /
	public void testLeftRemoveNoTrigger()
	{
	}

	/**
	 *  Test right removal, which does not trigger condition. 
	 * /
	public void testRightRemoveNoTrigger()
	{
	}
	
	/**
	 *  Test left modification, which triggers condition. 
	 * /
	public void testLeftModifyTrigger()
	{
	}
	
	
	/**
	 *  Test left modification, which does not trigger condition. 
	 * /
	public void testLeftModifyNoTrigger()
	{
	}
	
	/**
	 *  Test right modification, which triggers condition. 
	 * /
	public void testRightModifyTrigger()
	{
	}

	
	/**
	 *  Test right modification, which does not trigger condition. 
	 * /
	public void testRightModifyNoTrigger()
	{
	}*/
}
