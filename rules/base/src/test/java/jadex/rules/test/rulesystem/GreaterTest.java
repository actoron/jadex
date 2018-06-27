package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
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
 *  Test a constraint with a non-commutative operator (i.e. '>').
 */
public class GreaterTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The list of triggered blocks. */
	protected List	blocks;
	
	/** Small lightweight block. */
	protected Object	smalllight;
	
	/** Small heavy block. */
	protected Object	smallheavy;
	
	/** Big lightweight block. */
	protected Object	biglight;
	
	/** Big heavy block. */
	protected Object	bigheavy;
	
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		state	= OAVStateFactory.createOAVState(Blocks.blocksworld_type_model);
		blocks	= new ArrayList();
		
		// ?block <- (Block (size ?size) (weight > ?size) )
		
		// Matches a block that weights more than its size.
		ObjectCondition	cblock	= new ObjectCondition(Blocks.block_type);
		cblock.addConstraint(new BoundConstraint(null, new Variable("block", Blocks.block_type)));
		cblock.addConstraint(new BoundConstraint(Blocks.block_has_size, new Variable("size", OAVJavaType.java_integer_type)));
		cblock.addConstraint(new BoundConstraint(Blocks.block_has_weight, new Variable("size", OAVJavaType.java_integer_type), IOperator.GREATER));

		// Add block of triggered condition to list.
		IRule rule = new Rule("block", cblock, new IAction()
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
		
		// Add blocks with different weights and sizes.
		smalllight = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(smalllight, Blocks.block_has_size, Integer.valueOf(1));
		state.setAttributeValue(smalllight, Blocks.block_has_weight, Integer.valueOf(1));
		
		smallheavy = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(smallheavy, Blocks.block_has_size, Integer.valueOf(1));
		state.setAttributeValue(smallheavy, Blocks.block_has_weight, Integer.valueOf(5));
		
		biglight = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(biglight, Blocks.block_has_size, Integer.valueOf(5));
		state.setAttributeValue(biglight, Blocks.block_has_weight, Integer.valueOf(1));
		
		bigheavy = state.createRootObject(Blocks.block_type);
		state.setAttributeValue(bigheavy, Blocks.block_has_size, Integer.valueOf(5));
		state.setAttributeValue(bigheavy, Blocks.block_has_weight, Integer.valueOf(5));
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that the condition triggers initially. 
	 */
	public void testInitialTrigger()
	{
		system.fireAllRules();
		List	test	= Collections.singletonList(smallheavy);
		assertEquals("Small heavy block should trigger initially", test, blocks);
	}
}
