package jadex.rules.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.commons.beans.PropertyChangeListener;

/**
 *  A type model contains all known types. Type models must be associated
 *  with a state which uses the type model to ensure that no objects of
 *  undefined types are defined.
 */
public class OAVTypeModel
{
	// #ifndef MIDP
	//-------- constants --------
	
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static Class[]	PCL	= new Class[]{PropertyChangeListener.class};

	// #endif

	//-------- attributes --------
	
	/** The model name. */
	protected String name;
	
	/** All object types (name -> type). */
	protected Map types;
	
	/** The contained type models. */
	protected OAVTypeModel[] tmodels;
	
	/** The class loader. */
	protected ClassLoader classloader;
	
	//-------- constructor --------
	
	/**
	 *  Create a new model.
	 *  @param name The name.
	 */
	public OAVTypeModel(String name)
	{
		this(name, OAVTypeModel.class.getClassLoader());
	}
	
	/**
	 *  Create a new model.
	 *  @param name The name.
	 */
	public OAVTypeModel(String name, ClassLoader classloader)
	{
		this(name, classloader, null);
	}
	
	/**
	 *  Create a new model.
	 *  @param name The name.
	 */
	public OAVTypeModel(String name, ClassLoader classloader, OAVTypeModel[] subtypemodels)
	{
		this.name = name;
		this.types = new HashMap();
		this.classloader = classloader;
		if(subtypemodels!=null)
		{
			for(int i=0; i<subtypemodels.length; i++)
			{
				addTypeModel(subtypemodels[i]);
			}
		}
	}
	
	//-------- type management --------
	
	/**
	 *  Create a type.
	 *  Creates a type that can be used to create objects.
	 *  @param name The type name.
	 *  @return The type.
	 */
	public OAVObjectType createType(String name)
	{
		return createType(name, null);
	}
	
	/**
	 *  Create a type.
	 *  Creates a type that can be used to create objects.
	 *  @param name The type name.
	 *  @param supertype The supertype.
	 *  @return The type.
	 */
	public OAVObjectType createType(String name, OAVObjectType supertype)
	{
		OAVObjectType type = new OAVObjectType(name, supertype, this);
		if(contains(type))
			throw new RuntimeException("Type already exists: "+name);
		types.put(name, type);
		return type;
	}
	
	/**
	 *  Create a Java type.
	 *  @param clazz The java class.
	 *  @param kind The kind of type as defined in OAVJavaType.
	 *  @return The new type.
	 */
	public OAVJavaType createJavaType(Class clazz, String kind)
	{
		OAVJavaType type = new OAVJavaType(clazz, kind, this);
		if(contains(type))
			throw new RuntimeException("Type already exists: "+clazz);
		types.put(SReflect.getClassName(clazz), type);
		return type;
	}
	
	/**
	 *  Remove an existing type.
	 *  Deletes the type from the state.
	 *  @param type The object type.
	 *  @return True, if could be removed.
	 */
	public boolean removeType(OAVObjectType type)
	{
		boolean ret = types.remove(type.getName())!=null;
		for(int i=0; !ret && tmodels!=null && i<tmodels.length; i++)
			ret = tmodels[i].removeType(type);
		return ret;
	}
	
	/**
	 *  Adds a type model to the state.
	 *  @param tmodel The type model.
	 */
	public void addTypeModel(OAVTypeModel tmodel)
	{
		for(int i=0; tmodels!=null && i<tmodels.length; i++)
		{
			if(tmodels[i].equals(tmodel))
				throw new RuntimeException("Type model already added: "+tmodel);
		}
		
		// Check consistency of type models
		for(Iterator it=types.values().iterator(); it.hasNext(); )
		{
			OAVObjectType type = (OAVObjectType)it.next();
			if(tmodel.contains(type))
				throw new RuntimeException("Type already contained: "+type);
		}
			
		if(tmodels==null)
		{
			tmodels	= new OAVTypeModel[]{tmodel};
		}
		else
		{
			OAVTypeModel[]	temp	= tmodels;
			tmodels	= new OAVTypeModel[tmodels.length+1];
			System.arraycopy(temp, 0, tmodels, 0, temp.length);
			tmodels[temp.length]	= tmodel;
		}
	}
	
	/**
	 *  Test if a type is contained.
	 *  @param type The type.
	 *  @return True, if type is part of type model.
	 */
	public boolean contains(OAVObjectType type)
	{
		boolean ret = types.values().contains(type);
		for(int i=0; !ret && tmodels!=null && i<tmodels.length; i++)
			ret = tmodels[i].contains(type);
		return ret;
	}
	
