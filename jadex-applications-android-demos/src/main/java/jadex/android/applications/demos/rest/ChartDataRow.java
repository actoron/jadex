package jadex.android.applications.demos.rest;

public class ChartDataRow
{
	private String label;
	private int color;
	private double[] data;

	public ChartDataRow()
	{
		label = "";
		data = new double[0];
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
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
