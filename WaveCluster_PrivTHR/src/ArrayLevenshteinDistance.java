import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ArrayLevenshteinDistance implements EditDistance {

	private ArrayList<Integer> s1;
	private ArrayList<Integer> s2;

	public ArrayLevenshteinDistance(ArrayList<Integer> s1, ArrayList<Integer> s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	/**
	 * Compute the Levenshtein distance between the two strings <code>s1</code>
	 * and <code>s2</code>. This is defined to be the minimum number of edits
	 * (insertion, deletion, substitution) that is required to transform
	 * <code>s1</code> to <code>s2</code>.
	 * 
	 * Adapted <code>computeEditDistance</code> to use less space, in the order
	 * of O(min(m, n)), instead of O(m*n). Also, the input strings
	 * <code>s1</code> and <code>s2</code> are normalized to all uppercases.
	 * 
	 * @return The edit distance between <code>s1</code> and <code>s2</code>
	 */
	public int getDistance() {

		// check preconditions
		int m = s1.size();
		int n = s2.size();
		if (m == 0) {
			return n; 			// some simple heuristics
		} else if (n == 0) {
			return m; 			// some simple heuristics
		} else if (m > n) {
			ArrayList<Integer> tempArrayList = s1; 	// swap m with n to get O(min(m, n)) space
			s1 = s2;
			s2 = tempArrayList;
			int tempInt = m;
			m = n;
			n = tempInt;
		}

		// normalize case
//		s1 = s1.toUpperCase();
//		s2 = s2.toUpperCase();

		// Instead of a 2d array of space O(m*n) such as int d[][] = new int[m +
		// 1][n + 1], only the previous row and current row need to be stored at
		// any one time in prevD[] and currD[]. This reduces the space
		// complexity to O(min(m, n)).
		int prevD[] = new int[n + 1];
		int currD[] = new int[n + 1];
		int temp[]; // temporary pointer for swapping

		// the distance of any second string to an empty first string
		for (int j = 0; j < n + 1; j++) {
			prevD[j] = j;
		}

		// for each row in the distance matrix
		for (int i = 0; i < m; i++) {

			// the distance of any first string to an empty second string
			currD[0] = i + 1;
//			char ch1 = s1.charAt(i);
			int element1 = s1.get(i);

			// for each column in the distance matrix
			for (int j = 1; j <= n; j++) {

				int element2 = s2.get(j - 1);
				if (element1 == element2) {
					currD[j] = prevD[j - 1];
				} else {
					currD[j] = minOfThreeNumbers(prevD[j] + 1,
							currD[j - 1] + 1, prevD[j - 1] + 1);
				}

			}

			temp = prevD;
			prevD = currD;
			currD = temp;

		}

		// after swapping, the final answer is now in the last column of prevD
		return prevD[prevD.length - 1];

	}

	/**
	 * Computes the similiarity of the two strings <code>s1</code> and
	 * <code>s2</code> by the following formula: sim<sub>edit</sub>(x, y) = 1 /
	 * (1+editDist(x,y).
	 * 
	 * See also: {@link http://webdocs.cs.ualberta.ca/~lindek/papers/sim.pdf}
	 * 
	 * @return
	 */
	public double getDistanceSimilarity() {
		return ((double) 1) / (1 + getDistance());
	}

	/**
	 * Returns the minimum of three numbers
	 * 
	 * @param num1
	 *            The first number
	 * @param num2
	 *            The second number
	 * @param num3
	 *            The third number
	 * @return The minimum of first, second and third numbers
	 */
	private int minOfThreeNumbers(int num1, int num2, int num3) {
		return Math.min(num1, Math.min(num2, num3));
	}
	
	static double[][] ArrayEditDistances(ArrayList<ArrayList<Integer>> pointList1, ArrayList<ArrayList<Integer>> pointList2)
	{
		double[][] results = new double[pointList1.size()][pointList2.size()];
		
		ArrayLevenshteinDistance levendist = null;
		
		int contSetDiff = 0;
		for(int i = 0; i < pointList1.size(); i++)
		{
			
			ArrayList<Integer> t1 = pointList1.get(i);
			
			HashSet<Integer> set1 = new HashSet<Integer>(t1);
			
		
			System.out.println("set1 is: " + set1.toString());
			
//			System.out.println("True Cluster : " + t1);
			for(int j = 0; j < pointList2.size(); j++)
			{
				ArrayList<Integer> t2 = pointList2.get(j);
				
				HashSet<Integer> set2 = new HashSet<Integer>(t2);
							
				System.out.println("set2 is: " + set2.toString());
				
//				System.out.println("Private Cluster: " + t2);
				System.out.println("set1.size is: " + set1.size());
				System.out.println("set2.size is: " + set2.size());
				if(set1.size() >= set2.size())
				{
					HashSet<Integer> set1copy = new HashSet<Integer>(set1);
					HashSet<Integer> set2copy = new HashSet<Integer>(set2);
					
					System.out.println("set1 size is GREATER than set2 size");
					set1copy.removeAll(set2copy);
					contSetDiff = set1copy.size();
					System.out.println("set diff is: " + contSetDiff);
					System.out.println(" ");
				}
				else
				{
					HashSet<Integer> set1copy = new HashSet<Integer>(set1);
					HashSet<Integer> set2copy = new HashSet<Integer>(set2);
					
					System.out.println("set1 size is SMALLER than set2 size");
					set2copy.removeAll(set1copy);
					contSetDiff = set2copy.size();
					System.out.println("set diff is: " + contSetDiff);
					System.out.println(" ");
				}
				
//				levendist = new ArrayLevenshteinDistance(t1, t2);
//				int d = levendist.getDistance();
				double dist = (double) contSetDiff;
				results[i][j] = dist;
//				System.out.println("Edit Distance is: " + dist);
			}
			System.out.println("   ");
		}
		
		
		return results;
	}
	
	
//	static double[][] SetDistance(HashSet<HashSet<Integer>> pointList1, HashSet<HashSet<Integer>> pointList2)
//	{
//		double[][] results = new double[pointList1.size()][pointList2.size()];
//		
//		Set<Integer> setDiff = new HashSet<Integer>();
//		int contSetDiff = 0;
//		int i = -1;
//		int j = -1;
//		for(Iterator<HashSet<Integer>> it1 = pointList1.iterator(); it1.hasNext();)
//		{
//			HashSet<Integer> set1 = it1.next();
//			HashSet<Integer> newset1 = new HashSet<Integer>(set1);
//			i++;		
//			System.out.println("True Cluster : " + newset1);		
//			for(Iterator<HashSet<Integer>> it2 = pointList2.iterator(); it2.hasNext();)
//			{		
// 				HashSet<Integer> set2 = it2.next();
// 				HashSet<Integer> newset2 = new HashSet<Integer>(set2);
//				j++;
//				System.out.println("Private Cluster: " + newset2);
//								
//				if(set1.size() >= set2.size())
//				{
//					newset1.removeAll(newset2);
//					setDiff = newset1;
//					contSetDiff = setDiff.size();
//				}
//				else
//				{
//					newset2.removeAll(newset1);
//					setDiff = newset2;
//					contSetDiff = setDiff.size();
//				}
//				double dist = (double) contSetDiff;
//				results[i][j] = dist;
//				System.out.println("Edit Distance is: " + dist);
//			}
//			System.out.println("   ");
//		}
//		
//		return results;
//	}
//	
	
	
	public static ArrayList<ArrayList<Integer>> clusters(String filename) throws IOException
	{
//		"E:/cluster.txt"
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine = br.readLine();
		String[] mystring = new String[2];
		
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
		int key = 0;
//		String value = null;
		ArrayList<Integer> value = new ArrayList<Integer>();
		
		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			mystring = strLine.split("	");
//			System.out.println("mystring length is: " + mystring.length);
			
			
			double pointID = Double.parseDouble(mystring[0]);
			int ptIDint = (int)pointID;
//			String pointIDstring = Integer.toString(ptIDint);
//			System.out.println("pointID is: " + ptIDint);
			double clusterID = Double.parseDouble(mystring[1]);
			int clusterIDint = (int)clusterID;	
			
//			System.out.println("clusterID is: " + clusterIDint);
			
			
			
			
			key = clusterIDint;
			
			if (map.containsKey(key)) {
				value = map.get(key);
				value.add(ptIDint);
				map.put(key, value);
//				System.out.println("value is: " + value);
			} else {
//				value = pointIDstring;
				ArrayList<Integer> value_1 = new ArrayList<Integer>();
				value_1.add(ptIDint);
				map.put(key, value_1);

			}

		}
		in.close();
		
		
//		Map<String, String> map = getMyMap();
		ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
//		Collections.sort(keys, someComparator);
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		
		for (Integer keyelement: keys) {
			result.add(map.get(keyelement));
//		    System.out.println("key is: " + keyelement + "; value is: " + map.get(keyelement));
		}
		
//		for(int i = 0; i < result.size(); i++)
//		{
////			for(int j = 0; j < result.get(i).size(); j++)
////			{
//				System.out.println("list is: " + result.get(i).toString());
////			}
//		}
		return result;
	
	}
	
	
	
	
	
	public static void main(String [ ] args) throws IOException
	{
		
		String trueFileName = "E:/Clusters/Experiments11-23/result-0/pairs/siggrid-true-0.txt";
		String privateFileName = "E:/Clusters/Experiments11-23/result-0/pairs/siggrid-private-0.txt"; 
		ArrayList<ArrayList<Integer>> trueClusters = clusters(trueFileName);
		ArrayList<ArrayList<Integer>> privateClusters = clusters(privateFileName);
		
		
//		ArrayList<Integer> list11 = new ArrayList<Integer>();
//		list11.add(1);
//		list11.add(2);
//		list11.add(3);
//		ArrayList<Integer> list12 = new ArrayList<Integer>();
//		list12.add(3);
//		list12.add(4);
////		list12.add(6);
//		ArrayList<ArrayList<Integer>> list1 = new ArrayList<ArrayList<Integer>>();
//		list1.add(list11);
//		list1.add(list12);
//		
//		
//	
//		ArrayList<Integer> list21 = new ArrayList<Integer>();
//		list21.add(2);
//		list21.add(3);
//		list21.add(4);
//		ArrayList<Integer> list22 = new ArrayList<Integer>();
//		list22.add(4);
//		list22.add(3);
////		list22.add(33);
//		ArrayList<ArrayList<Integer>> list2 = new ArrayList<ArrayList<Integer>>();
//		list2.add(list21);
//		list2.add(list22);
//		
		
		for(int x = 0; x < trueClusters.size(); x++)
		{
			System.out.println("cluster #" + x + ": ");
			System.out.println(trueClusters.get(x).toString());
		}

		System.out.println("=====================");
		for(int y = 0; y < privateClusters.size(); y++)
		{
			System.out.println("cluster #" + y + ": ");
			System.out.println(privateClusters.get(y).toString());
		}
		double dist[][] = ArrayEditDistances(trueClusters, privateClusters);
		
		for(int i = 0; i < dist.length; i++)
		{
			for(int j = 0; j < dist[i].length; j++)
			{
				System.out.println("dist[" + i + "][" + j + "] is: "  + dist[i][j]);
			}
		}

		
//		ArrayList<ArrayList<Integer>> res = clusters("E:/cluster.txt");
		
		
		
	}

}








