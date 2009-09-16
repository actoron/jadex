package eis.jadex;

import java.util.HashSet;
import java.util.Set;


import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.QName;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;

/**
 * 
 */
public class MEisSpaceType extends MSpaceType
{
	//-------- attributes --------
	
	/** The concrete eis environment clazz. */
	protected Class eisclazz;
	
	//-------- methods --------
	
	/**
	 *  Get the eis class.
	 *  @retur The eis class.
	 */
	public Class getEisClazz()
	{
		return eisclazz;
	}

	/**
	 *  Set the eis class.
	 *  @param eisclazz The eis class.
	 */
	public void setEisClazz(Class eisclazz)
	{
		this.eisclazz = eisclazz;
	}
	
	//-------- static part --------
	
	/**
	 *  Get the XML mapping.
	 */
	public static Set getXMLMapping()
	{
		ITypeConverter typeconv = new ClassConverter();
		Set types = new HashSet();
		String uri = "http://jadex.sourceforge.net/jadex-eisspace";
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "eisspacetype")}, MEisSpaceType.class, null, null,
			new AttributeInfo[]{new BeanAttributeInfo("class", "eisClazz", null, typeconv, null, null)}, null));	
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "grouptype")}, MGroupType.class));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "role")}, MRoleType.class));
		types.add(new TypeInfo(null, new QName[]{new QName(uri, "eisspace")}, MEisSpaceInstance.class, null, null,
			new AttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));	
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "group")}, MGroupInstance.class, null, null,
//			new AttributeInfo[]{new BeanAttributeInfo("type", "typeName")}, null));
//		types.add(new TypeInfo(null, new QName[]{new QName(uri, "position")}, MPosition.class, null, null,
//			new AttributeInfo[]{new BeanAttributeInfo("agenttype", "agentType"), 
//			new BeanAttributeInfo("role", "roleType")}, null));
		return types;
	}
	
	/**
	 *  Parse class names.
	 */
	static class ClassConverter	implements ITypeConverter
	{
		/**
		 *  Convert a string value to a type.
		 *  @param val The string value to convert.
		 */
		public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
		{
//			if(!(val instanceof String))
//				throw new RuntimeException("Source value must be string: "+val);
			
			Object ret = val;
			if(val instanceof String)
			{
				ret = SReflect.findClass0((String)val, ((MApplicationType)root).getAllImports(), classloader);
				if(ret==null)
					throw new RuntimeException("Could not parse class: "+val);
			}
			return ret;
		}
		
		/**
		 *  Test if a converter accepts a specific input type.
		 *  @param inputtype The input type.
		 *  @return True, if accepted.
		 * /
		public boolean acceptsInputType(Class inputtype)
		{
			return String.class.isAssignableFrom(inputtype);
		}*/
	}
}
