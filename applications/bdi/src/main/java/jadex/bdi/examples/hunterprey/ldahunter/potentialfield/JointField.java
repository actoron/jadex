/*
 * Created on Sep 17, 2004
 */
package jadex.bdi.examples.hunterprey.ldahunter.potentialfield;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

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
	ISpaceObject myself;
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
	public void addCreature(ISpaceObject creature)
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
		IVector2 loc = (IVector2)creature.getProperty(Space2D.PROPERTY_POSITION);
		f.update(loc.getXAsInteger(), loc.getYAsInteger(), round);
	}

	/**
	 * @param f
	 */
	public void addFood(ISpaceObject f)
	{
		IVector2 loc = (IVector2)f.getProperty(Space2D.PROPERTY_POSITION);
		food.food[loc.getXAsInteger()][loc.getYAsInteger()] += 1.0;
	}

	/**
	 * @param o
	 */
	public void addObstacle(ISpaceObject o)
	{
		IVector2 loc = (IVector2)o.getProperty(Space2D.PROPERTY_POSITION);
		field.obstacles[loc.getXAsInteger()][loc.getYAsInteger()] = true;
	}

	/**
	 * @param wo
	 */
	public void add(ISpaceObject wo)
	{
		if(wo.getType().equals("hunter") || wo.getType().equals("prey"))
		{
			addCreature(wo);
		}
		else if(wo.getType().equals("obstacle"))
		{
			addObstacle(wo);
		}
		else if(wo.getType().equals("food"))
		{
			addFood(wo);
		}
	}

	/**
	 * @param w world objects
	 * @param myself
	 */
	public void update(final ISpaceObject[] w, ISpaceObject myself)
	{
		IVector2 myLoc = (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
		this.myX = myLoc.getXAsInteger();
		this.myY = myLoc.getYAsInteger();
		food.clearRange(myX, myY, range);
		field.clearRange(myX, myY, range, round);
		for(int i = w.length; i-->0;)
		{
			ISpaceObject wo = w[i];
			if(!myself.equals(wo)) add(wo);
		}

		round++; // update round

		field.calcDistance(myX, myY);
		calcDesire();
	}

	/**
	 * @param c
	 */
	public void eaten(ISpaceObject c)
	{
		eaten.add(c);
	}

	/**
	 * @return the best location
	 */
	public IVector2 getBestLocation()
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
		return new Vector2Int(bx, by);
	}

	/**
	 * @param loc
	 * @return true if location changed
	 */
	public boolean getNearerLocation(IVector2 loc)
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
				if(m.c.getType().equals("hunter"))
				{
					addModel(ev.hunter, m);
				}
				else if(m.c.getType().equals("prey"))
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
