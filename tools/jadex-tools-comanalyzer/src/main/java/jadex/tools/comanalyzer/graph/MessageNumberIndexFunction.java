package jadex.tools.comanalyzer.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.graph.GraphCanvas.AgentGroup;
import jadex.tools.comanalyzer.graph.GraphCanvas.MessageGroup;


/**
 * A class which creates and maintains indices for the edges so that parallel
 * edges are sorted. In this case the edges are sorted by the sequence number
 * of the message. This only applies for the directed multigraph because
 * in any other case only one message is in the edge (message group).
 */
public class MessageNumberIndexFunction implements EdgeIndexFunction
{
	// -------- attributes --------

	/** The map of edge indices */
	protected Map edge_index = new HashMap();

	// -------- EdgeIndexFunction interface --------

	/**
	 * Returns the index for the specified message group. Calculates the indices
	 * for the current group and for all groups parallel to this group. Only
	 * groups with only one containing message are considered.
	 * 
	 * @param graph The graph.
	 * @param group The message group.
	 * @return The index for the message group.
	 */
	public int getIndex(Graph graph, Object group)
	{
		Integer index = (Integer)edge_index.get(group);
		if(index == null)
		{
			if(((MessageGroup)group).isSingelton())
			{
				Pair ep = graph.getEndpoints(group);
				List comedgeset = new ArrayList();
				comedgeset.addAll(graph.findEdgeSet(ep.getFirst(), ep.getSecond()));

				Collections.sort(comedgeset, new Comparator()
				{
					public int compare(Object o1, Object o2)
					{
						MessageGroup mg1 = (MessageGroup)o1;
						MessageGroup mg2 = (MessageGroup)o2;

						if(mg1.isSingelton() && mg2.isSingelton())
						{
							Message m1 = (Message)mg1.getSingelton();
							Message m2 = (Message)mg2.getSingelton();
							return m1.compareTo(m2);
						}
						else
						{
							return 0;
						}
					}
				});

				int count = 0;
				for(Iterator it = comedgeset.iterator(); it.hasNext();)
				{
					MessageGroup other = (MessageGroup)it.next();
					Message m1 = (Message)((MessageGroup)group).getSingelton();
					Message m2 = (Message)((MessageGroup)other).getSingelton();
					if(!(m1.equals(m2)))
					{
						edge_index.put(other, Integer.valueOf(count));
						count++;
					}
				}
				edge_index.put(group, Integer.valueOf(count));
				index = Integer.valueOf(count);
			} else {
				index = Integer.valueOf(0);
			}
		}
		return index.intValue();
	}

	/**
	 * Resets the indices for this edge group and its parallel groups. Should be
	 * invoked when an edge parallel to the group has been added or removed.
	 * @param group
	 */
	public void reset(Graph graph, Object group)
	{
		Pair endpoints = graph.getEndpoints(group);
		getIndex(graph, (MessageGroup)group, (AgentGroup)endpoints.getFirst());
		getIndex(graph, (MessageGroup)group, (AgentGroup)endpoints.getFirst(), (AgentGroup)endpoints.getSecond());
	}

	/**
	 * Clears all edge indices for all edges in all graphs. Does not recalculate
	 * the indices.
	 */
	public void reset()
	{
		edge_index.clear();
	}

	// -------- helper methods --------

	/**
	 * Returns the index for the message group.
	 * 
	 * @param graph The graph.
	 * @param group The message group.
	 * @param agents1 The first agent group.
	 * @param agents2 The second agent group.
	 * @return The index of the message group.
	 */
	protected int getIndex(Graph graph, MessageGroup group, AgentGroup agents1, AgentGroup agents2)
	{
		Collection commonEdgeSet = new HashSet(graph.getIncidentEdges(agents1));
		int count = 0;
		for(Iterator it = commonEdgeSet.iterator(); it.hasNext();)
		{
			MessageGroup other = (MessageGroup) it.next();
			if(!group.equals(other))
			{
				edge_index.put(other, Integer.valueOf(count));
				count++;
			}
		}
		edge_index.put(group, Integer.valueOf(count));
		return count;
	}

	/**
	 * Returns the index for the message group.
	 * 
	 * @param graph The graph.
	 * @param e The message group.
	 * @param v The agent group.
	 * @return The index of the group.
	 */
	protected int getIndex(Graph graph, MessageGroup e, AgentGroup v)
	{
		Collection commonedgeset = new HashSet();
		for(Iterator it = graph.getIncidentEdges(v).iterator(); it.hasNext();)
		{
			MessageGroup other = (MessageGroup) it.next();
			AgentGroup u = (AgentGroup)graph.getOpposite(v, other);
			if(u.equals(v))
			{
				commonedgeset.add(other);
			}
		}
		int count = 0;
		for(Iterator it = commonedgeset.iterator(); it.hasNext();)
		{
			MessageGroup other = (MessageGroup)it.next();
			if(!e.equals(other))
			{
				edge_index.put(other, Integer.valueOf(count));
				count++;
			}
		}
		edge_index.put(e, Integer.valueOf(count));
		return count;
	}
}
