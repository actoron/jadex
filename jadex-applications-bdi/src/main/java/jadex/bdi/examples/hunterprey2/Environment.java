package jadex.bdi.examples.hunterprey2;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Int;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.collection.MultiCollection;

import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nuggets.Nuggets;

/**
 *  The environment is the container all objects and creatures.
 */
public class Environment implements IEnvironment
{
	public static final String OBJECT_TYPE_OBSTACLE 	= "obstacle"; 
	public static final String OBJECT_TYPE_FOOD 		= "food"; 
	public static final String OBJECT_TYPE_HUNTER 		= "hunter"; 
	public static final String OBJECT_TYPE_PREY 		= "prey";
	public static final String PROPERTY_ONTOLOGY 		= "ontologyObj"; 
	
	//-------- constants --------
	
	/** The default number of lease ticks. */
	public static int	DEFAULT_LEASE_TICKS	= 50;

	//-------- attributes --------
	
	// this is no plan, so we need a external access on the
	// agent that created this Environment to dispatch sim goals
	/** The agent created this evnironment */
	protected IExternalAccess agent;

	/** The creatures simId's */
	protected Map creatures;

	/** The obstacles simId's */
	protected Set obstacles;

	/** The prey food simId's */
	protected Set food;

	/** All world object simulation id's accessible per location. */
	public MultiCollection world;

//	/** The horizontal size. */
//	protected int sizex;
//
//	/** The vertictal size. */
//	protected int sizey;

	/** The list for move and eat requests. */
	protected List tasklist;

	/** The helper object for bean events. */
	protected SimplePropertyChangeSupport pcs;


	
	/** The highscore location. */
	protected List highscore;

	/** The last time the highscore was saved. */
	protected long	savetime;

	/** The interval between saves of highscore (-1 for autosave off). */
	protected long	saveinterval;
	
//	/** The foodrate determines how often new food pops up. */
//	protected int foodrate;
	
	/** The world age. */
	protected int age;
	
	//-------- constructors --------

	/**
	 *  Create a new environment.
	 */
	public Environment(String title, IVector2 areaSize, IExternalAccess agent)
	{

		
		
		this.agent = agent;
		
		this.creatures = new HashMap();
		this.obstacles = new HashSet();
		this.food = new HashSet();
		this.world = new MultiCollection();
		this.tasklist = new ArrayList();
		this.pcs = new SimplePropertyChangeSupport(this);
		
		// Pre-declare object types
//		super.declareObjectType(OBJECT_TYPE_HUNTER);
//		super.declareObjectType(OBJECT_TYPE_PREY);
//		super.declareObjectType(OBJECT_TYPE_OBSTACLE);
//		super.declareObjectType(OBJECT_TYPE_FOOD);
		
		// Actions
//		engine.addAction(new PickupWasteAction());
//		engine.addAction(new DisposeWasteAction());
//		engine.addAction(new ChargeBatteryAction());
		
		this.saveinterval	= 5000;
		// Read highscore list.
		try
		{
			ObjectInputStream is = new ObjectInputStream(SUtil.getResource("highscore.dmp", Environment.class.getClassLoader()));
			this.highscore = SUtil.arrayToList(is.readObject());
			is.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
			highscore = new ArrayList();
		}
	}

	//--- simulation engine support methods -----
	
	/**
	 * Create a sim object
	 * @return the sim object with new sim id
	 */
	protected WorldObject createSimObject(WorldObject wo, String type, Map properties, List tasks, Boolean sigDes, Boolean listen)
	{
		Location l = wo.getLocation();
		Map props = properties;
		if (properties == null)
			props = new HashMap();
		
		props.put(PROPERTY_ONTOLOGY, wo);
		
		IGoal cg = agent.createGoal("sim_create_object");
		cg.getParameter("type").setValue(type);
		cg.getParameter("properties").setValue(props);
		cg.getParameter("position").setValue(new Vector2Int(l.getX(), l.getY()));
		
		cg.getParameter("signal_destruction").setValue(sigDes);
		cg.getParameter("listen").setValue(listen);
		
		agent.dispatchTopLevelGoalAndWait(cg);
		wo.setSimId((Integer) cg.getParameter("object_id").getValue());
		
		return wo;
	}
	
