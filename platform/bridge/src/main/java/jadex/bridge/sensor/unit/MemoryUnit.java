package jadex.bridge.sensor.unit;

import java.text.DecimalFormat;

/**
 *  Memory unit.
 */
public enum MemoryUnit implements IConvertableUnit<Long>, IPrettyPrintUnit<Long>
{
	B, KB, MB, GB, TB;
	
	protected static final String[] units = new String[]{"Bi", "kiB", "MiB", "GiB", "TiB", "PiB", "EiB"};
	
	protected static final DecimalFormat format = new DecimalFormat("#,##0.#");
		
	/**
	 *  Convert to a known unit.
	 */
	public Long convert(Long value)
	{
		long ret = value;
		
		if(MemoryUnit.KB.equals(this))
		{
			ret = Math.round(ret/1024d);
		}
		else if(MemoryUnit.MB.equals(this))
		{
			ret = Math.round(ret/1024d/1024);
		}
		else if(MemoryUnit.GB.equals(this))
		{
			ret = Math.round(ret/1024d/1024/1024);
		}
		else if(MemoryUnit.TB.equals(this))
		{
			ret = Math.round(ret/1024d/1024/1024/1024); //1024*1024*1024*1024; -> 0 :-(
		}
		return ret;
	}
	
	/**
	 *  Pretty print a value according to the underlying unit to a string.
	 *  @param value The value.
	 *  @return The pretty printed string.
	 */
	public String prettyPrint(Long value)
	{
		if(value <= 0) return "0";
		int dg = (int)(Math.log10(value)/Math.log10(1024));
		return format.format(value/Math.pow(1024, dg)) + " " + units[dg];
	}
}
