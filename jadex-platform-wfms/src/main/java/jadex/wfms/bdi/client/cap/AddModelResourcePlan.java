package jadex.wfms.bdi.client.cap;

import jadex.bdi.runtime.IGoal;
import jadex.wfms.bdi.ontology.RequestAddModelResource;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class AddModelResourcePlan extends AbstractWfmsPlan
{
	public void body()
	{
		RequestAddModelResource ramr = new RequestAddModelResource();
		String path = (String) getParameter("resource_path").getValue();
		if (!path.toLowerCase().endsWith(".jar"))
			fail();
		File resourceFile = new File(path);
		try
		{
			MappedByteBuffer map = (new FileInputStream(resourceFile)).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, resourceFile.length());
			byte[] resource = new byte[map.capacity()];
			map.get(resource, 0, map.capacity());
			ramr.encodeResource(resource);
		}
		catch (Exception e)
		{
			fail(e);
		}
		ramr.setResourceName(resourceFile.getName());
		
		IGoal startGoal = createGoal("reqcap.rp_initiate");
		startGoal.getParameter("action").setValue(ramr);
		startGoal.getParameter("receiver").setValue(getPdInterface());
		dispatchSubgoalAndWait(startGoal);
	}
}
