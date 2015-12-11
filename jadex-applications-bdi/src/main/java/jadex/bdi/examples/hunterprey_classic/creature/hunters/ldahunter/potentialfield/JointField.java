/*
 * Created on Sep 17, 2004
 */
package jadex.bdi.examples.hunterprey_classic.creature.hunters.ldahunter.potentialfield;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jadex.bdi.examples.hunterprey_classic.Creature;
import jadex.bdi.examples.hunterprey_classic.Food;
import jadex.bdi.examples.hunterprey_classic.Hunter;
import jadex.bdi.examples.hunterprey_classic.Location;
import jadex.bdi.examples.hunterprey_classic.Obstacle;
import jadex.bdi.examples.hunterprey_classic.Prey;
import jadex.bdi.examples.hunterprey_classic.WorldObject;

/**
 *
 */
public class JointField extends HashMap
{
	final int h;
	final int w;
	final int range;
	final Evaluator ev;
	final HashSet eaten;
	Creature myself;
	int myX;
	int myY;
	int round;
	final FoodModel food;
	public final FieldModel field;

	/**
	 * @param h
	 * @param w
	 * @param range
	 * @param ev
	 */
	public JointField(final int h, final int w, final int range, Evaluator ev)
	{
		this.h = h;
		this.w = w;
		this.range = range;
		this.ev = ev;
		round = 0;
		eaten = new HashSet();
		desire = new double[w][h];
		field = new FieldModel(w, h);
		food = new FoodModel(w, h);
	}

	/**
	 * @param creature
	 */
	public void add(Creature creature)
	{
		CreatureModel f = (CreatureModel)get(creature);
		if(f==null)
		{
			f = new CreatureModel(creature, w, h);
			put(creature, f);
			//System.out.println("New: "+creature);
		}
		else
		{
			eaten.remove(creature);
		}
		Location loc = creature.getLocation();
		f.update(loc.getX(), loc.getY(), round);
	}

	/**
	 * @param f
	 */
	public void add(Food f)
	{
		Location loc = f.getLocation();
		food.food[loc.getX()][loc.getY()] += 1.0;
	}

	/**
	 * @param o
	 */
	public void add(Obstacle o)
	{
		Location loc = o.getLocation();
		field.obstacles[loc.getX()][loc.getY()] = true;
	}

	/**
	 * @param wo
	 */
	public void add(WorldObject wo)
	{
		if(wo instanceof Creature)
		{
			add((Creature)wo);
		}
		else if(wo instanceof Obstacle)
		{
			add((Obstacle)wo);
		}
		else if(wo instanceof Food)
		{
			add((Food)wo);
		}
	}

	/**
	 * @param w world objects
	 * @param myself
	 */
	public void update(final WorldObject[] w, Creature myself)
	{
		Location myLoc = myself.getLocation();
		this.myX = myLoc.getX();
		this.myY = myLoc.getY();
		food.clearRange(myX, myY, range);
		field.clearRange(myX, myY, range, round);
		for(int i = w.length; i-->0;)
		{
			WorldObject wo = w[i];
			if(!myself.equals(wo)) add(wo);
		}

		round++; // update round

		field.calcDistance(myX, myY);
		calcDesire();
	}

	/**
	 * @param c
	 */
	public void eaten(Creature c)
	{
		eaten.add(c);
	}

	/**
	 * @return the best location
	 */
	public Location getBestLocation()
	{
		double best = desire[myX][myY];
		int bx = myX;
		int by = myY;

		for(int i = w; i-->0;)
		{
			for(int j = h; j-->0;)
			{
				if(!field.obstacles[i][j])
				{
					double q = desire[i][j]+0.01f*(w*h-field.distance[i][j])/w*h;
					if(q>best)
					{
						best = q;
						bx = i;
						by = j;
					}
				}
			}
		}
		return new Location(bx, by);
	}

	/**
	 * @param loc
	 * @return true if location changed
	 */
	public boolean getNearerLocation(Location loc)
	{
		return field.getNearerLocation(loc);
	}


	/**
	 * <code>desire</code>
	 */
	public final double[][] desire;

	/**
	 * <code>maxDesire</code> maximum value in desire
	 */
	public double maxDesire;

	/**
	 * <code>minDesire</code> minimum value in desire > 0
	 */
	public double minDesire;

	/**
	 *
	 */
	public void calcDesire()
	{
		// calc food and explore
		for(int i = w; i-->0;)
		{
			for(int j = h; j-->0;)
			{
				if(field.obstacles[i][j])
				{
					desire[i][j] = Double.NEGATIVE_INFINITY;
				}
				else
				{

					desire[i][j] = ev.food*food.food[i][j]
							+ev.explore*(Math.min(round-field.visits[i][j], w*h));
				}
			}
		}
		// calc hunters and preys
		Iterator it = keySet().iterator();
		while(it.hasNext())
		{
			Object o = it.next();
			CreatureModel m = (CreatureModel)get(o);
			if(!eaten.contains(m.c))
			{
				if(m.c instanceof Hunter)
				{
					addModel(ev.hunter, m);
				}
				else if(m.c instanceof Prey)
				{
					addModel(ev.prey, m);
				}
			}
		}
		// maxDesire
		double d;
		maxDesire = 0.0;
		minDesire = Double.MAX_VALUE;
		for(int i = w; i-->0;)
		{
			for(int j = h; j-->0;)
			{
				d = desire[i][j];
				if(d>maxDesire) maxDesire = d;
				if(d<minDesire && d>=0.0) minDesire = d;
			}
		}
	}

	/**
	 * @param factor
	 * @param model
	 */
	protected void addModel(double factor, CreatureModel model)
	{
		for(int i = w; i-->0;)
		{
			for(int j = h; j-->0;)
			{
				if(!field.obstacles[i][j])
					desire[i][j] += factor*model.getProbability(i, j, round);
			}
		}
	}

}
