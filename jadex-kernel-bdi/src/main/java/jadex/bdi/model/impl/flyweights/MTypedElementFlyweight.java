package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMTypedElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMETypedElement;
import jadex.rules.state.IOAVState;

/**
 *  Typed element flyweight.
 */
public class MTypedElementFlyweight extends MReferenceableElementFlyweight implements IMTypedElement, IMETypedElement
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz. 
	 */
	public void setClazz(final Class clz)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class, clz);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_class, clz);
		}
	}
	
//	/**
//	 *  Set the class name.
//	 *  @param name The class name. 
//	 */
//	public void setClassname(final String name)
//	{
//		if(isExternalThread())
//		{
//			new AgentInvocation()
//			{
//				public void run()
//				{
//					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_classname, name);
//				}
//			};
//		}
//		else
//		{
//			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_classname, name);
//		}
//	}
	
	/**
	 *  Set the update rate.
	 *  @param updaterate The update rate.
	 */
	public void setUpdateRate(final long updaterate)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_updaterate, updaterate);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_updaterate, updaterate);
		}
	}
	
	/**
	 *  Set the evaluation mode.
	 *  @param mode The evaluation mode.
	 */
	public void setEvaluationMode(final String mode)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_evaluationmode, mode);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.typedelement_has_evaluationmode, mode);
		}
	}
}
