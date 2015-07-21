package jadex.json.data;

import java.util.Arrays;
import java.util.List;

public class Transporter
{
//	protected Car[] cars;
//
//	/**
//	 *  Get the cars. 
//	 *  @return The cars
//	 */
//	public Car[] getCars()
//	{
//		return cars;
//	}
//
//	/**
//	 *  Set the cars.
//	 *  @param cars The cars to set
//	 */
//	public void setCars(Car[] cars)
//	{
//		this.cars = cars;
//	}
//	
//	public String toString()
//	{
//		return "Transporter [cars=" + Arrays.toString(cars) + "]";
//	}
	
	protected List<Car> cars;

	public List<Car> getCars()
	{
		return cars;
	}

	public void setCars(List<Car> cars)
	{
		this.cars = cars;
	}

	public String toString()
	{
		return "Transporter [cars=" + cars + "]";
	}
}
