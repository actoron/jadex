package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMReferenceableElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEReferenceableElement;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *  Referenceable element model flyweight.
 */
public class MReferenceableElementFlyweight extends MElementFlyweight implements IMReferenceableElement, IMEReferenceableElement
{
	//-------- constructors --------
	
	/**
	 *  Create a new referenceable element flyweight.
	 */
	public MReferenceableElementFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String isExported()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported);
		}
	}
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String[] getAssigntos()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
				}
			};
			return invoc.object==null? SUtil.EMPTY_STRING_ARRAY: (String[])((List)invoc.object).toArray(new String[((List)invoc.object).size()]);
		}
		else
		{
			Collection atos = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
			return atos==null? SUtil.EMPTY_STRING_ARRAY: (String[])(atos.toArray(new String[atos.size()]));
		}
	}
	
	/**
	 *  Set exported state.
	 *  @param exported The exported state. 
	 */
	public void setExported(final String exported)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported, exported);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_exported, exported);
		}
	}
	
	/**
	 *  Set the assigntos.
	 *  @param assigntos The assign to elements. 
	 */
	public void setAssigntos(final String[] assigntos)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Collection	old	= getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
					if(old!=null)
					{
						for(Iterator it=old.iterator(); it.hasNext(); )
						{
							getState().removeAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto, it.next());
						}
					}
					if(assigntos!=null)
					{
						for(int i=0; i<assigntos.length; i++)
						{
							getState().addAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto, assigntos[i]);
						}
					}
				}
			};
		}
		else
		{
			Collection	old	= getState().getAttributeValues(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto);
			if(old!=null)
			{
				for(Iterator it=old.iterator(); it.hasNext(); )
				{
					getState().removeAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto, it.next());
				}
			}
			if(assigntos!=null)
			{
				for(int i=0; i<assigntos.length; i++)
				{
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.referenceableelement_has_assignto, assigntos[i]);
				}
			}
		}
	}
}
