package jadex.bdi.examples.garbagecollector_classic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.collection.MultiCollection;

/**
 *  The environment.
 */
public class Environment
{
	//-------- constants --------

	/** The directions. */
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";

	/** The world object/agent types. */
	public static final String BURNER = "GarbageBurner";
	public static final String COLLECTOR = "GarbageCollector";
	public static final String GARBAGE = "Garbage";

	//-------- attributes --------

	/** The size. */
	protected int size;

	/** The agents (name -> agent info). */
	protected Map name_objects;

	/** The garbages. */
	protected MultiCollection pos_objects;

	/** The random number generator. */
	protected Random randgen;

	/** The helper object for bean events. */
	//public ConcurrentPropertyChangeSupport pcs;
	public SimplePropertyChangeSupport pcs;

	//-------- constructors --------

	/**
	 *  Create an environment.
	 */
	public Environment(int size)
	{
		this.size = size;
		this.name_objects = Collections.synchronizedMap(new HashMap());
		this.pos_objects = new MultiCollection();
		this.randgen = new Random();
		//this.pcs = new ConcurrentPropertyChangeSupport(this);
		this.pcs = new SimplePropertyChangeSupport(this);
	}

	protected static volatile Environment instance;
	
	/**
	 *  Get a singleton instance.
	 */
	public static Environment getInstance(String type, String name)
	{
//		System.out.println("Called getInstance with: "+type+" "+name);
		if(instance==null)
		{
			instance = new Environment(10);
			//instance.addWorldObject(GARBAGE, "garb#0", new Position(0,0));
			//instance.addWorldObject(GARBAGE, "garb#1", new Position(0,0));
//			System.out.println("Created new environment object: "+instance);
		}
		if(type!=null && name!=null)
			instance.addWorldObject(type, name, null);
		return instance;
	}
	
	/**
	 *  Clear the singleton instance.
	 */
	public static void clearInstance()
	{
		instance = null;
	}

	//-------- methods --------

	/*java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  Note: 1.5 compliant code.
	 * /
	public void getLock()
	{
		boolean haslock = false;
		try{haslock = lock.tryLock(5000, java.util.concurrent.TimeUnit.MILLISECONDS);}
		catch(InterruptedException e){e.printStackTrace();}
		if(!haslock)
			throw new RuntimeException("Could not get lock: "
				+Thread.currentThread()+" "+lock);

		lock.unlock();
	}*/


	/**
	 *  Add an object to the environment.
	 */
	public void addWorldObject(String type, String name, Position pos)
	{
		WorldObject wo;

		// Do not synchronize property change as it may cause microplansteps.
		synchronized(this)
		{
			//System.out.println("[add");
			Position newpos = pos!=null? pos: getFreePosition();
			if(newpos==null)
				newpos = getRandomPosition();
			wo = new WorldObject(name, type, newpos);
			name_objects.put(name, wo);
			pos_objects.add(wo.getPosition(), wo);
		}

		pcs.firePropertyChange("worldObjects", null, wo);
		//System.out.println("add]");
	}

	/**
	 *  Go in a specific direction.
	 */
	public void go(String name, String dir)
	{
		WorldObject wo;
		Position pos;
		Position newpos;

		// Do not synchronize property change as it may cause microplansteps.
		synchronized(this)
		{
			//System.out.println("[go");
	
			assert dir.equals(UP) || dir.equals(DOWN) || dir.equals(LEFT) || dir.equals(RIGHT);
	
			pos = getPosition(name);
			newpos = null;
	
			int px = pos.getX();
			int py = pos.getY();
			if(UP.equals(dir))
				newpos = new Position(px, (py-1+size)%size);
			else if(DOWN.equals(dir))
				newpos = new Position(px, (py+1)%size);
			else if(LEFT.equals(dir))
				newpos = new Position((px-1+size)%size, py);
			else if(RIGHT.equals(dir))
				newpos = new Position((px+1)%size, py);
	
			assert newpos!=null;
			assert newpos.getX()>=0 && newpos.getX()<size;
			assert newpos.getY()>=0 && newpos.getY()<size;
	
			wo = getWorldObject(name);
			pos_objects.removeObject(wo.getPosition(), wo);
			getWorldObject(name).setPosition(newpos);
			pos_objects.add(wo.getPosition(), wo);
		}

		//System.out.println("Agent moved: "+name+" "+getPosition(name));
		pcs.firePropertyChange("worldObjects", null, wo);
		//System.out.println("go]");
	}

