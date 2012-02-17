package sodekovs.bikesharing.simulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import sodekovs.bikesharing.fahrrad.FahrradVerleihStation;


import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.evaluation.CSVFileDataConsumer;
import jadex.extension.envsupport.evaluation.DataTable;
import jadex.extension.envsupport.evaluation.ITableDataConsumer;
import jadex.extension.envsupport.evaluation.ITableDataProvider;
import jadex.extension.envsupport.evaluation.XYChartDataConsumer;
import jadex.commons.IPropertiesProvider;

//import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
//import jadex.application.space.envsupport.evaluation.CSVFileDataConsumer;
//import jadex.application.space.envsupport.evaluation.DataTable;
//import jadex.application.space.envsupport.evaluation.ITableDataConsumer;
//import jadex.application.space.envsupport.evaluation.ITableDataProvider;
//import jadex.application.space.envsupport.evaluation.XYChartDataConsumer;
//import jadex.base.gui.IPropertiesProvider;
import jadex.commons.SimplePropertyObject;

public class StationsDatenConsumer extends SimplePropertyObject implements ITableDataConsumer
{
	
	class Writerdaten
	{
		Writer w;
		long letztesSchreiben;
	}
	
	private Map<String, Writerdaten> _dateizugreifer;
	
	public StationsDatenConsumer()
	{
		_dateizugreifer = new HashMap<String, Writerdaten>();
	}
	
	@Override
	public void consumeData(long time, double tick)
	{
//		System.out.println("Stationsdaten!");
		ITableDataProvider dataProvider = getTableDataProvider();
		DataTable table = dataProvider.getTableData(time, tick);
		
		int belegt = 0;
		String fvs = "";
		for ( Object s : table.getRows())
		{
			if ( s instanceof Object[] )
			{
				for ( Object m : (Object[])s )
				{
//					System.out.println("S: " + m + " " + m.getClass() );
					if ( m instanceof Integer )
					{
						belegt = (Integer)m;
					}
					if ( m instanceof String )
					{
						fvs = (String) m;
					}
				}
//				System.out.println("Name: " + fvs + " Anzahl: " + belegt);
				if ( ! _dateizugreifer.containsKey( fvs ))
				{
					try {
//						System.out.println("Neuer Writer!");
						Writer w = new BufferedWriter( new FileWriter( "stationen/" + fvs + ".csv"));
						Writerdaten daten = new Writerdaten();
						daten.letztesSchreiben = time;
						daten.w = w;
						_dateizugreifer.put(fvs, daten);
						
						w.write(time + " " + belegt + "\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				else
				{
					if ( _dateizugreifer.get(fvs ) .letztesSchreiben < time )
					{
						Writer w = _dateizugreifer.get( fvs ).w;
						try {
//							System.out.println("Versuche schreiben: " + fvs);
							w.write(time + " " + belegt + "\n");
							_dateizugreifer.get(fvs).letztesSchreiben = time;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
//						System.out.println("Schreibe nicht: " + fvs + " " + time + " " + _dateizugreifer.get(fvs ) .letztesSchreiben);
					}
				}
			}
			
			for ( Map.Entry<String, Writerdaten> w : _dateizugreifer.entrySet() )
			{
				try {
					w.getValue().w.flush();
//					w.getValue().letztesSchreiben = time;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	protected ITableDataProvider getTableDataProvider()
	{
		String providername = (String)getProperty("dataprovider");
		ITableDataProvider provider = getSpace().getDataProvider(providername);
		if(provider==null)
			throw new RuntimeException("Data provider nulls: "+providername);
		return provider;
	}
	
	public AbstractEnvironmentSpace getSpace()
	{
		return (AbstractEnvironmentSpace)getProperty("envspace");
	}

}