	/**
	 * Destroy a sim object
	 * @param wo
	 * @return
	 */
	protected WorldObject destroySimObject(WorldObject wo)
	{

		IGoal cg = agent.createGoal("sim_destroy_object");
		cg.getParameter("object_id").setValue(wo.getSimId());
		agent.dispatchTopLevelGoalAndWait(cg);
		
		return wo;
	}


	//-------- interface methods --------

	/**
	 *  Move one field upwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveUp(Creature me)
	{
		return move(me, RequestMove.DIRECTION_UP);
	}

	/**
	 *  Move one field downwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveDown(Creature me)
	{
		return move(me, RequestMove.DIRECTION_DOWN);
	}

	/**
	 *  Move one field to the left. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveLeft(Creature me)
	{
		return move(me, RequestMove.DIRECTION_LEFT);
	}

	/**
	 *  Move one field to the right. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveRight(Creature me)
	{
		return move(me, RequestMove.DIRECTION_RIGHT);
	}

	/**
	 *  Eat some object. The object has to be at the same location.
	 *  This method does not block, and can be called multiple
	 *  times during each simulation step.
	 *  @param food The object.
	 *  @return True, when the operation succeeded.
	 */
	public boolean eat(Creature me, WorldObject food)
	{
		boolean ret = false;
		int	points	= 0;
		me = getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		if(food instanceof Food)
		{
			if(me.getLocation().equals(food.getLocation()))
			{
				ret	= removeFood((Food)food);
				points	= 1;
			}
		}
		else if(me instanceof Hunter && food instanceof Prey)
		{
			// Get actual creature from world model.
			Creature	creat	= getCreature((Creature)food);
			if(me.getLocation().equals(creat.getLocation()))
			{
				ret	= removeCreature(creat);
				points	= 5;
			}
		}

		if(ret)
		{
			me.setPoints(me.getPoints()+points);
		}
		/*else
		{
			System.out.println("Creature tried to cheat: "+me.getName());
		}*/
		//block(); todo: make blocking for local case
		return ret;
	}

	/**
	 *  Add a move or eat action to the queue.
	 */
	public TaskInfo addEatTask(Creature me, WorldObject obj)
	{
		TaskInfo ret = new TaskInfo(new Object[]{"eat", me, obj});
		tasklist.add(ret);
		this.pcs.firePropertyChange("taskSize", tasklist.size()-1, tasklist.size());
		return ret;
	}

	/**
	 *  Add a move or eat action to the queue.
	 */
	public TaskInfo addMoveTask(Creature me, String dir)
	{
		TaskInfo ret = new TaskInfo(new Object[]{"move", me, dir});
		tasklist.add(ret);
		this.pcs.firePropertyChange("taskSize", tasklist.size()-1, tasklist.size());
		return ret;
	}

	/**
	 *  Get the current vision (without updating the creatures leaseticks).
	 *  @param me The creature.
	 */
	public Vision internalGetVision(Creature me)
	{
		Vision ret = new Vision();
		me = getCreature(me);
		WorldObject[] wos;
		if(me instanceof Observer)
		{
			wos	= getAllObjects();
		}
		else
		{
			wos = getNearObjects(me.getLocation(), me.getVisionRange());
		}
		ret.setObjects(wos);
		return ret;
	}

	/**
	 *  Get the current vision. This method does not block,
	 *  and can be called multiple times during each simulation step.
	 *  @param me The creature.
	 */
	public Vision getVision(Creature me)
	{
		me = getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		return internalGetVision(me);
	}

	/**
	 *  Get the width of the world.
	 */
	public int	getWidth()
	{
//		return sizex;
		
//		return super.getAreaSize().getX().getAsInteger();
		// HACK! use sim environment
		return Configuration.AREA_SIZE.getXAsInteger();
	}

	/**
	 *  Get the height of the world.
	 */
	public int	getHeight()
	{
//		return sizey;
//		return super.getAreaSize().getY().getAsInteger();
		// HACK! use sim environment
		return Configuration.AREA_SIZE.getYAsInteger();
	}

	//-------- management methods --------

	/**
	 *  Add a new prey food to the world.
	 *  @param nfood The new food.
	 */
	public void addFood(Food nfood)
	{		
//		Location l = nfood.getLocation();
//		Map props = new HashMap();
//		props.put(PROPERTY_ONTOLOGY, nfood);
//		nfood.setSimId(super.createSimObject(OBJECT_TYPE_FOOD, props, null, new Vector2Int(l.getX(), l.getY()), false, null));
		
		WorldObject food = createSimObject(nfood, OBJECT_TYPE_FOOD, null, null, Boolean.TRUE, Boolean.FALSE);
		
		this.food.add(food);
		this.world.put(food.getLocation(), food);
	}

