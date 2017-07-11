import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class ExponentialMech {

	/**
	 * @param args
	 */
	public static boolean isDebug = false;

	private static void debug(String text) {
		if (isDebug) {
			System.out.println("DEBUG: " + text);
		}
	}

	public static double ExponentialMechanism(double[] data, double epsilon,
			double threshold) {
		Arrays.sort(data);
		Percentile p = new Percentile();
		double percentileVal = p.evaluate(data, threshold);
//		System.out.println("True percentileVal is " + percentileVal);

		// true index of percentile value
		for (int i = 0; i < data.length; i++) {
			if (data[i] == percentileVal) {
//				System.out
//						.println("the index of the true percentile value is: "
//								+ i);
				break;
			}
		}

		ArrayList<Double> dataList = new ArrayList<Double>();
		dataList.add(0.0);
		for (double d : data) {
			dataList.add(d);
		}
		// System.out.println("maxmim is: " + dataList.get(data.length) +
		// ", min is: " + dataList.get(1));

		double[] probSum = new double[dataList.size()];
		probSum[0] = 0.0;
		double interval = 0.0;
		int percentileRank = 0;
		for (int i = 1; i < dataList.size(); i++) {
			if (dataList.get(i) > percentileVal) {
				percentileRank = i;
				break;
			}
		}

		double base = Math.exp((-1) * epsilon / 2);

		for (int i = 1; i < dataList.size(); i++) {
			double current = dataList.get(i);
			double previous = dataList.get(i - 1);
			interval = current - previous;
			double prob = Math.pow(base, Math.abs(i - percentileRank));
			probSum[i] = probSum[i - 1] + interval * prob;
			// System.out.println(i + "th: interval * prob is : " + interval *
			// prob);
		}

		double randTemp = Math.random();
		double rand = randTemp * probSum[probSum.length - 1];

		int idx = 0;
		for (int i = 1; i < probSum.length; i++) {
			if (probSum[i] > rand) {
				idx = i;
				break;
			}
		}
//		System.out
//				.println("the exponential mechanism selected index is:" + idx);

		double chosenVal = dataList.get(idx - 1) + Math.random()
				* (dataList.get(idx) - dataList.get(idx - 1));
		return chosenVal;

	}

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		// TODO Auto-generated method stub

		// simple test: this simple test shows that private results are good.
		// close to the true
		// median 5.5. sometimes smaller than 5.5, sometimes larger than 5.5.
		// the biased behavior in the "positivewdata.csv" may due to the
		// skewness of the dataset,
		// not the algorithm itself.
		
		
		 double[] data = new double[]{3.0, 2.0, 3.5, 4.5, 1.0, 1.0};
//		 double[] data1 = new double[]{1.0, 1.0, 2.0, 3.0, 3.5, 4.5};

//		 double[] data = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//		 double[] data = new double[]{1.0, 2.1, 3.3, 4.5, 5.7, 6.8, 7.12,
//		 8.09, 9.75, 10.088};
//		 for (int i = 0; i < 10; i++) {
//		 double results = ExponentialMechanism(data, 1.0, 40);
//		 System.out.println("simple test results--00 is: " + results);
//		 }
		 	 
		 double result_sum = 0;
			for (int i = 0; i < 10; i++) {

				double result = ExponentialMechanism(data, 0.3, 10);
				result_sum += result;

				System.out.println("======simple test result is: " + result);
				System.out.println("  ");
			}

			double avg_result = result_sum / 10;
			System.out.println("  ");
			System.out.println("Avg result for 10 times of experiments is: "
					+ avg_result);
		 
		 
		 
		 System.out.println("======================= ");
		 
//		 for (int i = 0; i < 10; i++) {
//			 double results1 = ExponentialMechanism(data1, 1.0, 40);
//			 System.out.println("simple test results--11 is: " + results1);
//		 }

//		double epsilon = 0.3;
//		double threshold = 20;

		// read datasets
		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/true-positivewdata-test.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = null;
		ArrayList<Double> arrayDouble = new ArrayList<Double>();
		strLine = br.readLine();
		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			double d = Double.parseDouble(strLine);
			arrayDouble.add(d);
		}
		in.close();
		// for(int i = 0; i < arrayDouble.size(); i++)
		// {
		// System.out.println("array is: " + arrayDouble.get(i));
		// }

		Double[] d = arrayDouble.toArray(new Double[arrayDouble.size()]);

		double[] inputData = new double[d.length];

		for (int i = 0; i < d.length; i++) {
			inputData[i] = d[i];
		}

		// double epsilon = 0.3;
		// double threshold = 40;

//		double result_sum = 0;
//		for (int i = 0; i < 10; i++) {
//
//			double result = ExponentialMechanism(inputData, epsilon, threshold);
//			result_sum += result;
//
//			System.out.println("result is: " + result);
//			System.out.println("  ");
//		}
//
//		double avg_result = result_sum / 10;
//		System.out.println("  ");
//		System.out.println("Avg result for 10 times of experiments is: "
//				+ avg_result);

	}

	public static double noisyPercentileValue(String exeID, double threshold, double epsilon)
			throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		// simple test: this simple test shows that private results are good.
		// close to the true
		// median 5.5. sometimes smaller than 5.5, sometimes larger than 5.5.
		// the biased behavior in the "positivewdata.csv" may due to the
		// skewness of the dataset,
		// not the algorithm itself.
		// double[] data = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		// double[] data = new double[]{1.0, 2.1, 3.3, 4.5, 5.7, 6.8, 7.12,
		// 8.09, 9.75, 10.088};
		// for (int i = 0; i < 10; i++) {
		// double results = ExponentialMechanism(data, 1.0, 40);
		// System.out.println("results is: " + results);
		// }

//		double epsilon = 0.3;
		System.out.println("privacy budget is: " + epsilon);

		// read datasets
		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/true-positivewdata-" + exeID + ".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = null;
		ArrayList<Double> arrayDouble = new ArrayList<Double>();
		strLine = br.readLine();
		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			double d = Double.parseDouble(strLine);
			arrayDouble.add(d);
		}
		in.close();
		// for(int i = 0; i < arrayDouble.size(); i++)
		// {
		// System.out.println("array is: " + arrayDouble.get(i));
		// }

		Double[] d = arrayDouble.toArray(new Double[arrayDouble.size()]);

		double[] inputData = new double[d.length];

		for (int i = 0; i < d.length; i++) {
			inputData[i] = d[i];
		}

		// double epsilon = 0.3;
		// double threshold = 40;
		Arrays.sort(inputData);
		Percentile p = new Percentile();
		double percentileVal = p.evaluate(inputData, threshold);
		System.out.println("True percentileVal is " + percentileVal);

		double result_sum = 0;
		for (int i = 0; i < 10; i++) {

			double result = ExponentialMechanism(inputData, epsilon, threshold);
			result_sum += result;

//			System.out.println("Private Result is: " + result);
//			System.out.println("  ");
		}

		double avg_result = result_sum / 10;
		System.out.println("  ");
		System.out.println("Avg result for 10 times of experiments is: "
				+ avg_result);
		return avg_result;
	}

}
