package maventest;

import java.io.File;

public class MavenTest2
{
	public static void main(String[] args)
	{
		// Shrinkwrap code
//		MavenDependencyResolver	resolver	= DependencyResolvers.use(MavenDependencyResolver.class).loadDependenciesFromPom("testpom.xml");
		
		MavenBuilder	mb	= new MavenBuilder();
		mb.loadDependenciesFromPom("testpom.xml");
		File[]	files	= mb.resolveAsFiles();
		for(int i=0; i<files.length; i++)
		{
			System.out.println("resolved: "+files[i]);
		}
	}
}
