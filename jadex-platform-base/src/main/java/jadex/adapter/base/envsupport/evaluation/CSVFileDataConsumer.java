package jadex.adapter.base.envsupport.evaluation;

import jadex.commons.SimplePropertyObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

/**
 * 
 */
public class CSVFileDataConsumer extends SimplePropertyObject implements ITableDataConsumer
{
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
//	/** The table data provider. */
//	protected ITableDataProvider provider;
//	
//	/** The filename. */
//	protected String filename;
	
	/** The writer. */
	protected Writer writer;
	
	/**
	 * 
	 */
	public CSVFileDataConsumer()
	{
	}
	
	/**
	 * 
	 */
//	public TableCSVFileWriter(ITableDataProvider provider, String filename)
//	{
//		this.provider = provider;
//		this.filename = filename;
//	}
	
	/**
	 *  Consume data from the provider.
	 */
	public void consumeData(long time)
	{
		try
		{
			ITableDataProvider provider = (ITableDataProvider)getProperty("dataprovider");
			DataTable table = provider.getTableData(time);
			
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
}
