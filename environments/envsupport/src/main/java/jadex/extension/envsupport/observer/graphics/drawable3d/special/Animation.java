package jadex.extension.envsupport.observer.graphics.drawable3d.special;

import jadex.javaparser.IParsedExpression;

/**
 * Dataholder for Animations
 */
public class Animation
{
	protected String name;
	
	protected String channel;
	
	protected boolean loop;

	protected float speed;
	
	protected IParsedExpression cond;
	
	
	public Animation(String name, String channel, boolean loop, double speed, IParsedExpression cond)
	{
		this.name = name;
		this.channel = channel;
		this.loop = loop;
		this.cond = cond;
		this.speed = (float) speed;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the channel
	 */
	public String getChannel()
	{
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel)
	{
		this.channel = channel;
	}

	/**
	 * @return the loop
	 */
	public boolean isLoop()
	{
		return loop;
	}

	/**
	 * @param loop the loop to set
	 */
	public void setLoop(boolean loop)
	{
		this.loop = loop;
	}

	/**
	 * @return the animationCondition
	 */
	public IParsedExpression getAnimationCondition()
	{
		return cond;
	}

	/**
	 * @param animationCondition the animationCondition to set
	 */
	public void setAnimationCondition(IParsedExpression animationCondition)
	{
		this.cond = animationCondition;
	}

	/**
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
