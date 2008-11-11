/*
 * ADelegate.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 18, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets.delegate;

import nuggets.IAssembler;
import nuggets.ICruncher;
import nuggets.IDelegate;


/** ADelegate 
 * @author walczak
 * @since  Jan 18, 2006
 */
public class ADelegate implements IDelegate
{

	/** 
	 * @param o
	 * @param mill
	 * @see nuggets.IDelegate#persist(java.lang.Object, nuggets.ICruncher)
	 */
	public void persist(Object o, ICruncher mill, ClassLoader classloader) 
	{
		mill.startConcept(o);
	}
	

	/** 
	 * @param clazz
	 * @param asm
	 * @return clazz.newInstance();
	 * @throws Exception
	 * @see nuggets.IDelegate#getInstance(java.lang.Class, nuggets.IAssembler)
	 */
	public Object getInstance(Class clazz, IAssembler asm) throws Exception
	{
		return clazz.newInstance();
	}

	/** 
	 * @param obj
	 * @param asm
	 * @throws Exception
	 * @see nuggets.IDelegate#assemble(java.lang.Object, nuggets.IAssembler)
	 */
	public void assemble(Object obj, IAssembler asm) throws Exception
	{ /* NOP */ }
	
	/** 
	 * @param object
	 * @param attribute
	 * @param value
	 * @throws Exception 
	 * @see nuggets.IDelegate#set(java.lang.Object, java.lang.String, java.lang.Object)
	 */
	public void set(Object object, String attribute, Object value) throws Exception
	{
		/* NOP */
	}

	
	/** 
	 * @return false
	 * @see nuggets.delegate.ASimpleDelegate#isSimple()
	 */
	public boolean isSimple()
	{
		return false;
	}

	
	/** 
	 * @param exp
	 * @return the expression - bitsets are referenced
	 * @see nuggets.delegate.ASimpleDelegate#getMarshallString(java.lang.String)
	 */
	public String getMarshallString(String exp)
	{
		return exp;
	}

	/** 
	 * @param className 
	 * @param exp
	 * @return this unmarshaller
	 * @see nuggets.delegate.ASimpleDelegate#getUnmarshallString(String, java.lang.String)
	 */
	public String getUnmarshallString(String className, String exp)
	{
		return "(" + className + ")" + exp;
	}
	
	/** 
	 * @param clazz
	 * @param value
	 * @return the value
	 * @see nuggets.IDelegate#unmarshall(java.lang.Class, java.lang.Object)
	 */
	public Object unmarshall(Class clazz, Object value)
	{
		return value;
	}


	/** 
	 * @param clazz
	 * @param object
	 * @return the to string representation of this object
	 * @see nuggets.IDelegate#marshall(java.lang.Class, java.lang.Object)
	 */
	public String marshall(Class clazz, Object object)
	{
		return String.valueOf(object);
	}
	
}