	/**
	 *  remove a prey food to the world.
	 *  @param nfood Thefood.
	 */
	public boolean removeFood(Food nfood)
	{

		//super.destroySimObject(nfood.getSimId());
		destroySimObject(nfood);
		
		this.world.remove(nfood.getLocation(), nfood);
		return this.food.remove(nfood);
		
	}

	/**
	 *  Add a new obstacle to the world.
	 *  @param obstacle The new obstacle.
	 */
	public void addObstacle(Obstacle obstacle)
	{
//		Location l = obstacle.getLocation();
//		Map props = new HashMap();
//		props.put(PROPERTY_ONTOLOGY, obstacle);
//		obstacle.setSimId(super.createSimObject(OBJECT_TYPE_OBSTACLE, props, null, new Vector2Int(l.getX(), l.getY()), false, null));
		
		WorldObject wo = createSimObject(obstacle, OBJECT_TYPE_OBSTACLE, null, null, Boolean.TRUE, Boolean.FALSE);
		
		this.obstacles.add(wo);
		this.world.put(obstacle.getLocation(), wo);
	}

	/**
	 *  Remove a  obstacle to the world.
	 *  @param obstacle The obstacle.
	 */
	public boolean removeObstacle(Obstacle obstacle)
	{
		//super.destroySimObject(obstacle.getSimId());
		destroySimObject(obstacle);
		
		this.world.remove(obstacle.getLocation(), obstacle);
		return this.obstacles.remove(obstacle);
	}

	/**
	 *  Add a new creature to the world.
	 *  @param creature The creature.
	 */
	public synchronized Creature addCreature(Creature creature)
	{
		Creature copy;
		
		if(!creatures.containsKey(creature))
		{
			copy = (Creature) creature.clone();
			copy.setLeaseticks(DEFAULT_LEASE_TICKS);
			copy.setWorldWidth(getWidth());
			copy.setWorldHeight(getHeight());

			if (!(copy instanceof Observer)) {			
				copy.setAge(0);
				copy.setPoints(0);
				copy.setLocation(getEmptyLocation(Creature.CREATURE_SIZE.copy()));
				//				if(copy instanceof Hunter)
				//					copy.setVisionRange(5);
				//				else
				copy.setVisionRange(3);
				
				Map props = new HashMap();
				props.put(PROPERTY_ONTOLOGY, copy);
				
				List tasks = new ArrayList();
				tasks.add(new MoveObjectTask(new Vector2Double(0.0)));
				
				
				//copy.setSimId(super.createSimObject(getSimObjectType(copy), props, tasks, new Vector2Int(copy.getLocation().getX(),copy.getLocation().getY()), true, null));
				copy = (Creature) createSimObject(copy, getSimObjectType(copy), props, tasks, Boolean.TRUE, Boolean.TRUE);
				
				this.creatures.put(copy.getSimId(), copy);
				this.world.put(copy.getLocation(), copy);
				this.highscore.add(copy);
			}
			//System.out.println("Environment, creature added: "+copy.getName()+" "+copy.getLocation());
		} else {
			throw new RuntimeException("Creature already exists: "
					+ creature);
		}
		return copy;
	}

	/**
	 *  Remove a creature to the world.
	 *  @param creature The creature.
	 */
	public synchronized boolean removeCreature(Creature creature)
	{
		// Remove tasks of this creature.
		int	tasks	= tasklist.size();
		for(int i=0; i<tasks; i++)
		{
			Object[] params = (Object[])((TaskInfo)tasklist.get(i)).getAction();
			if(creature.equals(params[1]))
			{
				//System.out.println("Removed: "+tasklist.get(i));
				tasklist.remove(i);
			}
		}
		this.pcs.firePropertyChange("taskSize", tasks, tasklist.size());
		if(this.world.containsKey(creature.getLocation()))
			this.world.remove(creature.getLocation(), creature);
		
		//super.destroySimObject(creature.getSimId());
		destroySimObject(creature);
		return this.creatures.remove(creature.getSimId())!=null;

	}

