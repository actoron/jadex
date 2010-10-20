package jadex.bdi.model;

import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bridge.Argument;
import jadex.bridge.ErrorReport;
import jadex.bridge.IArgument;
import jadex.bridge.IErrorReport;
import jadex.bridge.ModelInfo;
import jadex.commons.ICacheableModel;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.javaparser.IParsedExpression;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The capability model contains the OAV capability model in a state
 *  and a type-specific compiled rulebase (matcher functionality).
 */
public class OAVCapabilityModel implements ICacheableModel//, IModelInfo
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The agent handle. */
	protected Object handle;
	
	/** The (actual) object types contained in the state. */
	protected Set	types;
	
	/** The rulebase of the capability (includes type-specific rules, if any). */
	protected Rulebase rulebase;
	
	/** The last modified date. */
	protected long	lastmod;
	
	/** The last checked date (when the file date was last read). */
	protected long	lastcheck;
	
	/** The model info. */
	protected ModelInfo modelinfo;
	
	// todo: use some internal report for collecting error stuff?!
	/** The multi-collection holding the report messages. */
	protected MultiCollection	entries;
	
	/** The documents for external elements (e.g. capabilities). */
	protected Map externals;
	
	//-------- constructors --------
	
	/**
	 *  Create a model.
	 */
	public OAVCapabilityModel(IOAVState state, Object handle, Set types, String filename, long lastmod, MultiCollection entries)
	{
		this.state	= state;
		this.handle	= handle;
		this.types	= types;
		this.rulebase	= new Rulebase();
		this.lastmod	= lastmod;
		this.entries	= entries;
	
		boolean startable = !this.getClass().equals(OAVCapabilityModel.class);
		this.modelinfo = new ModelInfo(getName(), getPackage(), getDescription(), null, getConfigurations(), getArguments(), 
			getResults(), startable, filename, getProperties(), getClassLoader());
	}
	
	//-------- IAgentModel methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.capability_has_package);
	}
	
	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 * /
	public String getFullName()
	{
		String pkg = getPackage();
		return pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
	}*/
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		String ret = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description);
		return ret!=null? ret: "No description available."; 
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 * /
	public IReport getReport()
	{
		return report;
	}*/
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations()
	{
		String[] ret = SUtil.EMPTY_STRING_ARRAY;
		
		Collection configs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_configurations);
		if(configs!=null)
		{
			List tmp = new ArrayList();
			String	defname = (String)state.getAttributeValue(handle, OAVBDIMetaModel.capability_has_defaultconfiguration);
			if(defname!=null)
				tmp.add(defname);
			
			for(Iterator it=configs.iterator(); it.hasNext(); )
			{
				String name = (String)state.getAttributeValue(it.next(), OAVBDIMetaModel.modelelement_has_name);
				if(defname==null || !defname.equals(name))
					tmp.add(name);
			}
			
			ret = (String[])tmp.toArray(new String[tmp.size()]);
		}
		
		return ret;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		List ret = new ArrayList();
		
		Collection bels = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefs);
		if(bels!=null)
		{
			for(Iterator it=bels.iterator(); it.hasNext(); )
			{
				Object bel = it.next();
				String exported = (String)state.getAttributeValue(bel, 
					OAVBDIMetaModel.referenceableelement_has_exported);
				Boolean argu = (Boolean)state.getAttributeValue(bel, 
					OAVBDIMetaModel.belief_has_argument);
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported) || Boolean.TRUE.equals(argu))
					ret.add(createArgument(state, handle, bel, false));
			}
		}
		
		Collection belrefs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefrefs);
		if(belrefs!=null)
		{
			for(Iterator it=belrefs.iterator(); it.hasNext(); )
			{
				Object belref = it.next();
				String exported = (String)state.getAttributeValue(belref, 
					OAVBDIMetaModel.referenceableelement_has_exported);
				Boolean argu = (Boolean)state.getAttributeValue(belref, 
					OAVBDIMetaModel.beliefreference_has_argument);
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported) || Boolean.TRUE.equals(argu))
					ret.add(createArgument(state, handle, belref, false));
			}
		}
		
		Collection belsets = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefsets);
		if(belsets!=null)
		{
			for(Iterator it=belsets.iterator(); it.hasNext(); )
			{
				Object belset = it.next();
				String exported = (String)state.getAttributeValue(belset, 
					OAVBDIMetaModel.referenceableelement_has_exported);
				Boolean argu = (Boolean)state.getAttributeValue(belset, 
					OAVBDIMetaModel.beliefset_has_argument);
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported) || Boolean.TRUE.equals(argu))
					ret.add(createArgument(state, handle, belset, true));
			}
		}
		
		Collection belsetrefs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefsetrefs);
		if(belsetrefs!=null)
		{
			for(Iterator it=belsetrefs.iterator(); it.hasNext(); )
			{
				Object belsetref = it.next();
				String exported = (String)state.getAttributeValue(belsetref, 
					OAVBDIMetaModel.referenceableelement_has_exported);
				Boolean argu = (Boolean)state.getAttributeValue(belsetref, 
					OAVBDIMetaModel.beliefsetreference_has_argument);
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported) || Boolean.TRUE.equals(argu))
					ret.add(createArgument(state, handle, belsetref, true));
			}
		}
		
		return (IArgument[])ret.toArray(new IArgument[ret.size()]);
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		List ret = new ArrayList();
		
		Collection bels = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefs);
		if(bels!=null)
		{
			for(Iterator it=bels.iterator(); it.hasNext(); )
			{
				Object bel = it.next();
				Boolean result = (Boolean)state.getAttributeValue(bel, 
					OAVBDIMetaModel.belief_has_result);
				if(Boolean.TRUE.equals(result))
					ret.add(createArgument(state, handle, bel, false));
			}
		}
		
		Collection belrefs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefrefs);
		if(belrefs!=null)
		{
			for(Iterator it=belrefs.iterator(); it.hasNext(); )
			{
				Object belref = it.next();
				Boolean result = (Boolean)state.getAttributeValue(belref, 
					OAVBDIMetaModel.beliefreference_has_result);
				if(Boolean.TRUE.equals(result))
					ret.add(createArgument(state, handle, belref, false));
			}
		}
		
		Collection belsets = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefsets);
		if(belsets!=null)
		{
			for(Iterator it=belsets.iterator(); it.hasNext(); )
			{
				Object belset = it.next();
				Boolean result = (Boolean)state.getAttributeValue(belset, 
					OAVBDIMetaModel.beliefset_has_result);
				if(Boolean.TRUE.equals(result))
					ret.add(createArgument(state, handle, belset, true));
			}
		}
		
		Collection belsetrefs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefsetrefs);
		if(belsetrefs!=null)
		{
			for(Iterator it=belsetrefs.iterator(); it.hasNext(); )
			{
				Object belsetref = it.next();
				Boolean result = (Boolean)state.getAttributeValue(belsetref, 
					OAVBDIMetaModel.beliefsetreference_has_result);
				if(Boolean.TRUE.equals(result))
					ret.add(createArgument(state, handle, belsetref, true));
			}
		}
		
		return (IArgument[])ret.toArray(new IArgument[ret.size()]);
	}
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return false;
	}
	
	/**
	 *  Get the model type.
	 *  @reeturn The model type (kernel specific).
	 * /
	public String getType()
	{
		// todo: 
		return "v2capability";
	}*/
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 * /
	public String getFilename()
	{
		return this.filename;
	}*/
	
	/**
	 *  Get the last modified date.
	 *  @return The last modified date.
	 */
	public long getLastModified()
	{
		return this.lastmod;
	}
	
	/**
	 *  Return the class loader corresponding to the model.
	 *  @return The class loader corresponding to the model.
	 */
	public ClassLoader getClassLoader()
	{
		return getState().getTypeModel().getClassLoader();
	}

	//-------- methods --------

	/**
	 *  Get the last check date.
	 */
	public long getLastChecked()
	{
		return this.lastcheck;
	}
	
	/**
	 *  Set the last modified date.
	 *  @return The last modified date.
	 */
	public void	setLastChecked(long lastcheck)
	{
		this.lastcheck	= lastcheck;
	}

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools. 
	 *  @return The properties.
	 */
	public Map	getProperties()
	{
		Map ret = new HashMap();
		addCapabilityProperties(ret, handle);
		return ret;
	}
	
	/**
	 *  Add the properties of a capability.
	 *  @param props The map to add the properties.
	 *  @param capa The start capability.
	 */
	public void addCapabilityProperties(Map props, Object capa)
	{
		// Properties from loaded model.
		Collection	oprops	= state.getAttributeKeys(capa, OAVBDIMetaModel.capability_has_properties);
		if(oprops!=null)
		{
			for(Iterator it=oprops.iterator(); it.hasNext(); )
			{
				Object	key	= it.next();
				Object	mexp	= state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_properties, key);
				Class	clazz	= (Class)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_class);
				// Ignore future properties, which are evaluated at component instance startup time.
				if(clazz==null || !SReflect.isSupertype(IFuture.class, clazz))
				{
					IParsedExpression	pex = (IParsedExpression)state.getAttributeValue(mexp, OAVBDIMetaModel.expression_has_content);
					try
					{
						Object	value	= pex.getValue(null);
						props.put(key, value);
					}
					catch(Exception e)
					{
						// Hack!!! Exception should be propagated.
						System.err.println(pex.getExpressionText());
						e.printStackTrace();
					}
				}
			}
		}
		
