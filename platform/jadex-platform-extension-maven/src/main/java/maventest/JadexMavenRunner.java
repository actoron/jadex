package maventest;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.platform.service.dependency.maven.MavenDependencyResolverService;

/**
 *  Resolve Jadex libraries using Maven and start the platform. 
 */
public class JadexMavenRunner
{
	public static void main(String[] args) throws Exception
	{
		String[]	paths	= new String[]
		{
			"../jadex-tools-base/target/classes",
			"../jadex-platform-standalone/target/classes",
			"../jadex-kernel-component/target/classes",
			"../jadex-kernel-micro/target/classes",
			"../jadex-runtimetools/target/classes"
		};
		
//		String[]	gids	= new String[]
//		{
//			// Latest snapshot version.
//			"org.activecomponents.jadex:jadex-tools-base:[2.0-SNAPSHOT,)",
//			"org.activecomponents.jadex:jadex-platform-standalone:[2.0-SNAPSHOT,)",
//			"org.activecomponents.jadex:jadex-kernel-component:[2.0-SNAPSHOT,)",
//			"org.activecomponents.jadex:jadex-kernel-micro:[2.0-SNAPSHOT,)",
//			"org.activecomponents.jadex:jadex-runtimetools:[2.0-SNAPSHOT,)"
//			
////			"org.activecomponents.jadex:jadex-platform-standalone:2.0",
////			"org.activecomponents.jadex:jadex-kernel-component:2.0",
////			"org.activecomponents.jadex:jadex-kernel-micro:2.0",
////			"org.activecomponents.jadex:jadex-runtimetools:2.0"
//		};
		
		IComponentIdentifier	cid	= new BasicComponentIdentifier("dummy");
		MavenDependencyResolverService	mh	= new MavenDependencyResolverService(cid);
		IResourceIdentifier[]	rids	= new IResourceIdentifier[paths.length];	// [gids.length];
		for(int i=0; i<rids.length; i++)
		{
			rids[i]	= new ResourceIdentifier(new LocalResourceIdentifier(cid, new File(paths[i]).toURI().toURL()), null);
//			rids[i]	= new ResourceIdentifier(null, gids[i]);
		}
				
		// Flat class loader.
		Set<URL>	urls	= new HashSet<URL>();
		for(int i=0; i<rids.length; i++)
		{
			Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>	dependencies	= mh.loadDependencies(rids[i], true).get();
			for(IResourceIdentifier rid: dependencies.getSecondEntity().keySet())
			{
				urls.add(SUtil.toURL(rid.getLocalIdentifier().getUri()));
			}
		}
		ClassLoader	cl	= new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
		
//		// Delegate class loader.
//		Map<URL, DelegationURLClassLoader>	cls	= new HashMap<URL, DelegationURLClassLoader>();
//		DelegationURLClassLoader[]	delegates	= new DelegationURLClassLoader[rids.length];
//		for(int i=0; i<rids.length; i++)
//		{
//			Map<IResourceIdentifier, List<IResourceIdentifier>>	dependencies	= mh.loadDependencies(rids[i]).get(null);
//			for(IResourceIdentifier rid: dependencies.keySet())
//			{
//				if(!cls.containsKey(rid.getLocalIdentifier().getUrl()))
//				{
//					cls.put(rid.getLocalIdentifier().getUrl(), getDelegationClassLoader(rid, cls, dependencies));
//				}
//			}
//			delegates[i]	= cls.get(rids[i].getLocalIdentifier().getUrl());
//		}
//		ClassLoader	cl	= new DelegationURLClassLoader(null, delegates);
		
		Thread.sleep(10000);

		
		// Load classes using wrong imports (classes will be found in java.lang fallback).
		String[]	imports	= new String[]{"a.*", "b.*", "c.*", "d.*", "e.*", "f.*", "g.*", "h.*", "i.*", "j.*"};
		long	start	= System.nanoTime();
		SReflect.findClass("Object", imports, cl);
		long	time	= System.nanoTime() - start;
		System.out.println("1st load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("String", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("2nd load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("Integer", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("3nd load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("Long", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("4th load took: "+(time/100000)/10.0+" ms");
		
		// Load same classes again
		start	= System.nanoTime();
		SReflect.findClass("Object", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("dup load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("String", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("2nd load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("Integer", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("3nd load took: "+(time/100000)/10.0+" ms");
		start	= System.nanoTime();
		SReflect.findClass("Long", imports, cl);
		time	= System.nanoTime() - start;
		System.out.println("4th load took: "+(time/100000)/10.0+" ms");
		
		Thread.sleep(20000000);
//		Class<?>	starter	= Class.forName("jadex.base.Starter", true, cl);
//		starter.getMethod("main", new Class[]{String[].class}).invoke(null, new Object[]{args});
	}
	
//	protected static DelegationURLClassLoader	getDelegationClassLoader(IResourceIdentifier rid,
//		Map<URL, DelegationURLClassLoader> cls, Map<IResourceIdentifier, List<IResourceIdentifier>> dependencies)
//	{
//		List<IResourceIdentifier>	deps	= dependencies.get(rid);
//		DelegationURLClassLoader[]	depcls	= new DelegationURLClassLoader[deps.size()];
//		for(int i=0; i<depcls.length; i++)
//		{
//			if(!cls.containsKey(deps.get(i).getLocalIdentifier().getUrl()))
//			{
//				cls.put(deps.get(i).getLocalIdentifier().getUrl(), getDelegationClassLoader(deps.get(i), cls, dependencies));
//			}
//			depcls[i]	= cls.get(deps.get(i).getLocalIdentifier().getUrl());
//		}
//		
//		return new DelegationURLClassLoader(rid, JadexMavenRunner.class.getClassLoader(), depcls);
//	}
}
