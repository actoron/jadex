package maventest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.Tuple2;
import jadex.platform.service.dependency.maven.MavenDependencyResolverService;

public class MavenTest2
{
	public static void main(String[] args) throws Exception
	{
		File[]	files	= new File[]{
			new File("C:\\Users\\Alex\\Desktop\\jadex-bridge-2.0.jar"),
			new File("C:\\Users\\Alex\\Desktop\\jadex-applications-bdi-2.0.jar"),
			new File("../jadex-applications-bdi/target/classes"),
			new File("../jadex-bridge/target/classes"),
			new File("testproject/testmodule2/target/classes"),
//			new File("C:\\Users\\Alex\\.m2\\repository\\net\\sourceforge\\jadex\\jadex-bridge\\2.1\\jadex-bridge-2.1.jar"),
			new File("C:\\Users\\Alex\\.m2\\repository\\net\\sourceforge\\jadex\\jadex-bridge\\2.0-rc10\\jadex-bridge-2.0-rc10.jar")
		};
		String[]	gids	= new String[]{
			"de.huxhorn.sulky:de.huxhorn.sulky.3rdparty.jlayer:1.0",
			"org.activecomponents.jadex:jadex-applications-bdi:2.0"
		};
		
		MavenDependencyResolverService	mh	= new MavenDependencyResolverService(new BasicComponentIdentifier("dummy"));
		
		for(int i=0; i<files.length; i++)
		{
			try
			{
				System.out.println("\nDependencies for: "+files[i]);
				IResourceIdentifier	rid	= mh.getResourceIdentifier(MavenDependencyResolverService.getUrl(files[i])).get();
				Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>	dependencies	= mh.loadDependencies(rid, true).get();
				printDependencies(rid, dependencies.getSecondEntity(), 0, new ArrayList<Boolean>());
			}
			catch(Exception e)
			{
				System.err.println("Error getting dependencies for: "+files[i]);
				e.printStackTrace();
			}
		}
		
		for(int i=0; i<gids.length; i++)
		{
			try
			{
				System.out.println("\nDependencies for: "+gids[i]);
				IResourceIdentifier	rid	= new ResourceIdentifier(null, new GlobalResourceIdentifier(gids[i], null, null));
				Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>	dependencies	= mh.loadDependencies(rid, true).get();
				printDependencies(rid, dependencies.getSecondEntity(), 0, new ArrayList<Boolean>());
			}
			catch(Exception e)
			{
				System.err.println("Error getting dependencies for: "+gids[i]);
				e.printStackTrace();
			}
		}
	}
	
	public static void	printDependencies(IResourceIdentifier parent, Map<IResourceIdentifier, List<IResourceIdentifier>> dependencies, int indent, List<Boolean> has_children)
	{
		for(int i=0; i<indent; i++)
		{
			if(i>0 && has_children.get(i-1).booleanValue())
			{
				System.out.print("| ");
			}
			else
			{
				System.out.print("  ");				
			}
		}
		System.out.println("+-"+parent);
		
		if(dependencies.containsKey(parent))
		{
			List<IResourceIdentifier>	children	= dependencies.get(parent);
			for(int i=0; i<children.size(); i++)
			{
				has_children.add(i==children.size()-1 ? Boolean.FALSE : Boolean.TRUE);
				printDependencies(children.get(i), dependencies, indent+1, has_children);
				has_children.remove(indent);
			}
		}
		else
		{
			System.err.print("Missing dependencies for: "+parent);
		}
	}
}
