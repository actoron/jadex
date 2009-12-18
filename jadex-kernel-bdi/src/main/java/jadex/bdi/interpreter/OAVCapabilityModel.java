package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IReport;
import jadex.commons.ICacheableModel;
import jadex.commons.SUtil;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The capability model contains the OAV capability model in a state
 *  and a type-specific compiled rulebase (matcher functionality).
 */
public class OAVCapabilityModel implements ILoadableComponentModel, ICacheableModel
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState state;
	
	/** The agent handle. */
	protected Object handle;
	
	/** The type model. */
	protected OAVTypeModel	typemodel;

	/** The (actual) object types contained in the state. */
	protected Set	types;
	
	/** The rulebase of the capability (includes type-specific rules, if any). */
	protected Rulebase rulebase;
	
	/** The filename. */
	protected String	filename;
	
	/** The last modified date. */
	protected long	lastmod;
	
	/** The last checked date (when the file date was last read). */
	protected long	lastcheck;
	
	/** The check report. */
	protected Report	report;
	
	//-------- constructors --------
	
	/**
	 *  Create a model.
	 */
	public OAVCapabilityModel(IOAVState state, Object handle, 
		OAVTypeModel typemodel, Set types, String filename, long lastmod, Report report)
	{
		this.state	= state;
		this.handle	= handle;
		this.typemodel	= typemodel;
		this.types	= types;
		this.rulebase	= new Rulebase();
		this.filename	= filename;
		this.lastmod	= lastmod;
		this.report	= report;
		report.setModel(this);
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
	 */
	public IReport getReport()
	{
		return report;
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations()
	{
		String[] ret = SUtil.EMPTY_STRING;
		
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
	 */
	public String getType()
	{
		// todo: 
		return "v2capability";
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return this.filename;
	}
	
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
		return getTypeModel().getClassLoader();
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
		// Todo: implement me.
		return Collections.EMPTY_MAP;
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
	 *  Get the type model.
	 */
	public OAVTypeModel getTypeModel()
	{
		return typemodel;
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
}
