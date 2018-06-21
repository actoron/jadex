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
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.MethodCall;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test method calls in pattern matching.
 */
public class MethodCallTest2 extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem system;
	
	/** The test bean. */
	protected TestBean bean1;
	
	/** The list of triggered results. */
	protected List	triggered;
	
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{		
		state	= OAVStateFactory.createOAVState(OAVJavaType.java_type_model);
		triggered	= new ArrayList();
		
		// test for two beans with same name:
		// (TestBean (toString () ?string) (toString () startswith "A"))

		OAVJavaType	clazz	= OAVJavaType.java_type_model.getJavaType(TestBean.class);
		MethodCall	method	= new MethodCall(clazz, TestBean.class.getMethod("toString", new Class[0]));
		ObjectCondition	cobject	= new ObjectCondition(clazz);
		Variable string = new Variable("?string", OAVJavaType.java_string_type);
		cobject.addConstraint(new BoundConstraint(method, string));
		cobject.addConstraint(new LiteralConstraint(method, "A", IOperator.STARTSWITH));
		
		// Add string of triggered bean to list.
		IRule	rule	= new Rule("collect_rule", cobject, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object	string	= assigments.getVariableValue("?string");
				triggered.add(string);
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		system	= new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb));
		system.init();
		
//			state.notifyEventListeners();
//			RetePanel.createReteFrame("Collect Node Test", system, new Object());
//			synchronized(system){system.wait();}
		
		bean1	= new TestBean("A");
		state.addJavaRootObject(bean1);
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that condition triggers initially. 
	 */
	public void testInitialNoTrigger()
	{
//			RetePanel.createReteFrame("Collect Node Test", system, new Object());

		system.fireAllRules();
		List	test	= Collections.singletonList("A");
		assertEquals("Condition should trigger initially", test, triggered);
	}

	/**
	 *  Test that condition triggers when adding. 
	 */
	public void testAddTrigger()
	{
		state.addJavaRootObject(new TestBean("A2"));
		system.fireAllRules();
		List test = new ArrayList();
		test.add("A");
		test.add("A2");
		assertEquals("Condition should trigger on addition", test, triggered);
	}
	
	/**
	 *  Test that condition triggers when adding. 
	 */
	public void testAddNoTrigger()
	{
		state.addJavaRootObject(new TestBean("B"));
		system.fireAllRules();
		List test = new ArrayList();
		test.add("A");
		assertEquals("Condition should trigger on addition", test, triggered);
	}


	/**
	 *  Test that condition triggers on bean change. 
	 */
	// Todo: do we want this semantics?
	// Jess/Drools have this, but just because they do remove/add instead of modify.
	public void testBeanchangeTrigger()
	{
		// Remove initial match
		system.fireAllRules();
		triggered.clear();
		
		bean1.setName("A2");
		system.fireAllRules();
		List	test	= Collections.singletonList("A2");
		assertEquals("Condition should trigger on bean change", test, triggered);
	}
	
	//-------- helper methods --------
	
	/**
	 *  Main for Lars.
	 */
	public static void main(String[] args) throws Exception
	{
		MethodCallTest2 test = new MethodCallTest2();
		test.setUp();
		test.testAddTrigger();
	}
}