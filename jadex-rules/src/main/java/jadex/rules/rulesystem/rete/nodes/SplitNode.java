package jadex.rules.rulesystem.rete.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.SReflect;
import jadex.rules.rulesystem.AbstractAgenda;
import jadex.rules.rulesystem.rete.extractors.AttributeSet;
import jadex.rules.rulesystem.rete.extractors.IValueExtractor;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IProfiler;
import jadex.rules.state.OAVAttributeType;

/**
 *  A split node has the purpose of generating virtual facts
 *  for multislot bindings that use a non-multi variable or
 *  variable patterns.
 */
public class SplitNode extends AbstractNode implements IObjectConsumerNode, IObjectSourceNode
{
	//-------- attributes --------
	
	/** The constant for a multi variable. */
	public static final String MULTI = "multi";
	
	/** The constant for a single variable. */
	public static final String SINGLE = "single";

	/** The constant for a dummy multi variable. */
	public static final String MULTI_DUMMY = "multi_dummy";
	
	//-------- attributes --------
	
	/** The object source. */
	protected IObjectSourceNode osource;
	
	/** The object consumers. */
	protected IObjectConsumerNode[]	oconsumers;

	/** The set of relevant attributes. */
	protected volatile AttributeSet relevants;
	
	/** The set of indirect attributes. */
	protected volatile AttributeSet indirects;

	/** The values extractor. */
	// Needed as long as multifield extractor is based on attribute
	protected OAVAttributeType attr;
	
	/** The values extractor. */
	protected IValueExtractor extractor;
	
	/** The splitpattern (multi, single or multi dummy). */
	protected String[] splitpattern;
	
	/** The minimum number of required values. */
	protected int min_values;

	//-------- constructors --------
	
	/**
	 *  Create a new node.
	 *  @param state The state.
	 */
	public SplitNode(int nodeid, IValueExtractor extractor, OAVAttributeType attr, String[] splitpattern)
	{
		super(nodeid);
		
		assert extractor!=null;
		assert attr!=null;
		assert !OAVAttributeType.NONE.equals(attr.getMultiplicity());
		assert splitpattern.length>0;
			
		this.extractor = extractor;
		this.attr = attr;
		
		this.min_values = 0;
		for(int i=0; i<splitpattern.length; i++)
			if(splitpattern[i].equals(SINGLE))
				min_values++;
		
		// Are there no multi values?
		if(min_values==splitpattern.length)
		{
			// Make first and last dummy multi
			this.splitpattern = new String[splitpattern.length+2];
			System.arraycopy(splitpattern, 0, this.splitpattern, 1, splitpattern.length);
			this.splitpattern[0] = MULTI_DUMMY;
			this.splitpattern[this.splitpattern.length-1] = MULTI_DUMMY;
		}
		else
		{
			this.splitpattern = splitpattern;
		}
	}

	//-------- object consumer interface --------
	
