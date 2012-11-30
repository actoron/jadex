package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMParameter;
import jadex.bdi.model.IMParameterElement;
import jadex.bdi.model.IMParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEParameter;
import jadex.bdi.model.editable.IMEParameterElement;
import jadex.bdi.model.editable.IMEParameterSet;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for parameter element model.
 */
public class MParameterElementFlyweight extends MReferenceableElementFlyweight implements IMParameterElement, IMEParameterElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MParameterElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get a parameter.
	 *  @return The parameter.
	 */
	public IMParameter getParameter(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, name);
					if(handle==null)
						throw new RuntimeException("Parameter not found: "+name);
					object = new MParameterFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMParameter)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, name);
			if(handle==null)
				throw new RuntimeException("Parameter not found: "+name);
			return new MParameterFlyweight(getState(), getScope(), handle);
		}
	}
	
	/**
	 *  Get parameters.
	 *  @return The parameters.
	 */
	public IMParameter[] getParameters()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters);
					IMParameter[] ret = new IMParameter[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MParameterFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMParameter[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters);
			IMParameter[] ret = new IMParameter[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MParameterFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a parameter set.
	 *  @return The parameter set.
	 */
	public IMParameterSet getParameterSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets, name);
					if(handle==null)
						throw new RuntimeException("Parameterset not found: "+name);
					object = new MParameterSetFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMParameterSet)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets, name);
			if(handle==null)
				throw new RuntimeException("Parameterset not found: "+name);
			return new MParameterSetFlyweight(getState(), getScope(), handle);
		}
	}
	
	/**
	 *  Get parameter sets.
	 *  @return The parameter sets.
	 */
	public IMParameterSet[] getParameterSets()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets);
					IMParameterSet[] ret = new IMParameterSet[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MParameterSetFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMParameterSet[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets);
			IMParameterSet[] ret = new IMParameterSet[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MParameterSetFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}

	/**
	 *  Get a parameter.
	 *  @return The parameter.
	 */
	public IMEParameter createParameter(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	param	= getState().createObject(OAVBDIMetaModel.parameter_type);
					getState().setAttributeValue(param, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, param);
					object	= new MParameterFlyweight(getState(), getScope(), param);
				}
			};
			return (IMEParameter)invoc.object;
		}
		else
		{
			Object	param	= getState().createObject(OAVBDIMetaModel.parameter_type);
			getState().setAttributeValue(param, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, param);
			return new MParameterFlyweight(getState(), getScope(), param);
		}
	}
	
	/**
	 *  Get a parameter set.
	 *  @return The parameter set.
	 */
	public IMEParameterSet createParameterSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object	param	= getState().createObject(OAVBDIMetaModel.parameterset_type);
					getState().setAttributeValue(param, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets, param);
					object	= new MParameterSetFlyweight(getState(), getScope(), param);
				}
			};
			return (IMEParameterSet)invoc.object;
		}
		else
		{
			Object	param	= getState().createObject(OAVBDIMetaModel.parameterset_type);
			getState().setAttributeValue(param, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parametersets, param);
			return new MParameterSetFlyweight(getState(), getScope(), param);
		}
	}
}
