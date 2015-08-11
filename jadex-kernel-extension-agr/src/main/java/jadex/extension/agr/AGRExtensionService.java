package jadex.extension.agr;

import java.util.HashSet;
import java.util.Set;

import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;
import jadex.xml.stax.QName;

/**
 *  Extension service for loading AGR (agent, group, role) models.
 */
//@Service
public class AGRExtensionService //implements IComponentFactoryExtensionService
{
//	/**
//	 *  Get extension. 
//	 */
//	public IFuture<Set<Object>> getExtension(String componenttype)
//	{
//		return new Future<Set<Object>>(getXMLMapping());
//	}

	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set<Object> getXMLMapping()
	{
		Set types = new HashSet();
		String uri = "http://jadex.sourceforge.net/jadex-agrspace";
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agrspacetype")}), new ObjectInfo(MAGRSpaceType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "grouptype")}), new ObjectInfo(MGroupType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "role")}), new ObjectInfo(MRoleType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agrspace")}), 
			new ObjectInfo(MAGRSpaceInstance.class), new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("type", "typeName"))})));	
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "group")}), new ObjectInfo(MGroupInstance.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("type", "typeName"))}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "position")}), new ObjectInfo(MPosition.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("componenttype", "componentType")), 
			new AttributeInfo(new AccessInfo("role", "roleType"))}, null)));
		
		return types;
	}
}
