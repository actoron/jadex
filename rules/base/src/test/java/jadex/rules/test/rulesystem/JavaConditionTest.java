package jadex.rules.test.rulesystem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test if conditions on Java objects are working.
 */
public class JavaConditionTest extends TestCase
{
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args) throws Exception
	{
		JavaConditionTest	test	= new JavaConditionTest();
		test.setUp();
		test.testPropertyChangeNoTrigger();
	}
	
	//-------- attributes --------
	
	/** The list of triggered blocks. */
	protected List triggers;	

	/** The rule system. */
	protected RuleSystem	system;

	//-------- test setup --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		String c	= "?block = (jadex.rules.test.rulesystem.Block (clear true))";
//		ANTLRStringStream exp = new ANTLRStringStream(c);
//		ClipsJadexLexer lexer = new ClipsJadexLexer(exp);			
//		CommonTokenStream tokens = new CommonTokenStream(lexer);
//		ClipsJadexParser parser = new ClipsJadexParser(tokens);
//		ICondition cond = parser.rhs(Blocks.blocksworld_type_model);
		ICondition cond	= ParserHelper.parseClipsCondition(c, Blocks.blocksworld_type_model);//, new String[]{"jadex.rules.test.rulesystem.*"});
//		System.out.println(cond);
		
		this.triggers = new ArrayList();
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object res = assigments.getVariableValue("?block");
//				System.out.println("TRIGGERED: block="+res);
				triggers.add(res);
			}
		};
		
		Rule rule = new Rule("block_is_clear", cond, action);
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		RetePatternMatcherFunctionality pm = new RetePatternMatcherFunctionality(rb);
		this.system = new RuleSystem(OAVStateFactory.createOAVState(Blocks.blocksworld_type_model), rb, pm);
		system.init();
	}
	
	//-------- test methods --------
	
	/**
	 *  Test trigger after object addition.
	 */
	public void testAddTrigger()
	{
		IOAVState state = system.getState();
		Object bc = state.createRootObject(Blocks.blockcontainer_type);
		Block b1 = new Block(Color.red, null);
		state.addAttributeValue(bc, Blocks.blockcontainer_has_blocks, b1);
		system.fireAllRules();
		assertEquals(1, triggers.size());
		assertEquals(b1, triggers.get(0));
	}

	/**
	 *  Test deactivation of rule through property change.
	 */
	public void testPropertyChangeNoTrigger()
	{
		// Add two blocks -> should activate condition twice.
		IOAVState state = system.getState();
		Object bc = state.createRootObject(Blocks.blockcontainer_type);
		Block b1 = new Block(Color.red, null);
		state.addAttributeValue(bc, Blocks.blockcontainer_has_blocks, b1);
		Block b2 = new Block(Color.green, null);
		state.addAttributeValue(bc, Blocks.blockcontainer_has_blocks, b2);
		state.notifyEventListeners();

		// Stack one block on the other -> should deactivate one condition 
		b2.stackOn(b1);
		system.fireAllRules();
		assertEquals(1, triggers.size());
		assertEquals(b2, triggers.get(0));
	}	
}
