package jadex.rules.test.rulesystem;

import java.util.List;

import jadex.rules.rulesystem.RuleSystem;
import junit.framework.TestCase;

/**
 *  Test if conditions on Java objects are working.
 */
public class JavaMultifieldTest extends TestCase
{
	/**
	 *  Main for testing. 
	 * /
	public static void main(String[] args) throws Exception
	{
		JavaMultifieldTest	test	= new JavaMultifieldTest();
		test.setUp();
		test.testSplit();
	}*/
	
	//-------- attributes --------
	
	/** The list of triggered blocks. */
	protected List triggers;	

	/** The rule system. */
	protected RuleSystem	system;

	//-------- test setup --------
	
	/**
	 *  Test setup.
	 * /
	protected void setUp() throws Exception
	{
		String c	= "?table = (jadex.rules.test.Table (blocks $?b1 ?b2 $?b3))";
		ANTLRStringStream exp = new ANTLRStringStream(c);
		ClipsJadexLexer lexer = new ClipsJadexLexer(exp);			
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ClipsJadexParser parser = new ClipsJadexParser(tokens);
		ICondition cond = parser.rhs(Blocks.blocksworld_type_model);
		System.out.println(cond);
		
		this.triggers = new ArrayList();
		
		IAction action = new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object res = assigments.getVariableValue("?table");
				System.out.println("TRIGGERED: table="+res);
				triggers.add(res);
			}
		};
		
		Rule rule = new Rule("table_blocks", cond, action);
		Rulebase rb = new Rulebase();
		RetePatternMatcherFunctionality pm = new RetePatternMatcherFunctionality(rb);
		this.system = new RuleSystem(OAVStateFactory.createOAVState(Blocks.blocksworld_type_model), rb, pm);
		
		system.getRulebase().addRule(rule);
		system.init();
	}*/
	
	//-------- test methods --------
	
	// todo...
	/**
	 *  Test split.
	 */
	public void testSplit()
	{/*
		IOAVState state = system.getState();
		Object bc = state.createRootObject(Blocks.blockcontainer_type);
		Table table = new Table();
		
		Block b1 = new Block(Color.red, table);
//		Block b2 = new Block(Color.red, table);
//		Block b3 = new Block(Color.red, table);
//		Block b4 = new Block(Color.red, table);
//		Block b5 = new Block(Color.red, table);
	
		state.addAttributeValue(bc, Blocks.blockcontainer_has_blocks, table);
		
		system.fireAllRules();
		assertEquals(1, triggers.size());
		assertEquals(b1, triggers.get(0));*/
	}
}
