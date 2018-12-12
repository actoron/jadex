package jadex.rules.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.commons.IPropertyObject;
import jadex.commons.SReflect;
import jadex.commons.beans.BeanInfo;
import jadex.commons.beans.IndexedPropertyDescriptor;
import jadex.commons.beans.IntrospectionException;
import jadex.commons.beans.Introspector;
import jadex.commons.beans.PropertyDescriptor;

/**
 *  A java type that
 */
public class OAVJavaType extends OAVObjectType
{
	//-------- constants --------
	
	/** The value kind (for immutable java objects). */
	public static final String	KIND_VALUE	= "value";
	
	/** The object kind (for normal java objects). */
	public static final String	KIND_OBJECT	= "object";
	
	/** The bean kind (for java beans supporting property changes). */
	public static final String	KIND_BEAN	= "bean";
	
	//-------- java type definitions --------
	
	/** The java type model. */
	public static final OAVTypeModel java_type_model;
	
	/** The java object type. */
	public static final OAVJavaType java_object_type;
	
	/** The java class type. */
	public static final OAVJavaType java_class_type;
	
	/** The java exception type. */
	public static final OAVJavaType java_exception_type;
	
	/** The java string type. */
	public static final OAVJavaType java_string_type;
	
	/** The java boolean type. */
	public static final OAVJavaType java_boolean_type;
	
	/** The java integer type. */
	public static final OAVJavaType java_integer_type;
	
	/** The java long type. */
	public static final OAVJavaType java_long_type;
	
	/** The java double type. */
	public static final OAVJavaType java_double_type;
	
	/** The java float type. */
	public static final OAVJavaType java_float_type;
	
	/** The java collection type. */
	public static final OAVJavaType java_collection_type;
	

	static
	{
		java_type_model = new OAVTypeModel("java_type_model");
		java_object_type = java_type_model.createJavaType(Object.class, KIND_OBJECT);
		java_class_type = java_type_model.createJavaType(Class.class, KIND_VALUE);
		java_exception_type = java_type_model.createJavaType(Exception.class, KIND_OBJECT);
		java_string_type = java_type_model.createJavaType(String.class, KIND_VALUE);
		java_boolean_type = java_type_model.createJavaType(Boolean.class, KIND_VALUE);
		java_integer_type = java_type_model.createJavaType(Integer.class, KIND_VALUE);
		java_long_type = java_type_model.createJavaType(Long.class, KIND_VALUE);
		java_double_type = java_type_model.createJavaType(Double.class, KIND_VALUE);
		java_float_type = java_type_model.createJavaType(Float.class, KIND_VALUE);
		java_collection_type = java_type_model.createJavaType(Collection.class, KIND_OBJECT);
	}

	//-------- attributes --------
	
	/** The java class. */
	protected Class clazz;
	
	/** The kind of type (value, object, bean). */
	protected String	kind;
	
