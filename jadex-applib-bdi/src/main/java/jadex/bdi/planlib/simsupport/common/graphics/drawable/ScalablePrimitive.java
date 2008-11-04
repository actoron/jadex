package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.math.IVector2;

public abstract class ScalablePrimitive implements IDrawable
{
    /** x-axis position
     */
    protected float px_;
    
    /** y-axis position
     */
    protected float py_;
    
    /** Width
     */
    protected float w_;
    
    /** Height
     */
    protected float h_;
    
    /** Sets the position of the object.
     *
     *  @param pos new position
     */
    public synchronized void setPosition(IVector2 pos)
    {
        px_ = pos.getXAsFloat();
        py_ = pos.getYAsFloat();
    }
    
    /** Sets the size of the object.
     *
     *  @param size new size
     */
    public synchronized void setSize(IVector2 size)
    {
        w_ = size.getXAsFloat();
        h_ = size.getYAsFloat();
    }
    
    /** Sets the velocity of the object.
     *  Depending on implementation, this may result in rotation or other
     *  graphical features being used.
     *  
     * 	@param velocity new velocity
     */
    public void setVelocity(IVector2 velocity)
    {
    }
}
