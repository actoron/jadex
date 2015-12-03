package jadex.gpmn.editor.model.gpmn.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;

import jadex.gpmn.editor.model.gpmn.IActivationEdge;
import jadex.gpmn.editor.model.gpmn.IActivationPlan;
import jadex.gpmn.editor.model.gpmn.IContext;
import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGoal;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;
import jadex.gpmn.editor.model.gpmn.IModelCodec;
import jadex.gpmn.editor.model.gpmn.INode;
import jadex.gpmn.editor.model.gpmn.IPlanEdge;
import jadex.gpmn.editor.model.gpmn.IRefPlan;
import jadex.gpmn.editor.model.gpmn.ISuppressionEdge;
import jadex.gpmn.editor.model.visual.VEdge;
import jadex.gpmn.editor.model.visual.VGoal;
import jadex.gpmn.editor.model.visual.VPlan;

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
		IMPL_TO_INTERFACE.put(RefPlan.class, IRefPlan.class);
		IMPL_TO_INTERFACE.put(PlanEdge.class, IPlanEdge.class);
		IMPL_TO_INTERFACE.put(ActivationEdge.class, IActivationEdge.class);
		IMPL_TO_INTERFACE.put(ActivationPlan.class, IActivationPlan.class);
		IMPL_TO_INTERFACE.put(SuppressionEdge.class, ISuppressionEdge.class);
	}
	
	/** The description. */
	protected String description;
	
	/** The package. */
	protected String pkg;
	
	/** The name repository. */
	protected NameRepository namerepository;
	
	/** Model nodes. */
	protected Map<Class, Set<INode>> nodes;
	
	/** Model edges. */
	protected Map<Class, Set<IEdge>> edges;
	
	/** The context. */
	protected IContext context;
	
	/**
	 *  Creates a new model.
	 */
	public GpmnModel()
	{
		description = "";
		pkg = "";
		namerepository = new NameRepository();
		nodes = new HashMap<Class, Set<INode>>();
		edges = new HashMap<Class, Set<IEdge>>();
		context = new Context();
	}
	
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
	 *  Gets the description.
	 *  
	 *  @return The description.
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 *  Sets the name.
	 *  
	 *  @param description The description.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 *  Gets the package.
	 *  
	 *  @return The package.
	 */
	public String getPackage()
	{
		return pkg;
	}
	
	/**
	 *  Sets the package.
	 *  
	 *  @param pkg The package.
	 */
	public void setPackage(String pkg)
	{
		this.pkg = pkg;
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
			p.setName("Unnamed Plan");
			node = p;
		}
		else if (IRefPlan.class.equals(nodetype))
		{
			RefPlan p = new RefPlan(this);
			p.setName("Unnamed Plan");
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
	 *  @param type The type of the codec.
	 *  @return The codec.
	 */
	public IModelCodec getModelCodec(String type)
	{
		IModelCodec ret = null;
		if (IModelCodec.CODEC_TYPE_GPMN.equals(type))
		{
			ret = new GpmnModelCodec(this);
		}
		else if (IModelCodec.CODEC_TYPE_BDI.equals(type))
		{
			ret = new BdiModelCodec(this);
		}
		return ret;
	}
	
	/**
	 *  Generates a visual model for a GPMN model.
	 *  
	 *  @return A visual model.
	 */
	public mxIGraphModel generateGraphModel()
	{
		mxGraphModel graphmodel = new mxGraphModel();
		Object root = graphmodel.getRoot();
		Object parent = graphmodel.getChildAt(root, 0);
		
		graphmodel.beginUpdate();
		
		Map<String, VGoal> goals = new HashMap<String, VGoal>();
		Set<INode> nodes = getNodeSet(IGoal.class);
		for (INode node : nodes)
		{
			Goal goal = (Goal) node;
			VGoal vgoal = new VGoal(goal, new mxPoint());
			goals.put(goal.getName(), vgoal);
			graphmodel.add(parent, vgoal, graphmodel.getChildCount(parent));
		}
		
		Map<String, VPlan> plans = new HashMap<String, VPlan>();
		nodes = getNodeSet(IActivationPlan.class);
		for (INode node : nodes)
		{
			ActivationPlan actplan = (ActivationPlan) node;
			VPlan vplan = new VPlan(actplan, new mxPoint());
			plans.put(actplan.getName(), vplan);
			graphmodel.add(parent, vplan, graphmodel.getChildCount(parent));
		}
		
		nodes = getNodeSet(IRefPlan.class);
		for (INode node : nodes)
		{
			RefPlan refplan = (RefPlan) node;
			VPlan vplan = new VPlan(refplan, new mxPoint());
			plans.put(refplan.getName(), vplan);
			graphmodel.add(parent, vplan, graphmodel.getChildCount(parent));
		}
		
		Set<IEdge> edges = getEdgeSet(IPlanEdge.class);
		for (IEdge iedge : edges)
		{
			PlanEdge edge = (PlanEdge) iedge;
			VGoal source = goals.get(edge.getSource().getName());
			VPlan target = plans.get(edge.getTarget().getName());
			VEdge vedge = new VEdge(source, target, edge);
			graphmodel.add(parent, vedge, graphmodel.getChildCount(parent));
		}
		
		edges = getEdgeSet(IActivationEdge.class);
		for (IEdge iedge : edges)
		{
			ActivationEdge edge = (ActivationEdge) iedge;
			VPlan source = plans.get(edge.getSource().getName());
			VGoal target = goals.get(edge.getTarget().getName());
			VEdge vedge = new VEdge(source, target, edge);
			graphmodel.add(parent, vedge, graphmodel.getChildCount(parent));
		}
		
		edges = getEdgeSet(ISuppressionEdge.class);
		for (IEdge iedge : edges)
		{
			SuppressionEdge edge = (SuppressionEdge) iedge;
			VGoal source = goals.get(edge.getSource().getName());
			VGoal target = goals.get(edge.getTarget().getName());
			VEdge vedge = new VEdge(source, target, edge);
			graphmodel.add(parent, vedge, graphmodel.getChildCount(parent));
		}
		
		graphmodel.endUpdate();
		
		return graphmodel;
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
