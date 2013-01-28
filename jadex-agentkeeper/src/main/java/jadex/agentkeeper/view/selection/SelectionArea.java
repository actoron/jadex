package jadex.agentkeeper.view.selection;

import jadex.extension.envsupport.math.Vector2Int;

import com.jme3.math.Vector2f;

/**
 *
 * @author 7willuwe : Philip Willuweit
 */
public class SelectionArea
{
    public Vector2f start = new Vector2f();
    public Vector2f end = new Vector2f();
    
    private Vector2Int worldstart;
    private Vector2Int worldend;
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
        this.worldend = new Vector2Int(Math.round(end.divide(scale).x), Math.round(end.divide(scale).y));
        this.worldstart = new Vector2Int(Math.round(start.divide(scale).x), Math.round(start.divide(scale).y));
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
        this.worldstart = new Vector2Int(Math.round(start.divide(scale).x), Math.round(start.divide(scale).y));
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
        this.worldend = new Vector2Int(Math.round(end.divide(scale).x), Math.round(end.divide(scale).y));
//        System.out.println("worldend " + worldend + " end " + end);
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
	/**
	 * @return the worldstart
	 */
	public Vector2Int getWorldstart()
	{
		return new Vector2Int(Math.round(start.divide(scale).x), Math.round(start.divide(scale).y));
	}
	/**
	 * @return the worldend
	 */
	public Vector2Int getWorldend()
	{
		return new Vector2Int(Math.round(end.divide(scale).x), Math.round(end.divide(scale).y));
	}

}
