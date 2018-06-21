package jadex.extension.envsupport.environment.space2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jadex.commons.IFilter;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.math.IVector2;

/**
 * Implementation of a 2D variant of a k-dimensional-tree for spatial separation and searches.
 */
public class KdTree
{
	/** Default value for maximum number of objects in leaf nodes. */
	protected static final int DEFAULT_MAX_LEAF_NODE_SIZE = 6;
	
	/** Default value for maximum number of samples taken to estimate coordinate median. */
	protected static final int DEFAULT_MAX_MEDIAN_SAMPLES = 10;
	
	/** Current list of objects being used for tree rebuilds. */
	List<ISpaceObject> objects;
	
	/** The root node. */
	protected KdNode rootNode;
	
	/** Random for picking object samples for estimating median. */
	protected Random random;
	
	/** Maximum number of objects in leaf nodes. */
	protected int maxLeafNodeSize;
	
	/** Maximum number of samples taken to estimate coordinate median. */
	protected int maxMedianSamples;
	
	/**
	 *  Generates an empty KdTree using default configuration.
	 */
	public KdTree()
	{
		this(DEFAULT_MAX_LEAF_NODE_SIZE, DEFAULT_MAX_MEDIAN_SAMPLES);
	}
	
	/**
	 *  Generates an empty KdTree.
	 * 	@param maxLeafNodeSize Maximum number of objects in leaf nodes.
	 * 	@param maxMedianSamples Maximum number of samples taken to estimate coordinate median.
	 */
	public KdTree(int maxLeafNodeSize, int maxMedianSamples)
	{
		objects = new ArrayList<ISpaceObject>();
		random = new Random();
		this.maxLeafNodeSize = maxLeafNodeSize;
		this.maxMedianSamples = maxMedianSamples;
	}
	
	/**
	 *  Finds all objects within a given search radius.
	 *  
	 *  @param point Center of the search area.
	 *  @param radius The search radius.
	 */
	public List<ISpaceObject> getNearestObjects(IVector2 point, double radius)
	{
		return getNearestObjects(point, radius, null);
	}
	
	/**
	 *  Finds all objects within a given search radius.
	 *  
	 *  @param point Center of the search area.
	 *  @param radius The search radius.
	 *  @param filter Object filter.
	 */
	public List<ISpaceObject> getNearestObjects(IVector2 point, double radius, IFilter filter)
	{
		return rootNode != null? rootNode.getNearestObjects(point, radius * radius, filter) : new ArrayList();
	}
	
	/**
	 *  Finds an object closest to the given point (exhaustive search!).
	 * 	@param point The point.
	 * 	@return Object closest to the point.
	 */
	public ISpaceObject getNearestObject(IVector2 point)
	{
		return rootNode != null? rootNode.getNearestObject(point, Double.MAX_VALUE) : null;
	}
	
	/**
	 *  Finds an object closest to the given point while filtering objects (exhaustive search!).
	 *  
	 * 	@param point The point.
	 *  @param filter Object filter.
	 * 	@return Object closest to the point.
	 */
	public ISpaceObject getNearestObject(IVector2 point, IFilter filter)
	{
		return rootNode != null? rootNode.getNearestObject(point, Double.MAX_VALUE, filter) : null;
	}
	
	/**
	 *  Finds an object closest to the given point with a given search radius.
	 *  
	 * 	@param point The point.
	 * 	@param searchRadius The search radius.
	 * 	@return Object closest to the point.
	 */
	public ISpaceObject getNearestObject(IVector2 point, double searchRadius)
	{
		return getNearestObject(point, searchRadius, null);
	}
	
	/**
	 *  Finds an object closest to the given point with a given search radius,
	 *  while filtering objects.
	 *  
	 * 	@param point The point.
	 * 	@param searchRadius The search radius.
	 *  @param filter Object filter.
	 * 	@return Object closest to the point.
	 */
	public ISpaceObject getNearestObject(IVector2 point, double searchRadius, IFilter filter)
	{
		ISpaceObject ret = null;
		if (rootNode != null)
		{
			double sr2 = searchRadius * searchRadius;
			ret = rootNode.getNearestObject(point, sr2, filter);
			if (ret != null && KdNode.getDistance(ret, point).getSquaredLength().getAsDouble() > sr2)
				return null;
		}
		return ret;
	}
	
	/**
	 *  Adds an object to the tree. The object will not become visible until rebuild() is called.
	 *  
	 *  @param obj The object being added.
	 */
	public void addObject(ISpaceObject obj)
	{
		objects.add(obj);
	}
	
	/**
	 *  Removes an object to the tree. The object will not vanish until rebuild() is called.
	 *  
	 *  @param obj The object being removed.
	 */
	public void removeObject(ISpaceObject obj)
	{
		int index = objects.indexOf(obj);
		if (index == -1)
			return;
		if (objects.size() > 1)
		{
			objects.set(index, objects.get(objects.size() - 1));
			objects.remove(objects.size() - 1);
		}
		else
		{
			objects.clear();
		}
	}
	
	/**
	 *  Rebuilds the tree, updating spatial information, adding objects and removing objects.
	 */
	public void rebuild()
	{
		if (objects != null && !objects.isEmpty())
			rootNode = new KdNode(objects, random);
		else
			rootNode = null;
	}
}