	/**
	 *  Execute a step.
	 */
	public synchronized void executeStep()
	{
		
		// Creatures that already acted in this step.
		Set	acted	= new HashSet();

		Creature[]	creatures	= getCreatures();
		for(int i=0; i<creatures.length; i++)
		{
			creatures[i].setAge(creatures[i].getAge()+1);
			creatures[i].setLeaseticks(creatures[i].getLeaseticks()-1);
			if(creatures[i].getLeaseticks()<0)
				removeCreature(creatures[i]);
		}

		// Perform eat/move tasks.
		TaskInfo[]	tasks	= (TaskInfo[])tasklist.toArray(new TaskInfo[tasklist.size()]);
		for(int i=0; i<tasks.length; i++)
		{
			Object[] params = (Object[])tasks[i].getAction();
			if(params[0].equals("eat"))
			{
				if(!acted.contains(params[1]))
				{
					tasks[i].setResult(new Boolean(eat((Creature)params[1], (WorldObject)params[2])));
					acted.add(params[1]);
				}
				else
				{
					tasks[i].setResult(new Boolean(false));
				}
			}
		}
		for(int i=0; i<tasks.length; i++)
		{
			Object[] params = (Object[])tasks[i].getAction();
			if(params[0].equals("move"))
			{
				if(!acted.contains(params[1]))
				{
					tasks[i].setResult(new Boolean(move((Creature)params[1], (String)params[2])));
					acted.add(params[1]);
				}
				else
				{
					tasks[i].setResult(new Boolean(false));
				}
			}
		}

//		// Place new food.
//		if(age%foodrate==0)
//		{
//			Location	loc	= getEmptyLocation();
//			Location	test= getEmptyLocation();
//			// Make sure there will be some empty location left.
//			if(!loc.equals(test))
//			{
//				addFood(new Food(loc));
//			}
//		}

		tasklist.clear();
		this.pcs.firePropertyChange("taskSize", tasks.length, tasklist.size());

		// Save highscore.
		long	time	= System.currentTimeMillis();
		if(saveinterval>=0 && savetime+saveinterval<=time)
		{
			saveHighscore();
			savetime	= time;
		}
		
		age++;
	}

	/**
	 *  Get the world age.
	 *  @return The age of the world.
	 */
	public int getWorldAge()
	{
	    return age;
	}
	
	/**
	 *  Get the foodrate.
	 *  @return The foodrate.
	 */
	public int getFoodrate()
	{
		// TODO: implement belief get / process get
	    //return foodrate;
		return 1000;
	}
	
	/**
	 *  Set the foodrate. 
	 *  @param foodrate The foodrate.
	 */
	public void setFoodrate(int foodrate)
	{
		// TODO: implement belief change / process change
	    //this.foodrate = foodrate;
	}
	
	/**
	 *  Perform a move.
	 *  @param me The creature.
	 *  @param dir The direction.
	 */
	public boolean move(Creature me, String dir)
	{
		boolean ret = true;
		me	= getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		Location newloc = createLocation(me.getLocation(), dir);
		
		Collection col = world.getCollection(newloc);
		if(col!=null && col.size()==1 && col.iterator().next() instanceof Obstacle)
		{
			ret = false;
		}
		else
		{
			assert agent != null : this + " - no external access provided";

			// request simulation engine move
			IGoal goToDestination = agent.createGoal("sim_go_to_destination");
			goToDestination.getParameter("object_id").setValue(me.getSimId());
			goToDestination.getParameter("destination").setValue(new Vector2Int(newloc.getX(), newloc.getY()));
			goToDestination.getParameter("speed").setValue(Creature.CREATURE_SPEED);
			goToDestination.getParameter("tolerance").setValue(new Vector1Double(0.1));
			agent.dispatchTopLevelGoalAndWait(goToDestination);
			
			// fail move if simulation engine goal has failed
			if (!goToDestination.isSucceeded())
			{
				return false;
			}

			// Move creature in discrete world
			try
			{
				world.remove(me.getLocation(), me);
				me.setLocation(newloc);
				world.put(me.getLocation(), me);
			}
			catch(Exception e)
			{
				//System.out.println("!!! "+me);
				System.out.println(world+" "+me);
				e.printStackTrace();
				// ??
				return false; 
			}
		}
		//block(); todo: make blocking for local case
		return ret;
	}

