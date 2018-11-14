package jadex.extension.envsupport.evaluation;

import java.util.List;


/**
 *  An object source is used from a data provider 
 *  to fetch all objects from a data source.
 *  It calculates the cartesian products from all row providers. 
 */
public interface IObjectSource
{
	/**
	 *  Get all objects from the data source.
	 *  @return All objects from the data source.
	 */
	public List getObjects();

	/**
	 *  Get the source name.
	 *  @return The source name.
	 */
	public String getSourceName();
}
