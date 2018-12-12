package jadex.bdiv3.model;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  Model element for a message.
 */
public class MMessageEvent extends MProcessableElement
{
	public static Map<String, Direction> dirs = new HashMap<String, MMessageEvent.Direction>();

	/** The message direction. */
	public enum Direction
	{
		SEND("send"),
		RECEIVE("receive"),
		SENDRECEIVE("send_receive");
		
		protected String str;
		
		/**
		 *  Create a new direction
		 */
		Direction(String str)
		{
			this.str = str;
			dirs.put(str, this);
		} 
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
		
		/**
		 * 
		 */
		public static Direction getDirection(String name)
		{
			return dirs.get(name);
		}
	}
	
//	/** The parameters. */
//	protected List<MParameter> parameters;
	
	/** The direction. */
	protected Direction direction = Direction.SENDRECEIVE; // default of BDI XML schema
	
	/** The message type. */
	protected ClassInfo type;
	
	/** The spec. degree. */
	protected int degree;

	/** The match expression. */
	protected UnparsedExpression matchexp;
	
	/**
	 *  Create a new message event.
	 *  @param name
	 *  @param posttoall
	 *  @param randomselection
	 *  @param excludemode
	 *  @param type
	 */
	public MMessageEvent()
	{
		// bean constructor
	}
	
//	/**
//	 * @param name
//	 * @param posttoall
//	 * @param randomselection
//	 * @param excludemode
//	 * @param type
//	 */
//	public MMessageEvent(String name, boolean posttoall, boolean randomselection, String excludemode, MessageType type)
//	{
//		super(name, posttoall, randomselection, excludemode);
//		this.type = type;
//	}

	/**
	 *  Get the direction.
	 *  @return The direction
	 */
	public Direction getDirection()
	{
		return direction;
	}

	/**
	 *  The direction to set.
	 *  @param direction The direction to set
	 */
	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}
	
	/**
	 *  Get the type.
	 *  @return The type
	 */
	public ClassInfo getType()
	{
		return type;
	}

	/**
	 *  The type to set.
	 *  @param type The type to set
	 */
	public void setType(ClassInfo type)
	{
		this.type = type;
	}
	
//	/**
//	 *  Get the parameters.
//	 *  @return The parameters.
//	 */
//	public List<MParameter> getParameters()
//	{
//		return parameters;
//	}
//	
//	/**
//	 *  Get a parameter by name.
//	 */
//	public MParameter getParameter(String name)
//	{
//		MParameter ret = null;
//		if(parameters!=null && name!=null)
//		{
//			for(MParameter param: parameters)
//			{
//				if(param.getName().equals(name))
//				{
//					ret = param;
//					break;
//				}
//			}
//		}
//		return ret;
//	}
//	
//	/**
//	 *  Test if goal has a parameter.
//	 */
//	public boolean hasParameter(String name)
//	{
//		return getParameter(name)!=null;
//	}
//
//	/**
//	 *  Set the parameters.
//	 *  @param parameters The parameters to set.
//	 */
//	public void setParameters(List<MParameter> parameters)
//	{
//		this.parameters = parameters;
//	}
//	
//	/**
//	 *  Add a parameter.
//	 *  @param parameter The parameter.
//	 */
//	public void addParameter(MParameter parameter)
//	{
//		if(parameters==null)
//			parameters = new ArrayList<MParameter>();
//		this.parameters.add(parameter);
//	}
	
	/**
	 *  Get the matchExpression.
	 *  @return The matchExpression
	 */
	public UnparsedExpression getMatchExpression()
	{
		return matchexp;
	}

	/**
	 *  The match expression to set.
	 *  @param matchexp The matchExpression to set
	 */
	public void setMatchExpression(UnparsedExpression matchexp)
	{
		this.matchexp = matchexp;
	}

	/**
	 *  Get the specialization degree.
	 *  @return The degree.
	 */
	public int getSpecializationDegree()
	{
		if(degree==-1)
		{
			// Calculate specificity by summing up fixed parameters and parameter sets.
			if(parameters!=null)
			{
				for(MParameter param: parameters)
				{
					if(param.getDirection().equals(jadex.bdiv3.model.MParameter.Direction.FIXED)
						&& param.getDefaultValue()!=null)
//						&& (parametersets[i].getDefaultValuesExpression()!=null || parametersets[i].getDefaultValues().length>0))
					{
						degree++;
					}
				}
			}
			
			// Messages with match expression have higher degree.
			if(getMatchExpression()!=null)
				degree++;
			
			if(degree==-1)
				degree = 0;
		}
		return degree;
	}
}
