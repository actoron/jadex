package eis.jadex;

import java.util.HashSet;
import java.util.Set;


import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceType;
import jadex.commons.SReflect;
import jadex.xmlteInfo;
import jadex.xmjadex.xml
import jadex.xml.QName;jadex.xmll.TypeInfo;
impjadex.xmln.BeanAttributeInfojadex.xml/
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
		public Object convertObject(Object val, ClassLoader classloader, Object context)
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
		
	}
}
