package jadex.component;

import jadex.bridge.AbstractErrorReportBuilder;
import jadex.bridge.IErrorReport;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.MultiCollection;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class ComponentModel implements ICacheableModel
{
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;

	/** The model info. */
	protected IModelInfo modelinfo;

	/**
	 * 
	 */
	public ComponentModel()
	{
	}
	
	/**
	 * 
	 */
	public ComponentModel(IModelInfo modelinfo)
	{
		this.modelinfo = modelinfo;
	}
	
	/**
	 *  Get the modelinfo.
	 *  @return the modelinfo.
	 */
	public IModelInfo getModelInfo()
	{
		return modelinfo;
	}
	
	/**
	 *  Set the modelinfo.
	 *  @param modelinfo The modelinfo to set.
	 */
	public void setModelInfo(IModelInfo modelinfo)
	{
		this.modelinfo = modelinfo;
	}

	/**
	 *  Get the lastmodified.
	 *  @return the lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the lastchecked.
	 *  @return the lastchecked.
	 */
	public long getLastChecked()
	{
		return lastchecked;
	}

	/**
	 *  Set the lastchecked.
	 *  @param lastchecked The lastchecked to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
	}

//	/**
//	 *  Get the imports.
//	 *  @return the imports.
//	 */
//	public String[] getImports()
//	{
//		return imports==null? SUtil.EMPTY_STRING_ARRAY: (String[])imports.toArray(new String[imports.size()]);
//	}
//	
//	/**
//	 *  Get the imports.
//	 *  @return the imports.
//	 */
//	public String[] getAllImports()
//	{
//		if(getPackage()==null)
//			return getImports();
//			
//		String[] ret = imports==null? new String[1]: (String[])imports.toArray(new String[imports.size()+1]);
//		ret[ret.length-1] = getPackage()+".*";
//		
//		return ret;
//	}
//
//	/**
//	 *  Set the imports.
//	 *  @param imports The imports to set.
//	 */
//	public void setImports(String[] imports)
//	{
//		this.imports = SUtil.arrayToList(imports);
//	}
	
//	/**
//	 *  Add an import statement.
//	 */
//	public void addImport(String imp)
//	{
//		if(imports==null)
//			imports = new ArrayList();
//		imports.add(imp);
//	}

//	/**
//	 *  Get the filename.
//	 *  @return the filename.
//	 */
//	public String getFilename()
//	{
//		return filename;
//	}
//
//	/**
//	 *  Set the filename.
//	 *  @param filename The filename to set.
//	 */
//	public void setFilename(String filename)
//	{
//		this.filename = filename;
//	}
//
//	/**
//	 *  Get the classloader.
//	 *  @return the classloader.
//	 */
//	public ClassLoader getClassLoader()
//	{
//		return classloader;
//	}
//
//	/**
//	 *  Set the classloader.
//	 *  @param classloader The classloader to set.
//	 */
//	public void setClassLoader(ClassLoader classloader)
//	{
//		this.classloader = classloader;
//	}
	
	/**
	 *  Build the error report.
	 */
	public static IErrorReport buildReport(String modelname, String filename, String[] cats, MultiCollection entries, Map externals)
	{
		return new AbstractErrorReportBuilder(modelname, filename,
			new String[]{"Component", "Configuration"}, entries, null)
		{
			public boolean isInCategory(Object obj, String category)
			{
				return "Component".equals(category) && obj instanceof SubcomponentTypeInfo
					|| "Configuration".equals(category) && obj instanceof ConfigurationInfo;
			}
			
			public Object getPathElementObject(Object element)
			{
				return ((StackElement)element).getObject();
			}
			
			public String getObjectName(Object obj)
			{
				String	name	= null;
				String	type	= obj!=null ? SReflect.getInnerClassName(obj.getClass()) : null;
				if(obj instanceof SubcomponentTypeInfo)
				{
					name	= ((SubcomponentTypeInfo)obj).getName();
				}
				else if(obj instanceof ConfigurationInfo)
				{
					name	= ((ConfigurationInfo)obj).getName();
					type	= "Configuration";
				}
				else if(obj instanceof UnparsedExpression)
				{
					name	= ((UnparsedExpression)obj).getName();
				}
//				else if(obj instanceof MExpressionType)
//				{
//					IParsedExpression	pexp	= ((MExpressionType)obj).getParsedValue();
//					String	exp	= pexp!=null ? pexp.getExpressionText() : null;
//					name	= exp!=null ? ""+exp : null;
//				}
				
//				if(type!=null && type.startsWith("M") && type.endsWith("Type"))
//				{
//					type	= type.substring(1, type.length()-4);
//				}
				if(type!=null && type.endsWith("Info"))
				{
					type	= type.substring(0, type.length()-4);
				}
				
				return type!=null ? name!=null ? type+" "+name : type : name!=null ? name : "";
			}
		}.buildErrorReport();
	}
}
