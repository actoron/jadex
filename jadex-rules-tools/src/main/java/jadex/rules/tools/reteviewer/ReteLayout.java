package jadex.rules.tools.reteviewer;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import jadex.rules.rulesystem.rete.nodes.BetaNode;
import jadex.rules.rulesystem.rete.nodes.INode;
import jadex.rules.rulesystem.rete.nodes.ITupleConsumerNode;
import jadex.rules.rulesystem.rete.nodes.TerminalNode;

/**
 *  Jung layout for a Rete network.
 */
public class ReteLayout extends AbstractLayout implements Layout
{
	/** The size. */
	// Managed directly, because jung's abstractlayout introduces
	// it's own (incompatible) offsets. 
	protected Dimension	rsize;
	
	/** The nodes, sorted in layers. */
	protected List	layers;
	
	/** Flag to indicate when positions are up-to-date. */
	protected boolean	positions;
	
	/** Flag to enable layout optimization.
	 *  With layout optimization, not only edge lengths are optimized,
	 *  but also left/right directions of connected alpha and beta nodes. */
	protected boolean layout;
	
	/** Flag to enable simulated annealing (SA).
	 *  With SA, the layout algorithm will take longer,
	 *  but can escape sub optimal local minima. */
	protected boolean annealing;
	
	//-------- constructors --------
	
