/*
 * IWriter.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 2, 2005.  
 * Last revision $Revision: 4047 $ by:
 * $Author: walczak $ on $Date: 2006-02-17 13:48:54 +0100 (Fr, 17 Feb 2006) $.
 */
package nuggets;

/** IWriter - this writes the parts of a bean to a stream
 * @author walczak
 * @since  Dec 2, 2005
 */
public interface IWriter
{
	/** 
	 * This writes the prolog of the message
	 * @param ontology 
	 */
	void start(String ontology);

	/**
	 * This writes the epilog of the message 
	 *
	 */
	void end();

	/** Start a given tag
	 * @param tag
	 * @param id 
	 */
	void start(String tag, String id);

	/** 
	 * End a tag
	 * @param tag 
	 */
	void end(String tag);

	/** Sets the b of this attribute to a string b.
	 * @param name 
	 * @param value
	 */
	void put(String name, String value);

	/** 
	 * @param name
	 * @param id - the reference id
	 */
	void putRef(String name, String id);

	/** Add a reference element or simple token. It will not be encoded. 
	 * @param id
	 */
	void addToken(String id);
	
	/** Adds a binary data as token. The byte array will be encoded
	 * @param ba
	 */
	void write(byte[] ba);
	
	/** Set the text. It will be encoded
	 * @param text
	 */
	void write(String text);

	/** 
	 * @param chars
	 * @param start
	 * @param len
	 */
	void write(char[] chars, int start, int len);

}

/* 
 * $Log$
 * Revision 1.5  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.4  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.3  2006/02/14 17:39:05  walczak
 * new version of nuggets
 *
 * Revision 1.2  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */