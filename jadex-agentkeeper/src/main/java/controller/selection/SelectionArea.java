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

    public SelectionArea(Vector2f start, Vector2f end)
    {
        this.start = start;
        this.end = end;
    }
    /**
     * For single square use only
     */
    public SelectionArea(Vector2f pos)
    {
        this.start = pos;
        this.end = pos;
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
}
