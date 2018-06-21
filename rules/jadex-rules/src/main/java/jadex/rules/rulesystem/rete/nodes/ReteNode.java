package jadex.rules.rulesystem.rete.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.rete.builder.ReteBuilder;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;

/**
 *  ReteNode implementation of the IConditionSystem.
 */
public class ReteNode extends AbstractNode implements IObjectSourceNode
{
	//-------- attributes --------
	
	/** The type nodes. */
	protected Map typenodes;
	
	/** Matching nodes for each (sub)type (cached for speed). */
	protected Map typenodesets;
	
	/** Indirectly affected nodes for an attribute type (cached for speed). */
	protected volatile Map indirectnodesets;
	
	/** The initial fact node (if any). */
	protected InitialFactNode	initialfact;
	
	/** The terminal nodes (IRule -> Node). */
	protected Map terminalnodes;
	
	/** The rete builder. */
	protected ReteBuilder builder;
	
	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;
	
	/** Do a consistency check after each state change (requires asserts). */
	protected boolean	check;
	
	/** The node counter in this network. */
	protected int nodecounter;
	
	/** For debugging: node is inited and network must not be changed anymore. */
	protected boolean inited;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rete system.
	 *  @param state The state.
	 */
	public ReteNode()
	{
		super(0);
		this.nodecounter = 1;
		this.typenodes = new LinkedHashMap();		
		this.terminalnodes = new LinkedHashMap();

		// The typenode mapping  for each object type is dynamically created on first access. hack???
		this.typenodesets = Collections.synchronizedMap(new LinkedHashMap());
	}

	//-------- methods --------

