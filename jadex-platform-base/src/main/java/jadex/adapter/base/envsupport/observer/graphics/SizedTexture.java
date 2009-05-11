package jadex.adapter.base.envsupport.observer.graphics;

import jadex.adapter.base.envsupport.math.IVector2;

/**
 * A texture that is supposed to be displayed with a certain size.
 */
public class SizedTexture
{
	private int texId;
	
	private IVector2 size;
	
	public SizedTexture(int texId, IVector2 size)
	{
		this.texId = texId;
		this.size = size;
	}
	
	public int getTexId()
	{
		return texId;
	}
	
	public IVector2 getSize()
	{
		return size;
	}
}
