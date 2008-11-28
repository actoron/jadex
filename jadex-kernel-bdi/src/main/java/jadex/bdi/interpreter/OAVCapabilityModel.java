package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.bridge.IAgentModel;
import jadex.bridge.IReport;
import jadex.commons.SUtil;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVTypeModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The capability model contains the OAV capability model in a state
 *  and a type-specific compiled rulebase (matcher functionality).
 */
public class OAVCapabilityModel implements IAgentModel
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
	
	//-------- constructors --------
	
	/**
	 *  Create a model for later copying content into.
	 */
	protected OAVCapabilityModel()
	{
	}
	
	/**
	 *  Create a model.
	 */
	public OAVCapabilityModel(IOAVState state, Object handle, 
		OAVTypeModel typemodel, Set types)
	{
		this.state	= state;
		this.handle	= handle;
		this.typemodel	= typemodel;
		this.types	= types;
		this.rulebase	= new Rulebase();
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
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		String ret = null;
		try
		{
			// Try to extract first comment from file.
			// todo: is context class loader correct?
			InputStream is = SUtil.getResource(getFilename(), Thread.currentThread().getContextClassLoader());
			int read;
			while((read = is.read())!=-1)
			{
				if(read=='<')
				{
					read = is.read();
					if(Character.isLetter((char)read))
					{
						// Found first tag, use whatever comment found up to now.
						break;
					}
					else if(read=='!' && is.read()=='-' && is.read()=='-')
					{
						// Found comment.
						StringBuffer comment = new StringBuffer();
						while((read = is.read())!=-1)
						{
							if(read=='-')
							{
								if((read = is.read())=='-')
								{
									if((read = is.read())=='>')
									{
										// Finished reading <!-- ... --> statement
										ret = comment.toString();
										break;
									}
									comment.append("--");
									comment.append((char)read);
								}
								else
								{
									comment.append('-');
									comment.append((char)read);
								}
							}
							else
							{
								comment.append((char)read);
							}
						}
					}
				}
			}
			is.close();
		}
		catch(Exception e)
		{
			ret = "No description available: "+e;
		}
		return ret;
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		// todo: 
		return new IReport()
		{
			public Map getDocuments()
			{
				return null;
			}
			
			public boolean isEmpty()
			{
				return true;
			}
			
			public String toHTMLString()
			{
				return "";
			}
		};
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
			Object defconfig = state.getAttributeValue(handle, OAVBDIMetaModel.capability_has_defaultconfiguration);
			String defname = null;
			if(defconfig!=null)
			{
				defname = (String)state.getAttributeValue(defconfig, OAVBDIMetaModel.modelelement_has_name);
				tmp.add(defname);
			}
			
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
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported))
					ret.add(new MBeliefArgument(state, handle, bel));
			}
		}
		
		Collection belrefs = state.getAttributeValues(handle, OAVBDIMetaModel.capability_has_beliefrefs);
		if(belrefs!=null)
		{
			for(Iterator it=belrefs.iterator(); it.hasNext(); )
			{
				Object bel = it.next();
				String exported = (String)state.getAttributeValue(bel, 
					OAVBDIMetaModel.referenceableelement_has_exported);
				if(!OAVBDIMetaModel.EXPORTED_FALSE.equals(exported))
					ret.add(new MBeliefReferenceArgument(state, handle, bel));
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
		String fn = (String)state.getAttributeValue(handle, OAVBDIMetaModel.capability_has_filename);
//		System.out.println("Filename: "+fn);
		return fn;
	}
	
	//-------- methods --------

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
//		this.state	= new OAVState(model.getTypeModel());
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