//		// Merge with subproperties
//		Collection subcaparefs = state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_capabilityrefs);
//		if(subcaparefs!=null)
//		{
//			for(Iterator it=subcaparefs.iterator(); it.hasNext(); )
//			{
//				Object subcaparef = it.next();
//				Object subcapa = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
//				addCapabilityProperties(props, subcapa);
//			}
//		}
	}
	
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public IOAVState getState()
	{
		return state;
	}
	
	/**
	 *  Get the agent state handle.
	 *  @return The state.
	 */
	public Object getHandle()
	{
		return handle;
	}

	/**
	 *  Get the object types contained in the state.
	 *  @return The types.
	 */
	public Set getTypes()
	{
		return types;
	}

	/**
	 *  Get the rulebase.
	 *  The rulebase of the capability includes
	 *  type-specific rules (if any).
	 *  @return The rule base.
	 */
	public Rulebase getRulebase()
	{
		return rulebase;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
		return "OAVCapabilityModel("+name+")";
	}

	/**
	 *  Copy content from another capability model.
	 * /
	protected void	copyContentFrom(OAVCapabilityModel model)
	{
//		// Todo: use state factory.
//		this.state	= OAVStateFactory.createOAVState(model.getTypeModel());
//		this.handle	= model.getState().cloneObject(model.getHandle(), this.state);
//		this.types	= model.getTypes();
//		this.rulebase	= model.getRulebase();
		
		this.state	= model.getState();
		this.handle	= model.getHandle();
		this.types	= model.getTypes();
		this.typemodel	= model.getTypeModel();
		this.rulebase	= model.getRulebase();
	}*/

	/**
	 *  Add a subcapability.
	 */
	public void addSubcapabilityModel(OAVCapabilityModel cmodel)
	{
		// Add state from subcapability.
		state.addSubstate(cmodel.getState());

		// Add rules from subcapability.
		for(Iterator rules=cmodel.getRulebase().getRules().iterator(); rules.hasNext(); )
		{
			rulebase.addRule((IRule)rules.next());
		}
		// Add types from subcapability.
		types.addAll(cmodel.getTypes());
	}

	/**
	 *  Get the modelinfo.
	 *  @return the modelinfo.
	 */
	public ModelInfo getModelInfo()
	{
		return modelinfo;
	}
	
	//-------- error stuff -> move somewhere?! --------

	/**
	 *  Build the error report (if any).
	 */
	public void	buildErrorReport()
	{
		IErrorReport report = entries==null || entries.size()==0 ? null
			: new ErrorReport(generateErrorText(), generateErrorHTML(), externals);
		modelinfo.setReport(report);
	}
	
	/**
	 *  Get the error entries.
	 */
	public Map getErrorEntries()
	{
		return entries;
	}
	
	/**
	 *  Add an entry to the report.
	 *  @param stack	The path to the element to which the entry applies.
	 *  @param message	The problem description. 
	 */
	public void	addEntry(Tuple stack, String message)
	{
		if(entries==null)
			// Use index map to keep insertion order for elements.
			this.entries	= new MultiCollection(new IndexMap().getAsMap(), LinkedHashSet.class);

		if(!entries.getCollection(stack).contains(message))
			entries.put(stack, message);
	}

	/**
	 *  Get all invalid elements.
	 */
	public Tuple[]	getElements()
	{
		if(entries==null)
			return new Tuple[0];
		else
			return (Tuple[])entries.getKeys(Tuple.class);
	}

	/**
	 *  Get the messages for a given element.
	 */
	public String[]	getMessages(Tuple path)
	{
		if(entries==null)
		{
			return SUtil.EMPTY_STRING_ARRAY;
		}
		else
		{
			Collection	ret	= entries.getCollection(path);
			return (String[])ret.toArray(new String[ret.size()]);
		}
	}

	/**
	 *  Generate a string representation of the report.
	 */
	public String generateErrorText()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("Report for ");
		buf.append(getModelInfo().getName());
		buf.append("\n");
		if(getModelInfo().getFilename()!=null)
		{
			buf.append("File: ");
			buf.append(getModelInfo().getFilename());
			buf.append("\n");
		}
		
		buf.append("\n");

		Tuple[]	elements	= getElements();
		for(int i=0; i<elements.length; i++)
		{
			buf.append(elements[i]);
			buf.append(":\n");
			String[]	messages	= 	getMessages(elements[i]);
			for(int j=0; j<messages.length; j++)
			{
				buf.append("\t");
				buf.append(messages[j]);
				buf.append("\n");
			}
		}
		
		return SUtil.stripTags(buf.toString());
	}

	/**
	 *  Generate an html representation of the report.
	 */
	public String generateErrorHTML()
	{
		StringBuffer	buf	= new StringBuffer();

		buf.append("<h3>Report for ");
		buf.append(getModelInfo().getName());
		buf.append("</h3>\n");
		if(getModelInfo().getFilename()!=null)
		{
			buf.append("File: ");
			buf.append(getModelInfo().getFilename());
			buf.append("\n");
		}

//		Tuple[]	capabilities	= getCapabilityErrors();
		Set	capabilities	= getOwnedElementErrors(OAVBDIMetaModel.capabilityref_type);
		Set	beliefs	= getOwnedElementErrors(OAVBDIMetaModel.belief_type);
		beliefs.addAll(getOwnedElementErrors(OAVBDIMetaModel.beliefset_type));
		beliefs.addAll(getOwnedElementErrors(OAVBDIMetaModel.beliefreference_type));
		beliefs.addAll(getOwnedElementErrors(OAVBDIMetaModel.beliefsetreference_type));
		Set	goals	= getOwnedElementErrors(OAVBDIMetaModel.goal_type);
		goals.addAll(getOwnedElementErrors(OAVBDIMetaModel.goalreference_type));
		Set	plans	= getOwnedElementErrors(OAVBDIMetaModel.plan_type);
		Set	events	= getOwnedElementErrors(OAVBDIMetaModel.event_type);
		events.addAll(getOwnedElementErrors(OAVBDIMetaModel.internaleventreference_type));
		events.addAll(getOwnedElementErrors(OAVBDIMetaModel.messageeventreference_type));
//		Tuple[]	configs	= getOwnedElementErrors(IMConfigBase.class);
		Set excludes	= new HashSet();
		excludes.addAll(capabilities);
		excludes.addAll(beliefs);
		excludes.addAll(goals);
		excludes.addAll(plans);
		excludes.addAll(events);
		Set	others	= getOtherErrors(excludes);

		
		// Summaries.
		buf.append("<h4>Summary</h4>\n<ul>\n");
		generateOverview(buf, "Capability", capabilities);
		generateOverview(buf, "Belief", beliefs);
		generateOverview(buf, "Goal", goals);
		generateOverview(buf, "Plan", plans);
		generateOverview(buf, "Event", events);
//		generateOverview(buf, "Configuration", configs);
		generateOverview(buf, "Other element", others);
		buf.append("</ul>\n");


		// Details.
		generateDetails(buf, "Capability", capabilities);
		generateDetails(buf, "Belief", beliefs);
		generateDetails(buf, "Goal", goals);
		generateDetails(buf, "Plan", plans);
		generateDetails(buf, "Event", events);
//		generateDetails(buf, "Configuration", configs);
		generateDetails(buf, "Other element", others);
		
		return buf.toString();
	}

	/**
	 *  Get capability references which have errors, or contain elements with errors.
	 * /
	public StackElement[]	getCapabilityErrors()
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();
		for(int i=0; i<elements.length; i++)
		{
			MElement	element	= (MElement)elements[i];
			while(element!=null && !errors.contains(element))
			{
				if(element instanceof IMCapabilityReference)
				{
					errors.add(element);
					break;
				}
				element	= element.getOwner();
			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}*/

	/**
	 *  Get elements of the given owner type, which have errors or contain elements with errors.
	 */
	public Set	getOwnedElementErrors(OAVObjectType type)
	{
		Set	errors	= SCollection.createLinkedHashSet();
		Tuple[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	added	= false;
			for(int j=0; !added && j<elements[i].getEntities().length; j++)
			{
				StackElement	se	= (StackElement)elements[i].getEntity(j);
				if(se.getObject()!=null)
				{
					added	= errors.contains(se.getObject());
					if(!added && state.getType(se.getObject()).isSubtype(type))
					{
						errors.add(se.getObject());
						added	= true;
					}
				}
			}
		}
		return errors;
	}

	/**
	 *  Get other errors, not in the given tags.
	 */
	public Set	getOtherErrors(Set excludes)
	{
		Set	errors	= SCollection.createLinkedHashSet();
		Tuple[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	excluded	= false;
			for(int j=0; !excluded && j<elements[i].getEntities().length; j++)
			{
				StackElement	se	= (StackElement)elements[i].getEntity(j);
				if(se.getObject()!=null)
				{
					excluded	= excludes.contains(se.getObject());
				}
			}
			if(!excluded)
			{
				Object	obj	= getObject(elements[i]);
				if(obj!=null && !errors.contains(obj))
					errors.add(obj);
			}
		}
		return errors;
	}

	protected Object getObject(Tuple element)
	{
		Object	ret	= null;
		for(int j=element.getEntities().length-1; ret==null && j>=0; j--)
		{
			StackElement	se	= (StackElement)element.getEntity(j);
			if(se.getObject()!=null)
			{
				ret	= se.getObject();
			}
		}
		return ret;
	}

	/**
	 *  Get all elements which have errors and are contained in the given element.
	 */
	public Tuple[]	getElementErrors(Object ancestor)
	{
		List	errors	= SCollection.createArrayList();
		Tuple[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	added	= errors.contains(elements[i]);
			for(int j=0; !added && j<elements[i].getEntities().length; j++)
			{
				StackElement	se	= (StackElement)elements[i].getEntity(j);
				if(ancestor.equals(se.getObject()))
				{
					errors.add(elements[i]);
					added	= true;
				}
			}
		}
		return (Tuple[])errors.toArray(new Tuple[errors.size()]);
	}
	
	//-------- helper methods --------

	/**
	 *  Generate overview HTML code for the given elements.
	 */
	protected void	generateOverview(StringBuffer buf, String type, Set elements)
	{
		if(!elements.isEmpty())
		{
			buf.append("<li>");
			buf.append(type);
			buf.append(" errors\n<ul>\n");
			for(Iterator it=elements.iterator(); it.hasNext(); )
			{
				Object	obj	= it.next();
				String name = getElementName(obj);
				buf.append("<li><a href=\"#");
				buf.append(SUtil.makeConform(name));
				buf.append("\">");
				buf.append(SUtil.makeConform(name));
				buf.append("</a> has errors.</li>\n");
			}
			buf.append("</ul>\n</li>\n");
		}
	}

	/**
	 *  Get the name of an element.
	 */
	protected String getElementName(Object obj)
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
			Object	exp	=state.getAttributeValue(obj, OAVBDIMetaModel.expression_has_content);
			name	= exp!=null ? ""+exp : null;
		}
		
		if(name==null)
		{
			name	= ""+obj;
		}
		
		return state.getType(obj).getName().substring(1) + " " + name;
	}

	/**
	 *  Generate detail HTML code for the given elements.
	 */
	protected void	generateDetails(StringBuffer buf, String type, Set elements)
	{
		if(!elements.isEmpty())
		{
			buf.append("<h4>");
			buf.append(type);
			buf.append(" details</h4>\n<ul>\n");
			for(Iterator it=elements.iterator(); it.hasNext(); )
			{
				Object	obj	= it.next();
				String name = getElementName(obj);
				buf.append("<li><a name=\"");
				buf.append(SUtil.makeConform(name));
				buf.append("\">");
				buf.append(SUtil.makeConform(name));
				// Add name of configuration (hack???)
//				if(elements[i] instanceof IMConfigElement)
//				{
//					MElement	owner	= (MElement)elements[i];
//					while(owner!=null && !(owner instanceof IMConfiguration))
//						owner	= owner.getOwner();
//					if(owner!=null)
//					buf.append(" in ");
//					buf.append(SUtil.makeConform(""+owner));
//				}
				buf.append("</a> errors:\n");

				Tuple[]	errors	= getElementErrors(obj);
				buf.append("<dl>\n");
				for(int j=0; j<errors.length; j++)
				{
					Object	obj2	= getObject(errors[j]);
					if(!obj.equals(obj2))
					{
						buf.append("<dt>");
						buf.append(getElementName(obj2));
						buf.append("</dt>\n");
					}
//					SourceLocation	loc	= errors[j].getSourceLocation();
//					if(loc!=null)
//					{
//						buf.append(" (");
//						buf.append(loc.getFilename());
//						buf.append(": line ");
//						buf.append(loc.getLineNumber());
//						buf.append(", column ");
//						buf.append(loc.getColumnNumber());
//						buf.append(")");
//					}
					
					String[]	msgs	= getMessages(errors[j]);
					buf.append("<dd>");
					for(int k=0; k<msgs.length; k++)
					{
						buf.append(msgs[k]);
						buf.append("\n");
						if(msgs.length>1 && k!=msgs.length-1)
							buf.append("<br>");
					}
					buf.append("</dd>\n");
				}
				buf.append("</dl>\n</li>\n");
				
			}
			buf.append("</ul>\n");
		}
	}
	
	/**
	 *  Add an external document.
	 *  @param id	The document id as used in anchor tags.
	 *  @param doc	The html text.
	 */
	public void	addDocument(String id, String doc)
	{
		if(externals==null)
			this.externals	= SCollection.createHashMap();
		
		externals.put(id, doc);
	}

	/**
	 *  Get the external documents.
	 */
	public Map	getDocuments()
	{
		return externals;//==null ? Collections.EMPTY_MAP : externals;
	}
	
	//-------- helpers --------
	
	/**
	 *  Create an argument. 
	 */
	public static IArgument createArgument(IOAVState state, Object capa, Object handle, boolean beliefset)
	{
		String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
		String description = (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description);
//		String typename = SReflect.getInnerClassName(beliefset? findBeliefSetType(state, capa, handle)
//			: findBeliefType(state, capa, handle));

		Argument arg = new Argument(name, description, null);
		
		return arg;
	}
	
	/**
	 *  Init an argument.
	 */
	public static void initArgument(Argument arg, IOAVState state, Object capa)
	{
		String name = arg.getName();
		
		boolean beliefset = false;
		Object handle = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_beliefs, name);
		if(handle==null)
		{
			handle = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_beliefrefs, name);
			if(handle==null)
			{
				beliefset = true;
				handle = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_beliefsets, name);
				if(handle==null)
				{
					handle = state.getAttributeValue(capa, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
				}
			}
		}
		
		String typename = SReflect.getInnerClassName(beliefset? findBeliefSetType(state, capa, handle)
			: findBeliefType(state, capa, handle));
		arg.setTypename(typename);
		
		Collection configs = (Collection)state.getAttributeValues(capa, OAVBDIMetaModel.capability_has_configurations);
		if(configs!=null)
		{
			Map defvals = new HashMap();
			for(Iterator it=configs.iterator(); it.hasNext(); )
			{
				Object config = it.next();
				String configname = (String)state.getAttributeValue(config, OAVBDIMetaModel.modelelement_has_name);
				Object val = beliefset? findBeliefSetDefaultValue(state, capa, handle, configname, name)
					: findBeliefDefaultValue(state, capa, handle, configname, name);
				defvals.put(configname, val);
			}
			arg.setDefaultValues(defvals);
		}
		else
		{
			Object val =beliefset? findBeliefSetDefaultValue(state, capa, handle, null, name)
				: findBeliefDefaultValue(state, capa, handle, null, name);
			arg.setDefaultValue(val);
		}
	}
	
	/**
	 *  Find the belief/ref type.
	 */
	protected static Class findBeliefType(IOAVState state, Object scope, Object handle)
	{
		Class	ret	= null;
		
		if(OAVBDIMetaModel.belief_type.equals(state.getType(handle)))
		{
			ret	= (Class)state.getAttributeValue(handle, OAVBDIMetaModel.typedelement_has_class);
		}
		else
		{
			String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
			Object belref;
			int idx = name.indexOf(".");
			if(idx==-1)
			{
				belref = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefrefs, name);
				name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
			}
			String capaname = name.substring(0, idx);
			String belname = name.substring(idx+1);
			
			Object subcaparef = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
			Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
			
			belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
			if(belref==null)
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
			
			ret = findBeliefType(state, subcapa, belref);
		}
		
		return ret;
	}
	
	/**
	 *  Find the belief/ref value.
	 */
	protected static Object findBeliefDefaultValue(IOAVState state, Object mcapa, Object handle, String configname, String elemname)
	{
		Object ret = null;
		boolean found = false;
		
		// Search initial value in configurations.
		Object config;
		if(configname==null)
		{
			configname = (String)state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_defaultconfiguration);
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, configname);
		}
		else
		{
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, configname);
		}
	
		if(config!=null)
		{
			Object[] belres;
			if(OAVBDIMetaModel.beliefreference_type.equals(state.getType(handle)))
			{
				String ref = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				belres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.belief_type, mcapa, state);
			}
			else
			{
				belres = new Object[]{elemname, mcapa};
			}
			
			Collection inibels = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefs);
			if(inibels!=null)
			{
				for(Iterator it=inibels.iterator(); it.hasNext(); )
				{
					Object inibel = it.next();
					String ref = (String)state.getAttributeValue(inibel, OAVBDIMetaModel.configbelief_has_ref);
					Object[] inibelres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.belief_type, mcapa, state);
					
					if(Arrays.equals(inibelres, belres))
					{	
						Object exp = state.getAttributeValue(inibel, OAVBDIMetaModel.belief_has_fact);
						// todo: string rep?
						IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
						ret = parsedexp.getExpressionText();
						found = true;
					}
				}
			}
		}
		
		// If not found 
		// a) its a belief -> get default value
		// b) its a ref -> recursively call this method with ref, subcapa and config
		
		if(!found)
		{
			if(OAVBDIMetaModel.belief_type.equals(state.getType(handle)))
			{
				Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.belief_has_fact);
				if(exp!=null)
				{
					IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
					ret = parsedexp.getExpressionText();
				}
			}
			else
			{
				String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				Object belref;
				int idx = name.indexOf(".");
				if(idx==-1)
				{
					belref = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefrefs, name);
					name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
				}
				String capaname = name.substring(0, idx);
				String belname = name.substring(idx+1);
				
				Object subcaparef = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
				Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
				if(belref==null)
					belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
				
				String subconfigname = null;
				if(config!=null)
				{
					Collection inicapas = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialcapabilities);
					if(inicapas!=null)
					{
						for(Iterator it=inicapas.iterator(); subconfigname==null && it.hasNext(); )
						{
							Object inicapa = it.next();
							
							if(state.getAttributeValue(inicapa, OAVBDIMetaModel.initialcapability_has_ref).equals(subcaparef))
							{	
								subconfigname = (String)state.getAttributeValue(inicapa, OAVBDIMetaModel.initialcapability_has_configuration);
							}
						}
					}
				}
				
				ret = findBeliefDefaultValue(state, subcapa, belref, subconfigname, belname);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Find the belief/ref type.
	 */
	protected static Class findBeliefSetType(IOAVState state, Object scope, Object handle)
	{
		Class	ret	= null;
		
		if(OAVBDIMetaModel.beliefset_type.equals(state.getType(handle)))
		{
			ret	= (Class)state.getAttributeValue(handle, OAVBDIMetaModel.typedelement_has_class);
		}
		else
		{
			String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
			Object belref;
			int idx = name.indexOf(".");
			if(idx==-1)
			{
				belref = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_beliefrefs, name);
				name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
			}
			String capaname = name.substring(0, idx);
			String belname = name.substring(idx+1);
			
			Object subcaparef = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
			Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
			
			belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefs, belname);
			if(belref==null)
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefrefs, belname);
			
			ret = findBeliefSetType(state, subcapa, belref);
		}
		
		return ret;
	}
	
	/**
	 *  Find the beliefset/ref value.
	 */
	protected static Object findBeliefSetDefaultValue(IOAVState state, Object mcapa, Object handle, String configname, String elemname)
	{
		Object ret = null;
		boolean found = false;
		
		// Search initial value in configurations.
		Object config;
		if(configname==null)
		{
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_defaultconfiguration);
		}
		else
		{
			config = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_configurations, configname);
		}
	
		if(config!=null)
		{
			Object[] belsetres;
			if(OAVBDIMetaModel.beliefreference_type.equals(state.getType(handle)))
			{
				String ref = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				belsetres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.belief_type, mcapa, state);
			}
			else
			{
				belsetres = new Object[]{elemname, mcapa};
			}
			
			Collection inibelsets = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefsets);
			if(inibelsets!=null)
			{
				for(Iterator it=inibelsets.iterator(); it.hasNext(); )
				{
					Object inibelset = it.next();
					String ref = (String)state.getAttributeValue(inibelset, OAVBDIMetaModel.configbeliefset_has_ref);
					Object[] inibelsetres = AgentRules.resolveMCapability(ref, OAVBDIMetaModel.beliefset_type, mcapa, state);
					
					if(Arrays.equals(inibelsetres, belsetres))
					{	
						Collection vals = state.getAttributeValues(inibelset, OAVBDIMetaModel.beliefset_has_facts);
						if(vals==null)
						{
							Object exp = state.getAttributeValue(inibelset, OAVBDIMetaModel.beliefset_has_factsexpression);
							IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
							ret = parsedexp.getExpressionText();
						}
						else
						{
							ret = vals.toString();
						}
						found = true;
					}
				}
			}
		}
		
		// If not found 
		// a) its a belief -> get default value
		// b) its a ref -> recursively call this method with ref, subcapa and config
		
		if(!found)
		{
			if(OAVBDIMetaModel.beliefset_type.equals(state.getType(handle)))
			{
				Collection vals = state.getAttributeValues(handle, OAVBDIMetaModel.beliefset_has_facts);
				if(vals==null)
				{
					Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.beliefset_has_factsexpression);
					if(exp!=null)
					{
						IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
						ret = parsedexp.getExpressionText();
					}
				}
				else
				{
					ret = vals.toString();
				}
			}
			else
			{
				String name = (String)state.getAttributeValue(handle, OAVBDIMetaModel.elementreference_has_concrete);
				Object belref;
				int idx = name.indexOf(".");
				if(idx==-1)
				{
					belref = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, name);
					name = (String)state.getAttributeValue(belref, OAVBDIMetaModel.elementreference_has_concrete);
				}
				String capaname = name.substring(0, idx);
				String belname = name.substring(idx+1);
				
				Object subcaparef = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_capabilityrefs, capaname);
				Object subcapa  = state.getAttributeValue(subcaparef, OAVBDIMetaModel.capabilityref_has_capability);
				
				belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefsets, belname);
				if(belref==null)
					belref = state.getAttributeValue(subcapa, OAVBDIMetaModel.capability_has_beliefsetrefs, belname);
				
				String subconfigname = null;
				if(config!=null)
				{
					Collection inicapas = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialcapabilities);
					if(inicapas!=null)
					{
						for(Iterator it=inicapas.iterator(); subconfigname==null && it.hasNext(); )
						{
							Object inicapa = it.next();
							if(state.getAttributeValue(inicapa, OAVBDIMetaModel.initialcapability_has_ref).equals(subcaparef))
							{	
								subconfigname = (String)state.getAttributeValue(inicapa, OAVBDIMetaModel.initialcapability_has_configuration);
							}
						}
					}
				}
				
				ret = findBeliefSetDefaultValue(state, subcapa, belref, subconfigname, belname);
			}
		}
		
		return ret;
	}
}
