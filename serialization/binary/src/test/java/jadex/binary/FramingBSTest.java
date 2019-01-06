package jadex.binary;

import java.io.ByteArrayInputStream;

public class FramingBSTest extends BSTest
{
	/**
	 * 
	 */
	public Object doWrite(Object wo)
	{
//		Object[] arr = new Object[100];
//		for (int i = 0; i < arr.length; ++i)
//			arr[i] = new BigBean();
//		long ts = System.currentTimeMillis();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		SBinarySerializer.writeObjectToStream(baos, arr, null);
////		SBinarySerializer.writeObjectToFramedArray(arr, null);
//		ts = System.currentTimeMillis() - ts;
//		System.out.println("Speed: " + ts);
		return SBinarySerializer.writeObjectToFramedArray(wo, null);
	}
	
	/**
	 * 
	 */
	public Object doRead(Object ro) 
	{
		ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) ro);
		return SBinarySerializer.readObjectFromFramedStream(bais, null, null, null, null, null);
	}
	
	class BigBean
	{
		public Object[] beans = new Object[1000];
		
		public BigBean()
		{
			for (int i = 0; i < beans.length; ++i)
				beans[i] = getABean();
		}
		
		public Object[] getBeans()
		{
			return beans;
		}
		
		public void setBeans(Object[] beans)
		{
			this.beans = beans;
		}
	}
}
