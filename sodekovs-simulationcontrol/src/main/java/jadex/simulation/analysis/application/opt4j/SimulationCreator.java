package jadex.simulation.analysis.application.opt4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.IntegerBounds;
import org.opt4j.genotype.IntegerMapGenotype;

public class SimulationCreator implements Creator<IntegerMapGenotype<String>>
{

	Random random = new Random();

	List<String> keys = new LinkedList<String>();
	List<Integer> lower = new LinkedList<Integer>();
	List<Integer> upper = new LinkedList<Integer>();
	{
		keys.add("Wert1");
		lower.add(0);
		upper.add(10000);
		keys.add("Wert2");
		lower.add(0);
		upper.add(10000);
		keys.add("Wert3");
		lower.add(0);
		upper.add(10000);
	}
	IntegerBounds bounds = new IntegerBounds(lower, upper);

	@Override
	public IntegerMapGenotype<String> create()
	{

		IntegerMapGenotype<String> genotype = new IntegerMapGenotype<String>(keys, bounds);
		genotype.init(random);
		return genotype;
	}

}
