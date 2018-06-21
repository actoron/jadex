package jadex.bdiv3.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Extended parameter to store mappings.
 */
// only for xml
public class MPlanParameter	extends MParameter
{
	//-------- attributes --------
	
	/** The goal mappings. */
	protected List<String> goalmappings;
	
	/** The message event mappings. */
	protected List<String> messageeventmappings;
	
	/** The internal event mappings. */
	protected List<String> internaleventmappings;
	
	//-------- methods --------
	
	/**
	 *  Get the goal mappings.
	 */
	public List<String>	getGoalMappings()
	{
		return goalmappings;
	}
	
	/**
	 *  Set the goal mappings.
	 */
	public void	setGoalMappings(List<String> goalmappings)
	{
		this.goalmappings	= goalmappings;
	}
	
	/**
	 *  Add a goal mapping.
	 */
	public void	addGoalMapping(String mapping)
	{
		// Convert capability separator.
		int	idx	= mapping.lastIndexOf('.');
		if(idx==-1)
		{
			throw new RuntimeException("Parameter mapping must follow the form <elementname>.<parametername>: "+name+", "+mapping);
		}
		String	param	= mapping.substring(idx+1);
		String	elem	= mapping.substring(0, idx);
		mapping	= MElement.internalName(elem) + "." + param;
		
		if(goalmappings==null)
		{
			goalmappings	= new ArrayList<String>();
		}
		goalmappings.add(mapping);
	}
	
	/**
	 *  Get the message event mappings.
	 */
	public List<String>	getMessageEventMappings()
	{
		return messageeventmappings;
	}
	
	/**
	 *  Set the message event mappings.
	 */
	public void	setMessageEventMappings(List<String> messageeventmappings)
	{
		this.messageeventmappings	= messageeventmappings;
	}
	
	/**
	 *  Add a message event mapping.
	 */
	public void	addMessageEventMapping(String mapping)
	{
		// Convert capability separator.
		int	idx	= mapping.lastIndexOf('.');
		if(idx==-1)
		{
			throw new RuntimeException("Parameter mapping must follow the form <elementname>.<parametername>: "+name+", "+mapping);
		}
		String	param	= mapping.substring(idx+1);
		String	elem	= mapping.substring(0, idx);
		mapping	= MElement.internalName(elem) + "." + param;
		
		if(messageeventmappings==null)
		{
			messageeventmappings	= new ArrayList<String>();
		}
		messageeventmappings.add(mapping);
	}
	
	/**
	 *  Get the internal event mappings.
	 */
	public List<String>	getInternalEventMappings()
	{
		return internaleventmappings;
	}
	
	/**
	 *  Set the internal event mappings.
	 */
	public void	setInternalEventMappings(List<String> internaleventmappings)
	{
		this.internaleventmappings	= internaleventmappings;
	}
	
	/**
	 *  Add a internal event mapping.
	 */
	public void	addInternalEventMapping(String mapping)
	{
		// Convert capability separator.
		int	idx	= mapping.lastIndexOf('.');
		if(idx==-1)
		{
			throw new RuntimeException("Parameter mapping must follow the form <elementname>.<parametername>: "+name+", "+mapping);
		}
		String	param	= mapping.substring(idx+1);
		String	elem	= mapping.substring(0, idx);
		mapping	= MElement.internalName(elem) + "." + param;
		
		if(internaleventmappings==null)
		{
			internaleventmappings	= new ArrayList<String>();
		}
		internaleventmappings.add(mapping);
	}
}
