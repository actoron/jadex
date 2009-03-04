package jadex.rules.state.io.xml;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

import java.util.HashMap;
import java.util.Map;

/**
 *  Mapping from tag (or path fragment) to OAV type.
 */
public class OAVMappingInfo	extends AbstractOAVInfo
{
	//-------- attributes -------- 
	
	/** The type. */
	protected OAVObjectType type;
	
	/** The comment mapping. */
	protected OAVAttributeType comment;
	
	/** The content mapping. */
	protected OAVAttributeType content;
	
	/** The attribute mappings. */
	protected Map attributes;

	/** The post processor (if any). */
	protected IPostProcessor	pproc;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public OAVMappingInfo(String xmlpath, OAVObjectType type)
	{
		this(xmlpath, type, null, null, null, null);
	}
	
	/**
	 * 
	 */
	public OAVMappingInfo(String xmlpath, OAVObjectType type, OAVAttributeType comment, OAVAttributeType content, Map attributes, IPostProcessor pproc)
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
	public OAVObjectType getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(OAVObjectType type)
	{
		this.type = type;
	}

	/**
	 * @return the comment
	 */
	public OAVAttributeType getComment()
	{
		return this.comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(OAVAttributeType comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the content
	 */
	public OAVAttributeType getContent()
	{
		return this.content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(OAVAttributeType content)
	{
		this.content = content;
	}
	
	/**
	 * 
	 */
	public void addAttribute(String xmlname, OAVAttributeType attrtype)
	{
		if(attributes==null)
			attributes = new HashMap();
		attributes.put(xmlname, attrtype);
	}
	
	/**
	 * 
	 */
	public OAVAttributeType getAttributeType(String xmlname)
	{
		return attributes==null? null: (OAVAttributeType)attributes.get(xmlname);
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
