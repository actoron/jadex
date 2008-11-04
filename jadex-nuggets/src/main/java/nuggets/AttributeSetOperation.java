/*
 * AttributeSetOperation.java
 * Copyright (c) 2005 by University of Hamburg. All Rights Reserved.
 * Departament of Informatics. 
 * Distributed Systems and Information Systems.
 *
 * Created by walczak on Jan 19, 2006.  
 * Last revision $Revision: 4064 $ by:
 * $Author: walczak $ on $Date: 2006-02-23 18:48:47 +0100 (Do, 23 Feb 2006) $.
 */
package nuggets;



/**
 * AttributeSetOperation
 * 
 * @author walczak
 * @since Jan 19, 2006
 */
public class AttributeSetOperation implements IDelayedOperation
{

	private final IDelegate	delegate;

	private final Object	object;

	private final String	attribute;

	private final String		id;

	/**
	 * Constructor for AttributeSetOperation.
	 * 
	 * @param delegate
	 * @param object
	 * @param attribute
	 * @param id
	 */
	public AttributeSetOperation(IDelegate delegate, Object object, String attribute, String id)
	{
		this.delegate = delegate;
		this.object = object;
		this.attribute = attribute;
		this.id = id;
	}

	/**
	 * @param asm
	 * @throws Exception
	 * @see nuggets.IDelayedOperation#perform(nuggets.IAssembler)
	 */
	public void perform(IAssembler asm) throws Exception
	{
		delegate.set(object, attribute, asm.getValue(id));
	}
	
	/** 
	 * @return  object+"set("+attribute+"="+id+")";
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return object+"set("+attribute+"="+id+")";
	}

}


/*
 * $Log$
 * Revision 1.4  2006/02/23 17:46:25  walczak
 * LF
 *
 * Revision 1.3  2006/02/17 12:48:54  walczak
 * yet even faster
 *
 * Revision 1.2  2006/02/16 17:41:08  walczak
 * no reference to strings in Maps but a direct inclusion.
 *
 * Revision 1.1  2006/01/20 18:11:02  walczak
 * ------------------------
 *
 */