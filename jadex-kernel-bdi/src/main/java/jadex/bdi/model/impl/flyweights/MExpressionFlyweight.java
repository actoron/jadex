package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for expression model.
 */
public class MExpressionFlyweight extends MReferenceableElementFlyweight implements IMExpression
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MExpressionFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the expression language.
	 *  @return The language.
	 */
	public String getLanguage()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_language);
		}
	}
	
	/**
	 *  Get the expression content.
	 *  @return The content.
	 */
	public Object getContent()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_content);
				}
			};
			return invoc.object;
		}
		else
		{
			return getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_content);
		}
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
	public Class getClazz()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					clazz = (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class);
				}
			};
			return invoc.clazz;
		}
		else
		{
			return (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_class);
		}
	}
	
	/**
	 *  Get the class name.
	 *  @return The class name. 
	 */
	public String getClassname()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_classname);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_classname);
		}
	}
	
	/**
	 *  Get the variable name.
	 *  @return The variable name.
	 */
	public String getVariable()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.expression_has_variable);
		}
	}
}
