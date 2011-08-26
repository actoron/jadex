package jadex.simulation.analysis.application.commonsMath;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.direct.NelderMeadSimplex;

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

	public boolean tryReflect(RealPointValuePair reflected, Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final RealPointValuePair best = getPoint(0);
		final int n = getDimension();
		final RealPointValuePair secondBest = getPoint(n - 1);
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

	public double[] getExpand(RealPointValuePair reflected, Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final RealPointValuePair worst = getPoint(n);
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
		final RealPointValuePair best = getPoint(0);
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

	public void tryExpand(RealPointValuePair reflected, RealPointValuePair expanded, Comparator<RealPointValuePair> comparator)
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
	
	public double[] getOutContracted(RealPointValuePair reflected, Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final RealPointValuePair worst = getPoint(n);
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
	
	public double[] getInContracted(RealPointValuePair reflected, Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] centroid = new double[n];
		final RealPointValuePair worst = getPoint(n);
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

	public boolean tryOutContracted(RealPointValuePair reflected, RealPointValuePair outContracted,  Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		if (comparator.compare(outContracted, reflected) <= 0) {
            // Accept the contraction point.
            replaceWorstPoint(outContracted, comparator);
            return true;
        }
		return false;
	}

	public boolean tryInContracted(RealPointValuePair inContracted, Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final RealPointValuePair worst = getPoint(n);
		if (comparator.compare(inContracted, worst) < 0) {
            // Accept the contraction point.
            replaceWorstPoint(inContracted, comparator);
            return true;
        }
		return false;
	}

	public void shrink(Comparator<RealPointValuePair> comparator)
	{
		sort(comparator);
		final int n = getDimension();
		final double[] xSmallest = getPoint(0).getPointRef();
        for (int i = 1; i <= n; i++) {
            final double[] x = getPoint(i).getPoint();
            for (int j = 0; j < n; j++) {
                x[j] = xSmallest[j] + sigma * (x[j] - xSmallest[j]);
            }
            setPoint(i, new RealPointValuePair(x, Double.NaN, false));
        }
	}

	//
	public double[] iterate(Comparator<RealPointValuePair> comparator) throws MathUserException
	{
		sort(comparator);
		final int n = getDimension();

		// Interesting values.
		final RealPointValuePair worst = getPoint(n);
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
	
	private void sort(Comparator<RealPointValuePair> comparator)
	{
		RealPointValuePair[] simplex = getPoints();
		Arrays.sort(simplex, comparator);
		setPoints(simplex);
	}
	
	public void setSimplex(RealPointValuePair[] points)
	{
		setPoints(points);
	}

}
