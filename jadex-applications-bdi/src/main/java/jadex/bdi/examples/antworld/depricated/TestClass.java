package jadex.bdi.examples.antworld.depricated;

import java.security.SecureRandom;
import java.util.ArrayList;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SecureRandom rand = new SecureRandom();
		ArrayList randoms = new ArrayList();
		for (int i = 0; i < 7; i++) {
			// System.out.println(rand.nextInt(7));
			double doub = rand.nextDouble();
			System.out.println(doub);
			randoms.add(new Double(doub));
		}

		int a = 15;
		Object value = null;
		// Vector2Double pos = a<10 ? new Vector2Double(1.0) : new
		// Vector2Double(2.0,2.0);

		//	 
		// Integer d = new Integer(0);
		// int res = d.intValue() + 1;
		// d = d + new Integer(1);
		// System.out.println(res);

		// String aa = null;
		// aa.equals(new String("a")) ? System.out.println("JAa") :
		// System.out.println("Nö");

		// $object.getProperty("lastmove")==null ||
		// $object.getProperty("lastmove").equals("right")? null:
		// $object.getProperty("lastmove").equals("left")? new
		// Vector3Double(Math.PI,0,Math.PI):
		// $object.getProperty("lastmove").equals("up")? new
		// Vector3Double(0,0,Math.PI*3/2):
		// new Vector3Double(0,0,Math.PI/2)

		//Initial ordered List
		ArrayList sortedList = new ArrayList();
		sortedList.add(new Double((1.0)));
		sortedList.add(new Double((3.0)));
		sortedList.add(new Double((5.0)));
		sortedList.add(new Double((5.0)));
		sortedList.add(new Double((6.0)));
		sortedList.add(new Double((9.0)));
		sortedList.add(new Double((9.0)));

		// List that contains buckets
		ArrayList buckets = new ArrayList();
		double sum = 0.0;

		for (int i = 0; i < sortedList.size(); i++) {
			if (i == 0) {
				buckets.add(sortedList.get(i));
				sum = sum + new Double((String) sortedList.get(i)
						.toString()).doubleValue();
			} else {
				double maxVal = new Double((String) buckets.get(
						buckets.size() - 1).toString()).doubleValue();
				double currentVal = new Double((String) sortedList.get(i)
						.toString()).doubleValue();
				if (maxVal < currentVal) {
					buckets.add(sortedList.get(i));
					sum = sum + new Double((String) sortedList.get(i)
							.toString()).doubleValue();
				}
			}
		}
		
		System.out.println("Sorted and indentified buckets. Sum is: " + sum);
		for (int j = 0; j < buckets.size(); j++) {
			double bucketVal = new Double((String) buckets.get(j).toString())
					.doubleValue();
			System.out.println("Bucket No " + j + " : " + bucketVal);
		}
		
//		ArrayList buckets = new ArrayList();		
		for (int i = 0; i < buckets.size(); i++) {
			if(i == 0){
				double bucketVal = new Double((String) buckets.get(i).toString())
				.doubleValue() / sum;				
				buckets.set(i, new Double(bucketVal));
			}else{
				double bucketValSum = new Double((String) buckets.get(i-1).toString())
				.doubleValue();
				double bucketVal = new Double((String) buckets.get(i).toString())
				.doubleValue() / sum;				
				buckets.set(i, new Double(bucketVal+bucketValSum));
			}			
		}
		
		System.out.println("Sorted and indentified buckets with probablitiy borders." );
		for (int j = 0; j < buckets.size(); j++) {
			double bucketVal = new Double((String) buckets.get(j).toString())
					.doubleValue();
			System.out.println("Bucket No/Prob " + j + " : " + bucketVal);
		}
		
//		buckets = new ArrayList();
//
//		double sumOld = 0.0;
//		double sumProb = 0.0;
//		double probA = 9.0;
//		double probB = 6.0;
//		double probC = 5.0;
//		double probD = 3.0;
//		double probE = 1.0;
//
//		sumOld = probA + probB + probC + probD + probE;
//		System.out.println("Sum: " + sumOld);
//		System.out.println("DivA: " + probA / sumOld);
//		System.out.println("DivB: " + probB / sumOld);
//		System.out.println("DivC: " + probC / sumOld);
//		System.out.println("DivD: " + probD / sumOld);
//		System.out.println("DivE: " + probE / sumOld);
//
//		buckets.add(new Double((probA / sumOld)));
//		buckets.add(new Double((probA / sumOld) + (probB / sumOld)));
//		buckets.add(new Double((probA / sumOld) + (probB / sumOld) + (probC / sumOld)));
//		buckets.add(new Double((probA / sumOld) + (probB / sumOld) + (probC / sumOld)
//				+ (probD / sumOld)));
//		buckets.add(new Double((probA / sumOld) + (probB / sumOld) + (probC / sumOld)
//				+ (probD / sumOld) + (probE / sumOld)));
//
//		sumProb = (probA / sumOld) + (probB / sumOld) + (probC / sumOld) + (probD / sumOld)
//				+ (probE / sumOld);

		System.out.println("Erg: ");

		for (int j = 0; j < buckets.size(); j++) {
			double bucketVal = new Double((String) buckets.get(j).toString())
					.doubleValue();
			System.out.println("Bucket No " + j + " : " + bucketVal);
		}

		for (int i = 0; i < randoms.size(); i++) {
			System.out.println("Find bucket for " + randoms.get(i).toString());
			double currentRand = new Double((String) randoms.get(i).toString())
					.doubleValue();

			for (int j = 0; j < buckets.size(); j++) {
				double bucketVal = new Double((String) buckets.get(j)
						.toString()).doubleValue();

				if (buckets.size() == 1) {
					System.out.println("Choosen value: " + bucketVal);
					break;
				}
				if (currentRand <= bucketVal || j + 1 == buckets.size()) {
					System.out.println("Choosen value: " + bucketVal);
					break;
				}

			}
		}

		
		System.out.println("Res: "  );
	}

}
