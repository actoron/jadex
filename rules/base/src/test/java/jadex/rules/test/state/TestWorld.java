package jadex.rules.test.state;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  A test model.
 */
public class TestWorld
{
	//-------- OAV type definitions --------
	
	/** The testworld type model. */
	public static final OAVTypeModel testworld_type_model;
	
	/** The test type. */
	public static final OAVObjectType test_type;
	
	/** A test has a name. */
	public static final OAVAttributeType test_has_name;
	
	/** A test has others tests. */
	public static final OAVAttributeType test_has_testslist;
	
	/** A test has others tests. */
	public static final OAVAttributeType test_has_testsset;
	
	/** A test has others tests. */
	public static final OAVAttributeType test_has_testsqueue;
	
	/** A test has others tests. */
	public static final OAVAttributeType test_has_testsmap;
	
	/** The other type. */
	public static final OAVObjectType other_type;
	
	static
	{
		testworld_type_model = new OAVTypeModel("testworld_type_model");
		testworld_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		// test type
		test_type = testworld_type_model.createType("test");
		test_has_name = test_type.createAttributeType("test_has_name", OAVJavaType.java_string_type);
		test_has_testslist = test_type.createAttributeType("test_has_testslist", test_type, OAVAttributeType.LIST);
		test_has_testsset = test_type.createAttributeType("test_has_testsset", test_type, OAVAttributeType.SET);
		test_has_testsqueue = test_type.createAttributeType("test_has_testsqueue", test_type, OAVAttributeType.QUEUE);
		test_has_testsmap = test_type.createAttributeType("test_has_testsmap", test_type, OAVAttributeType.MAP);
	
		// other type
		other_type = testworld_type_model.createType("other");
	}
	
}
