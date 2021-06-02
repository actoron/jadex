package jadex.bdiv3;

import jadex.bridge.ComponentIdentifier;

public class BDIClassReaderTest
{
	public static void	main(String[] args) throws Exception
	{
		long start = System.currentTimeMillis();
		BDIClassReader	bcr	= new BDIClassReader(null);
		int cnt = 1000;
		for(int i=1; i<1000; i++)
		{
			if(i%100==0)
			{
				System.out.println("done: "+i);				
			}
			bcr.read("jadex.bdiv3.TestBDI.class", (String[])null,
				BDIClassReaderTest.class.getClassLoader(), null, new ComponentIdentifier("dummy"), null);
		}
		
		long needed = System.currentTimeMillis()-start;
		System.out.println("finished in: "+needed/1000+" secs");
		System.out.println("models per sec: "+cnt/(needed/1000.0));
//		Thread.sleep(3000000);
	}
}
