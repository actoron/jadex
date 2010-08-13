package jadex.application.model;

import jadex.bridge.IArgument;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.IReport;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Model representation of an application.
 */
public class ApplicationModel implements ILoadableComponentModel
{
	//-------- attributes --------
	
	/** The application type. */
	protected MApplicationType apptype;
	
	/** The filename. */
	protected String filename;
		
	/** The classloader. */
	protected ClassLoader classloader;
	
	protected Map	properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application model. 
	 */
	public ApplicationModel(MApplicationType apptype, String filename, ClassLoader classloader)
	{
		this.apptype = apptype;
		this.filename = filename;
		this.classloader = classloader;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the application type.
	 */
	public MApplicationType	getApplicationType()
	{
		return apptype;
	}
	
	//-------- ILoadableElementModel interface --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return apptype.getName();
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage()
	{
		return apptype.getPackage();
	}
	
	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 */
	public String getFullName()
	{
		String pkg = getPackage();
		return pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		return apptype.getDescription();
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 * /
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
	}*/
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		// todo
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
		List apps = apptype.getMApplicationInstances();
		String[] ret = new String[apps.size()];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = ((MApplicationInstance)apps.get(i)).getName();
		}
		return ret;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return (IArgument[])apptype.getArguments().toArray(new IArgument[0]);
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		return (IArgument[])apptype.getResults().toArray(new IArgument[0]);
	}
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return true;
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools. 
	 *  @return The properties.
	 */
	public Map	getProperties()
	{
		if(properties==null)
		{
			properties = new HashMap();
			List	props	= apptype.getProperties();
			for(int i=0; i<props.size(); i++)
			{
				MExpressionType	mexp	= (MExpressionType)props.get(i);
				Class	clazz	= mexp.getClazz();
				// Ignore future properties, which are evaluated at component instance startup time.
				if(clazz==null || !SReflect.isSupertype(IFuture.class, clazz))
				{
					IParsedExpression	pex = mexp.getParsedValue();
					try
					{
						Object	value	= pex.getValue(null);
						properties.put(mexp.getName(), value);
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
		return properties;
	}
	
	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return this.classloader;
	}
}
