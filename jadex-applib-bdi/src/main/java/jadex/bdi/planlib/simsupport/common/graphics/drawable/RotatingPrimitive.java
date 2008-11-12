package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.math.IVector2;

public abstract class RotatingPrimitive extends ScalablePrimitive
{
	private static final float PI_2 = (float) (Math.PI / 2.0);
	
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
        rot_ = velocity.getDirectionAsFloat() + PI_2;
    }
}
