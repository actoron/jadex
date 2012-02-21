package jadex.commons.transformation.traverser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *  This class is a struct for saving data about an inspected bean property.
 */
public class BeanProperty
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The type. */
	protected Class	type;

	/** The getter. */
	protected Method getter;

	/** The setter. */
	protected Method setter;
	
	/** The setter type. */
	protected Class	settertype;

	/** The field. */
	protected Field field;

	
	//-------- constructors --------
	
	/**
	 *  Create a new bean property.
	 */
	public BeanProperty() 
	{ 
	}
	
	/**
	 *  Create a new bean property.
	 */
	public BeanProperty(String name, Class type, Method getter, Method setter, Class settertype)
	{
		this.name = name;
		this.type = type;
		this.getter = getter;
		this.setter = setter;
		this.settertype = settertype;
	}
	
	/**
	 *  Create a new bean property.
	 */
	public BeanProperty(String name, Field field)
	{
		this.name = name;
		this.type = field.getType();
		this.settertype = type;
		this.field = field;
	}

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(Class type)
	{
		this.type = type;
	}

	/**
	 *  Get the getter.
	 *  @return The getter.
	 */
	public Method getGetter()
	{
		return this.getter;
	}

	/**
	 *  Set the getter.
	 *  @param getter The getter to set.
	 */
	public void setGetter(Method getter)
	{
		this.getter = getter;
	}

	/**
	 *  Get the setter.
	 *  @return The setter.
	 */
	public Method getSetter()
	{
		return this.setter;
	}

	/**
	 *  Set the setter.
	 *  @param setter The setter to set.
	 */
	public void setSetter(Method setter)
	{
		this.setter = setter;
	}

	/**
	 *  Get the setter_type.
	 *  @return The setterttype.
	 */
	public Class getSetterType()
	{
		return this.settertype;
	}

	/**
	 *  Set the setter type.
	 *  @param settertype The setter type to set.
	 */
	public void setSetterType(Class settertype)
	{
		this.settertype = settertype;
	}

	/**
	 *  Get the field.
	 *  @return The field.
	 */
	public Field getField()
	{
		return this.field;
	}

	/**
	 *  Set the field.
	 *  @param field The field to set.
	 */
	public void setField(Field field)
	{
		this.field = field;
	}

}