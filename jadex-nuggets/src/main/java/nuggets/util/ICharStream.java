/*
 * ICharStream.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Feb 2, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets.util;

/** ICharStream 
 * @author walczak
 * @since  Feb 2, 2006
 */
public interface ICharStream
{

	/** 
	 * @param chars
	 */
	void write(char[] chars);

	/** 
	 * @param b
	 */
	void write(int b);
	
	/** 
	 * @param c
	 */
	void write(char c);

	/** 
	 * @param string
	 */
	void write(String string);



	/** 
	 * 
	 */
	void flush();

}


/* 
 * $Log$
 * Revision 1.2  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.1  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 */