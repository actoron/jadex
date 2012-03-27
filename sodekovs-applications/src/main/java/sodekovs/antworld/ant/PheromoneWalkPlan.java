package sodekovs.antworld.ant;

import jadex.bdi.planlib.PlanFinishedTaskCondition;
import jadex.bdi.runtime.Plan;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sodekovs.antworld.env.PheromoneDistanceComparator;
import sodekovs.antworld.env.PheromoneStrengthComparator;
import sodekovs.antworld.movement.MoveTask;

/**
 * Walk through space according to felt pheromones. Next step is computed stochastically. TODO: Implement: Ignore pheromones and walk randomly on grid if strength of pheromones is too low?
 */
public class PheromoneWalkPlan extends Plan {

	/**
	 * Called when the ant is walking according to recognized pheromones. Walk is stochastically computed.
	 */
	public void body() {
//		System.out.println("Called Pheromone Walk Plan!!!!!!!!!");

		ISpaceObject myself = (ISpaceObject) getBeliefbase().getBelief("myself").getFact();
		ISpaceObject[] pheromones = (ISpaceObject[]) getBeliefbase().getBeliefSet("pheromones").getFacts();
		IEnvironmentSpace env = (IEnvironmentSpace) getBeliefbase().getBelief("environment").getFact();

		// Walking strategy consists of two steps in order to avoid ozilliation, i.e. ant walks always between two points
		// 1.) Go to the furthest pheromone
		// 2.) go to the strongest pheromone
//		while (pheromones.length > 0) {
			List<ISpaceObject> pheromoneList = (List<ISpaceObject>) Arrays.asList(pheromones);
//			System.out.println("#PheromoneWalkPlan# Size of PheromoneList: " + pheromoneList.size());

			// 1.) Furthest pheromone
			Vector2Double myPos = (Vector2Double) myself.getProperty("position");
			for (ISpaceObject pheromone : pheromoneList) {
				pheromone.setProperty("distance", myPos.getDistance((IVector2) pheromone.getProperty("position")));
			}
			Collections.sort(pheromoneList, new PheromoneDistanceComparator());
//			System.out.println("#PheromoneWalkPlan# Pheromones sorted by distance. Size: " + pheromoneList.size() + " - myPos: " + myPos);
//			for (ISpaceObject pheromone : pheromoneList) {
//				System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("distance"));
//			}
//			System.out.println("Furthest Pheromone: " + (IVector2) pheromoneList.get(pheromoneList.size() - 1).getProperty("position") + " - "
//					+ (IVector1) pheromoneList.get(pheromoneList.size() - 1).getProperty("distance"));
//			createDestinationSign((IVector2) pheromoneList.get(pheromoneList.size() - 1).getProperty("position"), env);
			moveToDestination((IVector2) pheromoneList.get(pheromoneList.size() - 1).getProperty("position"), env, myself);

			// 2.) Strongest pheromone
//			Collections.sort(pheromoneList, new PheromoneStrengthComparator());
////			System.out.println("#PheromoneWalkPlan# Pheromones sorted by strength. Size: " + pheromoneList.size());
////			for (ISpaceObject pheromone : pheromoneList) {
////				System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("strength"));
////			}
//			moveToDestination((IVector2) pheromoneList.get(0).getProperty("position"), env, myself);
//
//			pheromones = (ISpaceObject[]) getBeliefbase().getBeliefSet("pheromones").getFacts();
//		}

		// Smelled pheromones

		// for(ISpaceObject pheromone : pheromones){
		// System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("strength"));
		// }

		// ascending ordered.
		// Collections.sort(pheromoneList, new PheromoneStrengthComparator());
		// System.out.println("#PheromoneWalkPlan# Pheromones sorted by strength. Size: " + pheromoneList.size());
		// for (ISpaceObject pheromone : pheromoneList) {
		// System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("strength"));
		// }
		//
		// // Collections.sort(pheromones, new PheromoneDistanceComparator());
		// Vector2Double myPos = (Vector2Double) myself.getProperty("position");
		// for (ISpaceObject pheromone : pheromoneList) {
		// pheromone.setProperty("distance", myPos.getDistance((IVector2) pheromone.getProperty("position")));
		// // System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("strength"));
		// }
		// Collections.sort(pheromoneList, new PheromoneDistanceComparator());
		// System.out.println("#PheromoneWalkPlan# Pheromones sorted by distance. Size: " + pheromoneList.size() + " - myPos: " + myPos);
		// for (ISpaceObject pheromone : pheromoneList) {
		// System.out.println("Pheromone: " + pheromone.getProperty("position") + " - " + pheromone.getProperty("distance"));
		// }

		//
		// // drop other goals
		// IGoal[] goals = getGoalbase().getGoals();
		// // System.out.println("#PheromoneWalkPlan#GoalBase before drop...");
		// // for (int i = 0; i < goals.length; i++) {
		// // System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());
		// // }
		// // System.out.println("***\n");
		//
		// goals = getGoalbase().getGoals("check");
		// for (int i = 0; i < goals.length; i++) {
		// goals[i].drop();
		// }
		//
		// // goals = getGoalbase().getGoals();
		// // System.out.println("#GoalBase after drop...");
		// // for (int i = 0; i < goals.length; i++) {
		// // System.out.println(goals[i].getType() + " , " + goals[i].getLifecycleState() + ", " + goals[i].toString());
		// // }
		// while (getBeliefbase().getBeliefSet("pheromones").getFacts().length > 0) {
		// // System.out.println("#PheromoneWalkPlan# Executing while loop.");
		// Collection pheromonesCol = grid.getNearObjects((IVector2) myself.getProperty(Space2D.PROPERTY_POSITION), new Vector1Int(5), "pheromone");
		// // Select strongest pheromone!
		// ISpaceObject selectedPheromone = selectStrongestPheromone(Arrays.asList(pheromonesCol.toArray()), myself);
		// if (selectedPheromone == null) {
		// // System.out.println("#PheromoneWalkPlan# Break from While loop.");
		// break;
		// } else {
		// IGoal walkRandomly = createGoal("go");
		// walkRandomly.getParameter("pos").setValue(selectedPheromone.getProperty(Space2D.PROPERTY_POSITION));
		// dispatchSubgoalAndWait(walkRandomly);
		// }
		// }
		//
		// // System.out.println("#PheromoneWalkPlan# No pheromones in distance. Walking randomly on grid");
		// // Walk randomly on the grid.
		// IGoal randomWalk = createGoal("check");
		// dispatchTopLevelGoal(randomWalk);

	}