	/**
	 *  Get the creatures.
	 *  @return The creatures.
	 */
	public Creature[] getCreatures()
	{
		return (Creature[])creatures.values().toArray(new Creature[creatures.size()]);
		
//		ArrayList	al	= new ArrayList();		
//		
//		// lock in appropriate order to avoid deadlocks in simulation engine
//		Map simObjects = super.getSimObjectAccess();
//		synchronized (simObjects) 
//		{
//			Map typedAccess = super.getTypedSimObjectAccess();
//			synchronized (typedAccess) 
//			{
//				al.addAll((Collection) typedAccess.get(OBJECT_TYPE_HUNTER));
//				al.addAll((Collection) typedAccess.get(OBJECT_TYPE_PREY));
//			}
//		}
//		// Convert to array and return.
//		Creature[] ret = new Creature[al.size()];
//		for (int i = 0; i < al.size(); i++) 
//		{
//			ret[i] = (Creature) ((SimObject) al.get(i)).getProperty(PROPERTY_ONTOLOGY);
//		}
//		return ret;
		
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Obstacle[] getObstacles()
	{
		return (Obstacle[])obstacles.toArray(new Obstacle[obstacles.size()]);
		
//		List	ret;
//		Map typedAccess = super.getTypedSimObjectAccess();
//		synchronized (typedAccess) {
//			ret = (List) typedAccess.get(OBJECT_TYPE_OBSTACLE);
//		}
//		// Convert to array and return.
//		return (Obstacle[])ret.toArray(new Obstacle[ret.size()]);
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Food[] getFood()
	{
		return (Food[])food.toArray(new Food[food.size()]);
		
//		List	ret;
//		Map typedAccess = super.getTypedSimObjectAccess();
//		synchronized (typedAccess) {
//			ret = (List) typedAccess.get(OBJECT_TYPE_FOOD);
//		}
//		// Convert to array and return.
//		return (Food[])ret.toArray(new Obstacle[ret.size()]);
	}

	/**
	 *  Get all objects in the world (obstacles, food, and creature).
	 */
	public WorldObject[]	getAllObjects()
	{
	
		// Add obstacles and food to return set.
		ArrayList	ret	= new ArrayList();
		ret.addAll(obstacles);
		ret.addAll(food);

		// Add creatures.
		ret.addAll(creatures.values());

		// Convert to array and return.
		return (WorldObject[])ret.toArray(new WorldObject[ret.size()]);
			
//		// Add obstacles and food to return set.
//		ArrayList	al	= new ArrayList();
//		
//		Map typedAccess = super.getTypedSimObjectAccess();
//		synchronized (typedAccess) {
//			al.addAll((Collection) typedAccess.get(OBJECT_TYPE_FOOD));
//			al.addAll((Collection) typedAccess.get(OBJECT_TYPE_OBSTACLE));
//			al.addAll((Collection) typedAccess.get(OBJECT_TYPE_HUNTER));
//			al.addAll((Collection) typedAccess.get(OBJECT_TYPE_PREY));
//		}
//		
//		// Convert to array and return.
//		WorldObject[] ret = new WorldObject[al.size()];
//		for (int i = 0; i < al.size(); i++) {
//			ret[i] = (WorldObject) ((SimObject) al.get(i)).getProperty(PROPERTY_ONTOLOGY);
//		}
		
			
	}
	
	protected String getSimObjectType(WorldObject wo) {
		if (wo instanceof Hunter)
		{
			return OBJECT_TYPE_HUNTER;
		}
		else if (wo instanceof Prey)
		{
			return OBJECT_TYPE_PREY;
		}
		else if (wo instanceof Food)
		{
			return OBJECT_TYPE_FOOD;
		}
		else if (wo instanceof Obstacle) 
		{
			return OBJECT_TYPE_OBSTACLE;
		}
		else
		{
			return "undefined";
		}
		
	}

	/**
	 *  Create a location.
	 *  @param loc The location.
	 *  @param dir The direction.
	 *  @return The new location.
	 */
	protected Location createLocation(Location loc, String dir)
	{
		// TODO: respect world end, don't use sphere like behavior
		int sizey = getHeight();
		int sizex = getWidth();
		
		int x = loc.getX();
		int y = loc.getY();

		if(RequestMove.DIRECTION_UP.equals(dir))
		{
			y = (sizey+y-1)%sizey;
		}
		else if(RequestMove.DIRECTION_DOWN.equals(dir))
		{
			y = (y+1)%sizey;
		}
		else if(RequestMove.DIRECTION_LEFT.equals(dir))
		{
			x = (sizex+x-1)%sizex;
		}
		else if(RequestMove.DIRECTION_RIGHT.equals(dir))
		{
			x = (x+1)%sizex;
		}

		return new Location(x, y);
	}

	/**
	 *  Get an empty location.
	 *  @return The location.
	 */
	public Location getEmptyLocation(IVector2 edgedistance)
	{
		Location	ret	= null;
		while(ret==null)
		{
			IGoal getPos = agent.createGoal("sim_get_random_position");
			getPos.getParameter("distance").setValue(edgedistance.copy());
			agent.dispatchTopLevelGoalAndWait(getPos);
			IVector2 v = ((IVector2) getPos.getParameter("position").getValue()).copy();
			
			ret	= new Location(v.getXAsInteger(), v.getYAsInteger());
			if(world.containsKey(ret))
			{
				ret	= null;
			}
		}

		return ret;
	}

	/**
	 *  Return the size of the task list.
	 *  @return The task size.
	 */
	public int getTaskSize()
	{
		return tasklist.size();
	}

	/**
	 *  Block a thread until the monitor is notified.
	 * /
	protected void block()
	{
		try
		{
			synchronized(monitor)
			{
				monitor.wait();
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Get the internal representation of a creature.
	 *  If the creature is unknown it gets added to the environment.
	 *  @param creature The creature.
	 *  @return The creature as known in the environment.
	 */
	protected Creature getCreature(Creature creature)
	{
		
//		Creature ret = (Creature)creatures.get(creature);
		
//		Creature ret = null;
//		if (creature.getSimId() != null)
//			ret = (Creature) super.getSimulationObject(creature.getSimId()).getProperty(PROPERTY_ONTOLOGY);	
		
		Creature ret = (Creature)creatures.get(creature.getSimId());
		
		if(ret==null)
		{
			ret = addCreature(creature);
		}
		
		return ret;
	}

	/**
	 *  Get objects near a position.
	 *  @param loc The location.
	 *  @param range The range.
	 */
	protected WorldObject[] getNearObjects(Location loc, int range)
	{
		// TODO: respect world end, don't use sphere like behavior
		int sizey = getHeight();
		int sizex = getWidth();
		
		Collection ret = new ArrayList();
		int x = loc.getX();
		int y = loc.getY();

		for(int i=x-range; i<=x+range; i++)
		{
			for(int j=y-range; j<=y+range; j++)
			{
				Collection tmp = world.getCollection(new Location((i+sizex)%sizex, (j+sizey)%sizey));
				if(tmp!=null)
					ret.addAll(tmp);
			}
		}

		return (WorldObject[])ret.toArray(new WorldObject[ret.size()]);
	}

	/**
	 *  Get the current highscore.
	 *  @return The 10 best creatures.
	 */
	public synchronized Creature[] getHighscore()
	{
		Collections.sort(highscore, new Comparator()
		{
			public int	compare(Object o1, Object o2)
			{
				return ((Creature)o2).getPoints()
						- ((Creature)o1).getPoints();
			}
		});
		List copy = highscore.subList(0, Math.min(highscore.size(), 10));
		return (Creature[])copy.toArray(new Creature[copy.size()]);
	}

	/**
	 *  Save the highscore to a file.
	 */
	public synchronized void saveHighscore()
	{
		try
		{
			String outputFile = "highscore.dmp";
			
			// write as serialized object
			//ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputFile));
			//os.writeObject(getHighscore());
			//os.close();
			
			// write as xml file
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");
			os.write(Nuggets.objectToXML(getHighscore(),this.getClass().getClassLoader()));
			os.close();

		}
		catch(Exception e)
		{
			System.out.println("Error writing hunterprey highscore 'highscore.dmp'.");
			e.printStackTrace();
		}
	}

	/**
	 *  Set the highscore save interval (-1 for autosave off).
	 */
	public void	setSaveInterval(long saveinterval)
	{
		this.saveinterval	= saveinterval;
	}

	/**
	 *  Get the highscore save interval (-1 for autosave off).
	 */
	public long	getSaveInterval()
	{
		return this.saveinterval;
	}

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
