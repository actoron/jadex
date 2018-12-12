package jadex.commons.collection;

import java.util.ArrayList;
import java.util.List;

/**
 *  Tree data structure.
 */
public class Tree 
{ 
	//-------- constants --------
	
	/** The preorder constant. */
	public static final String PREORDER = "preorder";
	
	/** The postorder constant. */
	public static final String POSTORDER = "postorder";
	
	/** The inorder constant. */
//	public static final String INORDER = "inorder";
	
	//-------- attributes --------
	
	/** The root node. */
    protected TreeNode root;
     
    //-------- constructurs --------
    
    /**
     *  Create a new tree.
     */
    public Tree() 
    {
    	this(new TreeNode());
    }
    
    /**
     *  Create a new tree.
     */
    public Tree(TreeNode root) 
    {
    	this.root = root;
    }
 
    //-------- methods --------

    /**
     * Return the root node of the tree.
     * @return The root node.
     */
    public TreeNode getRootNode() 
    {
        return this.root;
    }
 
    /**
     * Set the root node for the tree.
     * @param root The root node to set.
     */
    public void setRootElement(TreeNode root) 
    {
        this.root = root;
    }
     
    /**
     * Returns the tree as a List of node objects. The elements of the
     * List are generated from a pre-order traversal of the tree.
     * @return Tree elements.
     */
    public List toList(String order) 
    {
        List ret = new ArrayList();
        if(root!=null)
        {
	        if(PREORDER.equals(order))
	        {
	        	walkPreorder(root, ret);
	        }
	        else if(POSTORDER.equals(order))
	        {
	        	walkPostorder(root, ret);
	        }
        }
        return ret;
    }
     
    /**
     *  Test if empty.
     */
    public boolean isEmpty()
    {
    	return root==null || root.getNumberOfChildren()==0;
    }
    
    /**
     * Returns a String representation of the tree. 
     * The elements are generated from a pre-order traversal of the tree.
     * @return the String representation of the Tree.
     */
    public String toString() 
    {
    	try
    	{
    		return toList(PREORDER).toString();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "error";
    }
     
    /**
     * Walks the tree in pre-order style. 
     * @param node The starting element.
     * @param list The output of the walk.
     */
    protected void walkPreorder(TreeNode node, List list) 
    {
        list.add(node);
        List children = node.getChildren();
        if(children!=null)
        {
	        for(int i=0; i<children.size(); i++) 
	        {
	        	TreeNode child = (TreeNode)children.get(i);
	        	walkPreorder(child, list);
	        }
        }
    }
    
    /**
     * Walks the tree in post-order style. 
     * @param node The starting element.
     * @param list The output of the walk.
     */
    protected void walkPostorder(TreeNode node, List list) 
    {
        List children = node.getChildren();
        if(children!=null)
        {
	        for(int i=0; i<children.size(); i++) 
	        {
	        	TreeNode child = (TreeNode)children.get(i);
	        	walkPostorder(child, list);
	        }
        }
        list.add(node);
    }
}
