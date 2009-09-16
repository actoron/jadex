package eis.examples.jarloader;

import java.io.File;
import java.io.IOException;

import eis.EnvironmentInterfaceStandard;

/**
 * Loads an environment interface from a file and instantiates it.
 * 
 * @author tristanbehrens
 *
 */
public class JarLoader {

	public static void main(String[] args) throws IOException {
		
		if( args.length == 0) {
			
			System.out.println("You have to provide a filename.");
			
		}
		else {
		
			EnvironmentInterfaceStandard ei = EnvironmentInterfaceStandard.fromJarFile( new File(args[0]) ); 
		
		}
		
	}
	
}
