package jadex.commons.xml;

import java.util.HashMap;
import java.util.Map;

/**
 *  Mapping from tag (or path fragment) to OAV type.
 */
public class TypeInfo	extends AbstractInfo
{
	//-------- attributes -------- 
	
	/** The type. */
	protected Object type;
	
	/** The comment mapping. */
	protected Object comment;
	
	/** The content mapping. */
	protected Object content;
	
	/** The attribute mappings. */
	protected Map attributes;

	/** The post processor (if any). */
	protected IPostProcessor	pproc;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public TypeInfo(String xmlpath, Object type)
	{
		this(xmlpath, type, null, null, null, null);
	}
	
	/**
	 * 
	 */
	public TypeInfo(String xmlpath, Object type, Object comment, Object content, Map attributes, IPostProcessor pproc)
	{
		super(xmlpath);
		this.type = type;
		this.comment = comment;
		this.content = content;
		this.attributes = attributes;
		this.pproc = pproc;
	}
	
	//-------- methods --------

	/**
	 * @return the type
	 */
	public Object getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Object type)
	{
		this.type = type;
	}

	/**
	 * @return the comment
	 */
	public Object getComment()
	{
		return this.comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(Object comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the content
	 */
	public Object getContent()
	{
		return this.content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object content)
	{
		this.content = content;
	}
	
	/**
	 * 
	 */
	public void addAttribute(String xmlname, Object attrtype)
	{
		if(attributes==null)
			attributes = new HashMap();
		attributes.put(xmlname, attrtype);
	}
	
	/**
	 * 
	 */
	public Object getAttributeType(String xmlname)
	{
		return attributes==null? null: attributes.get(xmlname);
	}

	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public IPostProcessor	getPostProcessor()
	{
		return this.pproc;
	}

	/**
	 *  Set the post-processor.
	 *  @param pproc The post-processor.
	 */
	public void setPostProcessor(IPostProcessor pproc)
	{
		this.pproc = pproc;
	}	
}
