package jadex.rules.test.rulesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.CollectCondition;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.ExtractMulti;
import jadex.rules.rulesystem.rules.functions.Length;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.rulesystem.rules.functions.Sum;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test collection pattern matching.
 */
public class CollectNodeTest extends TestCase
{
	//-------- type model --------

	protected static final OAVTypeModel	tmodel;
	protected static final OAVObjectType	store_type;
	protected static final OAVObjectType	music_type;
	protected static final OAVAttributeType	store_has_domain;
	protected static final OAVAttributeType	store_has_cds;
	protected static final OAVAttributeType	music_has_store;
	protected static final OAVAttributeType	music_has_artist;
	protected static final OAVAttributeType	music_has_title;
	protected static final OAVAttributeType	music_has_price;
	
	static
	{
		tmodel	= new OAVTypeModel("MusicStore");
		tmodel.addTypeModel(OAVJavaType.java_type_model);
		
		store_type	= tmodel.createType("Store");
		music_type	= tmodel.createType("Music");

		store_has_domain	= store_type.createAttributeType("store_has_domain", OAVJavaType.java_string_type);
		store_has_cds = store_type.createAttributeType("store_has_cds", music_type, OAVAttributeType.LIST);
		
		music_has_store	= music_type.createAttributeType("music_has_store", store_type);
		music_has_artist	= music_type.createAttributeType("music_has_artist", OAVJavaType.java_string_type);
		music_has_title	= music_type.createAttributeType("music_has_title", OAVJavaType.java_string_type);
		music_has_price	= music_type.createAttributeType("music_has_price", OAVJavaType.java_double_type);
	}

	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	/** The store. */
	protected Object store;
	
	/** The music 1. */
	protected Object music1;
	
	/** The music 2. */
	protected Object music2;
	
	/** The list of triggered stores. */
	protected List	stores;

	/** The list of triggered music collections. */
	protected List	collections;
		
	//-------- constructors --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{		
		state	= OAVStateFactory.createOAVState(tmodel);
		stores	= new ArrayList();
		collections	= new ArrayList();
		
		// ?s	<- (Store (store_has_domain "music"))
		// $?c	<- (collect (Music (music_has_store ?s) (music_has_artist "Miles Davis"))
		//			:equal(length($?c), 3))
		
		// Alternative with contains (first object condition is used for collection):
		//
		// ?s	<- (Store (store_has_domain "music"))
		// //?u	<- (User (use_has_name "hugo"))
		// $?c	<- (collect
		//			?m	<- (Music (music_has_artist "Miles Davis"))
		//			?s	<- (Store (store_has_cds contains ?m))
		//			//?u	<- (User (user_has_recommendations contains ?m)))

		
		// Matches a store in the "music" domain.
/*		ObjectCondition	cstore	= new ObjectCondition(store_type);
		cstore.addConstraint(new AttributeBoundConstraint(null, new Variable("?store", store_type)));
		cstore.addConstraint(new LiteralConstraint(store_has_domain, "music"));

		// Matches music from "Miles Davis" in store ?s.
		ObjectCondition	cmusic	= new ObjectCondition(music_type);
		cmusic.addConstraint(new LiteralConstraint(music_has_artist, "Miles Davis"));
		cmusic.addConstraint(new AttributeBoundConstraint(music_has_store, new Variable("?store", store_type)));
		
		// Collect music belonging to the same store and check that length==3.
		CollectCondition	collect	= new CollectCondition(cmusic);
		collect.addConstraint(new AttributeBoundConstraint(null, new Variable("$?collection", music_type, true)));
		FunctionCall fc_length = new FunctionCall(new Length(), new Object[]{new Variable("$?collection", music_type, true)});
		FunctionCall fc_length3 = new FunctionCall(new OperatorFunction(IOperator.EQUAL), new Object[]{fc_length, Integer.valueOf(3)});
		collect.addConstraint(new PredicateConstraint(fc_length3));
		
		FunctionCall fc_extract = new FunctionCall(new ExtractMulti(music_has_price), new Object[]{new Variable("$?collection", music_type, true)});
		FunctionCall fc_sum = new FunctionCall(new Sum(), new Object[]{fc_extract});
		FunctionCall fc_sum30 = new FunctionCall(new OperatorFunction(IOperator.LESS), new Object[]{fc_sum, Integer.valueOf(30)});
		collect.addConstraint(new PredicateConstraint(fc_sum30));
		
		// Add block of triggered condition to list.
		ICondition	cond	= new AndCondition(new ICondition[]{cstore, collect});
		IRule	rule	= new Rule("collect_rule", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object	store	= assigments.getVariableValue("?store");
				Object	collection	= assigments.getVariableValue("$?collection");
				stores.add(store);
				collections.add(collection);
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		system	= new RuleSystem(state,rb, new RetePatternMatcherFunctionality(rb));
		system.getRulebase().addRule(rule);
		system.init();
		
		// Add store and musics.
		store	= state.createRootObject(store_type);
		state.setAttributeValue(store, store_has_domain, "music");
		
		music1	= state.createRootObject(music_type);
		state.setAttributeValue(music1, music_has_artist, "Miles Davis");
		state.setAttributeValue(music1, music_has_title, "Kind of Blue");
		state.setAttributeValue(music1, music_has_store, store);
		state.setAttributeValue(music1, music_has_price, Integer.valueOf(10));
		
		music2	= state.createRootObject(music_type);
		state.setAttributeValue(music2, music_has_artist, "Miles Davis");
		state.setAttributeValue(music2, music_has_title, "Bitches Brew");
		state.setAttributeValue(music2, music_has_store, store);
		state.setAttributeValue(music2, music_has_price, Integer.valueOf(10));
*/		
		
