package jadex.extension.agr;

import jadex.commons.SReflect;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IContext;
import jadex.xml.IPostProcessor;
import jadex.xml.MappingInfo;
import jadex.xml.ObjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.XMLInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * An AGR space description.
 */
public class MAGRSpaceType	extends MSpaceType
{
	//-------- attributes --------
	
	/** The grouptypes. */
	protected List	grouptypes;

	//-------- methods --------
		
	/**
	 *  Get the group types of this space type.
	 *  @return An array of group types (if any).
	 */
	public MGroupType[] getMGroupTypes()
	{
		return grouptypes==null ? null :
			(MGroupType[])grouptypes.toArray(new MGroupType[grouptypes.size()]);
	}
	
	

	/**
	 *  Add a group type to this space type.
	 *  @param grouptype	The group type to add. 
	 */
	public void addMGroupType(MGroupType grouptype)
	{
		if(grouptypes==null)
			grouptypes	= new ArrayList();
		grouptypes.add(grouptype);
	}

	/**
	 *  Remove a group type from this space type.
	 *  @param grouptype	The group type to remove. 
	 */
	public void removeMGroupType(MGroupType grouptype)
	{
		if(grouptypes!=null)
		{
			grouptypes.remove(grouptype);
			if(grouptypes.isEmpty())
				grouptypes	= null;
		}
	}
	
	/**
	 *  Get a group type by name.
	 */
	public MGroupType getGroupType(String name)
	{
		MGroupType ret = null;
		for(int i=0; ret==null && grouptypes!=null && i<grouptypes.size(); i++)
		{
			MGroupType	gt	= (MGroupType)grouptypes.get(i);
			if(gt.getName().equals(name))
				ret = gt;
		}
		return ret;
	}

	/**
	 *  Get a string representation of this AGR space type.
	 *  @return A string representation of this AGR space type.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(name=");
		sbuf.append(getName());
		if(grouptypes!=null)
		{
			sbuf.append(", grouptypes=");
			sbuf.append(grouptypes);
		}
		sbuf.append(")");
		return sbuf.toString();
	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		Set types = new HashSet();
		String uri = "http://jadex.sourceforge.net/jadex-agrspace";
		
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agrspacetype")}), new ObjectInfo(MAGRSpaceType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "grouptype")}), new ObjectInfo(MGroupType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "role")}), new ObjectInfo(MRoleType.class)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "agrspace")}), 
			new ObjectInfo(MAGRSpaceInstance.class, new IPostProcessor() {
			public Object postProcess(IContext context, Object object)
			{
//				MSpaceInstance	si	= (MSpaceInstance)object;
				
				// todo:
				
//				MApplicationType apptype	= (MApplicationType)context.getRootObject();
//				List spacetypes = apptype.getMSpaceTypes();
//				for(int i=0; i<spacetypes.size(); i++)
//				{
//					MSpaceType st = (MSpaceType)spacetypes.get(i);
//					if(st.getName().equals(si.getTypeName()))
//					{
//						si.setType(st);
//						break;
//					}
//				}
				
				return null;
			}
			
			public int getPass()
			{
				return 1;
			}}),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("type", "typeName"))})));	
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "group")}), new ObjectInfo(MGroupInstance.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("type", "typeName"))}, null)));
		types.add(new TypeInfo(new XMLInfo(new QName[]{new QName(uri, "position")}), new ObjectInfo(MPosition.class),
			new MappingInfo(null, new AttributeInfo[]{new AttributeInfo(new AccessInfo("componenttype", "componentType")), 
			new AttributeInfo(new AccessInfo("role", "roleType"))}, null)));
		
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "agrspacetype")}, MAGRSpaceType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "grouptype")}, MGroupType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "role")}, MRoleType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "agrspace")}, MAGRSpaceInstance.class, null, null,
//			new AttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, new IPostProcessor()
//			{
//				public Object postProcess(Object context, Object object, Object root,
//						ClassLoader classloader)
//				{
//					MSpaceInstance	si	= (MSpaceInstance)object;
//					MApplicationType	apptype	= (MApplicationType)root;
//					List spacetypes = apptype.getMSpaceTypes();
//					for(int i=0; i<spacetypes.size(); i++)
//					{
//						MSpaceType st = (MSpaceType)spacetypes.get(i);
//						if(st.getName().equals(si.getTypeName()))
//						{
//							si.setType(st);
//							break;
//						}
//					}
//					return null;
//				}
//				
//				public int getPass()
//				{
//					return 1;
//				}
//			}));	
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "group")}, MGroupInstance.class, null, null,
//			new AttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "position")}, MPosition.class, null, null,
//			new AttributeInfo[]{new BeanAttributeInfo("agenttype", "agentType"), 
//			new BeanAttributeInfo("role", "roleType")}, null));
		return types;
	}
}
