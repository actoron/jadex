package jadex.simulation.analysis.application.commonsMath;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.NelderMeadSimplex;

/**
 * Extended NelderSimplex for Simulation
 * @author 5Haubeck
 *
 */
public class NelderMeadSimplexSim extends NelderMeadSimplex
{
	/** Reflection coefficient. */
	private final double rho;
	/** Expansion coefficient. */
	private final double khi;
	/** Contraction coefficient. */
	private final double gamma;
	/** Shrinkage coefficient. */
	private final double sigma;

	public NelderMeadSimplexSim(double[] steps, double rho, double khi, double gamma, double sigma)
	{
		super(steps);
		this.rho = rho;
		this.khi = khi;
		this.gamma = gamma;
		this.sigma = sigma;
	}

	public NelderMeadSimplexSim(final int n, double sideLength,
			final double rho, final double khi,
			final double gamma, final double sigma)
	{
		super(n, sideLength);
		this.rho = rho;
		this.khi = khi;
		this.gamma = gamma;
		this.sigma = sigma;
	}
	
	public NelderMeadSimplexSim(double[][] reference,
			final double rho, final double khi,
			final double gamma, final double sigma)
	{
		super(reference);
		this.rho = rho;
		this.khi = khi;
		this.gamma = gamma;
		this.sigma = sigma;
	}

	public boolean tryReflect(PointValuePair reflected, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final PointValuePair best = getPoint(0);
		final int n = getDimension();
		final PointValuePair secondBest = getPoint(n - 1);
		if (comparator.compare(best, reflected) <= 0 &&
				comparator.compare(reflected, secondBest) < 0)
		{
			// Accept the reflected point.
			replaceWorstPoint(reflected, comparator);
			return true;
		}
		else
		{
			return false;
		}
	}

	public double[] getExpand(PointValuePair reflected, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final PointValuePair worst = getPoint(n);
		final double[] xWorst = worst.getPointRef();
		for (int i = 0; i < n; i++)
		{
			final double[] x = getPoint(i).getPointRef();
			for (int j = 0; j < n; j++)
			{
				centroid[j] += x[j];
			}
		}
		final double[] xR = new double[n];
		for (int j = 0; j < n; j++)
		{
			xR[j] = centroid[j] + rho * (centroid[j] - xWorst[j]);
		}
		final PointValuePair best = getPoint(0);
		if (comparator.compare(reflected, best) < 0)
		{
			// Compute the expansion point.
			final double[] xE = new double[n];
			for (int j = 0; j < n; j++)
			{
				xE[j] = centroid[j] + khi * (xR[j] - centroid[j]);
			}
			return xE;
		}
		return null;
	}

	public void tryExpand(PointValuePair reflected, PointValuePair expanded, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		if (comparator.compare(expanded, reflected) < 0) {
            // Accept the expansion point.
            replaceWorstPoint(expanded, comparator);
        } else {
            // Accept the reflected point.
            replaceWorstPoint(reflected, comparator);
        }
	}
	
	public double[] getOutContracted(PointValuePair reflected, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final PointValuePair worst = getPoint(n);
		final double[] xWorst = worst.getPointRef();
		for (int i = 0; i < n; i++)
		{
			final double[] x = getPoint(i).getPointRef();
			for (int j = 0; j < n; j++)
			{
				centroid[j] += x[j];
			}
		}
		final double[] xR = new double[n];
		for (int j = 0; j < n; j++)
		{
			xR[j] = centroid[j] + rho * (centroid[j] - xWorst[j]);
		}
		if (comparator.compare(reflected, worst) < 0) {
            // Perform an outside contraction.
            final double[] xC = new double[n];
            for (int j = 0; j < n; j++) {
                xC[j] = centroid[j] + gamma * (xR[j] - centroid[j]);
            }
            return xC;
        } 
		return null;
	}
	
	public double[] getInContracted(PointValuePair reflected, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final PointValuePair worst = getPoint(n);
		final double[] xWorst = worst.getPointRef();
		for (int i = 0; i < n; i++)
		{
			final double[] x = getPoint(i).getPointRef();
			for (int j = 0; j < n; j++)
			{
				centroid[j] += x[j];
			}
		}
		final double[] xC = new double[n];
        for (int j = 0; j < n; j++) {
            xC[j] = centroid[j] - gamma * (centroid[j] - xWorst[j]);
        }
		return xC;
	}

	public boolean tryOutContracted(PointValuePair reflected, PointValuePair outContracted,  Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		if (comparator.compare(outContracted, reflected) <= 0) {
            // Accept the contraction point.
            replaceWorstPoint(outContracted, comparator);
            return true;
        }
		return false;
	}

	public boolean tryInContracted(PointValuePair inContracted, Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final PointValuePair worst = getPoint(n);
		if (comparator.compare(inContracted, worst) < 0) {
            // Accept the contraction point.
            replaceWorstPoint(inContracted, comparator);
            return true;
        }
		return false;
	}

	public void shrink(Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] xSmallest = getPoint(0).getPointRef();
        for (int i = 1; i <= n; i++) {
            final double[] x = getPoint(i).getPoint();
            for (int j = 0; j < n; j++) {
                x[j] = xSmallest[j] + sigma * (x[j] - xSmallest[j]);
            }
            setPoint(i, new PointValuePair(x, Double.NaN, false));
        }
	}

	//
	public double[] iterate(Comparator<PointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();

		// Interesting values.
		final PointValuePair worst = getPoint(n);
		final double[] xWorst = worst.getPointRef();

		// Compute the centroid of the best vertices (dismissing the worst
		// point at index n).
		final double[] centroid = new double[n];
		for (int i = 0; i < n; i++)
		{
			final double[] x = getPoint(i).getPointRef();
			for (int j = 0; j < n; j++)
			{
				centroid[j] += x[j];
			}
		}
		final double scaling = 1.0 / n;
		for (int j = 0; j < n; j++)
		{
			centroid[j] *= scaling;
		}

		// compute the reflection point
		final double[] xR = new double[n];
		for (int j = 0; j < n; j++)
		{
			xR[j] = centroid[j] + rho * (centroid[j] - xWorst[j]);
		}
		return xR;
	}
	
	private void sort(Comparator<PointValuePair> comparator)
	{
		PointValuePair[] simplex = getPoints();
		Arrays.sort(simplex, comparator);
		setPoints(simplex);
	}
	
	public void setSimplex(PointValuePair[] points)
	{
		setPoints(points);
	}

}
