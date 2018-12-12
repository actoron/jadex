package jadex.xml.tutorial.example14;

/**
 *  Basic computer implementation class.
 */
public class Computer extends Product
{
	//-------- attributes --------
	
	/** The invoice name. */
	protected String processor;
	
	/** The ram. */
	protected String ram;
	
	/** The harddrive. */
	protected String harddrive;

	//-------- methods --------
	
	/**
	 *  Get the processor.
	 *  @return The processor.
	 */
	public String getProcessor()
	{
		return processor;
	}

	/**
	 *  Set the processor.
	 *  @param processor The processor to set.
	 */
	public void setProcessor(String processor)
	{
		this.processor = processor;
	}

	/**
	 *  Get the ram.
	 *  @return The ram.
	 */
	public String getRam()
	{
		return ram;
	}

	/**
	 *  Set the ram.
	 *  @param ram The ram to set.
	 */
	public void setRam(String ram)
	{
		this.ram = ram;
	}

	/**
	 *  Get the harddrive.
	 *  @return The harddrive.
	 */
	public String getHarddrive()
	{
		return harddrive;
	}

	/**
	 *  Set the harddrive.
	 *  @param harddrive The harddrive to set.
	 */
	public void setHarddrive(String harddrive)
	{
		this.harddrive = harddrive;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Computer(harddrive=" + harddrive + ", processor=" + processor
			+ ", ram=" + ram + ", toString()=" + super.toString() + ")";
	}
}
