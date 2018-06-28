package jadex.rules.test.rulesystem;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  A box containing a list of numbers.
 */
public class Numberbox
{
	//-------- type definitions --------
	
	/** The numberbox type model. */
	public static final OAVTypeModel	numberbox_type_model;
	
	/** The numberbox type. */
	public static final OAVObjectType numberbox_type;
	
	/** A numberbox has numbers. */
	public static final OAVAttributeType numberbox_has_numbers;

	/** A numberbox has more numbers. */
	public static final OAVAttributeType numberbox_has_numbers2;

	/** A numberbox has a solution. */
	public static final OAVAttributeType numberbox_has_solution;

	static
	{
		numberbox_type_model	= new OAVTypeModel("numberbox_type_model");
		numberbox_type_model.addTypeModel(OAVJavaType.java_type_model);
		
		numberbox_type = numberbox_type_model.createType("numberbox");
		numberbox_has_numbers = numberbox_type.createAttributeType("numberbox_has_numbers", OAVJavaType.java_integer_type, OAVAttributeType.LIST);
		numberbox_has_numbers2 = numberbox_type.createAttributeType("numberbox_has_numbers2", OAVJavaType.java_integer_type, OAVAttributeType.LIST);
		numberbox_has_solution = numberbox_type.createAttributeType("numberbox_has_solution", OAVJavaType.java_integer_type);				
	}
}
