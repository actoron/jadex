package jadex.simulation.analysis.application.opt4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.Bounds;
import org.opt4j.genotype.DoubleBounds;
import org.opt4j.genotype.DoubleMapGenotype;

public class SimulationCreator implements Creator<DoubleMapGenotype<String>>
{

	Random random = new Random();

	List<String> keys = new LinkedList<String>();
	List<Double> lower = new LinkedList<Double>();
	List<Double> upper = new LinkedList<Double>();
	{
		keys.add("xKoordinate");
		lower.add(0.35);
		upper.add(0.75);
		keys.add("yKoordinate");
		lower.add(0.35);
		upper.add(0.75);
	}
	Bounds<Double> bounds = new DoubleBounds(lower, upper);

	@Override
	public DoubleMapGenotype<String> create()
	{
//		System.out.println("create");
		DoubleMapGenotype<String> genotype = new DoubleMapGenotype<String>(keys, bounds);
		genotype.init(random);
		return genotype;
	}

}
