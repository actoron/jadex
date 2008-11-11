/*
 * IDelegate.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 11, 2006.  
 * Last revision $Revision: 4401 $ by:
 * $Author: walczak $ on $Date: 2006-06-29 19:27:25 +0200 (Do, 29 Jun 2006) $.
 */
package nuggets;


/** IDelegate 
 * @author walczak
 * @since  Jan 11, 2006
 */
public interface IDelegate
{

	/** 
	 * @param o
	 * @param cruncher
	 * @throws Exception 
	 */
	void persist(Object o, ICruncher cruncher, ClassLoader classloader) throws Exception;


	/** 
	 * @param clazz 
	 * @param asm
	 * @return an instance of assembled object
	 * @throws Exception 
	 */
	Object getInstance(Class clazz, IAssembler asm) throws Exception;
	
	
	/** 
	 * @param obj
	 * @param asm
	 * @throws Exception
	 */
	void  assemble(Object obj, IAssembler asm) throws Exception;

	/** 
	 * @param object
	 * @param attribute
	 * @param value
	 * @throws Exception 
	 */
	void set(Object object, String attribute, Object value) throws Exception;
	
	// --------------- for the delegate generator -------------------
	
	/** 
	 * @return true if it can be represented as a simple string
	 */
	boolean isSimple();
	
	/** 
	 * @param exp the expression giving the attribute
	 * @return a string used to marshall the class
	 */
	String getMarshallString(String exp);

	/** 
	 * @param className TODO
	 * @param exp the expression giving string representation
	 * @return the string needed to unmarshall an attribute of this class from a string representation
	 */
	String getUnmarshallString(String className, String exp);


	/** 
	 * @param clazz
	 * @param value
	 * @return the unmarshalled object if string or the object itself
	 */
	Object unmarshall(Class clazz, Object value);


	/** 
	 * @param clazz
	 * @param object
	 * @return the string representation of this object 
	 */
	String marshall(Class clazz, Object object);
}