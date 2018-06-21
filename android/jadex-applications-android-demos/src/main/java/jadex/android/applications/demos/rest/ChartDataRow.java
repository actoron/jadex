package jadex.android.applications.demos.rest;

public class ChartDataRow
{
	private int color;
	private double[] data;

	public ChartDataRow()
	{
		data = new double[0];
	}

	public int getColor()
	{
		return color;
	}

	public void setColor(int color)
	{
		this.color = color;
	}

	public double[] getData()
	{
		return data;
	}

	public void setData(double[] data)
	{
		this.data = data;
	}

}
