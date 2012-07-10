package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

public class Sound3d extends Primitive3d
{

	protected String soundfile;
	
	protected boolean loop;
	
	protected double volume;
	
	protected boolean continuosly;
	
	protected boolean positional;

	protected IParsedExpression cond;

	public Sound3d(String soundfile, boolean loop, double volume, boolean continuosly, boolean positional, IParsedExpression cond)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_SOUND;
		this.soundfile = soundfile;
		this.loop = loop;
		this.volume = volume;
		this.continuosly = continuosly;
		this.positional = positional;
		this.cond = cond;
	}

	/**
	 * @return the soundfile
	 */
	public String getSoundfile()
	{
		return soundfile;
	}

	/**
	 * @param soundfile the soundfile to set
	 */
	public void setSoundfile(String soundfile)
	{
		this.soundfile = soundfile;
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
	 * @return the volume
	 */
	public double getVolume()
	{
		return volume;
	}

	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume)
	{
		this.volume = volume;
	}

	/**
	 * @return the continuosly
	 */
	public boolean isContinuosly()
	{
		return continuosly;
	}

	/**
	 * @param continuosly the continuosly to set
	 */
	public void setContinuosly(boolean continuosly)
	{
		this.continuosly = continuosly;
	}

	/**
	 * @return the positional
	 */
	public boolean isPositional()
	{
		return positional;
	}

	/**
	 * @param positional the positional to set
	 */
	public void setPositional(boolean positional)
	{
		this.positional = positional;
	}

	/**
	 * @return the cond
	 */
	public IParsedExpression getCond()
	{
		return cond;
	}

	/**
	 * @param cond the cond to set
	 */
	public void setCond(IParsedExpression cond)
	{
		this.cond = cond;
	}
	
	
}