	/**
	 *  Get a type per name.
	 *  Creates implicit java types on the fly.
	 *  TODO: Unify with getObjectType(String typename)
	 *  
	 *  @param typename The type name.
	 *  @return The type if contained.
	 *  @throws RuntimeException when the type is not found.
	 */
	public OAVObjectType getObjectType(Class clazz)
	{
		String typename = SReflect.getClassName(clazz);
		// Find type if existent.
		OAVObjectType ret = getDeepType(typename);
		
		// #ifndef MIDP		
		// Hack??? If not found, create implicit java type. 
		if(ret==null)
		{
			if(clazz!=null)
			{
				// Find super types to determine kind.
				List	superclasses	= new ArrayList();
				List	superoavtypes	= new ArrayList();
				superclasses.add(clazz);
				for(int i=0; i<superclasses.size(); i++)
				{
					// Search for corresponding oav type.
					Class	clz	= (Class)superclasses.get(i);
					OAVObjectType	oavtype	= getDeepType(SReflect.getClassName(clz));
					
					// Remember type if found.
					if(oavtype!=null)
					{
						superoavtypes.add(oavtype);
					}
					
					// Otherwise look further up in the hierarchy.
					else
					{
						Class	sup	= clz.getSuperclass();
						if(sup!=null)
							superclasses.add(sup);
						Class[]	ifs	= clz.getInterfaces();
						for(int j=0; j<ifs.length; j++)
							superclasses.add(ifs[j]);
					}
				}
				
				String	kind	= null;
				for(int i=0; i<superoavtypes.size(); i++)
				{
					String	superkind	= ((OAVJavaType)superoavtypes.get(i)).getKind();
					if(kind==null || kind.equals(OAVJavaType.KIND_OBJECT))
						kind	= superkind;
					else if(!kind.equals(superkind) && !superkind.equals(OAVJavaType.KIND_OBJECT))
						throw new RuntimeException("Incompatible kinds for type '"+typename+"': "+superoavtypes);
				}
				if(kind==null)
					kind	= OAVJavaType.KIND_OBJECT;
				
				if(OAVJavaType.KIND_OBJECT.equals(kind))
				{
					if(SReflect.getMethod(clazz, "addPropertyChangeListener", PCL)!=null)
						kind	= OAVJavaType.KIND_BEAN;
				}
				
//				ret	= createJavaType(clazz, kind);
				ret = new OAVJavaType(clazz, kind, this);
			}
		}
		// #endif
			
		if(ret==null)
			throw new RuntimeException("Type not found in type model: "+typename);
		
		return ret;
	}
	
	/**
	 *  Get a type per name.
	 *  Creates implicit java types on the fly.
	 *  TODO: Unify with getObjectType(Class clazz)
	 *  
	 *  @param typename The type name.
	 *  @return The type if contained.
	 *  @throws RuntimeException when the type is not found.
	 */
	public OAVObjectType getObjectType(String typename)
	{
		// Find type if existent.
		OAVObjectType ret = getDeepType(typename);
		
		// #ifndef MIDP		
		// Hack??? If not found, create implicit java type. 
		if(ret==null)
		{
			Class	clazz = SReflect.classForName0(typename, classloader);
			if(clazz!=null)
			{
				// Find super types to determine kind.
				List	superclasses	= new ArrayList();
				List	superoavtypes	= new ArrayList();
				superclasses.add(clazz);
				for(int i=0; i<superclasses.size(); i++)
				{
					// Search for corresponding oav type.
					Class	clz	= (Class)superclasses.get(i);
					OAVObjectType	oavtype	= getDeepType(SReflect.getClassName(clz));
					
					// Remember type if found.
					if(oavtype!=null)
					{
						superoavtypes.add(oavtype);
					}
					
					// Otherwise look further up in the hierarchy.
					else
					{
						Class	sup	= clz.getSuperclass();
						if(sup!=null)
							superclasses.add(sup);
						Class[]	ifs	= clz.getInterfaces();
						for(int j=0; j<ifs.length; j++)
							superclasses.add(ifs[j]);
					}
				}
				
				String	kind	= null;
				for(int i=0; i<superoavtypes.size(); i++)
				{
					String	superkind	= ((OAVJavaType)superoavtypes.get(i)).getKind();
					if(kind==null || kind.equals(OAVJavaType.KIND_OBJECT))
						kind	= superkind;
					else if(!kind.equals(superkind) && !superkind.equals(OAVJavaType.KIND_OBJECT))
						throw new RuntimeException("Incompatible kinds for type '"+typename+"': "+superoavtypes);
				}
				if(kind==null)
					kind	= OAVJavaType.KIND_OBJECT;
				
				if(OAVJavaType.KIND_OBJECT.equals(kind))
				{
					if(SReflect.getMethod(clazz, "addPropertyChangeListener", PCL)!=null)
						kind	= OAVJavaType.KIND_BEAN;
				}
				
//				ret	= createJavaType(clazz, kind);
				ret = new OAVJavaType(clazz, kind, this);
			}
		}
		// #endif
			
		if(ret==null)
			throw new RuntimeException("Type not found in type model: "+typename);
		
		return ret;
	}
	
	/**
	 *  Get all defined types.
	 *  @return The types.
	 * /
	public Collection getTypes()
	{
		todo: add types from contained models
		return types;
	}*/
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get the class loader.
	 *  @return The class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}

	/**
	 *  Test if two types are equal.
	 *  @return True if equal.
	 */
	public boolean equals(Object object)
	{
		return object instanceof OAVTypeModel && ((OAVTypeModel)object).getName().equals(name);
	}
	
	/**
	 *  Get the hash code.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 31+name.hashCode();
	}

	/**
	 *  Get a type for a java class.
	 */
	public OAVJavaType getJavaType(Class clazz)
	{
		return (OAVJavaType)getObjectType(clazz);
		//return (OAVJavaType)getObjectType(SReflect.getClassName(clazz));
	}


	/**
	 *  Get the contained typemodels.
	 */
	public OAVTypeModel[] getTypeModels()
	{
		return tmodels;
	}

	/**
	 *  Get a copy of the typemodel without contained models.
	 */
	public OAVTypeModel getDirectTypeModel()
	{
		OAVTypeModel newmodel = new OAVTypeModel(name+"_direct", classloader);
		newmodel.types.putAll(types);
		return newmodel;
	}

	/**
	 *  Search for a type in this model and all submodels.
	 *  @return null if not found.
	 */
	protected OAVObjectType getDeepType(String typename)
	{
		OAVObjectType	ret = (OAVObjectType)types.get(typename);
		for(int i=0; ret==null && tmodels!=null && i<tmodels.length; i++)
			ret = tmodels[i].getDeepType(typename);
		return ret;
	}
}