	/** Attributes for bean properties. */
	protected Map	properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new OAV object type.
	 *  @param name	The name of the OAV object type.
	 *  @param model	The type model.
	 */
	public OAVJavaType(Class clazz, String kind, OAVTypeModel tmodel)
	{
		super(clazz.getName(), null, tmodel);
		this.clazz	= clazz;
		this.kind	= kind;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if this object is same type or subtype of this type.
	 *  @param object The object to test.
	 *  @return True, if object is same type or subtype.
	 */
	public boolean isSubtype(OAVObjectType type)
	{
		return type instanceof OAVJavaType
			? SReflect.isSupertype(((OAVJavaType)type).getClazz(), clazz)
			: super.isSubtype(type);
	}
	
	/**
	 *  Get the Java class of the type.
	 */
	public Class	getClazz()
	{
		return clazz;
	}
	
	/**
	 *  Get the kind (i.e. value, object, or bean) of the type.
	 */
	public String	getKind()
	{
		return kind;
	}

	/**
	 *  Get an attribute type description.
	 *  @param attribute	The name of the attribute.
	 *  @return The OAV attribute type.
	 */
	public OAVAttributeType	getAttributeType(String attribute)
	{
		OAVAttributeType	ret	=(properties!=null)
			? (OAVAttributeType)properties.get(attribute) : null;
		
		// #ifndef MIDP
		if(ret==null)
		{
			if(clazz.isArray() && "length".equals(attribute))
			{
				ret	= new OAVJavaAttributeType(this, attribute,
					getTypeModel().getJavaType(int.class),
					OAVAttributeType.NONE, null, null);
			}
			else if(IPropertyObject.class.isAssignableFrom(clazz))
			{
				ret	= new OAVJavaAttributeType(this, attribute,
					getTypeModel().getJavaType(Object.class),
					OAVAttributeType.NONE, null, null);
			}
			else
			{
				try
				{
					BeanInfo	bi	= Introspector.getBeanInfo(clazz);
					PropertyDescriptor[] pds = bi.getPropertyDescriptors();
					for(int i=0; i<pds.length && ret==null; i++)
					{
						//System.out.println("Here: "+name+" "+pds[i].getName());
						if(pds[i].getName().equals(attribute))
						{
							if(!(pds[i] instanceof IndexedPropertyDescriptor))
							{
								ret	= new OAVJavaAttributeType(this, attribute,
									getTypeModel().getJavaType(pds[i].getPropertyType()),
									OAVAttributeType.NONE, null, pds[i]);
							}
							else
							{
								// HACK? how to find out which kind of multiplicity
								ret	= new OAVJavaAttributeType(this, attribute,
									getTypeModel().getJavaType(((IndexedPropertyDescriptor)pds[i])
									.getIndexedPropertyType()), OAVAttributeType.LIST, null, pds[i]); 
							}
	
							if(properties==null)
								properties	= new HashMap();
							properties.put(attribute, ret);
						}
					}
				}
				catch(IntrospectionException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		// #endif
		
		if(ret==null)
			throw new RuntimeException("No such attribute '"+attribute+"' in type: "+clazz.getName());

		return ret;
	}

	/**
	 *  Get the declared attribute types (i.e. not those of super types).
	 */
	public Collection	getDeclaredAttributeTypes()
	{
		if(attributes==null)
		{
			attributes	= new HashMap();
			
			// #ifndef MIDP
			try
			{
				BeanInfo	bi	= Introspector.getBeanInfo(clazz);
				PropertyDescriptor[] pds = bi.getPropertyDescriptors();
				for(int i=0; i<pds.length; i++)
				{
					if(properties!=null && properties.containsKey(pds[i].getName()))
					{
						attributes.put(pds[i].getName(), properties.get(pds[i].getName()));
					}
					else
					{
//						attributes.put(pds[i].getName(),
//							new OAVJavaAttributeType(this, pds[i].getName(),
//							getTypeModel().getJavaType(pds[i].getPropertyType()),
//							OAVAttributeType.NONE, null, pds[i]));	
//						
						if(!(pds[i] instanceof IndexedPropertyDescriptor))
						{
							attributes.put(pds[i].getName(),
								new OAVJavaAttributeType(this, pds[i].getName(),
								getTypeModel().getJavaType(pds[i].getPropertyType()),
								OAVAttributeType.NONE, null, pds[i]));
						}
						else
						{
							// HACK? how to find out which kind of multiplicity
							attributes.put(pds[i].getName(),
								new OAVJavaAttributeType(this, pds[i].getName(),
								getTypeModel().getJavaType(((IndexedPropertyDescriptor)pds[i])
								.getIndexedPropertyType()), OAVAttributeType.LIST, null, pds[i])); 
						}
					}
				}
			}
			catch(IntrospectionException e)
			{
				throw new RuntimeException(e);
			}
			// #endif

			if(clazz.isArray())
			{
				attributes.put("length", new OAVJavaAttributeType(this, "length",
					getTypeModel().getJavaType(int.class),
					OAVAttributeType.NONE, null, null));
			}
		}
		return attributes.values();
	}
}