		// Matches music from "Miles Davis".
		ObjectCondition	cmusic	= new ObjectCondition(music_type);
		cmusic.addConstraint(new BoundConstraint(null, new Variable("?music", music_type)));
		cmusic.addConstraint(new LiteralConstraint(music_has_artist, "Miles Davis"));
		
		// Matches a store in the "music" domain.
		ObjectCondition	cstore	= new ObjectCondition(store_type);
		cstore.addConstraint(new BoundConstraint(null, new Variable("?store", store_type)));
		cstore.addConstraint(new LiteralConstraint(store_has_domain, "music"));
		cstore.addConstraint(new BoundConstraint(store_has_cds, new Variable("?music", music_type), IOperator.CONTAINS));	
		
		// Collect music belonging to the same store and check that length==3.
		CollectCondition collect	= new CollectCondition(new ObjectCondition[]{cmusic, cstore}, null);
		collect.addConstraint(new BoundConstraint(null, new Variable("$?collection", music_type, true, false)));
		FunctionCall fc_length = new FunctionCall(new Length(), new Object[]{new Variable("$?collection", music_type, true, false)});
		FunctionCall fc_length3 = new FunctionCall(new OperatorFunction(IOperator.EQUAL), new Object[]{fc_length, Integer.valueOf(3)});
		collect.addConstraint(new PredicateConstraint(fc_length3));
		
		FunctionCall fc_extract = new FunctionCall(new ExtractMulti(music_has_price), new Object[]{new Variable("$?collection", music_type, true, false)});
		FunctionCall fc_sum = new FunctionCall(new Sum(), new Object[]{fc_extract});
		FunctionCall fc_sum30 = new FunctionCall(new OperatorFunction(IOperator.LESS), new Object[]{fc_sum, Integer.valueOf(30)});
		collect.addConstraint(new PredicateConstraint(fc_sum30));
		
		// Add block of triggered condition to list.
		ICondition	cond	= collect;//new AndCondition(new ICondition[]{collect});
		IRule	rule	= new Rule("collect_rule", cond, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assigments)
			{
				Object	store	= assigments.getVariableValue("?store");
				Object	collection	= assigments.getVariableValue("$?collection");
				stores.add(store);
				collections.add(collection);
			}
		});
		
		// Create rule system.
		Rulebase rb = new Rulebase();
		rb.addRule(rule);
		system	= new RuleSystem(state,rb, new RetePatternMatcherFunctionality(rb));
		system.init();
				
		music1	= state.createRootObject(music_type);
		state.setAttributeValue(music1, music_has_artist, "Miles Davis");
		state.setAttributeValue(music1, music_has_title, "Kind of Blue");
		state.setAttributeValue(music1, music_has_store, store);
		state.setAttributeValue(music1, music_has_price, Integer.valueOf(10));
		
		music2	= state.createRootObject(music_type);
		state.setAttributeValue(music2, music_has_artist, "Miles Davis");
		state.setAttributeValue(music2, music_has_title, "Bitches Brew");
		state.setAttributeValue(music2, music_has_store, store);
		state.setAttributeValue(music2, music_has_price, Integer.valueOf(10));

		// Add store and musics.
		store	= state.createRootObject(store_type);
		state.setAttributeValue(store, store_has_domain, "music");
		state.addAttributeValue(store, store_has_cds, music1);
		state.addAttributeValue(store, store_has_cds, music2);
		
