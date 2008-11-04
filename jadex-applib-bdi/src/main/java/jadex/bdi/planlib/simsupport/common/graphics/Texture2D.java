package jadex.bdi.planlib.simsupport.common.graphics;

/** Class for OpenGL textures.
 */
public class Texture2D
{
    private int texId_;
    private float maxX_;
    private float maxY_;
    
    /** Creates a new texture.
     *
     *  @param texId texture ID
     *  @param maxX maximum X texture coordinate
     *  @param maxY maximum Y texture coordinate
     */
    public Texture2D(int texId, float maxX, float maxY)
    {
        texId_ = texId;
        maxX_ = maxX;
        maxY_ = maxY;
    }
    
    /** Returns the texture ID
     */
    public int getTexId()
    {
        return texId_;
    }
    
    /** Returns texture x-coordinate maximum
     *
     *  @return texture x-coordinate maximum
     */
    public float getMaxX()
    {
        return maxX_;
    }
    
    /** Returns texture y-coordinate maximum
     *
     *  @return texture y-coordinate maximum
     */
    public float getMaxY()
    {
        return maxY_;
    }
}
