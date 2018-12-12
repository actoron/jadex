/*
 * Created on Sep 17, 2004
 */
package jadex.bdi.examples.hunterprey.ldahunter.potentialfield;

/**
 * 
 */
public class Evaluator
{
	final double food;
	final double prey;
	final double hunter;
	final double explore;

	/**
	 * @param food
	 * @param prey
	 * @param hunter
	 * @param explore
	 */
	public Evaluator(final double food, final double prey, final double hunter, final double explore)
	{
		this.food = food;
		this.prey = prey;
		this.hunter = hunter;
		this.explore = explore;
	}

	/**
	 * <code>SHEEP</code>
	 */
	public static final Evaluator SHEEP = new Evaluator(1.0, -0.1, -1.0, 0.1);

	/**
	 * <code>WOLF</code>
	 */
	public static final Evaluator WOLF = new Evaluator(0.0, 1.0, 0.0, 0.2);
}
