package jadex.simulation.analysis.application.opt4j;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.opt4j.core.Individual;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Value;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.viewer.ObjectivesMonitor;
import org.opt4j.viewer.ObjectivesMonitor.ObjectivesListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ConvergencePlotDataSim implements OptimizerIterationListener, ObjectivesListener {

	protected final Map<Objective, PlotDataObjective> map = new HashMap<Objective, PlotDataObjective>();

	protected int iteration = 0;

	private final Archive archive;

	// false until objectives are known and PlotDataObjectives are created
	// accordingly
	private boolean init = false;

	/**
	 * The {@link PlotDataObjective} contains the convergence information of a
	 * single {@link Objective}.
	 * 
	 * @author lukasiewycz
	 * 
	 */
	protected static class PlotDataObjective {
		protected final Objective objective;
		protected final List<Point2D.Double> minValues = new CopyOnWriteArrayList<Point2D.Double>();
		protected final List<Point2D.Double> maxValues = new CopyOnWriteArrayList<Point2D.Double>();
		protected final List<Point2D.Double> meanValues = new CopyOnWriteArrayList<Point2D.Double>();
		protected final int MAXVALUES = 2000;

		final Set<Double> currentIteration = new HashSet<Double>();

		PlotDataObjective(Objective objective) {
			this.objective = objective;
		}

		public void update(Objectives objectives) {
			Value<?> value = objectives.get(objective);
			if (value != null) {
				Object v = value.getValue();

				if (v != null && v instanceof Number) {
					Number n = (Number) v;
					double nextValue = n.doubleValue();
					currentIteration.add(nextValue);
				}
			}
		}

		protected synchronized void simplify(List<java.awt.geom.Point2D.Double> values) {
			if (values.size() > MAXVALUES) {
				List<Point2D.Double> copy = new ArrayList<Point2D.Double>(values);

				final Map<Point2D.Double, Double> dist = new HashMap<Point2D.Double, Double>();
				for (int i = 1; i < copy.size() - 1; i += 2) {
					Point2D.Double p0 = copy.get(i - 1);
					Point2D.Double p1 = copy.get(i);
					Point2D.Double p2 = copy.get(i + 1);

					double slope = (p2.y - p0.y) / (p2.x - p0.x);

					double y = p0.y + slope * (p1.x - p0.x);

					double v = Math.abs(y - p1.y);
					dist.put(p1, v);
				}

				copy.clear();
				copy.addAll(dist.keySet());

				Collections.sort(copy, new Comparator<Point2D.Double>() {
					@Override
					public int compare(Point2D.Double p1, Point2D.Double p2) {
						Double v1 = dist.get(p1);
						Double v2 = dist.get(p2);
						return v1.compareTo(v2);
					}

				});
				values.removeAll(copy.subList(0, copy.size() / 2));
			}
		}

		public void complete(int iteration) {
			if (!currentIteration.isEmpty()) {
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				double avg = 0;
				for (double value : currentIteration) {
					if (min > value) {
						min = value;
					}
					if (max < value) {
						max = value;
					}
					avg += value;
				}
				avg /= currentIteration.size();
				currentIteration.clear();

				addValue(min, minValues, iteration);
				addValue(max, maxValues, iteration);
				addValue(avg, meanValues, iteration);

				if (iteration % 10 == 0) {
					if (minValues.size() > MAXVALUES) {
						simplify(minValues);
					}
					if (maxValues.size() > MAXVALUES) {
						simplify(maxValues);
					}
					if (meanValues.size() > MAXVALUES) {
						simplify(meanValues);
					}
				}
			}
		}

		private void addValue(double min, List<java.awt.geom.Point2D.Double> points, int iteration) {
			if (!Double.isInfinite(min)) {
				if (points.isEmpty() || points.get(points.size() - 1).getY() != min) {
					if (!points.isEmpty()) {
						Point2D.Double steppoint = new Point2D.Double(iteration, points.get(points.size() - 1).getY());
						points.add(steppoint);
					}

					Point2D.Double point = new Point2D.Double(iteration, min);
					points.add(point);
				}
			}
		}
	}

	/**
	 * Constructs a {@link ConvergencePlotData}.
	 * 
	 * @param archive
	 *            the archive
	 */
	@Inject
	public ConvergencePlotDataSim(Archive archive, ObjectivesMonitor objectivesMonitor) {
		this.archive = archive;
		objectivesMonitor.addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opt4j.core.optimizer.OptimizerIterationListener#iterationComplete
	 * (org.opt4j.core.optimizer.Optimizer, int)
	 */
	@Override
	public void iterationComplete(Optimizer optimizer, int iteration) {
		this.iteration = iteration;

		for (PlotDataObjective data : map.values()) {
			for (Individual individual : archive) {
				data.update(individual.getObjectives());
			}
			data.complete(iteration);
		}
	}

	/**
	 * Returns the points for a given objective.
	 * 
	 * @param objective
	 *            the objective
	 * @return the convergence points
	 */
	public List<Point2D.Double> getMinPoints(Objective objective) {
		PlotDataObjective plotDataObjective = map.get(objective);
		assert plotDataObjective != null;
		return plotDataObjective.minValues;
	}

	/**
	 * Returns the points for a given objective.
	 * 
	 * @param objective
	 *            the objective
	 * @return the convergence points
	 */
	public List<Point2D.Double> getMaxPoints(Objective objective) {
		if (!init) {
			return Collections.emptyList();
		}
		PlotDataObjective plotDataObjective = map.get(objective);
		assert plotDataObjective != null;
		return plotDataObjective.maxValues;
	}

	/**
	 * Returns the points for a given objective.
	 * 
	 * @param objective
	 *            the objective
	 * @return the convergence points
	 */
	public List<Point2D.Double> getMeanPoints(Objective objective) {
		if (!init) {
			return Collections.emptyList();
		}
		PlotDataObjective plotDataObjective = map.get(objective);
		assert plotDataObjective != null;
		return plotDataObjective.meanValues;
	}

	/**
	 * Returns the current iteration.
	 * 
	 * @return the current iteration
	 */
	public int getIteration() {
		return iteration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opt4j.viewer.ObjectivesMonitor.ObjectivesListener#objectives(java
	 * .util.Collection)
	 */
	@Override
	public void objectives(Collection<Objective> objectives) {
		for (Objective obj : objectives) {
			map.put(obj, new PlotDataObjective(obj));
		}
		init = true;
	}
}
