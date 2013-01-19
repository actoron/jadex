package controller.selection;

import com.jme3.math.Vector2f;

/**
 *
 * @author lohnn
 */
public class SelectionArea
{
    public Vector2f start = new Vector2f();
    public Vector2f end = new Vector2f();
    public float delta_xaxis;
    public float delta_yaxis;
    public float scale = 1;

    public SelectionArea(Vector2f start, Vector2f end)
    {
        this.start = start;
        this.end = end;
    }
    /**
     * For single square use only
     * @param vector2f 
     * @param appScaled 
     */
    public SelectionArea(float appScaled, Vector2f start, Vector2f end)
    {
        this.start = start;
        this.end = end;
        this.scale = appScaled;
    }

    /**
     * @return the start
     */
    public Vector2f getStart()
    {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Vector2f start)
    {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Vector2f getEnd()
    {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Vector2f end)
    {
        this.end = end;
    }
	/**
	 * @return the scale
	 */
	public float getScale()
	{
		return scale;
	}
	/**
	 * @param scale the scale to set
	 */
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	/**
	 * @return the delta_xaxis
	 */
	public float getDeltaXaxis()
	{
		return end.x-start.x;
	}
	/**
	 * @return the delta_yaxis
	 */
	public float getDeltaYaxis()
	{
		return end.y-start.y;
	}

}
