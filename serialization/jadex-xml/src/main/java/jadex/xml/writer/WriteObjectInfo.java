package jadex.xml.writer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.collection.Tree;
import jadex.commons.collection.TreeNode;
import jadex.xml.stax.QName;

/**
 *  Info for writing an object.
 */
public class WriteObjectInfo
{
	//-------- attributes --------
	
	/** The comment. */
	protected String comment;
	
	/** The attribute values. */
	protected Map<Object, String> attributes;

	/** The content. */
	protected String content;
	
	/** The subobjects tree. */
	protected Tree subobjects;
	
	//-------- methods --------

	/**
	 *  Get the comment.
	 *  @return The comment.
	 */
	public String getComment()
	{
		return this.comment;
	}

	/**
	 *  Set the comment.
	 *  @param comment The comment to set.
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	/**
	 *  Get the attributes.
	 *  @return The attributes.
	 */
	public Map<Object, String> getAttributes()
	{
		return this.attributes;
	}

	/**
	 *  Add an attribute.
	 *  @param name The name.
	 *  @param value The value.
	 */
	public void addAttribute(Object name, String value)
	{
		if(attributes==null)
			attributes = new LinkedHashMap<Object, String>();
		else if(attributes.containsKey(name))
			throw new RuntimeException("Duplicate attribute: "+name);
		attributes.put(name, value);
	}

	/**
	 *  Get the content.
	 *  @return The content.
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 *  Set the content.
	 *  @param content The content to set.
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 *  Get the subobjects.
	 *  @return The subobjects.
	 */
	public Tree getSubobjects()
	{
		return this.subobjects;
	}
	
	/**
	 *  Add a subobject.
	 */
	public void addSubobject(QName[] pathname, Object subobject, boolean flatten)
	{
//		System.out.println("added: "+SUtil.arrayToString(pathname)+" "+subobject+" "+flatten);
		if(subobjects==null)
			subobjects = new Tree();
		
		// Build the path in the tree (on each level a decision about flattening needs to be done)
		TreeNode node = subobjects.getRootNode();
		for(int i=0; i<pathname.length; i++)
		{
			// Never flatten last (object) layer (is this a hack?)
			node = getOrCreateChild(node, pathname[i], i+1==pathname.length? false: flatten);
		}
		
		// Last node data is [tag, object]
		node.setData(new Object[]{node.getData(), subobject});
	}
	
	/**
	 *  Get or create a tree child.
	 */
	protected TreeNode getOrCreateChild(TreeNode node, QName tag, boolean flatten)
	{
		TreeNode ret = null;
		
		if(flatten)
		{
			// Find fitting tag 
			List<TreeNode> children = node.getChildren();
			if(children!=null)
			{
				for(int i=0; i<children.size() && ret==null; i++)
				{
					TreeNode tmp = (TreeNode)children.get(i);
					if(tag.equals(tmp.getData()))
						ret = tmp;
				}
			}
		}
		
		if(ret==null)
		{
			ret = new TreeNode(tag);
			node.addChild(ret);
		}
		
		return ret;
	}
}
