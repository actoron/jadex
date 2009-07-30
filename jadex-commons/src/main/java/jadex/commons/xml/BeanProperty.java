/*
 * BeanProperty.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Mar 22, 2006.  
 * Last revision $Revision: 4401 $ by:
 * $Author: walczak $ on $Date: 2006-06-29 19:27:25 +0200 (Do, 29 Jun 2006) $.
 */
package jadex.commons.xml;

import java.lang.reflect.Method;

/**
 *  BeanProperty
 */
public class BeanProperty
{

	protected String name;

	protected Class	type;

	protected Method getter;

	protected Method setter;

	protected Class	setter_type;

	/**
	 *  Create a new bean property.
	 */
	public BeanProperty() 
	{ 
	}
	
	/**
	 *  Create a new bean property.
	 */
	public BeanProperty(String name, Class type, Method getter, Method setter, Class setter_type)
	{
		this.name = name;
		this.type = type;
		this.getter = getter;
		this.setter = setter;
		this.setter_type = setter_type;
	}

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
	 *  @return The setter_type.
	 */
	public Class getSetter_type()
	{
		return this.setter_type;
	}

	/**
	 *  Set the setter_type.
	 *  @param setterType The setter_type to set.
	 */
	public void setSetter_type(Class setterType)
	{
		this.setter_type = setterType;
	}

}