	/**
	 *  Create a new Rete layout.
	 */
	public ReteLayout(Graph g)
	{
		super(g);
		this.layout	= true;
		this.annealing	= true;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a (re-)layout is needed.
	 */
	public void initialize()
	{
		// Method required by Layout interface.
		// Nothing to do here: use lazy evaluation triggered by getGraph().
	}

	/**
	 *  Called when ?
	 */
	public void reset()
	{
		// Method required by Layout interface.
		// Nothing to do here?
	}

	/**
	 *  Get the graph to be layouted.
	 */
	public Graph getGraph()
	{
		if(layers==null)
			layoutLayers();
		if(!positions)
			setPositions();
		
		return super.getGraph();
	}
	
	/**
	 *  Called, when the component is resized.
	 *  New positions will be calculated on next redrawn.
	 */
	public void setSize(Dimension size)
	{
		positions	= false;
		this.rsize	= size;
	}
	
	/**
	 *  Get the size.
	 */
	public Dimension	getSize()
	{
		return this.rsize;
	}
	
	/**
	 *  Called, when the graph structure has changed.
	 *  New layer structure will be calculated on next redraw.
	 */
	public void graphChanged()
	{
		layers	= null;
		positions	= false;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Called when a (re-)layout is needed.
	 *  Arrange node in layers structure (lists of lists of nodes),
	 *  which is independent of component size.
	 */
	protected void layoutLayers()
	{
		// Find root/leaf node(s).
		Graph	graph	= super.getGraph();
		List	rootnodes	= new ArrayList();
		List	leafnodes	= new ArrayList();
		for(Iterator it=graph.getVertices().iterator(); it.hasNext(); )
		{
			Object	next	= it.next();
			if(graph.getInEdges(next).isEmpty())
			{
				rootnodes.add(next);
			}
			if(graph.getOutEdges(next).isEmpty())
			{
				leafnodes.add(next);
			}
		}

		// Add nodes to layers according to depth in rete network.
		this.layers	= new ArrayList();
		layers.add(rootnodes);
		for(int l=0; l<layers.size(); l++)
		{
			Object[]	nodes	= ((List)layers.get(l)).toArray();
			for(int n=0; n<nodes.length; n++)
			{
				Object[]	edges	= graph.getOutEdges(nodes[n]).toArray();
				for(int e=0; e<edges.length; e++)
				{
					Object	child	= ((ReteEdge)edges[e]).getEnd();
					// Add child nodes (except leaf nodes, which are added in separate layer).
					if(!leafnodes.contains(child))
					{
						// Remove previous occurrence (if any).
						for(int l2=0; l2<layers.size(); l2++)
							((List)layers.get(l2)).remove(child);
						
						// Add to next layer.
						if(l==layers.size()-1)
							layers.add(new ArrayList());
						((List)layers.get(l+1)).add(child);
					}
				}
			}
		}
		// Add final layer for leaf nodes.
		layers.add(leafnodes);
		
		// Set all layers to same length.
		// When annealing and layouting, add some more space for finding better solutions.
		int	maxsize	= 0;
		for(int l=0; l<layers.size(); l++)
		{
//			System.out.println("Layer "+l+": "+layers.get(l));
			maxsize	= Math.max(maxsize, ((List)layers.get(l)).size());
		}
		maxsize	= annealing && layout ?
			(int)Math.sqrt(layers.size()*layers.size()+maxsize*maxsize)
			: maxsize;
		for(int l=0; l<layers.size(); l++)
		{
			List	layer	= (List)layers.get(l);
			while(layer.size()<maxsize)
				layer.add(null);
		}
		
		// Optimize node positions.
		boolean	swap	= true;
		double	threshold	= annealing
			? graph.getVertexCount()*graph.getVertexCount()
			: 0;
		while(swap)
		{
			swap	= false;
			for(int l=0; l<layers.size(); l++)
			{
				List	layer	= (List)layers.get(l);
				for(int i=0; i<layer.size(); i++)
				{
					for(int j=i+1; j<layer.size(); j++)
					{
						INode	node1	= (INode)layer.get(i);
						INode	node2	= (INode)layer.get(j);
						if(node1!=null || node2!=null)
						{
							double	pre	= (node1!=null?calcEdgeLengths(graph, node1, l, i):0) + (node2!=null?calcEdgeLengths(graph, node2, l, j):0);
							double	post	= (node1!=null?calcEdgeLengths(graph, node1, l, j):0) + (node2!=null?calcEdgeLengths(graph, node2, l , i):0); 
							if(post-pre < threshold)
							{
								layer.set(i, node2);
								layer.set(j, node1);
								swap	= true;
//								System.out.println("Gain: "+(pre-post)+", pre="+pre+", post="+post+", threshold="+threshold);
							}
						}
					}
				}
			}
			threshold	*= 0.9;
			if(threshold<0.1)
				threshold	= 0;
		}
	}
	
	/**
	 *  Called, when the component has been resized.
	 *  Use layer structure to place nodes inside component bounds.
	 */
	protected void setPositions()
	{
		positions	= true;
		Dimension	dim	= getSize();
		int	height	= dim.height / layers.size();

		// Find offsets to ignore extra space added for optimization.
		int	minleft	= Integer.MAX_VALUE;
		int	maxright	= 0;
		for(int l=0; l<layers.size(); l++)
		{
			boolean	found	= false;
			List	layer	= (List)layers.get(l);
			for(int i=0; i<layer.size(); i++)
			{
				if(layer.get(i)!=null
				// Ignore beta and terminal nodes, which are layouted according to their parents.
					&& !(layer.get(i) instanceof BetaNode)
					&& !(layer.get(i) instanceof TerminalNode))
				{
					if(!found)
					{
						minleft	= Math.min(i, minleft);
					}
					maxright	= Math.max(i, maxright);
					found	= true;
				}
			}
		}
//		System.out.println("minleft="+minleft+", maxright="+maxright);

		// Place nodes on available space.
		int	width	= dim.width / (maxright+1-minleft);
		for(int l=0; l<layers.size(); l++)
		{
			INode[]	nodes	= (INode[])((List)layers.get(l)).toArray(new INode[0]);
			for(int n=0; n<nodes.length; n++)
			{
				if(nodes[n]!=null)
				{
					boolean	finished	= false;
					
					// Place a beta node centered below left/right parent.
					if(nodes[n] instanceof BetaNode)
					{
						INode	left	= ((BetaNode)nodes[n]).getTupleSource();
						INode	right	= ((BetaNode)nodes[n]).getObjectSource();
						if(left!=null && right!=null)
						{
							Point2D	leftpos	= transform(left);//getLocation(left);
							Point2D	rightpos	= transform(right);//getLocation(right);
							if(leftpos!=null && rightpos!=null)
							{
								setLocation(nodes[n], (leftpos.getX()+rightpos.getX())/2, l*height+ height/2);
								finished	= true;
							}
						}
					}
	
					// Place a terminal node centered below parent.
					else if(nodes[n] instanceof TerminalNode)
					{
						INode	parent	= ((ITupleConsumerNode)nodes[n]).getTupleSource();
						if(parent!=null)
						{
							Point2D	parentpos	= transform(parent);//getLocation(parent);
							if(parentpos!=null)
							{
								setLocation(nodes[n], parentpos.getX(), (layers.size()-1)*height+ height/2);
								finished	= true;
							}
						}
					}
					
					// Place other nodes according to location in layer.
					if(!finished)
					{
						setLocation(nodes[n], (n-minleft)*width + width/2, l*height+ height/2);
					}
				}
			}
		}
	}

	//-------- helper methods --------
		
	/**
	 *  Calculate the edge lengths between a node and its parents/children.
	 */
	protected double	calcEdgeLengths(Graph graph, INode node, int layer, int pos)
	{
		double	length	= 0;
		
		for(Iterator it=graph.getInEdges(node).iterator(); it.hasNext(); )
		{
			ReteEdge	edge	= (ReteEdge)it.next();
//			System.out.println(edge);
			length += calcEdgeLength(edge, node, layer, pos);
		}
		
		for(Iterator it=graph.getOutEdges(node).iterator(); it.hasNext(); )
		{
			ReteEdge	edge	= (ReteEdge)it.next();
//			System.out.println(edge);
			length += calcEdgeLength(edge, node, layer, pos);
		}
		
		return length;
	}

	/**
	 *  Calculate the edge length of the given edge.
	 *  If layout is enabled, the edge length is rated according
	 *  to layout constraints (shorter length = better layout). 
	 */
	protected double	calcEdgeLength(ReteEdge edge, INode node, int layer, int pos)
	{
		// Find node positions.
		int layer1, pos1;
		int layer2, pos2;
		if(node==edge.getStart())
		{
			layer1	= layer;
			pos1	= pos;

			pos2	= -1;
			for(layer2=0; pos2==-1 && layer2<layers.size(); layer2++)
			{
				pos2	= ((List)layers.get(layer2)).indexOf(edge.getEnd());
			}
			if(pos2==-1)
				throw new RuntimeException("Node not found: "+edge.getEnd());
		}
		else
		{
			pos1	= -1;
			for(layer1=0; pos1==-1 && layer1<layers.size(); layer1++)
			{
				pos1	= ((List)layers.get(layer1)).indexOf(edge.getStart());
			}
			if(pos1==-1)
				throw new RuntimeException("Node not found: "+edge.getStart());

			layer2	= layer;
			pos2	= pos;
		}
				
		double	a	= layer1 - layer2;
		double	b	= pos1 - pos2;
		
		// 1 field left-to-right adjustment and higher priority for beta edges.
		if(layout && edge.isTuple())
		{
			b	= (b + 1) * 2.5;
		}
		// 1 field right-to-left adjustment and slightly higher priority for alpha-to-beta edges.
		else if(layout && edge.getEnd() instanceof BetaNode)
		{
			b	= (b - 1) * 1.9;
		}
		
		return Math.sqrt(a*a + b*b);
	}
}
