package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.Plan;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.IExternalWfmsService;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class AddModelResourcePlan extends Plan
{
	public void body()
	{
		String path = (String) getParameter("resource_path").getValue();
		if (!path.toLowerCase().endsWith(".jar"))
			fail();
		File resourceFile = new File(path);
		ProcessResource pr = null;
		try
		{
			MappedByteBuffer map = (new FileInputStream(resourceFile)).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, resourceFile.length());
			byte[] resource = new byte[map.capacity()];
			map.get(resource, 0, map.capacity());
			pr = new ProcessResource(resourceFile.getName(), resource);
		}
		catch (Exception e)
		{
			fail(e);
		}
		IExternalWfmsService wfms = (IExternalWfmsService) getBeliefbase().getBelief("wfms").getFact();
		wfms.addProcessResource(getComponentIdentifier(), pr);
	}
}
