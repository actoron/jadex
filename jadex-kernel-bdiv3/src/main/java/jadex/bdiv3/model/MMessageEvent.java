package jadex.bdiv3.model;

import jadex.bridge.service.types.message.MessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MMessageEvent extends MElement
{
	/** The parameters. */
	protected List<MParameter> parameters;
	
	/** The direction. */
	protected String direction;
	
	/** The message type. */
	protected MessageType type;

	/**
	 *  Get the direction.
	 *  @return The direction
	 */
	public String getDirection()
	{
		return direction;
	}

	/**
	 *  The direction to set.
	 *  @param direction The direction to set
	 */
	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	/**
	 *  Get the type.
	 *  @return The type
	 */
	public MessageType getType()
	{
		return type;
	}

	/**
	 *  The type to set.
	 *  @param type The type to set
	 */
	public void setType(MessageType type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public List<MParameter> getParameters()
	{
		return parameters;
	}
	
	/**
	 *  Get a parameter by name.
	 */
	public MParameter getParameter(String name)
	{
		MParameter ret = null;
		if(parameters!=null && name!=null)
		{
			for(MParameter param: parameters)
			{
				if(param.getName().equals(name))
				{
					ret = param;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Test if goal has a parameter.
	 */
	public boolean hasParameter(String name)
	{
		return getParameter(name)!=null;
	}

	/**
	 *  Set the parameters.
	 *  @param parameters The parameters to set.
	 */
	public void setParameters(List<MParameter> parameters)
	{
		this.parameters = parameters;
	}
	
	/**
	 *  Add a parameter.
	 *  @param parameter The parameter.
	 */
	public void addParameter(MParameter parameter)
	{
		if(parameters==null)
			parameters = new ArrayList<MParameter>();
		this.parameters.add(parameter);
	}
}
