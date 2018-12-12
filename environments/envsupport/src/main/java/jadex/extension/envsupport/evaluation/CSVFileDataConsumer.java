package jadex.extension.envsupport.evaluation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;

/**
 *  Simple file consumer that writes data into a text file.
 */
public class CSVFileDataConsumer extends SimplePropertyObject implements ITableDataConsumer
{
	//-------- constants --------
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	//-------- attributes --------
	
	/** The writer. */
	protected Writer writer;
	
	//-------- constructors --------
	
	/**
	 * 
	 */
	public CSVFileDataConsumer()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Consume data from the provider.
	 */
	public void consumeData(long time, double tick)
	{
		try
		{
			ITableDataProvider provider = getTableDataProvider();
			DataTable table = provider.getTableData(time, tick);
			
			if(writer==null)
			{
				String filename = (String)getProperty("filename");
				writer = new BufferedWriter(new FileWriter(filename));
				String[] colnames = table.getColumnNames();
				for(int i=0; i<colnames.length; i++)
				{
					if(i!=0)
						writer.write(", ");
					writer.write(colnames[i]);
					System.out.println(colnames[i]);
				}
				writer.write(lf);
			}    
	
			List rows = table.getRows();
			if(rows!=null)
			{
				for(int i=0; i<rows.size(); i++)
				{
					Object[] row = (Object[])rows.get(i);
					for(int j=0; j<row.length; j++)
					{
						if(j!=0)
							writer.write(", ");
						writer.write(""+row[j]);
						System.out.println(""+row[j]);
					}
					writer.write(lf);
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	// todo: when to close?

	/**
	 *  Get the space.
	 *  @return The space.
	 */
	public AbstractEnvironmentSpace getSpace()
	{
		return (AbstractEnvironmentSpace)getProperty("envspace");
	}
	
	/**
	 *  Get the table data provider.
	 *  @return The table data provider.
	 */
	protected ITableDataProvider getTableDataProvider()
	{
		String providername = (String)getProperty("dataprovider");
		ITableDataProvider provider = getSpace().getDataProvider(providername);
		if(provider==null)
			throw new RuntimeException("Data provider nulls: "+providername);
		return provider;
	}
}
