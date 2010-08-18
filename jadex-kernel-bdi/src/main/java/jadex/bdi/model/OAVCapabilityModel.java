package jadex.bdi.model;

import jadex.bdi.runtime.interpreter.MBeliefArgument;
import jadex.bdi.runtime.interpreter.MBeliefSetArgument;
import jadex.bridge.ErrorReport;
import jadex.bridge.IArgument;
import jadex.bridge.IErrorReport;
import jadex.bridge.ModelInfo;
import jadex.commons.ICacheableModel;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.IndexMap;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;
import jadex.javaparser.IParsedExpression;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.state.IOAVState;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	
	/** The multicollection holding the report messages. */
	protected MultiCollection	entries;
	
	/** The documents for external elements (e.g. capabilities). */
	protected Map externals;
	
	//-------- constructors --------
	
	/**
	 *  Create a model.
	 */
	public OAVCapabilityModel(IOAVState state, Object handle, Set types, String filename, long lastmod)//, IErrorReport report)
	{
		this.state	= state;
		this.handle	= handle;
		this.types	= types;
		this.rulebase	= new Rulebase();
		this.lastmod	= lastmod;
	
		IErrorReport report = entries==null? null: new ErrorReport(generateErrorText(), generateErrorHTML(), externals);
		boolean startable = !this.getClass().equals(OAVCapabilityModel.class);
		
		this.modelinfo = new ModelInfo(getName(), getPackage(), getDescription(), report, getConfigurations(), getArguments(), 
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
					ret.add(new MBeliefArgument(state, handle, bel));
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
					ret.add(new MBeliefArgument(state, handle, belref));
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
					ret.add(new MBeliefSetArgument(state, handle, belset));
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
					ret.add(new MBeliefSetArgument(state, handle, belsetref));
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
					ret.add(new MBeliefArgument(state, handle, bel));
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
					ret.add(new MBeliefArgument(state, handle, belref));
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
					ret.add(new MBeliefSetArgument(state, handle, belset));
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
					ret.add(new MBeliefSetArgument(state, handle, belsetref));
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
	 *  Get the error entries.
	 */
	public Map getErrorEntries()
	{
		return entries;
	}
	
	/**
	 *  Add an entry to the report.
	 *  @param element	The element to which the entry applies.
	 *  @param message	The problem description. 
	 */
	public void	addEntry(StackElement element, String message)
	{
		if(entries==null)
			// Use index map to keep insertion order for elements.
			this.entries	= new MultiCollection(new IndexMap().getAsMap(), ArrayList.class);

		entries.put(element, message);
	}

	/**
	 *  Get all invalid elements.
	 */
	public StackElement[]	getElements()
	{
		if(entries==null)
			return new StackElement[0];
		else
			return (StackElement[])entries.getKeys(StackElement.class);
	}

	/**
	 *  Get the messages for a given element.
	 */
	public String[]	getMessages(StackElement element)
	{
		if(entries==null)
		{
			return new String[0];
		}
		else
		{
			Collection	ret	= entries.getCollection(element);
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

		StackElement[]	elements	= getElements();
		for(int i=0; i<elements.length; i++)
		{
//				buf.append(elements[i].path);
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

//		StackElement[]	capabilities	= getCapabilityErrors();
		StackElement[]	capabilities	= getOwnedElementErrors("capabilities");
		StackElement[]	beliefs	= getOwnedElementErrors("beliefs");
		StackElement[]	goals	= getOwnedElementErrors("goals");
		StackElement[]	plans	= getOwnedElementErrors("plans");
		StackElement[]	events	= getOwnedElementErrors("events");
//		StackElement[]	configs	= getOwnedElementErrors(IMConfigBase.class);
		StackElement[]	others	= getOtherErrors(new String[]{"capabilities", "beliefs", "goals", "plans", "events"});

		
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
	 *  Get elements contained in an element of the given ownertag, which have errors, or contain elements with errors.
	 */
	public StackElement[]	getOwnedElementErrors(String basetag)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
//			if(elements[i].path.indexOf(basetag)!=-1)
//			{
//				errors.add(elements[i]);
//			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}

	/**
	 *  Get other errors, not in the given tags.
	 */
	public StackElement[]	getOtherErrors(String[] excludes)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	add	= true;
			for(int j=0; add && j<excludes.length; j++)
			{
//				add	= elements[i].path.indexOf(excludes[j])==-1;
			}
			if(add)
				errors.add(elements[i]);
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}

	/**
	 *  Get all elements which have errors and are contained in the given element.
	 */
	public StackElement[]	getElementErrors(StackElement ancestor)
	{
		List	errors	= SCollection.createArrayList();
		StackElement[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
//			if(elements[i].path.startsWith(ancestor.path))
//			{
//				errors.add(elements[i]);
//			}
		}
		return (StackElement[])errors.toArray(new StackElement[errors.size()]);
	}
	
	//-------- helper methods --------

	/**
	 *  Generate overview HTML code for the given elements.
	 */
	protected void	generateOverview(StringBuffer buf, String type, StackElement[] elements)
	{
		if(elements.length>0)
		{
			buf.append("<li>");
			buf.append(type);
			buf.append(" errors\n<ul>\n");
			for(int i=0; i<elements.length; i++)
			{
				buf.append("<li><a href=\"#");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("\">");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("</a> has errors.</li>\n");
			}
			buf.append("</ul>\n</li>\n");
		}
	}

	/**
	 *  Generate detail HTML code for the given elements.
	 */
	protected void	generateDetails(StringBuffer buf, String type, StackElement[] elements)
	{
		if(elements.length>0)
		{
			buf.append("<h4>");
			buf.append(type);
			buf.append(" details</h4>\n<ul>\n");
			for(int i=0; i<elements.length; i++)
			{
				buf.append("<li><a name=\"");
//				buf.append(SUtil.makeConform(""+elements[i].path));
				buf.append("\">");
//				buf.append(SUtil.makeConform(""+elements[i].path));
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

				StackElement[]	errors	= getElementErrors(elements[i]);
				buf.append("<dl>\n");
				for(int j=0; j<errors.length; j++)
				{
					buf.append("<dt>");
//					buf.append(errors[j].path);
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
					buf.append("\n<dd>");
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
}
