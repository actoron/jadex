package jadex.simulation.analysis.buildingBlocks.optimisation.opt4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.DoubleBounds;
import org.opt4j.genotype.DoubleMapGenotype;
import org.opt4j.genotype.IntegerBounds;
import org.opt4j.genotype.IntegerMapGenotype;

public class SimulationCreator implements Creator<IntegerMapGenotype<String>> {

	Random random = new Random();
	
	List<String> keys = new LinkedList<String>();
	List<Integer> lower = new LinkedList<Integer>();
	List<Integer> upper = new LinkedList<Integer>();
	{
//		keys.add("population");
//		lower.add(0.0);
//		upper.add(200.0);
		keys.add("diffusion-rate");
		lower.add(0);
		upper.add(99);
		keys.add("evaporation-rate");
		lower.add(0);
		upper.add(99);//TODO: Hack to better neighbor mutation
		
	}
	IntegerBounds bounds = new IntegerBounds(lower, upper);
	
	@Override
	public IntegerMapGenotype<String> create() {
		
		IntegerMapGenotype<String> genotype = new IntegerMapGenotype<String>(keys, bounds);
		genotype.init(random);
		return genotype;
	}

}