	/**
	 *  Tell the condition system about a
	 *  new object in the state.
	 *  @param object The new object.
	 */
	public void addObject(Object id, OAVObjectType type, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(type.getName().equals("goal"))
//			System.out.println("Value added: "+id+" "+type);
//		System.out.println("Value added: "+id+" "+type);
		
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		
		Set	tns	= getTypeNodes(type);
		
		if(tns!=null)
		{
			for(Iterator it=tns.iterator(); it.hasNext(); )
				((AlphaNode)it.next()).addObject(id, state, mem, agenda);
			
			assert !check || checkConsistency(mem);
		}
//		else
//			System.out.println("No typenode(s) available for: "+type);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
		
	/**
	 *  Tell the condition system about a
	 *  removed object in the state.
	 *  @param object The removed object.
	 */
	public void removeObject(Object id, OAVObjectType type, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(type.getName().equals("goal"))
//			System.out.println("Value removed: "+id+" "+type);
//		if(type instanceof OAVJavaType && ((OAVJavaType)type).getClazz().getName().indexOf("Wastebin")!=-1)
//		{
//			System.out.println("removedRETE: "+id);
//		}
		
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);

		Set	tns	= getTypeNodes(type);
		
		if(tns!=null)
		{
			for(Iterator it=tns.iterator(); it.hasNext(); )
				((AlphaNode)it.next()).removeObject(id, state, mem, agenda);
			
			assert !check || checkConsistency(mem);
		}
//		else
//			System.out.println("No typenode(s) available for: "+type);
		
		//assert !mem.contains(id);
//		System.out.println("Value removed: "+id+" "+type);

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Tell the condition system about a
	 *  modified object in the state.
	 *  @param object The new object.
	 */
	public void modifyObject(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
//		if(type.getName().equals("goal"))
//			System.out.println("Value set: "+id+" "+type+" "+attr+" "+newvalue);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);
		
//		if(attr.getName().indexOf("daytime")!=-1)
//			System.out.println("test");
		
		if(getRelevantAttributes().contains(attr))
		{
//			if(type.getName().equals("goal"))
//				System.out.println("Value set: "+id+" "+type+" "+attr+" "+newvalue+" relevant!");
			Set	tns	= getTypeNodes(type);
			
			if(tns!=null && !tns.isEmpty())
			{
				for(Iterator it=tns.iterator(); it.hasNext(); )
				{
					TypeNode	tn	= (TypeNode)it.next();
					tn.modifyObject(id, attr, oldvalue, newvalue, state, mem, agenda);
					// old code for modified object
					//tn.removeObject(id, state, mem, agenda);
					//tn.addObject(id, state, mem, agenda);
				}
			
				assert !check || checkConsistency(mem);
			}
			//else
//				System.out.println("No typenode(s) available for: "+value);
		}
		
		Set	ins	= getIndirectNodes(attr, state.getTypeModel());
		if(ins!=null)
		{
			for(Iterator it=ins.iterator(); it.hasNext(); )
			{
				((INode)it.next()).modifyIndirectObject(id, attr, oldvalue, newvalue, state, mem, agenda);
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
		
	/**
	 *  Propagate an indirect object change to this node.
	 *  @param object The changed object.
	 */
	public void modifyIndirectObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		// Should never be called.
		throw new UnsupportedOperationException("Unsupported method.");
	}

	/**
	 *  Add a rule to the network.
	 *  @param rule The rule to add.
	 */
	public void addRule(IRule rule)
	{
		if(builder==null)
			builder = new ReteBuilder();
		builder.addRule(this, rule);
	}
	
	/**
	 *  Remove a rule from the network.
	 *  @param rule The rule to remove.
	 */
	public void removeRule(IRule rule)
	{
		if(builder==null)
			builder = new ReteBuilder();
		builder.removeRule(this, rule);
	}
	
	/**
	 *  Set the terminal node for a rule.
	 *  @param rule The rule.
	 *  @param node The node.
	 */
	public void putTerminalNode(TerminalNode node)
	{
		terminalnodes.put(node.getRule(), node);
	}
	
	/**
	 *  Set the terminal node for a rule.
	 *  @param rule The rule.
	 *  @param node The node.
	 */
	public TerminalNode getTerminalNode(IRule rule)
	{
		return (TerminalNode)terminalnodes.get(rule);
	}
	
	/**
	 *  Get the number of nodes in the network.
	 *  @return The number of nodes.
	 */
	public int getNodeCount()
	{
		List ret = new ArrayList();
		ret.add(this);
		
		for(int i=0; i<ret.size(); i++)
		{
			INode node = (INode)ret.get(i);
			if(node instanceof IObjectSourceNode)
			{
				IObjectConsumerNode[] consumers = ((IObjectSourceNode)node).getObjectConsumers();
				for(int j=0; j<consumers.length; j++)
				{
					if(!ret.contains(consumers[j]))
						ret.add(consumers[j]);
				}
			}
			if(node instanceof ITupleSourceNode)
			{
				ITupleConsumerNode[] consumers = ((ITupleSourceNode)node).getTupleConsumers();
				for(int j=0; j<consumers.length; j++)
				{
					if(!ret.contains(consumers[j]))
						ret.add(consumers[j]);
				}
			}
		}
		
		return ret.size();
	}
	
	//-------- object source node --------
	
	/**
	 *  Add an object consumer node.
	 *  @param node A new consumer node.
	 */
	public void addObjectConsumer(IObjectConsumerNode node)
	{
		assert !inited;
		
		if(node instanceof TypeNode)
		{
			if(typenodes.put(((TypeNode)node).getObjectType(), node)!=null)
				throw new RuntimeException("Type node already present in network: "+node);
		}
		else if(node instanceof InitialFactNode)
		{
			if(initialfact!=null)
				throw new RuntimeException("Initial fact node already present in network: "+node);
			initialfact	= (InitialFactNode)node;
		}
		else
		{
			throw new RuntimeException("Rete node only allows type or initial fact node children: "+node);
		}
		
		relevants	= null;	// Will be recalculated on next access;
	}
	
	/**
	 *  Remove an object consumer.
	 *  @param node The consumer node.
	 */
	public void removeObjectConsumer(IObjectConsumerNode node)
	{
		assert !inited;
		
		typenodes.remove(((TypeNode)node).getObjectType());
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		return mem.hasNodeMemory(this) ? (Collection)mem.getNodeMemory(this) : null;
	}
	
	/**
	 *  Get all object consumer nodes.
	 *  @return All object consumer nodes.
	 */
	public IObjectConsumerNode[] getObjectConsumers()
	{
		Collection	vals	= typenodes.values();
		IObjectConsumerNode[]	ret	= (IObjectConsumerNode[])vals.toArray(new IObjectConsumerNode[vals.size()+(initialfact!=null?1:0)]);
		if(initialfact!=null)
			ret[ret.length-1]	= initialfact;
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the node for a type.
	 *  @param type The type.
	 *  @return The type node (if any).
	 */
	public TypeNode getTypeNode(OAVObjectType type)
	{
		return (TypeNode)typenodes.get(type);
	}
	
	/**
	 *  Get the initial fact node (if any).
	 */
	public InitialFactNode getInitialFactNode()
	{
		return initialfact;
	}

	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return null;
	}

	/**
	 *  Get the set of relevant attribute types.
	 */
	public AttributeSet	getRelevantAttributes()
	{
		assert inited;
		if(relevants==null)
		{
			synchronized(this) 
			{
				if(relevants==null)
				{
					AttributeSet	relevants	= new AttributeSet();
					for(Iterator it=typenodes.values().iterator(); it.hasNext(); )
					{
						relevants.addAll(((INode)it.next()).getRelevantAttributes());
					}
					this.relevants	= relevants;
				}
			}
		}
		return relevants;
	}
	
	/**
	 *  Get the set of indirect attribute types.
	 *  I.e. attributes of objects, which are not part of an object conditions
	 *  (e.g. for chained extractors) 
	 *  @return The relevant attribute types.
	 */
	public AttributeSet	getIndirectAttributes()
	{
		return AttributeSet.EMPTY_ATTRIBUTESET;
	}

	/**
	 *  Get the builder.
	 *  @return The rete builder.
	 */
	public ReteBuilder getBuilder()
	{
		return builder;
	}

	//-------- helper methods --------
	
	/**
	 *  Get the set of matching type nodes for a (sub)type.
	 *  @param type The object type.
	 *  @return The set of type nodes for that object type.
	 */
	protected Set	getTypeNodes(OAVObjectType type)
	{
		Set	ret	= (Set)typenodesets.get(type);
		if(ret==null)
		{
			synchronized(this)
			{
				ret	= (Set)typenodesets.get(type);
				if(ret==null)
				{
					ret	= new HashSet();
					for(Iterator it=typenodes.values().iterator(); it.hasNext(); )
					{
						TypeNode	tnode	= (TypeNode)it.next();
						if(type.isSubtype(tnode.getObjectType()))
							ret.add(tnode);
					}
					typenodesets.put(type, ret);
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the set of indirectly affected nodes for an attribute type.
	 *  @param attrtype The attribute type.
	 *  @param tmodel The OAV type model.
	 *  @return The set of indirectly affected nodes for that attribute type.
	 */
	protected Set getIndirectNodes(OAVAttributeType attrtype, OAVTypeModel tmodel)
	{
		assert inited;
		if(indirectnodesets==null)
		{
			synchronized(this)
			{
				if(indirectnodesets==null)
				{
					HashMap	indirectnodesets = new HashMap();
					List nodelist	= new ArrayList();
					List nodeset	= new ArrayList();
					nodelist.addAll(typenodes.values());
					nodeset.addAll(nodelist);
					
					for(int i=0; i<nodelist.size(); i++)
					{
						INode node = (INode)nodelist.get(i);
						AttributeSet attrset = node.getIndirectAttributes();
						if(attrset.getAttributeSet()!=null)
						{
							for(Iterator it=attrset.getAttributeSet().iterator(); it.hasNext(); )
							{
								Object	attr	= it.next();
								Set	indinodes	= (Set)indirectnodesets.get(attr);
								if(indinodes==null)
								{
									indinodes	= new HashSet();
									indirectnodesets.put(attr, indinodes);
								}
								indinodes.add(node);
							}
						}
						if(attrset.getAllTypesSet()!=null)
						{
							for(Iterator it=attrset.getAllTypesSet().iterator(); it.hasNext(); )
							{
								Object objtype	= it.next();
								Set	indinodes	= (Set)indirectnodesets.get(objtype);
								if(indinodes==null)
								{
									indinodes	= new HashSet();
									indirectnodesets.put(objtype, indinodes);
								}
								indinodes.add(node);
							}
						}
						
						if(node instanceof IObjectSourceNode)
						{
							INode[]	subs	= ((IObjectSourceNode)node).getObjectConsumers();
							for(int n=0; subs!=null && n<subs.length; n++)
							{
								if(!nodeset.contains(subs[n]))
								{
									nodelist.add(subs[n]);
									nodeset.add(subs[n]);
								}
							}
						}
						if(node instanceof ITupleSourceNode)
						{
							INode[]	subs	= ((ITupleSourceNode)node).getTupleConsumers();
							for(int n=0; subs!=null && n<subs.length; n++)
							{
								if(!nodeset.contains(subs[n]))
								{
									nodelist.add(subs[n]);
									nodeset.add(subs[n]);
								}
							}
						}
					}
					this.indirectnodesets	= indirectnodesets;
				}
			}
		}
		
		Set tmp1 = (Set)indirectnodesets.get(attrtype);
		Set tmp2 = (Set)indirectnodesets.get(attrtype.getObjectType());

		Set ret = tmp1;
		if(tmp2!=null)
		{
			if(ret!=null)
				ret.addAll(tmp2);
			else
				ret = tmp2;
		}

		if(attrtype.getObjectType() instanceof OAVJavaType)
		{
			List	classes	= new ArrayList();
			Set	sclasses	= new HashSet();
			classes.add(((OAVJavaType)attrtype.getObjectType()).getClazz());
			for(int i=0; i<classes.size(); i++)
			{
				Class	clazz	= (Class)classes.get(i);
				if(clazz.getSuperclass()!=null && !sclasses.contains(clazz.getSuperclass()))
				{
					classes.add(clazz.getSuperclass());
					sclasses.add(clazz.getSuperclass());
				}
				Class[]	ifs	= clazz.getInterfaces();
				for(int j=0; j<ifs.length; j++)
				{
					if(!sclasses.contains(ifs[j]))
					{
						classes.add(ifs[j]);
						sclasses.add(ifs[j]);
					}
				}
			}
			
			for(Iterator it=sclasses.iterator(); it.hasNext(); )
			{
				Class	clazz	= (Class)it.next();
				tmp2	= (Set)indirectnodesets.get(tmodel.getJavaType(clazz));
				if(tmp2!=null)
				{
					if(ret!=null)
						ret.addAll(tmp2);
					else
						ret = tmp2;
				}
			}
		}
				
		return ret;
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  @param clone The clone.
	 */
	protected void doClone(Object theclone)
	{
		ReteNode clone = (ReteNode)theclone;
		
		// Deep clone type nodes
		clone.typenodes = new LinkedHashMap();
		for(Iterator it=typenodes.keySet().iterator(); it.hasNext(); )
		{
			OAVObjectType type = (OAVObjectType)it.next();
			clone.typenodes.put(type, getTypeNode(type).clone());
		}
		
		// Update typenodesets
		clone.typenodesets = new LinkedHashMap();
		for(Iterator it=typenodesets.keySet().iterator(); it.hasNext(); )
		{
			OAVObjectType type = (OAVObjectType)it.next();
			Set oldtns = getTypeNodes(type);
			if(oldtns!=null)
			{
				Set newtns = new HashSet();
				for(Iterator it2=oldtns.iterator(); it.hasNext(); )
				{
					INode oldtn = (INode)it2.next();
					newtns.add(oldtn.clone());
				}
				clone.typenodesets.put(type, newtns);
			}
		}
			
		// Deep clone initial fact node
		if(initialfact!=null)
			clone.initialfact = (InitialFactNode)initialfact.clone();
	
		// Refresh terminal nodes
		// Searches the whole net and if a terminal node is found
		// it is added to the terminalnodes map.
		clone.terminalnodes = new HashMap();
		List nodes = new ArrayList();
		nodes.addAll(clone.typenodes.values());
		for(int i=0; i<nodes.size(); i++)
		{
			Object node = nodes.get(i);
			if(node instanceof IObjectSourceNode)
			{
				IObjectSourceNode osn = (IObjectSourceNode)node;
				IObjectConsumerNode[] cons = osn.getObjectConsumers();
				for(int j=0; j<cons.length; j++)
				{
					if(!nodes.contains(cons[j]))
						nodes.add(cons[j]);
				}
			}
			if(node instanceof ITupleSourceNode)
			{
				ITupleSourceNode tsn = (ITupleSourceNode)node;
				ITupleConsumerNode[] cons = tsn.getTupleConsumers();
				for(int j=0; j<cons.length; j++)
				{
					if(!nodes.contains(cons[j]))
						nodes.add(cons[j]);
				}
			}
			
			if(node instanceof TerminalNode)
				clone.putTerminalNode((TerminalNode)((TerminalNode)node).clone());
		}
		
		// Keep same stateless rete builder
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			clone.relevants = (AttributeSet)((AttributeSet)relevants).clone();
	}
	
	//-------- checking --------
	
	protected int	changecnt;
	protected List	checked	= new ArrayList();
	
	/**
	 *  Check consistency of Rete network/memory.
	 *  For debugging. Only performs some simple
	 *  checks and does not assure complete consistency.
	 */
	protected boolean	checkConsistency(ReteMemory mem)
	{
		boolean	consistent	= true;
		
		// Iterate through all node.
		List	nodes	= new ArrayList();
		nodes.add(this);
		while(consistent && !nodes.isEmpty())
		{
			INode	node	= (INode)nodes.remove(nodes.size()-1);
			if(node instanceof ITupleSourceNode)
			{
				INode[]	subnodes	= ((ITupleSourceNode)node).getTupleConsumers();
				for(int i=0; i<subnodes.length; i++)
					nodes.add(subnodes[i]);
			}
			if(node instanceof IObjectSourceNode)
			{
				INode[]	subnodes	= ((IObjectSourceNode)node).getObjectConsumers();
				for(int i=0; i<subnodes.length; i++)
					nodes.add(subnodes[i]);
			}

			node.checkNodeConsistency(mem);
		}
		
		changecnt++;
		checked.clear();
		
		return consistent;
	}
	
	/**
	 *  Get the next nodecounter.
	 *  @return The id for the next node.
	 */
	public int getNextNodeId()
	{
		return nodecounter++;
	}
	
	//-------- debugging --------
	
	/**
	 *  Set the inited state.
	 */
	public void	setInited(boolean inited)
	{
		this.inited	= inited;
	}
}
