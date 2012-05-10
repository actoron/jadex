package jadex.bdi.model;

import jadex.bridge.AbstractErrorReportBuilder;
import jadex.commons.collection.MultiCollection;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;
import jadex.xml.StackElement;

import java.util.Map;

/**
 *  Convert XML errors into human readable form.
 */
public class BDIErrorReportBuilder extends AbstractErrorReportBuilder
{
	protected IOAVState	state;
	
	public BDIErrorReportBuilder(String name, String filename, MultiCollection entries, Map<String, String> externals, IOAVState state)
	{
		super(name, filename, new String[]{"XML", "Capability", "Belief", "Goal", "Plan", "Event"}, entries, externals);
		this.state	= state;
	}

	public boolean isInCategory(Object obj, String category)
	{
		boolean	ret	= false;
		if("XML".equals(category))
		{
			ret	= obj instanceof String;
		}
		else if("Capability".equals(category))
		{
			ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.capabilityref_type);
		}
		else if("Belief".equals(category))
		{
			ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.belief_type)
				|| state.getType(obj).isSubtype(OAVBDIMetaModel.beliefreference_type)
				|| state.getType(obj).isSubtype(OAVBDIMetaModel.beliefsetreference_type);
		}
		else if("Goal".equals(category))
		{
			ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.goal_type)
				|| state.getType(obj).isSubtype(OAVBDIMetaModel.goalreference_type);
		}
		else if("Plan".equals(category))
		{
			ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.plan_type);
		}
		else if("Event".equals(category))
		{
			ret	= state.getType(obj).isSubtype(OAVBDIMetaModel.event_type)
				|| state.getType(obj).isSubtype(OAVBDIMetaModel.internaleventreference_type)
				|| state.getType(obj).isSubtype(OAVBDIMetaModel.messageeventreference_type);
		}
		return ret;
	}

	public Object getPathElementObject(Object element)
	{
		return ((StackElement)element).getObject();
	}

	public String getObjectName(Object obj)
	{
		String	name	= null;
		if(state.getType(obj).isSubtype(OAVBDIMetaModel.modelelement_type))
		{
			name	= (String)state.getAttributeValue(obj, OAVBDIMetaModel.modelelement_has_name);
		}
		
		if(name==null && state.getType(obj).isSubtype(OAVBDIMetaModel.elementreference_type))
		{
			name	= (String)state.getAttributeValue(obj, OAVBDIMetaModel.elementreference_has_concrete);
		}
		
		if(name==null && state.getType(obj).isSubtype(OAVBDIMetaModel.expression_type))
		{
			IParsedExpression	exp	=(IParsedExpression)state.getAttributeValue(obj, OAVBDIMetaModel.expression_has_parsed);
			String	text	= (String)state.getAttributeValue(obj, OAVBDIMetaModel.expression_has_text);
			name	= exp!=null ? exp.getExpressionText() : text!=null ? text.trim() : null;
		}
		
		if(name==null)
		{
			name	= ""+obj;
		}
		
		return obj instanceof String ? (String)obj : state.getType(obj).getName().substring(1) + " " + name;
	}
}