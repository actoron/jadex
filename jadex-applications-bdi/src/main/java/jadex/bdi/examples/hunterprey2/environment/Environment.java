package jadex.bdi.examples.hunterprey2.environment;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.Food;
import jadex.bdi.examples.hunterprey2.Hunter;
import jadex.bdi.examples.hunterprey2.IEnvironment;
import jadex.bdi.examples.hunterprey2.Location;
import jadex.bdi.examples.hunterprey2.Observer;
import jadex.bdi.examples.hunterprey2.Obstacle;
import jadex.bdi.examples.hunterprey2.Prey;
import jadex.bdi.examples.hunterprey2.RequestMove;
import jadex.bdi.examples.hunterprey2.TaskInfo;
import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.examples.hunterprey2.WorldObject;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.commons.SUtil;
import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.collection.MultiCollection;

import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import nuggets.Nuggets;

/**
 *  The environment is the container all objects and creatures.
 */
public class Environment implements IEnvironment
{
	// The world behavior 
	public static final int WORLD_BEHAVIOR_EUCLID = 0;
	public static final int WORLD_BEHAVIOR_TORUS = 1;

	// predefined object types and properties
	public static final String OBJECT_TYPE_OBSTACLE 	= "obstacle"; 
	public static final String OBJECT_TYPE_FOOD 		= "food"; 
	public static final String OBJECT_TYPE_HUNTER 		= "hunter"; 
	public static final String OBJECT_TYPE_PREY 		= "prey";
	public static final String PROPERTY_ONTOLOGY 		= "ontologyObj"; 
	
	//-------- constants --------
	
	/** The default number of lease ticks. */
	public static int	DEFAULT_LEASE_TICKS	= 50;
	
	/** The behavior of the world */
	public static int WORLD_BEHAVIOR = WORLD_BEHAVIOR_TORUS;

	//-------- attributes --------
	
	/** The agent created this environment */
	protected IExternalAccess agent;
	
//	/** The simulation environment to wrap on */
//	protected ISimulationEngine engine;

	/** All world object simulation id's accessible per location. */
	public MultiCollection world;
	
	/** The creatures simId's */
	protected Map creatures;

	/** The obstacles simId's */
	protected Set obstacles;

	/** The prey food simId's */
	protected Set food;

	/** The list for move and eat requests. */
	protected List tasklist;
	
	/** The list for task that needed a sim goal */
	protected List goaltasks;

	/** The helper object for bean events. */
	protected SimplePropertyChangeSupport pcs;

	/** The highscore location. */
	protected List highscore;

	/** The last time the highscore was saved. */
	protected long	savetime;

	/** The interval between saves of highscore (-1 for autosave off). */
	protected long	saveinterval;
	
	/** The foodrate determines how often new food pops up. */
	protected int foodrate;
	
	/** The world age. */
	protected int age;
	
	/** The random number generator to create locations. */
	protected Random	rand = new Random(System.currentTimeMillis());
	
	
	//-------- constructors --------

	/**
	 *  Create a new environment.
	 */
	public Environment(IExternalAccess agent/*, ISimulationEngine engine*/)
	{
		assert agent != null : this + " - no external access provided";
//		assert engine != null : this + " - no engine access provided";
		
		this.agent = agent;
//		this.engine = engine;
		
		this.world = new MultiCollection();
		this.creatures = new HashMap();
		this.obstacles = new HashSet();
		this.food = new HashSet();
		
		this.tasklist = new ArrayList();
		this.goaltasks = new ArrayList();
		
		this.pcs = new SimplePropertyChangeSupport(this);
		
		this.foodrate = 5;
		this.saveinterval	= 5000;
		highscore = loadHighscore();
		
	}
	

