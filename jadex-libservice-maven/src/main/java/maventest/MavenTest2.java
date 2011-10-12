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
//		File	file	= new File("C:\\Users\\Alex\\Desktop\\jadex-bridge-2.0.jar");
//		File	file	= new File("C:\\Users\\Alex\\Desktop\\jadex-applications-bdi-2.0.jar");
		File	file	= new File("target/classes");
		URL	url	= MavenHandler.getUrl(file);
		
		MavenHandler	mh	= new MavenHandler();
		Map<URL, List<URL>>	dependencies	= mh.loadDependencies(url);
		printDependencies(url, dependencies, 0, new ArrayList<Boolean>());
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
