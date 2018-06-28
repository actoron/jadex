package jadex.extension.envsupport.observer.graphics.drawable3d.special;

import jadex.javaparser.IParsedExpression;

/**
 * Dataholder for Material informations
 */
public class Materialfile {
	
	protected String part;
	
	protected String path;
	
	protected IParsedExpression cond;
	
	protected boolean useAlpha;
	
	protected SpecialAction specialAction;

	public Materialfile(String part, String path, boolean useAlpha, SpecialAction specialaction, IParsedExpression cond) {
		super();
		this.part = part;
		this.useAlpha = useAlpha;
		this.path = path;
		this.cond = cond;
		this.specialAction = specialaction;
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return part;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.part = name;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the useAlpha
	 */
	public boolean isUseAlpha() {
		return useAlpha;
	}

	/**
	 * @param useAlpha the useAlpha to set
	 */
	public void setUseAlpha(boolean useAlpha) {
		this.useAlpha = useAlpha;
	}

	/**
	 * @return the specialAction
	 */
	public SpecialAction getSpecialAction()
	{
		return specialAction;
	}

	/**
	 * @param specialAction the specialAction to set
	 */
	public void setSpecialAction(SpecialAction specialAction)
	{
		this.specialAction = specialAction;
	}

}
