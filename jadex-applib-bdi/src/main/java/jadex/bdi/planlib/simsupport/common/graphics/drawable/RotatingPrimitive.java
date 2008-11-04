package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.math.IVector2;

public abstract class RotatingPrimitive extends ScalablePrimitive
{
    /** Object rotation
     */
    protected float rot_;
    
    /** Sets the velocity of the object.
     *  Depending on implementation, this may result in rotation or other
     *  graphical features being used.
     *  
     * 	@param velocity new velocity
     */
    public void setVelocity(IVector2 velocity)
    {
        rot_ = -velocity.getDirectionAsFloat();
    }
}
