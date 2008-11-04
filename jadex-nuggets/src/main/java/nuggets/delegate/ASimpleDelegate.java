/*
 * ASimpleDelegate.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4686 $ by:
 * $Author: walczak $ on $Date: 2006-12-20 23:55:56 +0100 (Mi, 20 Dez 2006) $.
 */
package nuggets.delegate;

/** ASimpleDelegate 
 * 
 * A delegate for final classes with a simple string representation
 * @author walczak
 * @since  Jan 17, 2006
 */
public abstract class ASimpleDelegate extends ADelegate
{

	/** 
	 * @param exp
	 * @return "String.valueOf(" + exp + ")";
	 * @see nuggets.delegate.ADelegate#getMarshallString(java.lang.String)
	 */
	public String getMarshallString(String exp)
	{
		return "String.valueOf(" + exp + ")";
	}

	/** 
	 * @param exp
	 * @return "new " + clazz.getName() + "((String)" + exp + ")";
	 * @see nuggets.delegate.ADelegate#getUnmarshallString(String, java.lang.String)
	 */
	public String getUnmarshallString(String className, String exp)
	{
		return "new " + className + "((String)" + exp + ")";
	}
	
	/** 
	 * @param clazz 
	 * @param value 
	 * @return the simple object with this string
	 */
	public abstract Object unmarshall(Class clazz, Object value);

	/** 
	 * @param clazz
	 * @param exp
	 * @return "("+clazz.getName()+")"+exp;
	 */
	protected static String cast(Class clazz, String exp)
	{
		return "(" + clazz.getName() + ")" + exp;
	}

	/** 
	 * @return true
	 * @see nuggets.delegate.ADelegate#isSimple()
	 */
	public boolean isSimple()
	{
		return true;
	}

}


/* 
 * $Log$
 * Revision 1.6  2006/12/20 22:55:56  walczak
 * Moved some classes represented as string to the reference representation.
 * Subclasses when serialized as a string loose their class identity with the old approach.
 *
 * Revision 1.5  2006/06/29 17:27:25  walczak
 * created a reflection delegate. alpha
 *
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/21 15:02:16  walczak
 * *** empty log message ***
 *
 * Revision 1.2  2006/01/20 18:11:01  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */