/*
 * ICruncher.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 17, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets;


/** ICruncher 
 * @author walczak
 * @since  Jan 17, 2006
 */
public interface ICruncher
{
	/** Declares some object to be included first 
	 * @param child
	 * @return the id of this object
	 */
	int declare(Object child, ClassLoader classloader);
	
	
	/** Called after all references have been declared 
	 * @param obj Object that declared its references
	 */
	void startConcept(Object obj);

	/** Sets the named attribute with given value. 
	 * @param attr
	 * @param id
	 */
	void put(String attr, int  id);

	/** Sets the named attribute with given value. 
	 * @param attr
	 * @param value
	 */
	void put(String attr, String value);

	/** Adds an object as an id
	 * @param value 
	 */
	void add(Object value);

	/** Adds a token to the element content.
	 * @param value
	 */
	void addToken(String value);
	
	/** This encodes a binary data. the token will be added to the element text. 
	 * @param ba
	 */
	void setData(byte[] ba);
	
	/** Sets the text of this element. The text will be encoded
	 * @param text
	 */
	void setText(String text);

	/** Set the text from this char array
	 * @param chars
	 * @param start 
	 * @param len 
	 */
	void setText(char[] chars, int start, int len);

}
