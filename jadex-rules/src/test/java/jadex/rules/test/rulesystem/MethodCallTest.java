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
public class MethodCallTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The frist (left) test bean. */
	protected TestBean bean1;
	
	/** The second (right) test bean. */
	protected TestBean2 bean2;
	
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
		// (TestBean (toString () ?string))
		// (TestBean2 (toString () ?string))

		OAVJavaType	clazz	= OAVJavaType.java_type_model.getJavaType(TestBean.class);
		MethodCall	method	= new MethodCall(clazz, TestBean.class.getMethod("toString", new Class[0]));
		ObjectCondition	cobject	= new ObjectCondition(clazz);
		cobject.addConstraint(new BoundConstraint(method, new Variable("?string", OAVJavaType.java_string_type)));
		
		OAVJavaType	clazz2	= OAVJavaType.java_type_model.getJavaType(TestBean2.class);
		MethodCall	method2	= new MethodCall(clazz2, TestBean2.class.getMethod("toString", new Class[0]));
		ObjectCondition	cobject2	= new ObjectCondition(clazz2);
		cobject2.addConstraint(new BoundConstraint(method2, new Variable("?string", OAVJavaType.java_string_type)));

		// Add string of triggered bean to list.
		IRule	rule	= new Rule("collect_rule", new AndCondition(new ICondition[]{cobject, cobject2}), new IAction()
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
		
//		state.notifyEventListeners();
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());
//		synchronized(system){system.wait();}
		
		bean1	= new TestBean("A");
		state.addJavaRootObject(bean1);
		bean2	= new TestBean2("B");
		state.addJavaRootObject(bean2);
	}
	
	//-------- test methods --------
	
	/**
	 *  Test that condition does not trigger initially. 
	 */
	public void testInitialNoTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger initially", test, triggered);
	}

	/**
	 *  Test that condition triggers when adding left. 
	 */
	public void testLeftAddTrigger()
	{
		state.addJavaRootObject(new TestBean("B"));
		system.fireAllRules();
		List	test	= Collections.singletonList("B");
		assertEquals("Condition should trigger on left addition", test, triggered);
	}

	/**
	 *  Test that condition triggers when adding right. 
	 */
	public void testRightAddTrigger()
	{
		state.addJavaRootObject(new TestBean2("A"));
		system.fireAllRules();
		List	test	= Collections.singletonList("A");
		assertEquals("Condition should trigger on right addition", test, triggered);
	}
	
	/**
	 *  Test that condition does not trigger when adding left. 
	 */
	public void testLeftAddNoTrigger()
	{
		state.addJavaRootObject(new TestBean("C"));
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger on left addition", test, triggered);
	}

	/**
	 *  Test that condition does not trigger when adding right. 
	 */
	public void testRightAddNoTrigger()
	{
		state.addJavaRootObject(new TestBean2("C"));
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger on right addition", test, triggered);
	}
	
	/**
	 *  Test that condition triggers when modifying left. 
	 */
	public void testLeftModifyTrigger()
	{
		bean1.setName("B");
		system.fireAllRules();
		List	test	= Collections.singletonList("B");
		assertEquals("Condition should trigger on left modify", test, triggered);
	}

	/**
	 *  Test that condition triggers when modifying right. 
	 */
	public void testRightModifyTrigger()
	{
		bean2.setName("A");
		system.fireAllRules();
		List	test	= Collections.singletonList("A");
		assertEquals("Condition should trigger on right modify", test, triggered);
	}

	/**
	 *  Test that condition does not trigger when modifying left. 
	 */
	public void testLeftModifyNoTrigger()
	{
		bean1.setName("C");
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger on left modify", test, triggered);
	}

	/**
	 *  Test that condition does not trigger when modifying right. 
	 */
	public void testRightModifyNoTrigger()
	{
		bean2.setName("C");
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger on right modify", test, triggered);
	}
		
	/**
	 *  Test that condition does not trigger after removing left. 
	 */
	public void testLeftRemoveNoTrigger()
	{
		// Modify bean to cause activation.
		bean1.setName("B");
		
		state.removeJavaRootObject(bean1);
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger after left remove", test, triggered);
	}

		
	/**
	 *  Test that condition does not trigger after removing right. 
	 */
	public void testRightRemoveNoTrigger()
	{
		// Modify bean to cause activation.
		bean2.setName("A");
		
		state.removeJavaRootObject(bean2);
		system.fireAllRules();
		List	test	= Collections.EMPTY_LIST;
		assertEquals("Condition should not trigger after right remove", test, triggered);
	}

	//-------- helper methods --------
	
	/**
	 *  Main for Lars.
	 */
	public static void main(String[] args) throws Exception
	{
		MethodCallTest test = new MethodCallTest();
		test.setUp();
		test.testInitialNoTrigger();
	}
}
