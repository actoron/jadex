package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMTypedElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

/**
 *  Typed element flyweight.
 */
public class MTypedElementFlyweight extends MReferenceableElementFlyweight implements IMTypedElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new typed element flyweight.
	 */
	public MTypedElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
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
					clazz = (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class);
				}
			};
			return invoc.clazz;
		}
		else
		{
			return (Class)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class);
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_classname);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_classname);
		}
	}
	
	/**
	 *  Get the update rate.
	 *  @return The update rate.
	 */
	public long getUpdateRate()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Long ur = (Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_updaterate);
					if(ur!=null)
						longint=ur.longValue();
				}
			};
			return invoc.longint;
		}
		else
		{
			Long ur = (Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_updaterate);
			return ur!=null? ur.longValue(): 0;
		}
	}
	
	
	/**
	 *  Get the evaluation mode.
	 *  @return The evaluation mode. 
	 */
	public String getEvaluationMode()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_evaluationmode);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_evaluationmode);
		}
	}
}
