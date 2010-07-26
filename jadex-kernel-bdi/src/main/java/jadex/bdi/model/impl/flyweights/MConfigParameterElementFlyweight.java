package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMConfigParameter;
import jadex.bdi.model.IMConfigParameterElement;
import jadex.bdi.model.IMConfigParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MConfigParameterElementFlyweight extends MElementFlyweight implements IMConfigParameterElement
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
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
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
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
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.configparameterelement_has_parameters);
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
}
