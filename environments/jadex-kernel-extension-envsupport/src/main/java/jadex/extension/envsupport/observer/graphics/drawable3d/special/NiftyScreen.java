package jadex.extension.envsupport.observer.graphics.drawable3d.special;

public class NiftyScreen
{
	String name;
	String path;
	boolean isStartScreen;
	
	
	public NiftyScreen(String name, String path, boolean isStartScreen)
	{
		this.name = name;
		this.path = path;
		this.isStartScreen = isStartScreen;
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
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	/**
	 * @return the isStartScreen
	 */
	public boolean isStartScreen()
	{
		return isStartScreen;
	}
	/**
	 * @param isStartScreen the isStartScreen to set
	 */
	public void setStartScreen(boolean isStartScreen)
	{
		this.isStartScreen = isStartScreen;
	}

}