	// /**
	// * Computes the strongest pheromone which determines the next destination of the ant.
	// *
	// * @param pheromonesCol
	// * the collection of recognized pheromones in neighbourhood.
	// * @return
	// */
	// private ISpaceObject selectStrongestPheromone(List initialPheromonesList, ISpaceObject myself) {
	//
	// System.out.println("#PheromoneWalkPlan# Before ordering list");
	// for (int i = 0; i < initialPheromonesList.size(); i++) {
	// System.out.println("#PheromoneWalkPlan#" + i + ": " + ((ISpaceObject) initialPheromonesList.get(i)).toString());
	// }
	//
	// // ascending ordered.
	// Collections.sort(initialPheromonesList, new PheromoneComparator());
	// Map strongestPheromonesAsBuckets = new HashMap();
	//
	// System.out.println("#PheromoneWalkPlan# Ordered list and size: " + initialPheromonesList.size());
	// for (int i = 0; i < initialPheromonesList.size(); i++) {
	// System.out.println("#PheromoneWalkPlan#" + i + ": " + ((ISpaceObject) initialPheromonesList.get(i)).toString());
	// }
	// // Returns a list of the pheromones as buckets.
	// // strongestPheromonesAsBuckets = getBuckets(initialPheromonesList);
	// strongestPheromonesAsBuckets = getBuckets(initialPheromonesList);
	//
	// return getStrongestPheromoneRandomly(strongestPheromonesAsBuckets);
	//
	// }
	//
	// /**
	// * This method takes a list of (ascending) sorted items. Those items are taken to get buckets, e.g. every element in a bucket has the
	// * same value. All items with the same strength value are put into the same bucket.
	// * right now.
	// *
	// * @param sortedList
	// * @return
	// */
	// private Map getBuckets(List sortedList) {
	// // List that contains buckets
	// // ArrayList buckets = new ArrayList();
	// // Key: the strength, value: arraylist, that contains all pheromones with that strength.
	// Map buckets = new HashMap();
	// double sum = 0;
	// int maxStrengthVal = 0;
	// for (int i = 0; i < sortedList.size(); i++) {
	// if (i == 0) {
	// ArrayList list = new ArrayList();
	// list.add(sortedList.get(i));
	// Integer strength = new Integer(((ISpaceObject) sortedList.get(i)).getProperty("strength").toString());
	// buckets.put(strength, list);
	// sum = strength.intValue();
	// maxStrengthVal = strength.intValue();
	// } else {
	// // int maxVal = new Integer(((ISpaceObject) buckets.get(buckets.size() - 1)).getProperty("strength").toString()).intValue();
	// int currentVal = new Integer(((ISpaceObject) sortedList.get(i)).getProperty("strength").toString()).intValue();
	// ArrayList list;
	//
	// // open new bucket
	// if (maxStrengthVal < currentVal) {
	// list = new ArrayList();
	// sum = sum + currentVal;
	// maxStrengthVal = currentVal;
	// } else {
	// list = (ArrayList) buckets.get(new Integer(currentVal));
	// }
	// list.add(sortedList.get(i));
	// buckets.put(new Integer(currentVal), list);
	// }
	// }
	//
	// System.out.println("Sorted and indentified buckets. Sum is: " + sum);
	//
	// for (Iterator it = buckets.keySet().iterator(); it.hasNext();) {
	// Integer bucketVal = (Integer) it.next();
	// ArrayList pheromoneList = (ArrayList) buckets.get(bucketVal);
	//
	// System.out.println("Bucket with strength: " + bucketVal);
	// for (int j = 0; j < pheromoneList.size(); j++) {
	// System.out.println((ISpaceObject) pheromoneList.get(j));
	// }
	// }
	// return getProbabiltiyBorders(buckets, sum);
	// }
	//
	// // private ArrayList getBuckets(List sortedList) {
	// // // List that contains buckets
	// // ArrayList buckets = new ArrayList();
	// // double sum = 0;
	// //
	// // for (int i = 0; i < sortedList.size(); i++) {
	// // if (i == 0) {
	// // buckets.add(sortedList.get(i));
	// // sum = new Integer(((ISpaceObject) sortedList.get(i)).getProperty("strength").toString()).intValue();
	// // } else {
	// // int maxVal = new Integer(((ISpaceObject) buckets.get(buckets.size() - 1)).getProperty("strength").toString()).intValue();
	// // int currentVal = new Integer(((ISpaceObject) sortedList.get(i)).getProperty("strength").toString()).intValue();
	// //
	// // if (maxVal < currentVal) {
	// // buckets.add(sortedList.get(i));
	// // sum = sum + new Integer(((ISpaceObject) sortedList.get(i)).getProperty("strength").toString()).intValue();
	// // }
	// // }
	// // }
	// //
	// // System.out.println("Sorted and indentified buckets. Sum is: " + sum);
	// // for (int j = 0; j < buckets.size(); j++) {
	// // int bucketVal = new Integer(((ISpaceObject) buckets.get(j)).getProperty("strength").toString()).intValue();
	// // System.out.println("Bucket No " + j + " : " + bucketVal);
	// // }
	// //
	// // return getProbabiltiyBorders(buckets, sum);
	// // }
	//
	// /**
	// * Takes map (which represent the buckets). Computes the probability borders between the buckets.
	// *
	// * @param buckets
	// * @param sum
	// * @return
	// */
	// private Map getProbabiltiyBorders(Map buckets, double sum) {
	// double bucketValSum = 0;
	// for (Iterator it = buckets.keySet().iterator(); it.hasNext();) {
	// Integer pheromoneStrength = (Integer) it.next();
	// // for (int i = 0; i < buckets.size(); i++) {
	// //add bucket border only for the first pheromone of a bucket.
	// ISpaceObject pheromone = (ISpaceObject) ((ArrayList) buckets.get(pheromoneStrength)).get(0);
	// double bucketVal = new Double(pheromoneStrength.toString()).doubleValue() / sum;
	// // if (i == 0) {
	//
	// // ISpaceObject pheromone = (ISpaceObject) ((ArrayList) buckets.get(new Integer(i))).get(0);
	// // double bucketVal = new Double(pheromone.getProperty("strength").toString()).doubleValue() / sum;
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(i)).getProperty("strength").toString()).doubleValue() / sum;
	// pheromone.setProperty("tmpBucketBorder", new Double(bucketVal + bucketValSum));
	// // ((ISpaceObject) buckets.get(i)).setProperty("tmpBucketBorder", new Double(bucketVal));
	// // ((ISpaceObject) buckets.get(i)).setProperty("strength", new
	// // Double(bucketVal));
	// // buckets.set(i, new Double(bucketVal));
	// bucketValSum = bucketVal + bucketValSum;
	// // } else {
	// // double bucketValSum = new Double(((ISpaceObject) buckets.get(i - 1)).getProperty("tmpBucketBorder").toString()).doubleValue();
	//
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(i)).getProperty("strength").toString()).doubleValue() / sum;
	// // ((ISpaceObject) buckets.get(i)).setProperty("strength", new
	// // Double(bucketVal + bucketValSum));
	// // ((ISpaceObject) buckets.get(i)).setProperty("tmpBucketBorder", new Double(bucketVal + bucketValSum));
	// //
	// // }
	// }
	//
	// System.out.println("Sorted and indentified buckets with probablitiy borders.");
	// int tmpCounter = 0;
	// for (Iterator it = buckets.keySet().iterator(); it.hasNext();) {
	// Integer bucketVal = (Integer) it.next();
	// ArrayList pheromoneList = (ArrayList) buckets.get(bucketVal);
	//
	// double tmpBucketVal = new Double(((ISpaceObject) pheromoneList.get(0)).getProperty("tmpBucketBorder").toString()).doubleValue();
	// System.out.println("Bucket No/Prob " + tmpCounter + " : " + tmpBucketVal);
	// tmpCounter = tmpCounter + 1;
	//
	// // for (int j = 0; j < pheromoneList.size(); j++) {
	// // System.out.println((ISpaceObject) pheromoneList.get(j));
	// // }
	// }
	//
	// // for (int j = 0; j < buckets.size(); j++) {
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(j)).getProperty("tmpBucketBorder").toString()).doubleValue();
	// // System.out.println("Bucket No/Prob " + j + " : " + bucketVal);
	// // }
	// return buckets;
	// }
	//
	//
	// // private ArrayList getProbabiltiyBorders(ArrayList buckets, double sum) {
	// // for (int i = 0; i < buckets.size(); i++) {
	// // if (i == 0) {
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(i)).getProperty("strength").toString()).doubleValue() / sum;
	// // ((ISpaceObject) buckets.get(i)).setProperty("tmpBucketBorder", new Double(bucketVal));
	// // // ((ISpaceObject) buckets.get(i)).setProperty("strength", new
	// // // Double(bucketVal));
	// // // buckets.set(i, new Double(bucketVal));
	// //
	// // } else {
	// // double bucketValSum = new Double(((ISpaceObject) buckets.get(i - 1)).getProperty("tmpBucketBorder").toString()).doubleValue();
	// //
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(i)).getProperty("strength").toString()).doubleValue() / sum;
	// // // ((ISpaceObject) buckets.get(i)).setProperty("strength", new
	// // // Double(bucketVal + bucketValSum));
	// // ((ISpaceObject) buckets.get(i)).setProperty("tmpBucketBorder", new Double(bucketVal + bucketValSum));
	// //
	// // }
	// // }
	// //
	// // System.out.println("Sorted and indentified buckets with probablitiy borders.");
	// // for (int j = 0; j < buckets.size(); j++) {
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(j)).getProperty("tmpBucketBorder").toString()).doubleValue();
	// // System.out.println("Bucket No/Prob " + j + " : " + bucketVal);
	// // }
	// // return buckets;
	// // }
	//
	// /**
	// * Takes a buckets with pheromones. The border determines which pheromone is taken.
	// */
	// private ISpaceObject getStrongestPheromoneRandomly(Map buckets) {
	//
	// double currentRand = getRandomNumber();
	// int tmpCounter = 0;
	// System.out.println("Random number that determines selected phermone: " + currentRand);
	//
	// for (Iterator it = buckets.keySet().iterator(); it.hasNext();) {
	// Integer strength = (Integer) it.next();
	// ArrayList pheromoneList = (ArrayList) buckets.get(strength);
	// double bucketBorder = new Double(((ISpaceObject) pheromoneList.get(0)).getProperty("tmpBucketBorder").toString()).doubleValue();
	//
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(j)).getProperty("tmpBucketBorder").toString()).doubleValue();
	//
	// if (buckets.size() == 1) {
	// int randomPheromone = getRandomNumber(pheromoneList.size());
	// System.out.println("Choosen value: " + strength + "; Pheromone: " + (ISpaceObject) pheromoneList.get(randomPheromone));
	// return (ISpaceObject) pheromoneList.get(randomPheromone);
	// }
	//
	// if (currentRand <= bucketBorder || tmpCounter + 1 == buckets.size()) {
	// int randomPheromone = getRandomNumber(pheromoneList.size());
	// System.out.println("Choosen value: " + strength + "; Pheromone: " + (ISpaceObject) pheromoneList.get(randomPheromone));
	// return (ISpaceObject) pheromoneList.get(randomPheromone);
	// }
	// tmpCounter = tmpCounter + 1;
	// }
	//
	// return null;
	// }
	//
	// // private ISpaceObject getStrongestPheromoneRandomly(ArrayList buckets) {
	// //
	// // double currentRand = getRandomNumber();
	// // System.out.println("Random number that determines selected phermone: " + currentRand);
	// //
	// // for (int j = 0; j < buckets.size(); j++) {
	// // double bucketVal = new Double(((ISpaceObject) buckets.get(j)).getProperty("tmpBucketBorder").toString()).doubleValue();
	// //
	// // if (buckets.size() == 1) {
	// // System.out.println("Choosen value: " + bucketVal);
	// // return (ISpaceObject) buckets.get(j);
	// //
	// // }
	// // if (currentRand <= bucketVal || j + 1 == buckets.size()) {
	// // System.out.println("Choosen value: " + bucketVal);
	// // return (ISpaceObject) buckets.get(j);
	// // }
	// // }
	// // return null;
	// // }
	//
	//
	//
	// /**
	// * Compute a random number.
	// */
	// private double getRandomNumber() {
	// try {
	// return SecureRandom.getInstance("SHA1PRNG").nextDouble();
	// } catch (NoSuchAlgorithmException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return 0.0;
	// }
	// }
	//
	// /**
	// * Compute a random number. Returns a integer between 0 and exclusiveBorder-1.
	// */
	// private int getRandomNumber(int exclusiveBorder) {
	// try {
	// return SecureRandom.getInstance("SHA1PRNG").nextInt(exclusiveBorder);
	// } catch (NoSuchAlgorithmException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return 0;
	// }
	// }

	/**
	 * Move to a destination task.
	 * 
	 * @param dest
	 * @param env
	 * @param myself
	 */
	private void moveToDestination(IVector2 dest, IEnvironmentSpace env, ISpaceObject myself) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.ACTOR_ID, myself.getId());
		props.put(MoveTask.PROPERTY_SCOPE, getScope().getExternalAccess());
		props.put(MoveTask.PHEROMONE_GRADIENT_WALK, true);
		props.put(AbstractTask.PROPERTY_CONDITION, new PlanFinishedTaskCondition(getPlanElement()));		
		Object taskid = env.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		SyncResultListener res = new SyncResultListener();
		env.addTaskListener(taskid, myself.getId(), res);
		res.waitForResult();
	}

	private void createDestinationSign(IVector2 pos, IEnvironmentSpace space) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(Space2D.PROPERTY_POSITION, pos);
		space.createSpaceObject("destination", props, null);
	}
}