	//--- simulation engine support methods -----
	
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
	 * Create a sim object
	 * @return the sim object with new sim id
	 */
	protected WorldObject createSimObject(WorldObject wo, String type, Map properties, List tasks, boolean signalDestruction, ISimulationEventListener listener)
	{
		
		Location l = wo.getLocation();
		
		if (properties == null)
			properties = new HashMap();
		properties.put(PROPERTY_ONTOLOGY, wo);
		
//		wo.setSimId(engine.createSimObject(type, properties, tasks, l.getAsIVector(), signalDestruction, listener));
//		return wo;
		
		final WorldObject fwo = wo;
		final IGoal cg = agent.createGoal("sim_create_object");
		cg.getParameter("type").setValue(type);
		cg.getParameter("properties").setValue(properties);
		cg.getParameter("position").setValue(l.getAsIVector());
		cg.getParameter("tasks").setValue(tasks);
		
		cg.getParameter("signal_destruction").setValue(new Boolean(signalDestruction));
		cg.getParameter("listen").setValue((listener!=null?new Boolean(true):new Boolean(false)));

		agent.dispatchTopLevelGoalAndWait(cg);
		wo.setSimId((Integer) cg.getParameter("object_id").getValue());
		return wo;
		
//		cg.addGoalListener(
//				new IGoalListener()
//				{
//					public void goalAdded(AgentEvent ae)
//					{
//					}
//		
//					public void goalFinished(AgentEvent ae)
//					{
//						IGoal g = (IGoal) ae.getSource();
//						if (g.isSucceeded())
//						{
//							fwo.setSimId((Integer) cg.getParameter("object_id").getValue());
//						}
//						else
//						{
//							// TODO: implement fail
//						}
//					}
//				});
//
//		agent.dispatchTopLevelGoal(cg);
//		return wo;
		
	}

	/**
	 * Destroy a sim object
	 * @param wo
	 * @return
	 */
	protected void destroySimObject(WorldObject wo)
	{
		assert wo.getSimId() == null : "WorldObject without SimId! " + wo;
		
//		engine.destroySimObject(wo.getSimId());
		
		final WorldObject fwo = wo;
		final IGoal cg = agent.createGoal("sim_destroy_object");
		cg.getParameter("object_id").setValue(wo.getSimId());
		
		agent.dispatchTopLevelGoalAndWait(cg);
		wo.setSimId(null);
		
//		cg.addGoalListener(
//				new IGoalListener()
//				{
//					public void goalAdded(AgentEvent ae)
//					{
//					}
//		
//					public void goalFinished(AgentEvent ae)
//					{
//						IGoal g = (IGoal) ae.getSource();
//						if (g.isSucceeded())
//						{
//							fwo.setSimId(null);
//						}
//						else
//						{
//							// TODO: implement failed 
//						}
//					}
//				});
//
//		agent.dispatchTopLevelGoal(cg);

	}
	
	protected IGoal createMoveGoal(Creature me, Location dest) 
	{
		IVector2 position = me.getLocation().getAsIVector();
		IVector2 destination = dest.getAsIVector();
		IGoal goToDest = null;
		
		// update useSetPosition on demand
		boolean useSetPosition = true;
		int dX = position.getX().subtract(destination.getX()).getAsInteger();
		int dY = position.getY().subtract(destination.getY()).getAsInteger();
		if (-1 <= dX && dX <= 1 && -1 <= dY && dY <= 1)
		{
			useSetPosition = false;
		}

		if (WORLD_BEHAVIOR == WORLD_BEHAVIOR_TORUS && useSetPosition)
		{			
			goToDest = agent.createGoal("sim_set_position");
			goToDest.getParameter("object_id").setValue(me.getSimId());
			goToDest.getParameter("position").setValue(destination);
		}
		else
		{
			goToDest = agent.createGoal("sim_go_to_precise_destination");
			goToDest.getParameter("object_id").setValue(me.getSimId());
			goToDest.getParameter("destination").setValue(destination);
			goToDest.getParameter("speed").setValue(Creature.CREATURE_SPEED.copy());
		}

		return goToDest;
	}
	
