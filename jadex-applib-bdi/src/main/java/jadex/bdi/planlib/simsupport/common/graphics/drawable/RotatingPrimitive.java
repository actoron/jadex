package jadex.bdi.planlib.simsupport.common.graphics.drawable;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

import java.awt.Graphics2D;

import javax.media.opengl.GL;


public abstract class RotatingPrimitive implements IDrawable
{
	private static final float	PI_2	= (float)(Math.PI / 2.0);

	/** Flag whether the drawable is rotating. */
	protected boolean			rotating_;

	/** Drawable rotation. */
	protected float				rot_;

	/** x-axis position. */
	protected float				px_;

	/** y-axis position. */
	protected float				py_;

	/** Width */
	protected float				w_;

	/** Height */
	protected float				h_;

	/** x-shift */
	protected float				shiftX_;

	/** y-shift */
	protected float				shiftY_;

	/**
	 * Initializes the drawable.
	 * 
	 * @param size initial size
	 * @param shift shift from the centered position using scale(1.0, 1.0)
	 * @param rotating if true, the resulting drawable will rotate depending on
	 *        the velocity
	 */
	protected RotatingPrimitive(IVector2 size, IVector2 shift, boolean rotating)
	{
		setPosition(new Vector2Double(0.0));
		setSize(size);
		setVelocity(new Vector2Double(0.0));
		setRotating(rotating);
		setShift(shift);
	}

	/**
	 * Gets the position of the drawable.
	 * 
	 * @return position of the drawable
	 */
	public synchronized IVector2 getPosition()
	{
		return new Vector2Double(px_, py_);
	}

	/**
	 * Sets the position of the drawable.
	 * 
	 * @param pos new position
	 */
	public synchronized void setPosition(IVector2 pos)
	{
		px_ = pos.getXAsFloat();
		py_ = pos.getYAsFloat();
	}

	/**
	 * Gets the size of the drawable.
	 * 
	 * @return size of the drawable
	 */
	public synchronized IVector2 getSize()
	{
		return new Vector2Double(w_, h_);
	}

	/**
	 * Sets the size of the drawable.
	 * 
	 * @param size new size
	 */
	public synchronized void setSize(IVector2 size)
	{
		w_ = size.getXAsFloat();
		h_ = size.getYAsFloat();
	}

	/**
	 * Sets the shift away from the centered position using scale(1.0, 1.0).
	 * 
	 * @param shift the shift from the centered position using scale(1.0, 1.0)
	 */
	public synchronized void setShift(IVector2 shift)
	{
		shiftX_ = shift.getXAsFloat();
		shiftY_ = shift.getYAsFloat();
	}

	/**
	 * Sets the velocity of the drawable.
	 * 
	 * @param velocity new velocity
	 */
	public void setVelocity(IVector2 velocity)
	{
		rot_ = velocity.getDirectionAsFloat() - PI_2;
	}

	/**
	 * Determines whether the drawable is rotating depending on its velocity.
	 * 
	 * @return true if the drawable is rotating depending on its velocity
	 */
	public boolean isRotating()
	{
		return rotating_;
	}

	/**
	 * Sets whether the drawable rotates depending on its velocity.
	 * 
	 * @param rotating if true, the drawable will rotate depending on its
	 *        velocity
	 */
	public void setRotating(boolean rotating)
	{
		rotating_ = rotating;
	}

	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param g graphics context
	 */
	protected void setupMatrix(Graphics2D g)
	{
		g.translate(px_, py_);
		g.translate(shiftX_, shiftY_);
		g.scale(w_, h_);
		if(rotating_)
		{
			g.rotate(rot_);
		}
	}

	/**
	 * Sets up the transformation matrix before drawing.
	 * 
	 * @param gl OpenGL context
	 */
	protected void setupMatrix(GL gl)
	{
		gl.glTranslatef(px_, py_, 0.0f);
		gl.glTranslatef(shiftX_, shiftY_, 0.0f);
		gl.glScalef(w_, h_, 1.0f);
		if(rotating_)
		{
			gl.glRotated(Math.toDegrees(rot_), 0.0, 0.0, 1.0);
		}
	}
}
