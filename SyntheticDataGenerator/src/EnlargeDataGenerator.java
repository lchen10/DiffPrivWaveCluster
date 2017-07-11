import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class EnlargeDataGenerator {

	public static void main(String[] args) throws Exception {

		
		// 5 parameters need to be specified: 
		// range: how many times you want to enlarge the original space to
		// unit: in original space, the smallest possible difference (*) times range, then divided by 2.
		//       This value is the side length of square which centered at each original point. In this square, you 
		//       uniform random generate certain amount of points. You can preserve the original data distribution.
		// nbrOfPoints: in each square, how many points you want to generate.
		//              which is also how many times the size of dataset you want to enlarge
		// delta: in the randomgeneration func, delta is fixed to 1. 
		// inputFile
		// outputFile
		
		
		
		int spaceRange = 10000;
		int unit = 6000; // for spiral dataset, eg. 0.01 * 10000 / 2 = 50
		int nbrOfPoints = 100;
		
		// input file
		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/SyntheticDataGenerator/data_grids/4OriginalFiles/Aggregation-original-wlabel.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// output file
		PrintWriter out = new PrintWriter(
				"E:/D_disk/workspace/SyntheticDataGenerator/synthetic_datasets/aggregation-30k-unit-6000.csv");

		String strLine = null;
		String[] mystring = new String[5];

		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			mystring = strLine.split(",");
			// System.out.println("mystring length is: " + mystring.length);

			if (mystring.length != 3) {
				throw new Exception("data must be in the format of 'x,y,label'");
			}

			double x = Double.parseDouble(mystring[0]);
			double y = Double.parseDouble(mystring[1]);
			int label = Integer.parseInt(mystring[2]);
			
			
//			String temp = mystring[2];
//			String tempLabel = temp.substring(1);
//			int label = Integer.parseInt(tempLabel);
			

			List<Tuple> r = synthesize(x, y, spaceRange, nbrOfPoints, unit);

			for (Tuple t : r) {
				out.println(t.getValue(0) + "," + t.getValue(1) + ",C" + label);
			}

		}
		in.close();
		out.close();
	}

	public static List<Tuple> synthesize(double x, double y, int range,
			int nbrOfPoints, int unit) {
		ArrayList<Tuple> r = new ArrayList<Tuple>();
		double xr = x * range;
		double yr = y * range;

		double xmin = xr - unit;
		double xmax = xr + unit;
		double ymin = yr - unit;
		double ymax = yr + unit;
		double delta = 1;
		for (int i = 0; i < nbrOfPoints; i++) {
			Tuple t = UniformRandom.getRandomTuple(xmin, xmax, ymin, ymax,
					delta);

			r.add(t);
		}

		return r;
	}
}