	/**
	 *  Drop a piece of garbage.
	 *  @param name The name of the agent that want to drop.
	 */
	public void drop(String name)
	{
		WorldObject garb;

		// Do not synchronize property change as it may cause microplansteps.
		synchronized(this)
		{
			//System.out.println("[drop");
			assert getWorldObject(name).getProperty(GARBAGE)!=null;
	
			WorldObject robot = getWorldObject(name);
			garb = (WorldObject)robot.getProperty(GARBAGE);
			garb.setPosition(robot.getPosition());
			pos_objects.add(garb.getPosition(), garb);
			name_objects.put(garb.getName(), garb);
			robot.setProperty(GARBAGE, null);
		}

		//System.out.println("Agent dropped garbage: "+name+" "+getPosition(name));
		pcs.firePropertyChange("worldObjects", null, garb);
		//System.out.println("drop]");
	}

	/**
	 *  Pickup a piece of garbage.
	 *  @param name The name of the agent that want to pick up.
	 */
	public synchronized boolean pickup(String name)
	{
//		System.out.println("[pickup "+name);
		WorldObject wo = getWorldObject(name);
		
		if(wo.getProperty(GARBAGE)!=null)
			System.out.println("pickup failed: "+wo);
		
		assert wo.getProperty(GARBAGE)==null: name;
		
		if(!isDirty(wo.getPosition()))
			return false;

		boolean ret = false;
		if(Math.random()>0.5)
		{
			Position pos = getPosition(name);
			WorldObject[] wos = getWorldObjects(pos);
			WorldObject garb = null;
			for(int i=0; i<wos.length && garb==null; i++)
			{
				if(wos[i].getType().equals(GARBAGE))
					garb = wos[i];
			}
			if(garb!=null)
			{
				//System.out.println("pickup: "+wo);
				wo.setProperty(GARBAGE, garb);
				name_objects.remove(garb.getName());
				pos_objects.removeObject(pos, garb);
				ret = true;
				pcs.firePropertyChange("worldObjects", garb, null);
			}
//			System.out.println("Agent picked up: "+name+" "+getPosition(name));
		}
		else
		{
//			System.out.println("Agent picked up failed: "+name+" "+getPosition(name));
		}

//		System.out.println("pickup] "+name);
		return ret;
	}

	/**
	 *  Burn a piece of garbage.
	 *  @param name The name of the agent that want to drop.
	 */
	public synchronized void burn(String name)
	{
		//System.out.println("[burn");
		assert getWorldObject(name).getProperty(GARBAGE)!=null;

		getWorldObject(name).setProperty(GARBAGE, null);
		//System.out.println("burn]");
	}

	/**
	 *  Test if a position is dirty.
	 *  @param pos The position.
	 *  @return True, if one or more pieces of garbage are present.
	 */
	public synchronized boolean isDirty(Position pos)
	{
		boolean dirty = false;
		WorldObject[] wos = getWorldObjects(pos);
		for(int i=0; i<wos.length && !dirty; i++)
		{
			if(wos[i].getType().equals(GARBAGE))
			{
				dirty = true;
			}
		}
		return dirty;
	}
	
	/**
	 *  Test if an agent has garbage.
	 *  @param name The agent name.
	 *  @return True, if has garbage.
	 */
	public synchronized boolean hasGarbage(String name)
	{
		boolean hasgarb = false;
		WorldObject wo = getWorldObject(name);
		if(wo!=null)
			hasgarb = wo.getProperty(GARBAGE)!=null;
		return hasgarb;
	}

	/**
	 *  Test if a position is dirty.
	 *  @param pos The position.
	 *  @return True, if one or more pieces of garbage are present.
	 * /
	public int getGarbageCount(Position pos)
	{
		int cnt = 0;
		Iterator it = getWorldObjects(pos).iterator();
		while(it.hasNext())
		{
			WorldObject wo = (WorldObject)it.next();
			if(wo.getType().equals(GARBAGE))
			{
				cnt++;
			}
		}
		return cnt;
	}*/

	/**
	 *  Get the position of an object.
	 *  @param name The name of the agent.
	 *  @return The position.
	 */
	public synchronized Position getPosition(String name)
	{
		assert name_objects.containsKey(name);

		return ((WorldObject)name_objects.get(name)).getPosition();
	}

	/**
	 *  Get the grid size.
	 *  @return The size of the grid.
	 */
	public synchronized int getGridSize()
	{
		return size;
	}

	/**
	 *  Get the position of a burner.
	 *  @return The (first found, hack) position of a burner.
	 */
	public synchronized Position getBurnerPosition()
	{
		List pospos = new ArrayList();
		WorldObject[] wos = getWorldObjects();
		for(int i=0; i<wos.length; i++)
		{
			if(wos[i].getType().equals(BURNER))
				pospos.add(wos[i].getPosition());
		}

		if(pospos.size()==0)
			throw new RuntimeException("No burner found.");
		return (Position)pospos.get(randgen.nextInt(pospos.size()));
	}

