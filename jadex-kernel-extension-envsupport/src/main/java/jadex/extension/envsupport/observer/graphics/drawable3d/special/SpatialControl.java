package jadex.extension.envsupport.observer.graphics.drawable3d.special;

import jadex.javaparser.IParsedExpression;

/**
 * Dataholder for custom Spatial Controls
 */
public class SpatialControl
{
	protected String			classpath;

	protected IParsedExpression	cond;

	public SpatialControl(String classpath, IParsedExpression cond)
	{
		this.classpath = classpath;
		this.cond = cond;
	}

	/**
	 * @return the classpath
	 */
	public String getClasspath()
	{
		return classpath;
	}

	/**
	 * @param classpath the classpath to set
	 */
	public void setClasspath(String classpath)
	{
		this.classpath = classpath;
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
