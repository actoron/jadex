package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMPropertybase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEPropertybase;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for the belief base model.
 */
public class MPropertybaseFlyweight extends MElementFlyweight implements IMPropertybase, IMEPropertybase
{
	//-------- constructors --------
	
	/**
	 *  Create a new propertybase flyweight.
	 */
	public MPropertybaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the properties.
	 *  @return The properties.
	 */
	public IMExpression[] getProperties()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_properties);
					IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMExpression[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_properties);
			IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Create a property.
	 *  @param name The name.
	 *  @param content The content.
	 *  @param lang The language.
	 *  @return The property.
	 */
	public IMEExpression createProperty(final String name, final String content, final String lang)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight fly = MExpressionbaseFlyweight.createExpression(content, lang, getState(), getScope());
					fly.setName(name);
					object = fly;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight fly = MExpressionbaseFlyweight.createExpression(content, lang, getState(), getScope());
			fly.setName(name);
			return fly;
		}
	}
}
	