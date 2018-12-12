/*
 * Created on Sep 17, 2004
 */
package jadex.bdi.examples.hunterprey_classic.creature.hunters.ldahunter.potentialfield;

import jadex.bdi.examples.hunterprey_classic.Creature;

/**
 * 
 */
public class CreatureModel
{
	final int w;
	final int h;
	final Creature c;

	/**
	 * @param creature
	 * @param w
	 * @param h
	 */
	public CreatureModel(Creature creature, final int w, final int h)
	{
		this.w = w;
		this.h = h;
		this.c = creature;
	}

	/**
	 * <code>x</code> last x of creature
	 */
	public int x;
	/**
	 * <code>y</code> last y of creature
	 */
	public int y;
	/**
	 * <code>round</code> last round updated
	 */
	public int round;


	/**
	 * @param px
	 * @param py
	 * @param r > round
	 * @return the probability the creature is in px,py in round r
	 */
	public double getProbability(int px, int py, int r)
	{
		final int dr = r-round;
		int d = 1+dr-Math.abs(x-px)%w-Math.abs(y-py)%h;
		d = d>0? d: 0; // cut
		final int dr2 = dr*dr;
		return 3.0*d/(dr2*dr*2+dr2*6+dr*7+3);
	}

	/**
	 * @param x2
	 * @param y2
	 * @param r
	 */
	public void update(int x2, int y2, int r)
	{
		x = x2;
		y = y2;
		round = r;
	}
}
