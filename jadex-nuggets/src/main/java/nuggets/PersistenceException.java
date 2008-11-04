/*
 * PersistenceException.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Dec 2, 2005.  
 * Last revision $Revision: 4047 $ by:
 * $Author: walczak $ on $Date: 2006-02-17 13:48:54 +0100 (Fr, 17 Feb 2006) $.
 */
package nuggets;

/** PersistenceException 
 * @author walczak
 * @since  Dec 2, 2005
 */
public class PersistenceException extends RuntimeException
{
	/** <code>cause</code>: */
	public final Throwable	cause;

	private String			string;


	/** 
	 * Constructor for PersistenceException.
	 */
	public PersistenceException()
	{
		cause = null;
	}

	/** 
	 * Constructor for PersistenceException.
	 * @param string
	 */
	public PersistenceException(String string)
	{
		super(string);
		cause = null;
	}

	/** 
	 * Constructor for PersistenceException.
	 * @param cause
	 */
	public PersistenceException(Throwable cause)
	{
		super(cause.getMessage() + ":" + cause);
		this.cause = cause;
	}

	/** Getter for cause
	 * @return Returns cause.
	 */
	public Throwable getCause()
	{
		return cause;
	}

	/** 
	 * @param message
	 */
	public void setMessage(String message)
	{
		this.string = message;
	}

	/** 
	 * @return the string representation of this class
	 * @see java.lang.Throwable#toString()
	 */
	public String toString()
	{
		if (string==null) return super.toString();
		return super.toString()+"\ntext=\n"+string;
	}

	
}


/* 
 * $Log$
 * Revision 1.4  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.3  2006/02/16 10:08:29  walczak
 * codding of XML text contents schould be better now
 *
 * Revision 1.2  2006/02/16 09:23:47  walczak
 * Extended debugg info
 *
 * Revision 1.1  2006/01/18 13:59:47  walczak
 * Introduced the nuggets package.
 *
 */