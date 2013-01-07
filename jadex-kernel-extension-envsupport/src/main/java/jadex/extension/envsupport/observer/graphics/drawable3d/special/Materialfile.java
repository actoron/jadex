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

	public Materialfile(String part, String path, boolean useAlpha, IParsedExpression cond) {
		super();
		this.part = part;
		this.useAlpha = useAlpha;
		this.path = path;
		this.cond = cond;
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

}
