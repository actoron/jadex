/*
 * NuggetsOutputStream.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Feb 20, 2006.  
 * Last revision $Revision: 6926 $ by:
 * $Author: braubach $ on $Date: 2008-09-28 22:16:58 +0200 (So, 28 Sep 2008) $.
 */
package nuggets;


/** NuggetsOutputStream 
 * @author walczak
 * @since  Feb 20, 2006
 */
public class NuggetsOutputStream 
{
//	private final CharStream	cos;
//	private final BeanCruncher	mill;
//	private final Writer	writer;

	/** 
	 * Constructor for NuggetsOutputStream.
	 * @param file
	 * @throws IOException 
	 * /
	public NuggetsOutputStream(OutputStream file) throws IOException {
		this.writer = new OutputStreamWriter(file);
		this.cos = new CharStream();
		this.mill = new BeanCruncher(new JavaXMLWriter(cos));
	}*/
	
	/** 
	 * @param value
	 * @throws IOException
	 * /
	public void writeObject(Object value) throws IOException
	{
		mill.persist(value);
		cos.writeTo(writer);
	}*/
	
	/** 
	 * @throws IOException
	 * /
	public void close() throws IOException {
		writer.close();
	}*/


}


/* 
 * $Log$
 * Revision 1.3  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.2  2006/02/20 15:00:56  walczak
 * ------------------------------------
 *
 * Revision 1.1  2006/02/20 14:11:24  walczak
 * Two help classes for the nuggets
 *
 */