	/**
	 *  Add a move or eat goal to the queue.
	 */
	private synchronized void addGoalTask(TaskInfo task)
	{
		goaltasks.add(task);
		this.pcs.firePropertyChange("goalTaskSize", goaltasks.size()-1, goaltasks.size());	
	}
	
	/**
	 * Remove a goal from the queue
	 * @param task
	 */
	public synchronized void removeGoalTask(TaskInfo task)
	{
		goaltasks.remove(task);
		this.pcs.firePropertyChange("goalTaskSize", goaltasks.size()+1, goaltasks.size());
	}
	
	/**
	 *  Return the size of the task list.
	 *  @return The task size.
	 */
	public int getGoalTaskSize()
	{
		return goaltasks.size();
	}

	//-------- interface methods --------

	/**
	 *  Move one field upwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveUp(Creature me)
	{
		//return move(me, RequestMove.DIRECTION_UP);
		try
		{
			IGoal mg = move(me, RequestMove.DIRECTION_UP);
			agent.dispatchTopLevelGoalAndWait(mg);
			return mg.isSucceeded();
		} catch (LocationBlockedException e)
		{
			return false;
		}
	}

	/**
	 *  Move one field downwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveDown(Creature me)
	{
		//return move(me, RequestMove.DIRECTION_DOWN);
		try
		{
			IGoal mg = move(me, RequestMove.DIRECTION_DOWN);
			agent.dispatchTopLevelGoalAndWait(mg);
			return mg.isSucceeded();
		} catch (LocationBlockedException e)
		{
			return false;
		}
	}

	/**
	 *  Move one field to the left. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveLeft(Creature me)
	{
		//return move(me, RequestMove.DIRECTION_LEFT);
		try
		{
			IGoal mg = move(me, RequestMove.DIRECTION_LEFT);
			agent.dispatchTopLevelGoalAndWait(mg);
			return mg.isSucceeded();
		} catch (LocationBlockedException e)
		{
			return false;
		}
	}

	/**
	 *  Move one field to the right. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveRight(Creature me)
	{
		//return move(me, RequestMove.DIRECTION_RIGHT);
		try
		{
			IGoal mg = move(me, RequestMove.DIRECTION_RIGHT);
			agent.dispatchTopLevelGoalAndWait(mg);
			return mg.isSucceeded();
		} catch (LocationBlockedException e)
		{
			return false;
		}
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
	public synchronized TaskInfo addEatTask(Creature me, WorldObject obj)
	{
		TaskInfo ret = new TaskInfo(new Object[]{"eat", me, obj});
		tasklist.add(ret);
		this.pcs.firePropertyChange("taskSize", tasklist.size()-1, tasklist.size());
		return ret;
	}

	/**
	 *  Add a move or eat action to the queue.
	 */
	public synchronized TaskInfo addMoveTask(Creature me, String dir)
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
		// return sizex;
		//return engine.getAreaSize().getXAsInteger();
		
