/*
 * IReader.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 19, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets;

import java.io.Reader;

/** IReader 
 * @author walczak
 * @since  Jan 19, 2006
 */
public interface IReader
{

	/** Buffer 
	 * @author walczak
	 * @since  Jan 19, 2006
	 */
	public class Buffer {
		/** the holder for char array */
		public char[] chars;
		/** where does the data start (inclusive) */
		public int    start;
		/** where does the data end (exclusive) */
		public int    len;
		
		void set(char[] chars, int start, int len) {
			this.chars=chars;
			this.start=start;
			this.len=len;
		}
	}
	
	/** 
	 * @return the next text token or null if no more
	 */
	public Buffer getText();

	/** 
	 * get the next element
	 * @return the name of this element
	 */
	public String nextElement();


	/** May include additonal attributes
	 * @return the number of attributes
	 */
	public int getAttributeCount();
	
	/** 
	 * @param i
	 * @return the name of an attribute
	 */
	public String getAttributeName(int i);
	
	/** 
	 * @param i
	 * @return true if this is a reference attribute
	 */
	public boolean isReferenceAttribute(int i);
	
	
	/** 
	 * @param i
	 * @return the decoded value of this attribute 
	 */
	public String getAttributeValue(int i);
	
	/** 
	 * @return the id of this element
	 */
	public String getID();
		
	/** 
	 * start parsing
	 * @param rdr 
	 */
	public void start(Reader rdr);

	/** 
	 * @param chars
	 * @param start
	 * @param len
	 * @return decodes the characters
	 */
	public String decodeText(char[] chars, int start, int len);
	
	/** 
	 * @param text
	 * @return decodes the characters
	 */
	public String decodeText(String text);

	/** 
	 * @return the byte data from the element text
	 */
	public byte[] getData();


	/** 
	 * @param string
	 * @return the value of this attribute
	 */
	public String getAttributeValue(String string);

	/** 
	 * @param attribute
	 * @return the index of this attribute
	 */
	public int getAttributeIndex(String attribute);

	
}


/* 
 * $Log$
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.2  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 * Revision 1.1  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 */