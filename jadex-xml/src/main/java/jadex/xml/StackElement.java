package jadex.xml;

import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  A struct to represent an element on the stack while parsing.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The xml tag. */
	protected QName tag;
	
	/** The created object. */
	protected Object object;
	
	/** The collected content. */
	protected String content;
	
	// todo: remove rest somehow
	
	/** The raw attributes. */
	protected Map rawattrs;
	
	/** The type info. */
	protected TypeInfo typeinfo;
	
	/** The line of the start tag. */
	protected int line;
	
	/** The column of the start tag. */
	protected int column;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(QName tag, Object object)
	{
		this(tag, object, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(QName tag, Object object, Map rawattrs)
	{
		this(tag, object, rawattrs, null, -1, -1);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(QName tag, Object object, Map rawattrs, TypeInfo typeinfo, int line, int column)
	{
		this.tag = tag;
		this.object = object;
		this.rawattrs = rawattrs;
		this.typeinfo = typeinfo;
		this.line	= line;
		this.column	= column;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the tag.
	 *  @return The tag.
	 */
	public QName getTag()
	{
		return this.tag;
	}

	/**
	 *  Get the object.
	 *  @return The object.
	 */
	public Object getObject()
	{
		return this.object;
	}
	
	/**
	 *  Set the object.
	 *  @param object The object to set.
	 */
	public void setObject(Object object)
	{
//		assert !hashcodecalled;
		this.object = object;
	}

	/**
	 *  Get the content (if any).
	 *  @return The content or null for empty elements.
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 *  Get the raw attributes.
	 *  @return The raw attributes.
	 */
	public Map getRawAttributes()
	{
		return this.rawattrs;
	}

	/**
	 *  Get the typeinfo.
	 *  @return The typeinfo.
	 */
	public TypeInfo getTypeInfo()
	{
		return this.typeinfo;
	}
	
	/**
	 *  Get the line.
	 *  @return The line.
	 */
	public int getLine()
	{
		return this.line;
	}
	
	/**
	 *  Set the column.
	 *  @param column The column to set.
	 */
	public void setColumn(int column)
	{
		this.column = column;
	}
	
	/**
	 *  Get the column.
	 *  @return The column.
	 */
	public int getColumn()
	{
		return this.column;
	}
	
	/**
	 *  Set the line.
	 *  @param line The line to set.
	 */
	public void setLine(int line)
	{
		this.line = line;
	}

	/**
	 *  Add content to the already collected content (if any).
	 *  @param content	The content to add.
	 */
	public void	addContent(String content)
	{
		if(this.content==null)
			this.content	= content;
		else
			this.content	+= content;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "StackElement(tag="+this.tag+", object=" + this.object + ")";
	}

//	boolean hashcodecalled	= false;

	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
//		hashcodecalled	= true;
		final int prime = 31;
		int result = 1;
		// Hack!!! Content set afterwards, cannot use for hashcode!
//		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((rawattrs == null) ? 0 : rawattrs.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((typeinfo == null) ? 0 : typeinfo.hashCode());
		return result;
	}

	/**
	 *  Test if two stack elements are equal.
	 */
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		StackElement other = (StackElement)obj;
		// Hack!!! Content set afterwards, cannot use for equals!
//		if(content == null)
//		{
//			if(other.content != null)
//				return false;
//		}
//		else if(!content.equals(other.content))
//			return false;
		if(object == null)
		{
			if(other.object != null)
				return false;
		}
		else if(!object.equals(other.object))
			return false;
		if(rawattrs == null)
		{
			if(other.rawattrs != null)
				return false;
		}
		else if(!rawattrs.equals(other.rawattrs))
			return false;
		if(tag == null)
		{
			if(other.tag != null)
				return false;
		}
		else if(!tag.equals(other.tag))
			return false;
		if(typeinfo == null)
		{
			if(other.typeinfo != null)
				return false;
		}
		else if(!typeinfo.equals(other.typeinfo))
			return false;
		return true;
	}
	
	
	
}