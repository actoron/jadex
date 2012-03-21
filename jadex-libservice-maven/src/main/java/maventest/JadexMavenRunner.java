package maventest;

import jadex.base.service.dependency.maven.MavenDependencyResolverService;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Resolve Jadex libraries using Maven and start the platform. 
 */
public class JadexMavenRunner
{
	public static void main(String[] args) throws Exception
	{
		MavenDependencyResolverService	mh	= new MavenDependencyResolverService(new ComponentIdentifier("dummy"));
		Set<URL>	urls	= new HashSet<URL>();
		String[]	gids	= new String[]
		{
			"net.sourceforge.jadex:jadex-tools-base:2.1-SNAPSHOT",
			"net.sourceforge.jadex:jadex-platform-standalone:2.1-SNAPSHOT",
			"net.sourceforge.jadex:jadex-kernel-component:2.1-SNAPSHOT",
			"net.sourceforge.jadex:jadex-kernel-micro:2.1-SNAPSHOT",
			"net.sourceforge.jadex:jadex-runtimetools:2.1-SNAPSHOT"
			
//			"net.sourceforge.jadex:jadex-platform-standalone:2.0",
//			"net.sourceforge.jadex:jadex-kernel-component:2.0",
//			"net.sourceforge.jadex:jadex-kernel-micro:2.0",
//			"net.sourceforge.jadex:jadex-runtimetools:2.0"
		};
		for(int i=0; i<gids.length; i++)
		{
			Map<IResourceIdentifier, List<IResourceIdentifier>>	dependencies	= mh.loadDependencies(
				new ResourceIdentifier(null, gids[i])).get(null);
			for(IResourceIdentifier rid: dependencies.keySet())
			{
				urls.add(rid.getLocalIdentifier().getUrl());
			}
		}
		
		System.out.println("Urls: "+urls);
		ClassLoader	cl	= new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
		Class<?>	starter	= Class.forName("jadex.base.Starter", true, cl);
		starter.getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
	}
}
