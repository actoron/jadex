package jadex.rules.rulesystem.rete.builder;

import java.util.HashMap;
import java.util.Map;

import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.rete.nodes.INode;
import jadex.rules.rulesystem.rete.nodes.ReteNode;
import jadex.rules.rulesystem.rules.Variable;

/**
 *  The build context contains all relevant data about
 *  the current net building process.
 */
public class BuildContext
{
	//-------- attributes --------
	
	/** The root node. */
	protected ReteNode root;
	
	/** The currently built rule. */
	protected IRule rule;
	
	/** The last alpha node. */
	protected INode lastanode;
	
	/** The last beta node. */
	protected INode lastbnode;
	
	// The global tuple count (which index should have the next tuple variable)
	/** The tuple cnt. */
	protected int tuplecnt;

	/** The first variable occurrence. */
	protected Map varinfos;
	
	/** Flag indicating that no right input is available (for collect nodes). */
	protected boolean rightunavailable;
	
	/** Flag indicating if the builder is currently creating alpha nodes. */
	protected boolean alpha;
	
	//-------- constructors --------

	/**
	 *  Create a new build context.
	 * /
	public BuildContext()
	{
		this(null, null, 0);
	}*/
	
	/**
	 *  Create a new build context.
	 */
	public BuildContext(ReteNode root, IRule rule)// INode lastbnode, int tuplecnt)
	{
		this.root	= root==null? new ReteNode(): root;
		this.rule	= rule;
		this.lastanode	= null;
		this.lastbnode	= null;
		this.tuplecnt	= 0;
//		this.lastbnode	= lastbnode;//lastnode==null? root: lastnode;
//		this.tuplecnt	= tuplecnt;
		this.varinfos	= new HashMap();
		this.alpha	= false;	// Will only be temporarily activated when building object conditions
	}
	
	//-------- methods --------

	/**
	 *  Get the root.
	 *  @return The root.
	 */
	public ReteNode getRootNode()
	{
		return root;
	}

	/**
	 *  Set the root.
	 *  @param root The root to set.
	 */
	public void setRootNode(ReteNode root)
	{
		this.root = root;
	}

	/**
	 *  Get the rule.
	 *  @return The rule.
	 */
	public IRule getRule()
	{
		return rule;
	}

	/**
	 *  Set the rule.
	 *  @param rule The rule to set.
	 */
	public void setRule(IRule rule)
	{
		this.rule = rule;
	}
	
	/**
	 *  Get the lastnode.
	 *  @return The lastnode.
	 */
	public INode getLastAlphaNode()
	{
		return lastanode;
	}

	/**
	 *  Set the lastnode.
	 *  @param lastnode The lastnode to set.
	 */
	public void setLastAlphaNode(INode lastanode)
	{
		this.lastanode = lastanode;
	}
	
	/**
	 *  Get the lastnode.
	 *  @return The lastnode.
	 */
	public INode getLastBetaNode()
	{
		return lastbnode;
	}

	/**
	 *  Set the lastnode.
	 *  @param lastnode The lastnode to set.
	 */
	public void setLastBetaNode(INode lastbnode)
	{
		this.lastbnode = lastbnode;
	}

	/**
	 *  Get the tuple count.
	 *  @return The tuplecnt.
	 */
	public int getTupleCount()
	{
		return tuplecnt;
	}

	/**
	 *  Set the tuple count.
	 *  @param tuplecnt The tuplecnt to set.
	 */
	public void setTupleCount(int tuplecnt)
	{
		this.tuplecnt = tuplecnt;
	}
	
	/**
	 *  Add a new var info.
	 */
	public void addVarInfo(VarInfo vi)
	{
		assert !varinfos.containsKey(vi.getVariable());
		varinfos.put(vi.getVariable(), vi);
	}
	
	/**
	 *  Get the variable info.
	 *  @param var The variable.
	 *  @return The variable info.
	 */
	public VarInfo getVarInfo(Variable var)
	{
		return (VarInfo)varinfos.get(var);
	}
	
	/**
	 *  Get the variable infos.
	 *  @return The variable infos.
	 */
	public Map getVarInfos()
	{
		return varinfos;
	}
	
	/**
	 *  Test if a found variable is joinable (beta node),
	 *  i.e. if it was formerly used in another condition.
	 *  @param var The variable.
	 *  @return True, if joinable.
	 */
	public boolean isJoinable(Variable var)
	{
		VarInfo vi = getVarInfo(var);
		return vi!=null && vi.getTupleIndex()!=getTupleCount();
	}
	
	/**
	 *  Test if a variable is constrainable (alpha node),
	 *  i.e. was formerly used in this condition.
	 *  @param var The variable.
	 *  @return True, if constrainable.
	 */
	public boolean isConstrainable(Variable var)
	{
		VarInfo vi = getVarInfo(var);
		return vi!=null && vi.getTupleIndex()==getTupleCount();
	}
	
	/**
	 *  Test if a variable is left available.
	 *  @param var The variable.
	 *  @return True, if left available.
	 */
	public boolean isLeftAvailable(Variable var)
	{
		VarInfo vi = (VarInfo)varinfos.get(var);
		return vi!=null && (isRightUnavailable() || vi.getTupleIndex()!=getTupleCount());
	}

	/**
	 *  Get the rightunavailable.
	 *  @return The rightunavailable.
	 */
	public boolean isRightUnavailable()
	{
		return rightunavailable;
	}

	/**
	 *  Set the rightunavailable.
	 *  @param rightunavailable The rightunavailable to set.
	 */
	public void setRightUnavailable(boolean rightunavailable)
	{
		this.rightunavailable = rightunavailable;
	}

	/**
	 *  For each object condition, the builder first
	 *  creates alpha nodes for all constraints that only
	 *  apply to the object itself.
	 *  Afterwards, a beta node is created, containing the
	 *  additional (join) constraints.   
	 *  @return The alpha flag.
	 */
	public boolean isAlpha()
	{
		return alpha;
	}

	/**
	 *  Set the alpha flag.
	 *  @see #isAlpha()
	 */
	public void setAlpha(boolean alpha)
	{
		this.alpha = alpha;
	}
}
