package jadex.bdi.examples.blackjack;


/**
 *  Java class for concept GameResult of blackjack_beans ontology.
 */
public class GameResult
{
	//-------- attributes ----------

	/** Attribute for slot won. */
	protected boolean won;

	/** Attribute for slot money. */
	protected int money;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>GameResult</code>.
	 */
	public GameResult()
	{
	}

	//-------- accessor methods --------

	/**
	 *  Get the won of this GameResult.
	 * @return won
	 */
	public boolean isWon()
	{
		return this.won;
	}

	/**
	 *  Set the won of this GameResult.
	 * @param won the value to be set
	 */
	public void setWon(boolean won)
	{
		this.won = won;
	}

	/**
	 *  Get the money of this GameResult.
	 * @return money
	 */
	public int getMoney()
	{
		return this.money;
	}

	/**
	 *  Set the money of this GameResult.
	 * @param money the value to be set
	 */
	public void setMoney(int money)
	{
		this.money = money;
	}

	//-------- bean related methods --------

	/** The property descriptors, constructed on first access. */
	private jadex.commons.beans.PropertyDescriptor[] pds = null;

	/**
	 *  Get the bean descriptor.
	 *  @return The bean descriptor.
	 */
	public jadex.commons.beans.BeanDescriptor getBeanDescriptor()
	{
		return null;
	}

	/**
	 *  Get the property descriptors.
	 *  @return The property descriptors.
	 */
	public jadex.commons.beans.PropertyDescriptor[] getPropertyDescriptors()
	{
		if(pds == null)
		{
			try
			{
				pds = new jadex.commons.beans.PropertyDescriptor[]{new jadex.commons.beans.PropertyDescriptor("won", this.getClass(), "isWon", "setWon"),
						new jadex.commons.beans.PropertyDescriptor("money", this.getClass(), "getMoney", "setMoney")};
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return pds;
	}

	/**
	 *  Get the default property index.
	 *  @return The property index.
	 */
	public int getDefaultPropertyIndex()
	{
		return -1;
	}

	/**
	 *  Get the event set descriptors.
	 *  @return The event set descriptors.
	 */
	public jadex.commons.beans.EventSetDescriptor[] getEventSetDescriptors()
	{
		return null;
	}

	/**
	 *  Get the default event index.
	 *  @return The default event index.
	 */
	public int getDefaultEventIndex()
	{
		return -1;
	}

	/**
	 *  Get the method descriptors.
	 *  @return The method descriptors.
	 */
	public jadex.commons.beans.MethodDescriptor[] getMethodDescriptors()
	{
		return null;
	}

	/**
	 *  Get additional bean info.
	 *  @return Get additional bean info.
	 */
	public jadex.commons.beans.BeanInfo[] getAdditionalBeanInfo()
	{
		return null;
	}

	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public java.awt.Image getIcon(int iconKind)
	{
		return null;
	}

	/**
	 *  Load the image.
	 *  @return The image.
	 */
	public java.awt.Image loadImage(final String resourceName)
	{
		try
		{
			final Class c = getClass();
			java.awt.image.ImageProducer ip = (java.awt.image.ImageProducer)java.security.AccessController.doPrivileged(new java.security.PrivilegedAction()
			{
				public Object run()
				{
					java.net.URL url;
					if((url = c.getResource(resourceName)) == null)
					{
						return null;
					}
					else
					{
						try
						{
							return url.getContent();
						}
						catch(java.io.IOException ioe)
						{
							return null;
						}
					}
				}
			});
			if(ip == null)
				return null;
			java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
			return tk.createImage(ip);
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this GameResult.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "GameResult(" + ")";
	}

}
