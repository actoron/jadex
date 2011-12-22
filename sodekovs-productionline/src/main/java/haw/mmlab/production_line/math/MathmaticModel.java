/**
 * 
 */
package haw.mmlab.production_line.math;

import java.math.BigInteger;

/**
 * @author thomas
 * 
 */
public class MathmaticModel {

	private static final long R = 70;

	private static final long C = R;

	private static final long N = R * 3;

	// private static final double DELTA = 0.35;

	// private static final double KAPPA = 2.2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// double workload = 0.1;
		// double redRate = 1.0;
		// long k = Math.round((redRate * C) - 1);
		// long t = Math.round(RHO * ((redRate * C) - 1));
		//
		// distributionFunction(300, redRate, workload, KAPPA, C, k, t);

		System.out.println("redRate\tworkload\texpectationValue\tdistributionFunction\tvariance\tstandardDeviation\ttwoSigma\tlower\tupper");

		for (int r = 1; r <= 10; r++) {
			double redRate = r / 10.0;
			long k = Math.round((redRate * C) - 1);
			long t = Math.round(delta(redRate) * ((redRate * C) - 1));

			for (int w = 1; w <= 10; w++) {
				double workload = w / 10.0;
				double expValue = expectationValue(N, redRate, workload, kappa(workload), C, k, t);
				double df = distributionFunction(N, redRate, workload, kappa(workload), C, k, t);
				double variance = variance(N, redRate, workload, kappa(workload), C, k, t);
				double sd = standardDeviation(variance);
				double twoSig = twoSigma(sd);
				double lower = expValue - twoSig;
				double upper = expValue + twoSig;

				System.out.println(redRate + "\t" + workload + "\t" + expValue + "\t" + df + "\t" + variance + "\t" + sd + "\t" + twoSig + "\t" + lower + "\t" + upper);
			}
		}

	}

	private static double twoSigma(double sd) {
		return 2 * sd;
	}

	private static double standardDeviation(double variance) {
		return Math.sqrt(variance);
	}

	private static double delta(double redRate) {
		return 1 / (redRate * C);
	}

	private static double kappa(double workload) {
		return workload * 10;
	}

	private static double variance(long n, double redRate, double workload, double kappa, long c, long k, long t) {
		double result = 0.0;
		for (int i = 1; i <= n; i++) {
			result += Math.pow(i - expectationValue(i, redRate, workload, kappa, c, k, t), 2) * p(i, redRate, workload, kappa, c, k, t);
		}

		return result;
	}

	private static double expectationValue(long n, double redRate, double workload, double kappa, long c, long k, long t) {
		double result = 0.0;
		for (int i = 1; i <= n; i++) {
			result += i * p(i, redRate, workload, kappa, c, k, t);
		}

		return result;
	}

	private static double distributionFunction(long n, double redRate, double workload, double kappa, long c, long k, long t) {
		double result = 0.0;
		for (int i = 1; i <= n; i++) {
			double p = p(i, redRate, workload, kappa, c, k, t);
			result += p;

			// System.out.println(i + "\t" + p + "\t" + result);
		}

		return result;
	}

	private static double p(long n, double redRate, double workload, double kappa, long c, long k, long t) {
		if (n < R) {
			return p1(redRate, kappa, c, k, t) * Math.pow(1 - p1(redRate, kappa, c, k, t), n - 1);
		} else if (n > R && n < (2 * R)) {
			return Math.pow(1 - p1(redRate, kappa, c, k, t), R - 1) * p2(redRate, workload) * Math.pow(1 - p2(redRate, workload), n - R - 1);
		} else if (n > (2 * R) && n < (3 * R)) {
			return Math.pow(1 - p1(redRate, kappa, c, k, t), R - 1) * Math.pow(1 - p2(redRate, workload), R - 1) * p3(redRate) * Math.pow(1 - p3(redRate), n - (2 * R) - 1);
		} else if (n == (3 * R)) {
			return Math.pow(1 - p1(redRate, kappa, c, k, t), R - 1) * Math.pow(1 - p2(redRate, workload), R - 1) * Math.pow(1 - p3(redRate), R - 1);
		}

		return 0.0;
	}

	private static double p3(double redRate) {
		return redRate;
	}

	private static double p2(double redRate, double workload) {
		return redRate * (1 - workload);
	}

	private static double p1(double redRate, double kappa, long c, long k, long t) {
		BigInteger fractionTop = binomialCoefficient(BigInteger.valueOf(c), BigInteger.valueOf(t)).subtract(binomialCoefficient(BigInteger.valueOf(c - k), BigInteger.valueOf(c - k - t)));
		BigInteger fractionDown = binomialCoefficient(BigInteger.valueOf(c), BigInteger.valueOf(t));
		double fraction = fractionTop.doubleValue() / fractionDown.doubleValue();

		return redRate * fraction;
	}

	private static BigInteger binomialCoefficient(BigInteger n, BigInteger k) {

		BigInteger n_minus_k = n.subtract(k);
		if (n_minus_k.compareTo(k) < 0) {
			BigInteger temp = k;
			k = n_minus_k;
			n_minus_k = temp;
		}

		BigInteger numerator = BigInteger.ONE;
		BigInteger denominator = BigInteger.ONE;

		for (BigInteger j = BigInteger.ONE; j.compareTo(k) <= 0; j = j.add(BigInteger.ONE)) {
			numerator = numerator.multiply(j.add(n_minus_k));
			denominator = denominator.multiply(j);
			BigInteger gcd = numerator.gcd(denominator);
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		return numerator;
	}
}