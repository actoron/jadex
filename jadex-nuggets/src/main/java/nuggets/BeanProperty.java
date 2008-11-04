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
package nuggets;

import java.lang.reflect.Method;

/**
 * BeanProperty
 * 
 * @author walczak
 * @since Mar 22, 2006
 */
public class BeanProperty
{

	private String	name;

	private Class	type;

	private Method	getter;

	private Method	setter;

	private Class	setter_type;


	/** 
	 * Default constructor for BeanProperty.
	 */
	public BeanProperty() { //nop
	}
	
	/**
	 * Constructor for BeanProperty.
	 * 
	 * @param name
	 * @param type
	 * @param getter
	 * @param setter
	 * @param setter_type
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
	 * @return the getter method
	 */
	public Method getGetter() {
		return getter;
	}
	
	/** Getter for getter
	 * @return Returns getter.
	 */
	public String getGetterName()
	{
		return this.getter.getName();
	}


	/** Getter for getter_type
	 * @return Returns getter_type.
	 */
	public Class getGetterType()
	{
		return this.getter.getReturnType();
	}

	/** Getter for name
	 * @return Returns name.
	 */
	public String getName()
	{
		return this.name;
	}

	/** Setter for name.
	 * @param name The BeanProperty.java value to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/** 
	 * @return the setter method
	 */
	public Method getSetter() {
		return setter;
	}
	
	/** Getter for setter
	 * @return Returns setter.
	 */
	public String getSetterName()
	{
		return this.setter.getName();
	}

	/** Getter for setter_type
	 * @return Returns setter_type.
	 */
	public Class getSetterType()
	{
		return this.setter_type;
	}

	/** Setter for setter_type.
	 * @param setter_type The BeanProperty.java value to set
	 */
	public void setSetterType(Class setter_type)
	{
		this.setter_type = setter_type;
	}

	/** Getter for type
	 * @return Returns type.
	 */
	public Class getType()
	{
		return this.type;
	}

	/** Setter for type.
	 * @param type The BeanProperty.java value to set
	 */
	public void setType(Class type)
	{
		this.type = type;
	}
}


/*
 * $Log$
 * Revision 1.2  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.1  2006/03/22 17:16:59  walczak
 * added an reflective introspector
 *
 */