//		state.notifyEventListeners();
//		RuleEnginePanel.createRuleEngineFrame(new RuleSystemExecutor(system, false), "Collect Node Test");
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());
//		synchronized(system){system.wait();}
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
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);
	}
		
	/**
	 *  Test left addition, which triggers condition. 
	 */
	public void testLeftAddTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		// Create new music -> should trigger condition.
		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music3);

		List	teststores	= Collections.singletonList(store);
		List	testcollections	= Collections.singletonList(new HashSet(Arrays.asList(new Object[]{music1, music2, music3})));
		system.fireAllRules();
		assertEquals("Condition should trigger for store.", teststores, stores);
		assertEquals("Condition should trigger for collection of three musics.", testcollections, collections);
	}
	
	/**
	 *  Test left addition, which triggers condition. 
	 */
	public void testLeftAddNoTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		// Create new music -> should trigger condition.
		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music3);
		
		// Create another music -> should remove activation.
		Object	music4	= state.createRootObject(music_type);
		state.setAttributeValue(music4, music_has_artist, "Miles Davis");
		state.setAttributeValue(music4, music_has_title, "Angels of Pain");
		state.setAttributeValue(music4, music_has_store, store);
		state.setAttributeValue(music4, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music4);
		
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("No condition should trigger for 4 musics", test, stores);
		assertEquals("No condition should trigger for 4 musics", test, collections);
	}
	
	/**
	 *  Test left removal, which triggers condition. 
	 */
	public void testLeftRemovalTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		// Create new music -> should trigger condition.
		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music3);

		Object	music4	= state.createRootObject(music_type);
		state.setAttributeValue(music4, music_has_artist, "Miles Davis");
		state.setAttributeValue(music4, music_has_title, "Angels of Pain");
		state.setAttributeValue(music4, music_has_store, store);
		state.setAttributeValue(music4, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music4);
		
		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);
	
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());
		state.dropObject(music3);
		List	teststores	= Collections.singletonList(store);
		List	testcollections	= Collections.singletonList(new HashSet(Arrays.asList(new Object[]{music1, music2, music4})));
		system.fireAllRules();
		assertEquals("Condition should trigger for store.", teststores, stores);
		assertEquals("Condition should trigger for collection of three musics.", testcollections, collections);	
	}
	
	/**
	 *  Test left removal, which triggers no condition. 
	 */
	public void testLeftRemovalNoTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music3);

		Object	music4	= state.createRootObject(music_type);
		state.setAttributeValue(music4, music_has_artist, "Miles Davis");
		state.setAttributeValue(music4, music_has_title, "Angels of Pain");
		state.setAttributeValue(music4, music_has_store, store);
		state.setAttributeValue(music4, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music4);
		
		Object	music5	= state.createRootObject(music_type);
		state.setAttributeValue(music5, music_has_artist, "Miles Davis");
		state.setAttributeValue(music5, music_has_title, "Mr. Vain");
		state.setAttributeValue(music5, music_has_store, store);
		state.setAttributeValue(music5, music_has_price, Integer.valueOf(1));
		state.addAttributeValue(store, store_has_cds, music5);
		
		state.dropObject(music3);

		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);
	}
	
	/**
	 *  Test left modify, which triggers condition. 
	 */
	public void testLeftModifyTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		// Should not trigger because price not less than 30
		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(10));
		state.addAttributeValue(store, store_has_cds, music3);

		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);
		
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(1));
		List	teststores	= Collections.singletonList(store);
		List	testcollections	= Collections.singletonList(new HashSet(Arrays.asList(new Object[]{music1, music2, music3})));
		system.fireAllRules();
		assertEquals("Condition should trigger for store.", teststores, stores);
		assertEquals("Condition should trigger for collection of three musics.", testcollections, collections);	
	}
	
	/**
	 *  Test left modify, which not triggers condition. 
	 */
	public void testLeftModifyNoTrigger()
	{
//		RetePanel.createReteFrame("Collect Node Test", system, new Object());

		// Should not trigger because price not less than 30
		Object	music3	= state.createRootObject(music_type);
		state.setAttributeValue(music3, music_has_artist, "Miles Davis");
		state.setAttributeValue(music3, music_has_title, "Sketches of Spain");
		state.setAttributeValue(music3, music_has_store, store);
		state.setAttributeValue(music3, music_has_price, Integer.valueOf(10));
		state.addAttributeValue(store, store_has_cds, music3);

		List	test	= Collections.EMPTY_LIST;
		system.fireAllRules();
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);

		state.setAttributeValue(music3, music_has_price, Integer.valueOf(11));
		system.fireAllRules();
		assertEquals("No condition should trigger initially", test, stores);
		assertEquals("No condition should trigger initially", test, collections);

	}
	
	/**
	 * 
	 *  @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			CollectNodeTest test = new CollectNodeTest();
			test.setUp();
			test.testNoInitialTrigger();
			
			test.testLeftAddNoTrigger();
			test.testLeftModifyNoTrigger();
			test.testLeftRemovalNoTrigger();
			
			test.testLeftAddTrigger();
			test.testLeftModifyTrigger();
			test.testLeftRemovalTrigger();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
