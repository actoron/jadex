package jadex.rules.test.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test multi attributes.
 */
public class MultiAttributeTest extends TestCase
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	//-------- methods --------
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{
		// todo: make state impl customizable
		
		this.state = OAVStateFactory.createOAVState(TestWorld.testworld_type_model);
	}
	
	/**
	 *  Test if adding values to list attribute works.
	 */
	public void testAddValuesToListAttribute()
	{
		List tests = new ArrayList();
		for(int i=0; i<1000; i++)
			tests.add(state.createRootObject(TestWorld.test_type));
		
		for(int i=0; i<1000; i++)
			state.addAttributeValue(tests.get(0), TestWorld.test_has_testslist, tests.get(i));
		
		Collection coll = state.getAttributeValues(tests.get(0), TestWorld.test_has_testslist);
		assertTrue(coll.equals(tests));
	}
	
	/**
	 *  Test if adding identical values to list attribute works.
	 */
	public void testAddSameValuesToListAttribute()
	{
		Object test = state.createRootObject(TestWorld.test_type);
		
		for(int i=0; i<1000; i++)
			state.addAttributeValue(test, TestWorld.test_has_testslist, test);
		
		Collection coll = state.getAttributeValues(test, TestWorld.test_has_testslist);
		assertEquals(coll.size(), 1000);
	}
	
	/**
	 *  Test if removing values from list attribute works.
	 */
	public void testRemoveValuesFromListAttribute()
	{
		List tests = new ArrayList();
		for(int i=0; i<1000; i++)
			tests.add(state.createRootObject(TestWorld.test_type));
		
		for(int i=0; i<1000; i++)
			state.addAttributeValue(tests.get(0), TestWorld.test_has_testslist, tests.get(i));
		
		for(int i=0; i<1000; i++)
			state.removeAttributeValue(tests.get(0), TestWorld.test_has_testslist, tests.get(i));
	
		Collection coll = state.getAttributeValues(tests.get(0), TestWorld.test_has_testslist);
		// Do we need a clear contract preferring null or size==0 allowing only only option?
		assertTrue(coll==null || coll.size()==0);
	}
	
	
	/**
	 *  Test if adding values to set attribute works.
	 */
	public void testAddValuesToSetAttribute()
	{
		List tests = new ArrayList();
		for(int i=0; i<1000; i++)
			tests.add(state.createRootObject(TestWorld.test_type));
		
		for(int i=0; i<1000; i++)
			state.addAttributeValue(tests.get(0), TestWorld.test_has_testsset, tests.get(i));
		
		Collection coll = state.getAttributeValues(tests.get(0), TestWorld.test_has_testsset);
		assertTrue(coll.containsAll(tests));
	}
	
	/**
	 *  Test if adding identical values to set attribute works.
	 */
	public void testAddSameValuesToSetAttribute()
	{
		Object test = state.createRootObject(TestWorld.test_type);
		
		state.addAttributeValue(test, TestWorld.test_has_testsset, test);
		try
		{
			state.addAttributeValue(test, TestWorld.test_has_testsset, test);
			assertTrue(false);
		}
		catch(Exception e)
		{
			assertTrue(true);
		}
	}
	
	/**
	 *  Test if removing values from set attribute works.
	 */
	public void testRemoveValuesFromSetAttribute()
	{
		List tests = new ArrayList();
		for(int i=0; i<1000; i++)
			tests.add(state.createRootObject(TestWorld.test_type));
		
		for(int i=0; i<1000; i++)
			state.addAttributeValue(tests.get(0), TestWorld.test_has_testsset, tests.get(i));
		
		for(int i=0; i<1000; i++)
			state.removeAttributeValue(tests.get(0), TestWorld.test_has_testsset, tests.get(i));
	
		Collection coll = state.getAttributeValues(tests.get(0), TestWorld.test_has_testsset);
		// Do we need a clear contract preferring null or size==0 allowing only only option?
		assertTrue(coll==null || coll.size()==0);
	}
}