	/**
	 *  Send a new object to this node.
	 *  @param object The object.
	 */
	public void addObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Add object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		
		Map smem = (Map)mem.getNodeMemory(this);
		assert !smem.containsKey(object) : object;
//		if(!smem.containsKey(object))
		{
			Collection vfs = generateVirtualFacts(object, state);
			smem.put(object, vfs);
				
			//System.out.println("ADD: Object splitted to: "+object+" "+vfs);
			for(Iterator it=vfs.iterator(); it.hasNext(); )
			{
				propagateAdditionToObjectConsumers(it.next(), state, mem, agenda);
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTADDED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Send a removed object to this node.
	 *  @param object The object.
	 */
	public void removeObject(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		//System.out.println("Remove object called: "+this+" "+object);
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		
		assert mem.hasNodeMemory(this);
//		if(mem.hasNodeMemory(this))
		{
			Map smem = (Map)mem.getNodeMemory(this);
			Collection vfs = (Collection)smem.remove(object);
		
			if(vfs!=null)
			{
				//System.out.println("REM: Object splitted to: "+object+" "+vfs);
				for(Iterator it=vfs.iterator(); it.hasNext(); )
				{
					propagateRemovalToObjectConsumers(it.next(), state, mem, agenda);
				}
			}
		}

		state.getProfiler().stop(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTREMOVED);
		state.getProfiler().stop(IProfiler.TYPE_NODE, this);
	}
	
	/**
	 *  Propagate an object change to this node.
	 *  @param object The new object.
	 */
	public void modifyObject(Object object, OAVAttributeType type, Object oldvalue, Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		state.getProfiler().start(IProfiler.TYPE_NODE, this);
		state.getProfiler().start(IProfiler.TYPE_NODEEVENT, IProfiler.NODEEVENT_OBJECTMODIFIED);

		if(getRelevantAttributes().contains(type))
		{
			// Check if modification changes node memory.
			boolean affected = isAffected(type);
			Collection before = mem.hasNodeMemory(this)	? (Collection)((Map)mem.getNodeMemory(this)).get(object) : null;
		
			if(affected)
			{
				Collection after = generateVirtualFacts(object, state);
				//System.out.println("MOD: Object splitted to: "+object+" "+before+" "+after);
				
//				if(before==null)
//					System.out.println("a");
				//if(before!=null)
				{
					for(Iterator it=before.iterator(); it.hasNext(); )
					{
						Object o = it.next();
						
						// Remove a fact that was contained and is not anymore
						if(!contains(state, after, o))
						{
							it.remove();
							propagateRemovalToObjectConsumers(o, state, mem, agenda);
						}
					}
				}
				
				//if(after!=null)
				{
					for(Iterator it=after.iterator(); it.hasNext(); )
					{
						Object o = it.next();
//						if(o instanceof List)
//							System.out.println("shit");
						
						// Add a fact that was not contained is now
						if(!before.contains(o))
						{
							before.add(o);
							propagateAdditionToObjectConsumers(o, state, mem, agenda);
						}
					}
				}
			}
			else if(before!=null)
			{
				for(Iterator it=before.iterator(); it.hasNext(); )
				{
					propagateModificationToObjectConsumers(it.next(), type, oldvalue, newvalue, state, mem, agenda);
				}
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
		throw new UnsupportedOperationException("Unsupported method.");
	}

	/**
	 *  Set the object source of this node.
	 *  @param node The object source node.
	 */
	public void setObjectSource(IObjectSourceNode node)
	{
		this.osource = node;
	}
	
	/**
	 *  Get the object source of this node.
	 *  @return The object source node.
	 */
	public IObjectSourceNode getObjectSource()
	{
		return osource;
	}
	
	//-------- object source interface --------
		
	/**
	 *  Add an object consumer node.
	 *  @param node A new consumer node.
	 */
	public void addObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers==null)
		{
			oconsumers = new IObjectConsumerNode[]{node};
		}
		else
		{
			IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length+1];
			System.arraycopy(oconsumers, 0, tmp, 0, oconsumers.length);
			tmp[oconsumers.length]	= node;
			oconsumers	= tmp;
		}

		relevants	= null;	// Will be recalculated on next access;
	}
	
	/**
	 *  Remove an object consumer.
	 *  @param node The consumer node.
	 */
	public void removeObjectConsumer(IObjectConsumerNode node)
	{
		if(oconsumers!=null)
		{
			for(int i=0; i<oconsumers.length; i++)
			{
				if(oconsumers[i].equals(node))
				{
					if(oconsumers.length==1)
					{
						oconsumers	= null;
					}
					else
					{
						IObjectConsumerNode[]	tmp	= new IObjectConsumerNode[oconsumers.length-1];
						if(i>0)
							System.arraycopy(oconsumers, 0, tmp, 0, i);
						if(i<oconsumers.length-1)
							System.arraycopy(oconsumers, i+1, tmp, i, oconsumers.length-1-i);
						oconsumers	= tmp;
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Get the memory for this node.
	 *  @return The memory.
	 */
	public Collection getNodeMemory(ReteMemory mem)
	{
		Collection ret = null;
		if(mem.hasNodeMemory(this))
		{
			// todo: can storage be optimized?! -> flattened multi-collection
			ret = new ArrayList();
			for(Iterator it=((Map)mem.getNodeMemory(this)).values().iterator(); it.hasNext(); )
				ret.addAll((Collection)it.next());
		}
		return ret;
	}

	/**
	 *  Get all object consumer nodes.
	 *  @return All object consumer nodes.
	 */
	public IObjectConsumerNode[] getObjectConsumers()
	{
		return oconsumers;
	}	

	//-------- methods --------
	
	/**
	 *  Create the node memory.
	 *  @param state	The state.
	 *  @return The node memory.
	 */
	public Object createNodeMemory(IOAVState state)
	{
		return new LinkedHashMap();
	}
	
	//-------- helper methods --------

	/**
	 *  Propagate a new object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateAdditionToObjectConsumers(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].addObject(object, state, mem, agenda);
	}
	
	/**
	 *  Propagate a removed object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateRemovalToObjectConsumers(Object object, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].removeObject(object, state, mem, agenda);
	}
	
	/**
	 *  Propagate a modified object to all object consumers.
	 *  @param object The new object.
	 */
	protected void propagateModificationToObjectConsumers(Object object, OAVAttributeType type, Object oldvalue, 
		Object newvalue, IOAVState state, ReteMemory mem, AbstractAgenda agenda)
	{
		IObjectConsumerNode[]	ocon	= oconsumers;
		for(int i=0; ocon!=null && i<ocon.length; i++)
			ocon[i].modifyObject(object, type, oldvalue, newvalue, state, mem, agenda);
	}
	
	/**
	 *  Test if the node is affected from a modification.
	 *  @param type The attribute type.
	 *  @return True, if possibly affected.
	 */
	public boolean isAffected(OAVAttributeType attr)
	{
		return extractor.isAffected(-1, attr);
	}
	
	/**
	 *  Get the set of relevant attribute types.
	 *  @return The relevant attribute types.
	 */
	public AttributeSet getRelevantAttributes()
	{
		if(relevants==null)
		{
			synchronized(this)
			{
				if(relevants==null)
				{
					AttributeSet	relevants	= new AttributeSet();
					relevants.addAll(extractor.getRelevantAttributes());
					for(int i=0; oconsumers!=null && i<oconsumers.length; i++)
					{
						relevants.addAll(oconsumers[i].getRelevantAttributes());
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
		if(indirects==null)
		{
			synchronized(this)
			{
				if(indirects==null)
				{
					AttributeSet	indirects	= new AttributeSet();
					indirects.addAll(extractor.getIndirectAttributes());
					this.indirects	= indirects;
				}
			}
		}
		return indirects;
	}

	/**
	 *  Get the attribute.
	 *  @return The attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return attr;
	}
	
	/**
	 *  Generate virtual facts.
	 *  @param object The object.
	 *  @param state The state.
	 */
	protected Collection generateVirtualFacts(Object object, IOAVState state)
	{
		List ret = new ArrayList();
		
		Object vals = extractor.getValue(null, object, null, state);
		Object[] values = (Object[])SReflect.getArray(vals); // Hack! assumes Object[]
		 
		if(values!=null && values.length>=min_values)
			generateBindings(values.length, 0, new int[splitpattern.length], object, values, ret);
		
		return ret;
	}
	
	/**
	 *  Generate all possible bindings for a list of values.
	 *  @param weight The number of values to distribute on variables.
	 *  @param cur The current variable number.
	 *  @param binding Results are stored in this binding array (contains
	 *  for each variable how many values it should store).
	 *  @param values The values to distribute.
	 *  @param ret The result list containing all found bindings (in form of virtual facts).
	 */
	protected void generateBindings(int weight, int cur, int[] binding, Object object, 
		Object[] values, List ret)
	{
		if(cur==binding.length-1)
		{
			binding[cur] = weight;
			//System.out.println("Found binding: "+Srules.arrayToString(binding));
			ret.add(generateVirtualFact(object, binding, values));
		}
		else
		{
			if(!splitpattern[cur].equals(SINGLE))
			{
				for(int i=0; i<=weight-min_values; i++)
				{
					binding[cur] = i;
					generateBindings(weight-i, cur+1, binding, object, values, ret);					
				}
			}
			else
			{
				binding[cur] = 1;
				generateBindings(weight-1, cur+1, binding, object, values, ret);		
			}
		}
	}
	
	/**
	 *  Generate a virtual fact for a found binding.
	 *  @param binding The number of values for each variable.
	 *  @param values The multislot values.
	 *  @return A virtual fact with one binding.
	 */
	protected VirtualFact generateVirtualFact(Object object, int[] binding, Object[] values)
	{
		int off = 0;
		int bcnt = 0;
		List splitvals = new ArrayList();

		for(int i=0; i<splitpattern.length; i++)
		{
			if(!splitpattern[i].equals(MULTI_DUMMY))
			{
				if(binding[bcnt]>1)
				{
					List vals = new ArrayList();
					for(int j=off; j<off+binding[bcnt]; j++)
						vals.add(values[j]);
					splitvals.add(vals);
				}
				else if(binding[bcnt]==1)
				{
					if(splitpattern[i].equals(SINGLE))
					{
						splitvals.add(values[off]);
					}
					else
					{
						List vals = new ArrayList();
						vals.add(values[off]);
						splitvals.add(vals);
					}
				}
				else if(binding[bcnt]==0)
				{
					splitvals.add(Collections.EMPTY_LIST);
				}
			}
			off += binding[bcnt++];
		}
		
		return new VirtualFact(object, getAttribute(), splitvals);
	}
	
	/**
	 *  Get the split pattern.
	 */
	public String[] getSplitPattern()
	{
		return splitpattern;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation. 
	 */
	public String toString()
	{
		return toString(", attribute="+attr+" split in: "+splitpattern.length);
//		return toString(", extractor="+extractor+" split in: "+splitpattern.length);
	}
	
	//-------- cloneable --------
	
	/**
	 *  Do clone makes a deep clone without regarding cycles.
	 *  Method is overridden by subclasses to actually incorporate their attributes.
	 *  @param theclone The clone.
	 */
	protected void doClone(Object theclone)
	{
		SplitNode ret = (SplitNode)theclone;

		// Deep clone tuple consumers
		ret.oconsumers = new IObjectConsumerNode[oconsumers.length];
		for(int i=0; i<oconsumers.length; i++)
			ret.oconsumers[i] = (IObjectConsumerNode)oconsumers[i].clone();
		
		// Don't change the source, will be done by the source
		ret.osource = (IObjectSourceNode)osource.clone();
		
		// Shallow copy the relevant attributes
		if(relevants!=null)
			ret.relevants = (AttributeSet)((AttributeSet)relevants).clone();
		
		// Keep extractor
		
		// Keep the attribute
		
		// Keep the split pattern
		
		// Keep the min_values
	}
	
	/**
	 *  Main for testing.
	 *  @param args The arguments.
	 * /
	public static void main(String[] args)
	{
		/*List vals = new ArrayList();
		for(int i=0; i<10; i++)
			vals.add(Integer.valueOf(i));
		List ret = new ArrayList();
		generateBindings(5, 0, new int[3], null, vals.toArray(), 
			ret, new boolean[]{true,false,true}, 1);* /
		
		
		IOAVState state = OAVStateFactory.createOAVState();
		ReteMemory mem = new ReteMemory();
		Object b1 = state.createObject(OAVBlockMetamodel.block_type);
		Object b2 = state.createObject(OAVBlockMetamodel.block_type);
		Object b3 = state.createObject(OAVBlockMetamodel.block_type);
		Object b4 = state.createObject(OAVBlockMetamodel.block_type);
		Object b5 = state.createObject(OAVBlockMetamodel.block_type);
		
		state.addAttributeValue(b1, OAVBlockMetamodel.block_has_on, b2);
		state.addAttributeValue(b1, OAVBlockMetamodel.block_has_on, b3);
		state.addAttributeValue(b1, OAVBlockMetamodel.block_has_on, b4);
		state.addAttributeValue(b1, OAVBlockMetamodel.block_has_on, b5);
		
		// $?x ?y $?z
		SplitNode sn = new SplitNode(OAVBlockMetamodel.block_has_on, 
			new int[]{SINGLE});
		
		sn.addObject(b1, state, mem, null);
	}*/

	/**
	 *  Check if an object is contained in a collection.
	 *  Avoid the need for an "IdentityArrayList".
	 */
	protected boolean	contains(IOAVState state, Collection coll, Object o)
	{
		boolean	ret	= false;
		for(Iterator it=coll.iterator(); !ret && it.hasNext(); )
		{
			Object	val	= it.next();
			if(val instanceof ArrayList && o instanceof ArrayList)
			{
				ArrayList	l1	= (ArrayList) val;
				ArrayList	l2	= (ArrayList) o;
				if(l1.size()==l2.size())
				{
					ret	= true;
					for(int i=0; ret && i<l1.size(); i++)
						ret	= state.equals(l1.get(i), l2.get(i));
				}
			}
			else
			{
				ret	= state.equals(val, o);
			}
		}
		return ret;
	}
}
