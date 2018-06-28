package jadex.rules.test.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.rules.state.IOAVState;
import jadex.rules.state.javaimpl.OAVStateFactory;
import junit.framework.TestCase;

/**
 *  Test creating and dropping objects.
 */
public class CreateDropTest extends TestCase
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
	 *  Test if creating root objects works.
	 */
	public void testCreateRootObject()
	{
		int cnt = 1000;
		for(int i=0; i<cnt; i++)
			state.createRootObject(TestWorld.test_type);
		
		assertEquals(state.getSize(), cnt);
	
		Collection orphans = state.getUnreferencedObjects();
		
		assertEquals(orphans.size(), 0);
	}
	
	/**
	 *  Test if creating non-root objects works.
	 */
	public void testCreateObject()
	{
		int cnt = 1000;
		for(int i=0; i<cnt; i++)
			state.createObject(TestWorld.test_type);
		
		assertEquals(state.getSize(), cnt);
		
		Collection orphans = state.getUnreferencedObjects();
		
		assertEquals(orphans.size(), cnt);
	}
	
	/**
	 *  Test if drop object works for non-root objects.
	 */
	public void testDropRootObject()
	{
		List lids = new ArrayList();
		int cnt = 1000;
		for(int i=0; i<cnt; i++)
			lids.add(state.createRootObject(TestWorld.test_type));
		
		for(int i=0; i<cnt; i++)
			state.dropObject(lids.get(i));
		
		assertEquals(state.getSize(), 0);
	}
	
	/**
	 *  Test if drop object works for root objects.
	 */
	public void testDropObject()
	{
		List lids = new ArrayList();
		int cnt = 1000;
		for(int i=0; i<cnt; i++)
			lids.add(state.createObject(TestWorld.test_type));
		
		for(int i=0; i<cnt; i++)
			state.dropObject(lids.get(i));
		
		assertEquals(state.getSize(), 0);
	}
	
}
