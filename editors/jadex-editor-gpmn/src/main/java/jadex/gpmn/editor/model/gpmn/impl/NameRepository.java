package jadex.gpmn.editor.model.gpmn.impl;

import java.util.HashSet;
import java.util.Set;

import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IPlan;

/**
 *  Repository for names currently in use.
 *
 */
public class NameRepository
{
	/** Names of goals. */
	protected Set<String> goalnames;
	
	/** Names of plans. */
	protected Set<String> plannames;
	
	/** Names of edges. */
	protected Set<String> edgenames;
	
	/**
	 *  Creates a new name repository.
	 */
	public NameRepository()
	{
		goalnames = new HashSet<String>();
		plannames = new HashSet<String>();
		edgenames = new HashSet<String>();
	}
	
	/**
	 *  Creates a unique name for a model element.
	 *  
	 *  @param prefix Name prefix.
	 * 	@param element The target element.
	 *  
	 *  @return The unique name.
	 */
	public String createUniqueName(String prefix, IElement element)
	{
		prefix = prefix.trim();
		Set<String> usednames = null;
		if (element instanceof IGoal)
			usednames = goalnames;
		else if (element instanceof IPlan)
			usednames = plannames;
		else if (element instanceof IEdge)
			usednames = edgenames;
		
		int count = 0;
		String name = prefix;
		
		while (usednames.contains(name))
		{
			name = prefix + "_" + count++;
		}
		
		usednames.add(name);
		
		return name;
	}
	
	/**
	 *  Deletes a unique name of a model element.
	 *  
	 * 	@param element The target element.
	 */
	public void deleteUniqueName(IElement element)
	{
		Set<String> usednames = null;
		if (element instanceof IGoal)
			usednames = goalnames;
		else if (element instanceof IPlan)
			usednames = plannames;
		else if (element instanceof IEdge)
			usednames = edgenames;
		
		if (usednames != null)
		{
			usednames.remove(element.getName());
		}
	}
}
