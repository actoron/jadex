package jadex.bpmn.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  An annotation serves for storing extra information about the model.
 *  It saves this information in annotation details. 
 */
public class MAnnotation extends MIdElement
{
	//-------- attributes --------
	
	/** The type. */
	protected String type;
	
	/** The source. */
	protected String source;
	
	/** The details. */
	protected List details;
	
	//-------- methods --------
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public String getSource()
	{
		return this.source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(String source)
	{
		this.source = source;
	}
	
	/**
	 *  Add a detail.
	 *  @param detail The detail.
	 */
	public void addDetail(MAnnotationDetail detail)
	{
		if(details==null)
			details = new ArrayList();
		details.add(detail);
	}
	
	/**
	 *  Remove a detail.
	 *  @param detail The detail.
	 */
	public void removeDetail(MAnnotationDetail detail)
	{
		if(details!=null)
			details.remove(detail);
	}

	/**
	 *  Get the details.
	 *  @return The details.
	 */
	public List getDetails()
	{
		return this.details;
	}
	
}
