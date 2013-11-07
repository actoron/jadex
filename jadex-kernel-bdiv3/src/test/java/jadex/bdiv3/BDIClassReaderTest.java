package jadex.bdiv3;

import jadex.bridge.ComponentIdentifier;

public class BDIClassReaderTest
{
	public static void	main(String[] args) throws Exception
	{
		BDIClassReader	bcr	= new BDIClassReader(null);
		for(int i=1; i<10000; i++)
		{
			if(i%100==0)
			{
				System.out.println("done: "+i);				
			}
			bcr.read("jadex.bdiv3.TestBDI.class", (String[])null,
				BDIClassReaderTest.class.getClassLoader(), null, new ComponentIdentifier("dummy"));
		}
		
		System.out.println("finished");
		Thread.sleep(3000000);
	}
}
