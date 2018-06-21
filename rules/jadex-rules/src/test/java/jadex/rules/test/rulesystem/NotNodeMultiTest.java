package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test operation of Rete chained not nodes,
 *  trying to reproduce known bug.
 */
public class NotNodeMultiTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The list of triggered blocks. */
	protected List	blocks;
	
	/** Red ball. */
	protected Object	red;
	
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		state	= OAVStateFactory.createOAVState(Blocks.blocksworld_type_model);
		blocks	= new ArrayList();
		
		String	c	= "?red <- (ball (ball_has_color \"red\"))\n"
			+ "(not\n"
				+ "(and\n"
					+ "(ball (ball_has_color \"yellow\"))\n"
					+ "(not\n"
						+ "(and\n"
							+ "(ball (ball_has_color \"green\"))\n"
							+ "(ball (ball_has_color \"blue\"))\n"
						+ ")"
					+ ")"
				+ ")"
			+ ")";
		ICondition	cond	= ParserHelper.parseClipsCondition(c, Blocks.blocksworld_type_model);
		IRule	rule	= new Rule("blocks", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				blocks.add(assigments.getVariableValue("?red"));
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
		
		// Add yellow, green and red ball.
		Object	yellow	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(yellow, Blocks.ball_has_color, "yellow");
		Object	green	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(green, Blocks.ball_has_color, "green");
		this.red	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(red, Blocks.ball_has_color, "red");
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that no condition triggers initially. 
	 */
	public void testTrigger()
	{
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("No condition should trigger initially.", test, blocks);
		
		Object	blue	= state.createRootObject(Blocks.ball_type);
		state.setAttributeValue(blue, Blocks.ball_has_color, "blue");		
		system.fireAllRules();
		test	= Collections.singletonList(red);
		assertEquals("Red block should have triggered.", test, blocks);
	}
}
