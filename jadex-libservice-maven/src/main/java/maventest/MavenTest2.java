package maventest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MavenTest2
{
	public static void main(String[] args)
	{
		File[]	files	= new File[]{
			new File("C:\\Users\\Alex\\Desktop\\jadex-bridge-2.0.jar"),
			new File("C:\\Users\\Alex\\Desktop\\jadex-applications-bdi-2.0.jar"),
			new File("../jadex-applications-bdi/target/classes"),
			new File("../jadex-bridge/target/classes"),
			new File("testproject/testmodule2/target/classes"),
			new File("C:\\Users\\Alex\\.m2\\repository\\net\\sourceforge\\jadex\\jadex-bridge\\2.1-SNAPSHOT\\jadex-bridge-2.1-SNAPSHOT.jar"),
			new File("C:\\Users\\Alex\\.m2\\repository\\net\\sourceforge\\jadex\\jadex-bridge\\2.0-rc10\\jadex-bridge-2.0-rc10.jar")
		};
		
		for(int i=0; i<files.length; i++)
		{
			try
			{
				System.out.println("\nDependencies for: "+files[i]);
				URL	url	= MavenHandler.getUrl(files[i]);
				
				MavenHandler	mh	= new MavenHandler();
				Map<URL, List<URL>>	dependencies	= mh.loadDependencies(url);
				printDependencies(url, dependencies, 0, new ArrayList<Boolean>());
			}
			catch(Exception e)
			{
				System.err.println("Error getting dependencies for: "+files[i]);
				e.printStackTrace();
			}
		}
	}
	
	public static void	printDependencies(URL parent, Map<URL, List<URL>> dependencies, int indent, List<Boolean> has_children)
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
		
		List<URL>	children	= dependencies.get(parent);
		for(int i=0; i<children.size(); i++)
		{
			has_children.add(i==children.size()-1 ? Boolean.FALSE : Boolean.TRUE);
			printDependencies(children.get(i), dependencies, indent+1, has_children);
			has_children.remove(indent);
		}
	}
}
