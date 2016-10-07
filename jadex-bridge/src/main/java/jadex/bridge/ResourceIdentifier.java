package jadex.bridge;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;

/**
 *  Default implementation for resource identification.
 *  Contains only a local identifier and a global identifier
 *  that can be used to find the resource.
 */
@Reference(local=true, remote=false)
public class ResourceIdentifier implements IResourceIdentifier
{
	//-------- constants --------
	
	/** The ignored directories for determining resource project name. */
	protected static final Set<String>	IGNORED	= Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
		"bin",
		"classes",
		"target",
		"build",
		"main"
	)));
	
	/** The jadex project names. */
	protected static final Set<String>	JADEX_PROJECTS	= Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
		"jadex-android-antlr",
		"jadex-android-commons",
		"jadex-android-parent",
		"jadex-android-platformapp",
		"jadex-android-platformclient",
		"jadex-android-xmlpull",
		"jadex-applib-bdi",
		"jadex-bridge",
		"jadex-commons",
		"jadex-distribution-minimal",
		"jadex-distribution-pro",
		"jadex-distribution-standard",
		"jadex-javaparser",
		"jadex-kernel-application",
		"jadex-kernel-base",
		"jadex-kernel-bdi",
		"jadex-kernel-bdibpmn",
		"jadex-kernel-bdiv3",
		"jadex-kernel-bdiv3-android",
		"jadex-kernel-bpmn",
		"jadex-kernel-component",
		"jadex-kernel-extension-agr",
		"jadex-kernel-extension-envsupport",
		"jadex-kernel-extension-envsupport-jmonkey",
		"jadex-kernel-extension-envsupport-opengl",
		"jadex-kernel-gpmn",
		"jadex-kernel-micro",
		"jadex-model-bpmn",
		"jadex-nuggets",
		"jadex-parent",
		"jadex-parent-pro",
		"jadex-platform",
		"jadex-platform-android",
		"jadex-platform-extension-management",
		"jadex-platform-extension-maven",
		"jadex-platform-extension-relay",
		"jadex-platform-extension-relay-standalone",
		"jadex-platform-extension-securetransport",
		"jadex-platform-extension-webservice",
		"jadex-platform-extension-webservice-android",
		"jadex-platform-extension-webservice-desktop",
		"jadex-platform-standalone-launch",
		"jadex-rules",
		"jadex-rules-eca",
		"jadex-rules-tools",
		"jadex-runtimetools-android",
		"jadex-runtimetools-swing",
		"jadex-servletfilter",
		"jadex-tools-base",
		"jadex-tools-base-swing",
		"jadex-tools-bdi",
		"jadex-tools-bpmn",
		"jadex-tools-comanalyzer",
		"jadex-xml"
	)));
	
	//-------- attributes --------
	
	/** The local identifier. */
	protected ILocalResourceIdentifier	lid;
	
	/** The global identifier. */
	protected IGlobalResourceIdentifier gid;
	
	//-------- constructors --------

	/**
	 *  Create a resource identifier.
	 */
	public ResourceIdentifier()
	{
		// bean constructor
	}
	
	/**
	 *  Create a resource identifier.
	 *  @param lid The local identifier.
	 *  @param gid The global idenfifier.
	 */
	public ResourceIdentifier(ILocalResourceIdentifier lid, IGlobalResourceIdentifier gid)
	{
//		if(lid.getUrl()!=null && lid.getUrl().toString().indexOf("jar:")!=-1)
//			System.out.println("hjere");
		
		this.lid = lid;
		this.gid = gid;
		
		if(gid==null && lid!=null)
		{
			File f	= new File(lid.getUri().getPath());
			// Use SUtil.getHashCode(f, false) to test with directory and jar
			this.gid	= new GlobalResourceIdentifier("::"+SUtil.getHashCode(f, true), null, null);
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the local identifier.
	 *  The local identifier consists of the platform 
	 *  component identifier and the URL of the resource. 
	 *  @return The local identifier. 
	 */
	public ILocalResourceIdentifier getLocalIdentifier()
	{
		return lid;
	}
	
	/**
	 *  Get the global identifier.
	 *  @return The global identifier.
	 */
	public IGlobalResourceIdentifier getGlobalIdentifier()
	{
		return gid;
	}

	/**
	 *  Set the local identifier.
	 *  @param lid The lid to set.
	 */
	public void setLocalIdentifier(ILocalResourceIdentifier lid)
	{
		this.lid = lid;
	}

	/**
	 *  Set the global identifier.
	 *  @param gid The gid to set.
	 */
	public void setGlobalIdentifier(IGlobalResourceIdentifier gid)
	{
		this.gid = gid;
	}

	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (gid!=null? gid.hashCode(): lid.hashCode());
		return result;
	}

	/**
	 *  Test if equals.
	 *  They are equal when
	 *  a) global ids are equal
	 *  b) or global ids are null and local ids are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof IResourceIdentifier)
		{
			IResourceIdentifier other = (IResourceIdentifier)obj;
			ret = (getGlobalIdentifier()!=null && getGlobalIdentifier().equals(other.getGlobalIdentifier()))
				|| (getGlobalIdentifier()==null && other.getGlobalIdentifier()==null && SUtil.equals(getLocalIdentifier(), other.getLocalIdentifier()));
		}
		return ret;
	}
	
	/**
	 *  Test if the global id is non-null and a hash id.
	 */
	public static boolean	isHashGid(IResourceIdentifier rid)
	{
		return rid!=null && rid.getGlobalIdentifier()!=null && rid.getGlobalIdentifier().getResourceId()!=null && rid.getGlobalIdentifier().getResourceId().startsWith("::");
	}
	
	/**
	 *  Create properties from rid.
	 *  @param The resource identifier.
	 *  @return rid The resource identifier properties.
	 */
	public static Properties ridToProperties(IResourceIdentifier rid, IComponentIdentifier root)
	{
		Properties ret = new Properties();
		boolean	local	= false;
		boolean	global	= false;
		
		if(rid!=null && rid.getGlobalIdentifier()!=null && rid.getGlobalIdentifier().getResourceId()!=null
			&& !isHashGid(rid))	// Don't save hash ids as contents might change.
		{
			ret.addProperty(new Property("gid_ri", rid.getGlobalIdentifier().getResourceId()));
			ret.addProperty(new Property("gid_vi", rid.getGlobalIdentifier().getVersionInfo()));
//			ret.addProperty(new Property("url", rid.getGlobalIdentifier().getRepositoryInfo()));
			global	= true;
		}
		if(isLocal(rid, root))
		{
			ret.addProperty(new Property("lid_url", SUtil.convertPathToRelative(rid.getLocalIdentifier().getUri().toString())));
			local	= true;
		}
		
		if(rid!=null && !global && !local)
		{
			throw new RuntimeException("Cannot store non-local, non-global RID: "+rid);
		}
		
		return ret;
	}
	
	/**
	 *  Create a rid from properties.
	 *  @param rid The resource identifier properties.
	 *  @return The resource identifier.
	 */
	public static IResourceIdentifier ridFromProperties(Properties rid, IComponentIdentifier root)
	{
		String gid_ri = rid.getStringProperty("gid_ri");
		String gid_vi = rid.getStringProperty("gid_vi");
		
		String lid_url = rid.getStringProperty("lid_url");
		
		GlobalResourceIdentifier gid = null;
		if(gid_vi!=null)
		{
			gid = new GlobalResourceIdentifier(gid_ri, null, gid_vi);
		}
		
		LocalResourceIdentifier lid = null;
		if(lid_url!=null)
		{
			try
			{
				URL	url	= SUtil.getFile(new URL(lid_url)).getCanonicalFile().toURI().toURL();
//				System.out.println("url: "+url);
				lid = new LocalResourceIdentifier(root, url);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return gid!=null || lid!=null? new ResourceIdentifier(lid, gid): null;
	}

	/**
	 *  Test if a rid is local to this platform.
	 *  Test is performed by MAC address.
	 *  Root cid is only used as fallback.
	 */
	public static boolean	isLocal(IResourceIdentifier rid, IComponentIdentifier root)
	{
		return rid!=null && rid.getLocalIdentifier()!=null &&
			(SUtil.equals(rid.getLocalIdentifier().getHostIdentifier(), SUtil.getMacAddress()) || SUtil.equals(rid.getLocalIdentifier().getHostIdentifier(), root.getName()));
	}
	
	/**
	 *  Test if a rid refers to one of the jadex platform modules, i.e. is not application-specific.
	 */
	public static boolean	isJadexRid(IResourceIdentifier rid)
	{
		String	project	= null;
		
		// Maven id.
		if(rid.getGlobalIdentifier()!=null && rid.getGlobalIdentifier().getResourceId()!=null && !isHashGid(rid))
		{
			project	= rid.getGlobalIdentifier().getResourceId();
			// Strip group id.
			project	= project.substring(project.indexOf(':')+1);
			// Strip version
			if(project.indexOf(':')!=-1)
			{
				project	= project.substring(0, project.indexOf(':'));
			}
		}
		
		// file name
		else if(rid.getLocalIdentifier()!=null && rid.getLocalIdentifier().getUri()!=null)
		{
			File	file	= SUtil.getFile(SUtil.toURL(rid.getLocalIdentifier().getUri()));
			if(file.getName().endsWith(".jar"))
			{
				project	= SUtil.getJarName(file.getName());
			}
			else
			{
				while(file!=null && IGNORED.contains(file.getName()))
				{
					file	= file.getParentFile();
				}
				
				if(file!=null)
				{
					project	= file.getName();
				}
			}
		}
		
		return JADEX_PROJECTS.contains(project);
	}

	/**
	 *  Get a string representation of this object.
	 */
	public String	toString()
	{
//		return "ResourceIdentifier(globalid="+gid==null? "n/a": gid+", localid"+(lid!=null?lid.toString(): "n/a")+")";
		return "global="+(gid==null? "n/a": gid)+", local="+(lid!=null? lid.toString(): "n/a");
	}
}
