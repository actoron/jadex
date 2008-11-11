/*
 * DString.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.PersistenceException;


/** DString 
 * @author walczak
 * @since  Jan 17, 2006
 */
public class DClass extends ASimpleDelegate
{
	/** 
	 * @param clazz
	 * @param asm
	 * @return the string stored in "v"
	 * @throws Exception
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		// Should use SReflect.classForName()?
		return Class.forName((String)asm.getAttributeValue("name"));//, true, Thread.currentThread().getContextClassLoader());
	}

	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.delegate.ASimpleDelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader)
	{

		mill.startConcept(o);
		mill.put("name", ((Class)o).getName());

	}

	/** 
	 * @param exp
	 * @return exp+".getName()";
	 * @see nuggets.delegate.ASimpleDelegate#getMarshallString(java.lang.String)
	 */
	public String getMarshallString(String exp)
	{
		return exp + ".getName()";
	}

	/** 
	 * @param clazz
	 * @param o
	 * @return the name of the class
	 * @see nuggets.delegate.ADelegate#marshall(java.lang.Class, java.lang.Object)
	 */
	public String marshall(Class clazz, Object o)
	{
		return ((Class)o).getName();
	}

	/** 
	 * @param exp
	 * @return unmarshal expressions for boolean and Boolean
	 * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
	 */
	public String getUnmarshallString(String className, String exp)
	{
		// Todo: is this correct?
		return "Class.forName((String)" + exp + ", true, Thread.currentThread().getContextClassLoader())";
	}

	/** 
	 * @param clazz
	 * @param value
	 * @return the boolean expression
	 * @see nuggets.delegate.ASimpleDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		try
		{
			// Should use SReflect.classForName()???
			return Class.forName((String)value);//, true, Thread.currentThread().getContextClassLoader());
		}
		catch(ClassNotFoundException e)
		{
			throw new PersistenceException(e);
		}
	}
}