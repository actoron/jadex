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
	List<String>	goalmappings;
	
	/** The message event mappings. */
	List<String>	messageeventmappings;
	
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
	public void	getGoalMappings(List<String> goalmappings)
	{
		this.goalmappings	= goalmappings;
	}
	
	/**
	 *  Add a goal mapping.
	 */
	public void	addGoalMapping(String mapping)
	{
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
	public void	getMessageEventMappings(List<String> messageeventmappings)
	{
		this.messageeventmappings	= messageeventmappings;
	}
	
	/**
	 *  Add a message event mapping.
	 */
	public void	addMessageEventMapping(String mapping)
	{
		if(messageeventmappings==null)
		{
			messageeventmappings	= new ArrayList<String>();
		}
		messageeventmappings.add(mapping);
	}
}
