package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Animation
{
	protected String name;
	
	protected String channel;
	
	protected boolean loop;

	protected IParsedExpression cond;
	
	public Animation(String name, String channel, boolean loop, IParsedExpression cond)
	{
		this.name = name;
		this.channel = channel;
		this.loop = loop;
		this.cond = cond;
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

}
