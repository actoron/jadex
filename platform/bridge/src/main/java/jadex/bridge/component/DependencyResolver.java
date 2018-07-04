package jadex.bridge.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.transformation.traverser.SCloner;

/**
 *  The dependency resolver can be used to find a valid
 *  execution order of elements with dependencies.
 */
public class DependencyResolver<T> 
{
	/** The nodes with dependencies. */
	protected Map<T, NodeInfo<T>> nodes;
	
	/** The set of empty nodes. */
	protected Set<T> nodeps;
	
	/**
	 *  Create a new dependency resolver.
	 */
	public DependencyResolver() 
	{
		this.nodes = new LinkedHashMap<T, NodeInfo<T>>();
		this.nodeps = new LinkedHashSet<T>();
	}
	
	/**
	 *  Add a dependency that a depends on b.
	 *  @param a Then source node.
	 *  @param b The node the source depends on.
	 */
	public void addDependency(T a, T b)
	{
		if(a==null || b==null)
			throw new IllegalArgumentException("Object must not null.");
		
		NodeInfo<T> nia = getNodeInfo(a);
		nia.getMyDeps().add(b);
		NodeInfo<T> nib = getNodeInfo(b);
		nib.getOtherDeps().add(a);
		
		Set<T> t = new HashSet<T>(nia.getMyDeps());
		t.retainAll(nia.getOtherDeps());
		if(t.size()>0)
			throw new RuntimeException("error cycle detected: "+nia);
		
		t = new HashSet<T>(nib.getMyDeps());
		t.retainAll(nib.getOtherDeps());
		if(t.size()>0)
			throw new RuntimeException("error cycle detected: "+nib);
		
		// Update nodeps
		nodeps.remove(a);
		if(!hasDependencies(b))
		{
			nodeps.add(b);
		}
	}
	
	/**
	 *  Remove a dependency that a depends on b.
	 *  @param a Then source node.
	 *  @param b The node the source depends on.
	 */
	public void removeDependency(T a, T b)
	{
		NodeInfo<T> nia = getNodeInfo(a);
		if(!nia.getMyDeps().remove(b))
			throw new RuntimeException("Cannot remove dependency");
		NodeInfo<T> nib = getNodeInfo(b);
		if(!nib.getOtherDeps().remove(a))
			throw new RuntimeException("Cannot remove dependency");
		
		// Update nodeps
		if(!hasDependencies(a))
		{
			nodeps.add(a);
		}
	}
	
	/**
	 *  Resolve the DAG and deliver a valid order of nodes.
	 *  @return A valid list of nodes.
	 */
	public List<T> resolveDependencies(boolean keep)
	{
		List<T> ret = new ArrayList<T>();
		
		DependencyResolver<T> dr2 = !keep? null: (DependencyResolver<T>)SCloner.clone(this);
//		DependencyResolver<T> dr2 = !keep? null: (DependencyResolver<T>)Traverser.traverseObject(this, null, Traverser.getDefaultProcessors(), null, true, null);
		
		while(!nodes.isEmpty())
		{
			if(nodeps.size()==0)
				throw new RuntimeException("Dependency resolution problem.");
			T node = nodeps.iterator().next();
			ret.add(node);
			
			NodeInfo<T> nia = getNodeInfo(node);
			for(T dep: (T[])nia.getOtherDeps().toArray(new Object[0]))
			{
				removeDependency(dep, node);
			}
			nodeps.remove(node);
			nodes.remove(node);
		}
		
		// reset
		if(keep)
		{
			nodes = dr2.getNodes();
			nodeps = dr2.getNodeps();
		}
		
//		System.out.println("Resolved: "+ret);
		
		return ret;
	}
	
	/**
	 *  Add a node (without dependency). 
	 *  @param node The node id.
	 */
	public void addNode(T node)
	{
		getNodeInfo(node);
		
		// add to nodeps
		if(!hasDependencies(node))
		{
			nodeps.add(node);
		}
	}
	
	/**
	 *  Clear the resolver.
	 */
	public void clear()
	{
		this.nodes.clear();
		this.nodeps.clear();
	}
	
	/**
	 *  Get the node info for the node id.
	 *  @param node The node id.
	 *  @return The node info.
	 */
	protected NodeInfo<T> getNodeInfo(T node)
	{
		NodeInfo<T> ni = nodes.get(node);
		if(ni==null)
		{
			ni = new NodeInfo<T>();
			nodes.put(node, ni);
		}
		return ni;
	}
	
	/**
	 *  Test if a node has dependencies.
	 *  @return True, if dependencies exist.
	 */
	protected boolean hasDependencies(T node)
	{
		return nodes.containsKey(node) && nodes.get(node).getMyDeps().size()>0;
	}
	
	/**
	 *  Get the nodes.
	 *  @return The nodes
	 */
	public Map<T, NodeInfo<T>> getNodes()
	{
		return nodes;
	}
	
	/**
	 *  The nodes to set.
	 *  @param nodes The nodes to set
	 */
	public void setNodes(Map<T, NodeInfo<T>> nodes)
	{
		this.nodes = nodes;
	}

	/**
	 *  Get the nodeps.
	 *  @return The nodeps
	 */
	public Set<T> getNodeps()
	{
		return nodeps;
	}

	/**
	 *  The nodeps to set.
	 *  @param nodeps The nodeps to set
	 */
	public void setNodeps(Set<T> nodeps)
	{
		this.nodeps = nodeps;
	}

	/**
	 *  The main for testing. 
	 */
	public static void main(String[] args) 
	{
		DependencyResolver<String> dr = new DependencyResolver<String>();
//		dr.addDependency("a", "b");
//		dr.addDependency("b", "c");
//		dr.addDependency("c", "d");
//		System.out.println(dr.resolveDependencies());

		dr.clear();
		
		dr.addDependency("c", "a");
		dr.addDependency("d", "b");
		dr.addDependency("e", "c");
		dr.addDependency("e", "d");
		dr.addDependency("f", "a");
		dr.addDependency("f", "b");
		dr.addDependency("g", "e");
		dr.addDependency("g", "f");
		dr.addDependency("h", "g");
		dr.addDependency("i", "a");
		dr.addDependency("j", "b");
		System.out.println(dr.resolveDependencies(true));
	}
	
	
	/**
	 *  Info object for a node.
	 */
	public static class NodeInfo<T>
	{
		/** The set of nodes this node depends on. */
		protected Set<T> mydeps;
		
		/** The set of nodes depending on this node. */
		protected Set<T> otherdeps;

		/**
		 *  Create a node info.
		 */
		public NodeInfo() 
		{
			this.mydeps = new HashSet<T>();
			this.otherdeps = new HashSet<T>();
		}

		/**
		 *  Get the mydeps.
		 *  @return The mydeps.
		 */
		public Set<T> getMyDeps() 
		{
			return mydeps;
		}

		/**
		 *  Set the mydeps to set.
		 *  @param mydeps The mydeps to set.
		 */
		public void setMyDeps(Set<T> mydeps) 
		{
			this.mydeps = mydeps;
		}

		/**
		 *  Get the otherdeps.
		 *  @return The otherdeps.
		 */
		public Set<T> getOtherDeps() 
		{
			return otherdeps;
		}

		/**
		 *  Set the otherdeps to set.
		 *  @param otherdeps The otherdeps to set.
		 */
		public void setOtherDeps(Set<T> otherdeps) 
		{
			this.otherdeps = otherdeps;
		}
		
		/**
		 *  Get a string representation.
		 */
		public String toString()
		{
			return "pre: "+getMyDeps()+", post: "+getOtherDeps();
		}
	}
}
