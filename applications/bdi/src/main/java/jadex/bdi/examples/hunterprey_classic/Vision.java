package jadex.bdi.examples.hunterprey_classic;

import java.util.ArrayList;
import java.util.Iterator;


/**
 *  Editable Java class for concept Vision of hunterprey ontology.
 */
public class Vision
{
	//-------- attributes ----------

	/** The visible objects with locations relative to the creature. */
	protected java.util.List objects;

	//-------- constructors --------

	/**
	 *  Create a new Vision.
	 */
	public Vision()
	{
		// Empty constructor required for JavaBeans (do not remove).
		this.objects  = new java.util.ArrayList();
	}

	//-------- accessor methods --------

	/**
	 *  Get the objects of this Vision.
	 *   The visible objects with locations relative to the creature.
	 * @return objects
	 */
	public WorldObject[] getObjects()
	{
		return (WorldObject[])objects.toArray(new WorldObject[objects.size()]);
	}

	/**
	 *  Set the objects of this Vision.
	 *  The visible objects with locations relative to the creature.
	 * @param objects the value to be set
	 */
	public void setObjects(WorldObject[] objects)
	{
		this.objects.clear();
		for(int i = 0; i < objects.length; i++)
			this.objects.add(objects[i]);
	}

	/**
	 *  Get an objects of this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param idx The index.
	 *  @return objects
	 */
	public WorldObject getObject(int idx)
	{
		return (WorldObject)this.objects.get(idx);
	}

	/**
	 *  Set a object to this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param idx The index.
	 *  @param object a value to be added
	 */
	public void setObject(int idx, WorldObject object)
	{
		this.objects.set(idx, object);
	}

	/**
	 *  Add a object to this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param object a value to be removed
	 */
	public void addObject(WorldObject object)
	{
		this.objects.add(object);
	}

	/**
	 *  Remove a object from this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param object a value to be removed
	 *  @return  True when the objects have changed.
	 */
	public boolean removeObject(WorldObject object)
	{
		return this.objects.remove(object);
	}

	//-------- custom code --------

	/**
	 *  Get the creatures in the vision.
	 */
	public Creature[] getCreatures()
	{
		ArrayList ret = new ArrayList();
		for(Iterator i = objects.iterator(); i.hasNext();)
		{
			Object obj = i.next();
			if(obj instanceof Creature)
				ret.add(obj);
		}
		return (Creature[])ret.toArray(new Creature[ret.size()]);
	}

	/**
	 *  Get an object as represented in this vision.
	 * /
	public WorldObject	findObject(WorldObject template)
	{
		WorldObject	ret	= null;
		for(Iterator i=objects.iterator(); ret==null && i.hasNext(); )
		{
			Object	next	= i.next();
			if(next.equals(template))
				ret	= (WorldObject)next;
		}
		return ret;
	}*/

	/**
	 *  Test if an object is currently seen.
	 *  @return True, if seen.
	 */
	public boolean contains(WorldObject object)
	{
		return objects.contains(object);
	}

	/**
	 *  Get a string representation of this Vision.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Vision(" + ")";
	}
}
