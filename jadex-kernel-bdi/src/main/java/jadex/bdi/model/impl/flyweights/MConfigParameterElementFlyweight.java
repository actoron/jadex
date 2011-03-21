package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigParameter;
import jadex.bdi.model.IMConfigParameterElement;
import jadex.bdi.model.IMConfigParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEConfigParameter;
import jadex.bdi.model.editable.IMEConfigParameterElement;
import jadex.bdi.model.editable.IMEConfigParameterSet;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MConfigParameterElementFlyweight extends MElementFlyweight implements IMConfigParameterElement, IMEConfigParameterElement
{
	//-------- constructors --------
	
	/**
	 *  Create a element flyweight.
	 */
	public MConfigParameterElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get parameters.
	 *  @return The parameters.
	 */
	public IMConfigParameter[] getParameters()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
					IMConfigParameter[] ret = new IMConfigParameter[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigParameterFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigParameter[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
			IMConfigParameter[] ret = new IMConfigParameter[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigParameterFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get parameter sets.
	 *  @return The parameter sets.
	 */
	public IMConfigParameterSet[] getParameterSets()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
					IMConfigParameterSet[] ret = new IMConfigParameterSet[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MConfigParameterSetFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMConfigParameterSet[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
			IMConfigParameterSet[] ret = new IMConfigParameterSet[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MConfigParameterSetFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Create a parameter.
	 *  @param ref The name.
	 *  @return The parameter.
	 */
	public IMEConfigParameter createParameter(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configparameter_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configparameter_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configparameterelement_has_parameters, elem);
					object = new MConfigParameterFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigParameter)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configparameter_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configparameter_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configparameterelement_has_parameters, elem);
			return new MConfigParameterFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create a parameter set.
	 *  @param name The name.
	 *  @return The parameter sets.
	 */
	public IMEConfigParameterSet createParameterSet(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.configparameterset_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.configparameterset_has_ref, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configparameterelement_has_parametersets, elem);
					object = new MConfigParameterSetFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEConfigParameterSet)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.configparameterset_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.configparameterset_has_ref, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.configparameterelement_has_parametersets, elem);
			return new MConfigParameterSetFlyweight(getState(), getScope(), elem);

		}
	}
}
