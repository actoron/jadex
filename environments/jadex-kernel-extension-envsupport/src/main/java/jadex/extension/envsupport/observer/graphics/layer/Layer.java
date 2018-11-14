package jadex.extension.envsupport.observer.graphics.layer;

import java.awt.Color;

public class Layer
{
	/** The render information */
	private Object[] renderinfos;
	
	public static final int LAYER_TYPE_COLOR = 0;
	public static final int LAYER_TYPE_GRID	 = 1;
	public static final int LAYER_TYPE_TILED = 2;
	
	/** The type of the layer. */
	private int type;
	
	/** The color or color binding of the layer. */
	private Object color;
	
	public Layer(int type)
	{
		this(type, null);
	}
	
	public Layer(int type, Object color)
	{
		this.type = type;
		if (color == null)
			color = Color.WHITE;
		this.color = color;
		flushRenderInfo();
	}
	
	public Object getColor()
	{
		return color;
	}
	
	public void setColor(Object color)
	{
		this.color = color;
	}
	
	/**
	 *  Returns the type of the layer.
	 *  
	 *  @return Type of the layer.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Returns cached render information.
	 * 
	 * @param infoId id of the information
	 * @return render info
	 */
	public Object getRenderInfo(int infoId)
	{
		return renderinfos[infoId];
	}
	
	/**
	 * Sets cached render information.
	 * 
	 * @param infoId id of the information
	 * @param info the render info
	 */
	public void setRenderInfo(int infoId, Object info)
	{
		if (renderinfos.length <= infoId)
		{
			Object[] newInfoArray = new Object[infoId + 1];
			System.arraycopy(renderinfos, 0, newInfoArray, 0, renderinfos.length);
			renderinfos= newInfoArray;
		}
		
		renderinfos[infoId] = info;
	}
	
	/**
	 *  Flushes the render information.
	 */
	public void flushRenderInfo()
	{
		renderinfos = new Object[0];
	}
}
