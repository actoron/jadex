package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IBpmnPlan;
import jadex.gpmn.editor.model.gpmn.IContext;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IModelCodec;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  The GPMN model.
 *
 */
public class GpmnModel implements IGpmnModel
{
	/** Implementation to interface map. */
	protected static final Map<Class, Class> IMPL_TO_INTERFACE = Collections.synchronizedMap(new HashMap<Class, Class>());
	static
	{
		IMPL_TO_INTERFACE.put(Goal.class, IGoal.class);
		IMPL_TO_INTERFACE.put(BpmnPlan.class, IBpmnPlan.class);
		IMPL_TO_INTERFACE.put(PlanEdge.class, IPlanEdge.class);
		IMPL_TO_INTERFACE.put(ActivationEdge.class, IActivationEdge.class);
		IMPL_TO_INTERFACE.put(ActivationPlan.class, IActivationPlan.class);
		IMPL_TO_INTERFACE.put(SuppressionEdge.class, ISuppressionEdge.class);
	}
	
	/** The name repository. */
	protected NameRepository namerepository = new NameRepository();
	
	/** Model nodes. */
	protected Map<Class, Set<INode>> nodes = new HashMap<Class, Set<INode>>();
	
	/** Model edges. */
	protected Map<Class, Set<IEdge>> edges = new HashMap<Class, Set<IEdge>>();
	
	/** The context. */
	protected IContext context = new Context();
	
	/**
	 *  Gets the name repository.
	 *  
	 *  @return The name repository.
	 */
	public NameRepository getNameRepository()
	{
		return namerepository;
	}
	
	/**
	 *  Gets the context.
	 *
	 *  @return The context.
	 */
	public IContext getContext()
	{
		return context;
	}

	/**
	 *  Sets the context.
	 *
	 *  @param context The context.
	 */
	public void setContext(IContext context)
	{
		this.context = context;
	}

	/**
	 *  Creates a node in the model.
	 *  
	 *  @param nodetype The node type.
	 *  @return The node.
	 */
	public INode createNode(Class nodetype)
	{
		INode node = null;
		if (IGoal.class.equals(nodetype))
		{
			Goal g = new Goal(this);
			g.setName("Unnamed Goal");
			node = g;
		}
		else if (IActivationPlan.class.equals(nodetype))
		{
			ActivationPlan p = new ActivationPlan(this);
			p.setName("Unknown Plan");
			node = p;
		}
		else if (IBpmnPlan.class.equals(nodetype))
		{
			BpmnPlan p = new BpmnPlan(this);
			p.setName("Unknown Plan");
			node = p;
		}
		
		Set<INode> nodes = getNodeSet(nodetype);
		nodes.add(node);
		
		return node;
	}
	
	/**
	 *  Copies a node in the model.
	 *  
	 *  @param node The node.
	 *  @return The node copy.
	 */
	public INode copyNode(IElement node)
	{
		INode copy = null;
		
		if (node instanceof IActivationPlan)
		{
			IActivationPlan oaplan = (IActivationPlan) node;
			IActivationPlan naplan = new ActivationPlan(this);
			
			naplan.setMode(oaplan.getMode());
			naplan.setName(oaplan.getName());
			naplan.setContextCondition(oaplan.getContextCondition());
			naplan.setPreCondition(oaplan.getPreCondition());
			
			copy = naplan;
		}
		
		return copy;
	}
	
	/**
	 *  Creates an edge in the model.
	 *  
	 *  @param source Source of the edge.
	 *  @param target Target of the edge.
	 *  @param edgetype The edge type.
	 *  @return The edge.
	 */
	public IEdge createEdge(IElement source, IElement target, Class edgetype)
	{
		IEdge edge = null;
		if (IPlanEdge.class.equals(edgetype))
		{
			edge = new PlanEdge(this);
		}
		else if (IActivationEdge.class.equals(edgetype))
		{
			edge = new ActivationEdge(this);
		}
		else if (ISuppressionEdge.class.equals(edgetype))
		{
			edge = new SuppressionEdge(this);
		}
		
		edge.setSource(source);
		edge.setTarget(target);
		edge.setName(namerepository.createUniqueName("Edge", edge));
		Set<IEdge> edges = getEdgeSet(edgetype);
		edges.add(edge);
		
		return edge;
	}
	
	/**
	 *  Removes a node from the model.
	 *  
	 *  @param node The node.
	 */
	public void removeNode(INode node)
	{
		if (node != null)
		{
			Set<INode> nodes = getNodeSet(IMPL_TO_INTERFACE.get(node.getClass()));
			nodes.remove(node);
			namerepository.deleteUniqueName(node);
		}
	}
	
	/**
	 *  Removes an edge from the model.
	 *  
	 *  @param edge The edge.
	 */
	public void removeEdge(IEdge edge)
	{
		if (edge != null)
		{
			AbstractEdge aedge = (AbstractEdge) edge;
			Set<IEdge> edges = getEdgeSet(IMPL_TO_INTERFACE.get(edge.getClass()));
			((AbstractElement) edge.getSource()).removeSourceEdge(aedge);
			((AbstractElement) edge.getTarget()).removeTargetEdge(aedge);
			edges.remove(edge);
			namerepository.deleteUniqueName(edge);
		}
	}
	
	protected Set<INode> getNodeSet(Class type)
	{
		Set <INode> ret = nodes.get(type);
		if (ret == null)
		{
			ret = new HashSet<INode>();
			nodes.put(type, ret);
		}
		
		return ret;
	}
	
	protected Set<IEdge> getEdgeSet(Class type)
	{
		Set <IEdge> ret = edges.get(type);
		if (ret == null)
		{
			ret = new HashSet<IEdge>();
			edges.put(type, ret);
		}
		
		return ret;
	}
	
	/**
	 *  Returns the codec for loading and saving models.
	 *  
	 *  @return The codec.
	 */
	public IModelCodec getModelCodec()
	{
		return new ModelCodec(this);
	}
	
	/**
	 *  Clears the model.
	 */
	public void clear()
	{
		this.namerepository = new NameRepository();
		this.nodes = new HashMap<Class, Set<INode>>();
		this.edges = new HashMap<Class, Set<IEdge>>();
		this.context = new Context();
	}
}
