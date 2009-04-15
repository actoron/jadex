package jadex.adapter.base.envsupport;

import java.util.ArrayList;
import java.util.List;

/**
 *  An environment view is a cutout of the space objects, i.e.
 *  similar to a database view.
 */
public class MEnvView
{
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The implementation class. */
	protected Class clazz;

	/** The themes. */
	protected List themes;
	
	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public Class getClazz()
	{
		return this.clazz;
	}

	/**
	 *  Set the class name.
	 *  @param name The class name to set.
	 */
	public void setClazz(Class clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Add a theme.
	 *  @param theme The themes.
	 */
	public void addMEnvTheme(MEnvTheme theme)
	{
		if(themes==null)
			themes = new ArrayList();
		themes.add(theme);	
	}
	
	/**
	 *  Get the themes.
	 *  @return The themes.
	 */
	public List getMEnvThemes()
	{
		return themes;
	}
}
