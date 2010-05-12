package deco4mas.examples.agentNegotiation.evaluate;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Holds log Information
 */
public class LogInformation
{

	protected String name;

	protected HashSet<Long> values;

	public LogInformation(String name)
	{
		this.name = name;
		values = new HashSet<Long>();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public HashSet<Long> getValues()
	{
		return values;
	}

	public void addValue(Long value)
	{
		values.add(value);
	}

	public void writeDifferenz()
	{
		Long result = 0l;
		if (values.size() == 2)
		{
			Iterator<Long> it = values.iterator();
			Long first = it.next();
			Long second = it.next();
			if (first > second)
			{
				result = first - second;
			} else
			{
				result = second - first;
			}
		}

		try
		{
			System.out.println("*** LOG WRITE ***");
			FileWriter writer = new FileWriter(name + ".txt", true);
			writer.write(result.toString() + " \r\n");
			writer.flush();
			writer.close();

		} catch (IOException e)
		{
			System.out.println("Exception");
			e.printStackTrace();
		}

	}

}