	/**
	 *  Is a burner on the map.
	 *  @param pos The position to test.
	 *  @return True, if a burner is present.
	 */
	public synchronized boolean isBurnerPresent(Position pos)
	{
		boolean ret = false;
		WorldObject[] wos = getWorldObjects();
		for(int i=0; i<wos.length && !ret; i++)
		{
			if(wos[i].getType().equals(BURNER) && wos[i].getPosition().equals(pos))
				ret = true;
		}
		return ret;
	}

	/**
	 *  Get an world object for a name.
	 *  @param name The name of the world object.
	 *  @return The world object.
	 */
	protected WorldObject getWorldObject(String name)
	{
		assert name_objects.containsKey(name);

		return (WorldObject)name_objects.get(name);
	}

	/**
	 *  Get an robot for a name.
	 *  @param name The agents name.
	 *  @return The agent.
	 */
	protected WorldObject getRobot(String name)
	{
		assert !getWorldObject(name).getType().equals(GARBAGE);

		return getWorldObject(name);
	}

	/**
	 *  Get all world objects of a position.
	 *  @param pos The position.
	 *  @return All objects at the position.
	 */
	protected WorldObject[] getWorldObjects(Position pos)
	{
		Collection col = (Collection)pos_objects.get(pos);
		return col==null? new WorldObject[0]: (WorldObject[])col.toArray(new WorldObject[col.size()]);
	}

	/**
	 *  Get all world objects of a position.
	 *  @param pos The position.
	 *  @return All objects at the position.
	 */
	public WorldObject[] getGarbages(Position pos)
	{
		WorldObject[] wos = getWorldObjects(pos);
		List garbs = new ArrayList();
		for(int i=0; i<wos.length; i++)
		{
			if(wos[i].getType().equals(GARBAGE))
				garbs.add(wos[i]);
		}
		return (WorldObject[])garbs.toArray(new WorldObject[garbs.size()]);
	}


	/**
	 *  Get the world objects.
	 *  @return Get all world objects (except pickuped garbage).
	 */
	public WorldObject[] getWorldObjects()
	{
		return (WorldObject[])name_objects.values().toArray(new WorldObject[0]);
	}

	/**
	 *  Set the world objects.
	 */
	// Hack!!! Method required for property change (otherwise no bean property exists)
	public void setWorldObjects(WorldObject[] objects)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get a free position on the map.
	 *  @return The next free position on the grid.
	 */
	protected Position getFreePosition()
	{
		Position pos;
		int cnt = 0;
		do
		{
			pos = getRandomPosition();
		}
		while(!isFree(pos) && cnt++<20);
		if(!isFree(pos))
			pos = null;
		return pos;
	}

	/**
	 *  Get a free position on the map.
	 *  @return The next free position on the grid.
	 */
	protected Position getRandomPosition()
	{
		int x = randgen.nextInt(size);
		int y = randgen.nextInt(size);
		return new Position(x, y);
	}

	/**
	 *  Test if a position is free.
	 *  @return True, if a position is free.
	 */
	protected boolean isFree(Position pos)
	{
		boolean free = true;
		if(pos_objects.get(pos)!=null)
		{
			WorldObject[] wos = getWorldObjects(pos);
			for(int i=0; i<wos.length && free; i++)
			{
				if(wos[i].getPosition().equals(pos))
					free = false;
			}
		}
		return free;
	}

	/**
	 *  Main method for testing.
	 * /
	public static void main(String[] args)
	{
		Environment env = getInstance(BURNER, "Burny");
	}*/

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }
}

/**
 *  Info strcut for agents in the environment.
 */
class WorldObject
{
	/** The name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The position. */
	protected Position pos;

	/** The carries garbarge. */
	protected HashMap properties;

	/**
	 *  The type.
	 */
	public WorldObject(String name, String type, Position pos)
	{
		assert type.equals(Environment.BURNER)
				|| type.equals(Environment.COLLECTOR)
				|| type.equals(Environment.GARBAGE);

		this.name = name;
		this.type = type;
		this.pos = pos;
		this.properties = new HashMap();
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Get the position.
	 *  @return The position.
	 */
	public Position getPosition()
	{
		return pos;
	}

	/**
	 *  Set the position.
	 *  @param pos The position.
	 */
	public void setPosition(Position pos)
	{
		this.pos = pos;
	}

	/**
	 *  Get a property.
	 *  @param name The name.
	 *  @return The property.
	 */
	public Object getProperty(String name)
	{
		return properties.get(name);
	}

	/**
	 *  Set the garbarge.
	 *  @param name The name.
	 *  @param value The value.
	 */
	public void setProperty(String name, Object value)
	{
		properties.put(name, value);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return type+": "+name+", "+pos+", "+getProperty(Environment.GARBAGE);
	}
}
