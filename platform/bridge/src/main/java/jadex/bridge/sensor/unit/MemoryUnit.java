package jadex.bridge.sensor.unit;

/**
 *  Memory unit.
 */
public enum MemoryUnit implements IConvertableUnit<Long>
{
	B, KB, MB, GB, TB;
		
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
}