		// HACK!
		return Configuration.AREA_SIZE.getXAsInteger();
	}

	/**
	 *  Get the height of the world.
	 */
	public int	getHeight()
	{
		// return sizey;
		//return engine.getAreaSize().getYAsInteger();
		
		//HACK!
		return Configuration.AREA_SIZE.getYAsInteger();
	}

	//-------- management methods --------
	
	/**
	 * Wrapper Method to remove a WorldObject, calls sub methods
	 * removeCreature, removeObstacle, removeFood
	 * @param wo
	 */
	protected void removeWorldObject(WorldObject wo)
	{
		if (wo instanceof Creature) 
		{
			removeCreature((Creature)wo);
		}
		else if (wo instanceof Obstacle)
		{
			removeObstacle((Obstacle)wo);
		}
		else if (wo instanceof Food)
		{
			removeFood((Food)wo);
		}
	}
	
	/**
	 * Wrapper Method to add a WorldObject, calls sub methods
	 * addCreature, addObstacle, addFood
	 * @param wo
	 */
	protected void addWorldObject(WorldObject wo)
	{
		if (wo instanceof Creature) 
		{
			addCreature((Creature)wo);
		}
		else if (wo instanceof Obstacle)
		{
			addObstacle((Obstacle)wo);
		}
		else if (wo instanceof Food)
		{
			addFood((Food)wo);
		}
	}

	/**
	 *  Add a new prey food to the world.
	 *  @param nfood The new food.
	 */
	public void addFood(Food nfood)
	{		
		WorldObject wo = createSimObject(nfood, OBJECT_TYPE_FOOD, null, null, true, null);
		
		this.food.add(wo);
		this.world.put(wo.getLocation(), wo);
	}

	/**
	 *  remove a prey food to the world.
	 *  @param nfood The food.
	 */
	public boolean removeFood(Food nfood)
	{
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
		WorldObject wo = createSimObject(obstacle, OBJECT_TYPE_OBSTACLE, null, null, true, null);
		
		this.obstacles.add(wo);
		this.world.put(obstacle.getLocation(), wo);
	}

	/**
	 *  Remove a  obstacle to the world.
	 *  @param obstacle The obstacle.
	 */
	public boolean removeObstacle(Obstacle obstacle)
	{
		destroySimObject(obstacle);
		
		this.world.remove(obstacle.getLocation(), obstacle);
		return this.obstacles.remove(obstacle);
	}

	/**
	 *  Add a new creature to the world.
	 *  @param creature The creature.
	 */
	public /*synchronized*/ Creature addCreature(Creature creature)
	{
		Creature copy;
		
		if(!creatures.containsKey(creature))
		{
			copy = (Creature) creature.clone();
			copy.setLeaseticks(DEFAULT_LEASE_TICKS);
			copy.setWorldWidth(getWidth());
			copy.setWorldHeight(getHeight());
			
			synchronized (this)
			{
				this.creatures.put(copy, copy);
			}
			
			if (!(copy instanceof Observer)) {			
				copy.setAge(0);
				copy.setPoints(0);
				copy.setLocation(getEmptyLocation(Creature.CREATURE_SIZE.copy()));
				//if(copy instanceof Hunter)
				//	copy.setVisionRange(5);
				//else
				copy.setVisionRange(3);
				
				Map props = new HashMap();
				props.put(PROPERTY_ONTOLOGY, copy);
				
				List tasks = new ArrayList();
				tasks.add(new MoveObjectTask(new Vector2Double(0.0)));
				copy = (Creature) createSimObject(copy, getSimObjectType(copy), props, tasks, true, null);

				synchronized (this)
				{
					this.world.put(copy.getLocation(), copy);
					this.highscore.add(copy);
				}
			}
			//System.out.println("Environment, creature added: "+copy.getName()+" "+copy.getLocation());
		} else {
			throw new RuntimeException("Creature already exists: " + creature);
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
		for(Iterator it=tasklist.iterator(); it.hasNext(); )
		{
			TaskInfo	task	= (TaskInfo) it.next();
			Object[] params = (Object[])task.getAction();
			if(creature.equals(params[1]))
			{
				it.remove();
			}
		}
		
		this.pcs.firePropertyChange("taskSize", tasks, tasklist.size());
		
		if(this.world.containsKey(creature.getLocation()))
			this.world.remove(creature.getLocation(), creature);
		
		destroySimObject(creature);
		return this.creatures.remove(creature)!=null;
	}

	/**
	 *  Execute a step.
	 */
	public synchronized void executeStep()
	{
		System.out.println("executing step " + age);
		
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
					try
					{
						IGoal mg = move((Creature)params[1], (String)params[2]);
						tasks[i].setResult(mg);
						addGoalTask(tasks[i]);
					} 
					catch (LocationBlockedException e)
					{
						tasks[i].setResult(new Boolean(false));
					}
					acted.add(params[1]);
				}
				else
				{
					tasks[i].setResult(new Boolean(false));
				}
			}
		}

		// Place new food.
		if(age%foodrate==0)
		{
			if (food.size() < ((Integer) agent.getBeliefbase().getBelief("max_food").getFact()).intValue())
			{
				Location	loc	= getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
				Location	test= getEmptyLocation(WorldObject.WORLD_OBJECT_SIZE);
				// Make sure there will be some empty location left.
				if(!loc.equals(test))
				{
					addFood(new Food(loc));
				}
			}
			else
			{
//				// hack for testing- remove old food
//				System.out.println("-- removing old food --");
//				for (int i = 0; i < 10; i++)
//				{
//					Food nfood = (Food) food.iterator().next();
//					//System.out.println("remove: " +nfood+ " ? ->"+removeFood(nfood));
//					removeFood(nfood);
//				}
			}
		}

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
	    return foodrate;
	}
	
	/**
	 *  Set the foodrate. 
	 *  @param foodrate The foodrate.
	 */
	public void setFoodrate(int foodrate)
	{
	    this.foodrate = foodrate;
	}
	
	/**
	 *  Perform a move.
	 *  @param me The creature.
	 *  @param dir The direction.
	 */
	public IGoal move(Creature me, String dir) throws LocationBlockedException
	{
		me	= getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		Location newloc = createLocation(me.getLocation(), dir);
		
		Collection col = world.getCollection(newloc);
		if(col!=null && col.size()==1 && col.iterator().next() instanceof Obstacle)
		{
			//return false;
			throw new LocationBlockedException((WorldObject)col.iterator().next());
		}
		else
		{
			// create move goal
			IGoal mg = createMoveGoal(me, newloc);
			
			// move creature in discrete world
			try
			{
				// TODO: maybe move to "updateLocation" method to set location after engine move
				world.remove(me.getLocation(), me);
				me.setLocation(newloc);
				world.put(me.getLocation(), me);
			}
			catch(Exception e)
			{
				System.out.println(world+" "+me);
				e.printStackTrace();
				
				//return false;
				mg = null;
			}

			//return true;
			return mg;
		}
	}

	/**
	 *  Get the creatures.
	 *  @return The creatures.
	 */
	public Creature[] getCreatures()
	{
		return (Creature[])creatures.values().toArray(new Creature[creatures.size()]);
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Obstacle[] getObstacles()
	{
		return (Obstacle[])obstacles.toArray(new Obstacle[obstacles.size()]);
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Food[] getFood()
	{
		return (Food[])food.toArray(new Food[food.size()]);
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
	}
	
	

	/**
	 *  Create a location.
	 *  @param loc The location.
	 *  @param dir The direction.
	 *  @return The new location.
	 */
	protected Location createLocation(Location loc, String dir)
	{
		int sizey = getHeight();
		int sizex = getWidth();
		
		int x = loc.getX();
		int y = loc.getY();
		
		switch (WORLD_BEHAVIOR)
		{
		case WORLD_BEHAVIOR_EUCLID:
		{
			if(RequestMove.DIRECTION_UP.equals(dir))
			{
				y = (y-1 <= sizey ? y-1 : y);
			}
			else if(RequestMove.DIRECTION_DOWN.equals(dir))
			{
				y = (y+1 >= 0 ? y+1 : y);
			}
			else if(RequestMove.DIRECTION_LEFT.equals(dir))
			{
				x = (x-1 >= 0 ? x-1 : x);
			}
			else if(RequestMove.DIRECTION_RIGHT.equals(dir))
			{
				x = (x+1 <= sizex ? x+1 : x);
			}
		}
			break;
		
		case WORLD_BEHAVIOR_TORUS:
		{
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
		}
			break;

		default:
			// create no new location
			break;
		}

		return new Location(x, y);
	}

	/**
	 *  Get an empty location.
	 *  @return The location.
	 */
	public synchronized Location getEmptyLocation(IVector2 edgedistance)
	{
		Location	ret	= null;
		
		while(ret==null)
		{
			ret	= new Location(rand.nextInt(getWidth()), rand.nextInt(getHeight()));
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
	 *  Get the internal representation of a creature.
	 *  If the creature is unknown it gets added to the environment.
	 *  @param creature The creature.
	 *  @return The creature as known in the environment.
	 */
	protected Creature getCreature(Creature creature)
	{
		Creature ret = (Creature)creatures.get(creature);
		
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
		
		int sizey = getHeight();
		int sizex = getWidth();
		
		Collection ret = new ArrayList();
		int x = loc.getX();
		int y = loc.getY();

		switch (WORLD_BEHAVIOR)
		{
		case WORLD_BEHAVIOR_TORUS:
		{
			for(int i=x-range; i<=x+range; i++)
			{
				for(int j=y-range; j<=y+range; j++)
				{
					Collection tmp = world.getCollection(new Location(i, j));
					if(tmp!=null)
						ret.addAll(tmp);
				}
			}
		}
			break;
			
		case WORLD_BEHAVIOR_EUCLID:
		{
			int minx = (x-range>=0 ? x-range : 0 );
			int maxx = (x+range<=sizex ? x+range : sizex);
			
			int miny = (y-range>=0 ? y-range : 0 );
			int maxy = (y+range<=sizey ? y+range : sizey);
			
			for(int i=minx; i<=maxx; i++)
			{
				for(int j=miny; j<=maxy; j++)
				{
					Collection tmp = world.getCollection(new Location(i, j));
					if(tmp!=null)
						ret.addAll(tmp);
				}
			}
		}
			break;
			
		default:
			// no vision :-)
			break;
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
	 *  Load the highscore from a file.
	 */
	private List loadHighscore()
	{
		List highscore_ = null;
		
		// Read highscore list.
		InputStream is = null;
		try
		{
//			InputStream tmp = SUtil.getResource("highscore.dmp", Environment.class.getClassLoader());
//			ObjectInputStream is = new ObjectInputStream(tmp);
//			this.highscore = SUtil.arrayToList(is.readObject());
//			is.close();
			
			is = SUtil.getResource("highscore.dmp", Environment.class.getClassLoader());
			StringBuffer out = new StringBuffer();
			byte[] b = new byte[4096];
			for(int n; (n = is.read(b)) != -1;) 
			{
				out.append(new String(b, 0, n));
			}
			highscore_ = SUtil.arrayToList(Nuggets.objectFromXML(out.toString(), Environment.class.getClassLoader()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			System.out.println(e);
			highscore_ = new ArrayList();
		}
		finally
		{
			if(is!=null)
			{
				try
				{
					is.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return highscore_;
	}
	
	/**
	 *  Save the highscore to a file.
	 */
	public synchronized void saveHighscore()
	{
		OutputStreamWriter os = null;
		try
		{
			String outputFile = "highscore.dmp";
			
			// write as serialized object
			//ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputFile));
			//os.writeObject(getHighscore());
			//os.close();
			
			// write as xml file
			os = new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8");
			os.write(Nuggets.objectToXML(getHighscore(),this.getClass().getClassLoader()));
			os.close();

//			System.out.println("Saved highscore.");
		}
		catch(Exception e)
		{
			System.out.println("Error writing hunterprey highscore 'highscore.dmp'.");
			e.printStackTrace();
		}
		finally
		{
			if(os!=null)
			{
				try
				{
					os.close();
				}
				catch(Exception e)
				{
				}
			}
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
    
    // --- inner class ---
    
    /**
     * Excetion to block a World {@link Location}
     */
    class LocationBlockedException extends Exception {

    	// --- attributes ---
    	/** The Object that blocks the location */
    	protected WorldObject blockingObject;
    	
    	//--- constructor ---
    	/**
    	 * Default Constructor
    	 * @param WorldObject that blocks the Location
    	 */
		public LocationBlockedException(WorldObject obj)
		{
			super();
			this.blockingObject = obj;
		}

		//--- access methods ---
		
		/**
		 * Access the blocking Object
		 * @return {@link WorldObject} that blocks the requested location
		 */
		public WorldObject getBlockingObject()
		{
			return blockingObject;
		}
		
		
		
    	
    }
}
