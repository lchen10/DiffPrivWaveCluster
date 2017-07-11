import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import weka.core.Instances;

public class getRangeValue {

	/**
	 * @param args
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static void main(String[] args) throws NumberFormatException,
			IOException {
		// TODO Auto-generated method stub

		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/PrivateWaveCluster1/dataset/Aggregation-enlarge-36.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		ArrayList<Double> arrayDouble1 = new ArrayList<Double>();
		ArrayList<Double> arrayDouble2 = new ArrayList<Double>();
//		 strLine = br.readLine();
		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			String[] split = strLine.split(",");
			double d1 = Double.parseDouble(split[0]);
			double d2 = Double.parseDouble(split[1]);
			arrayDouble1.add(d1);
			arrayDouble2.add(d2);
		}
		in.close();
		// for(int i = 0; i < arrayDouble.size(); i++)
		// {
		// System.out.println("array is: " + arrayDouble.get(i));
		// }

		Double[] sortd1 = arrayDouble1.toArray(new Double[arrayDouble1.size()]);
		Double[] sortd2 = arrayDouble2.toArray(new Double[arrayDouble2.size()]);

		Arrays.sort(sortd1);
		Arrays.sort(sortd2);
		
		
		System.out.println("x-value xmin is: " + sortd1[0]);
		System.out.println("y-value ymin is: " + sortd2[0]);
		
		System.out.println("x-value xmax is: " + sortd1[sortd1.length - 1]);
		System.out.println("y-value ymax is: " + sortd2[sortd2.length - 1]);

	}

}
