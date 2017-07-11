import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DeritativeEnlargeDataGenerator {

	public static void main(String[] args) throws Exception {

		// 5 parameters need to be specified:
		// ******range: how many times you want to enlarge the original space to
		// ******unit: in original space, the smallest possible difference (*) times
		// range, then divided by 2.
		// This value is the side length of square which centered at each
		// original point. In this square, you
		// uniform random generate certain amount of points. You can preserve
		// the original data distribution.
		// ******nbrOfPoints: in each square, how many points you want to generate.
		// which is also how many times the size of dataset you want to enlarge
		// ******delta: in the randomgeneration func, delta is fixed to 1.
		// ******inputFile
		// ******outputFile

		// for spiral data
//		int spaceRange = 30000; // based on original range, how many times you want to enlarge
//		double unit = 0.2; // for spiral dataset, the possible minimum data distance between two points, is 0.1, if use 0.01, its synthetic data will result in bad results in hungarian mapping & xor
//		                   // observe the orginal dataset, it is true that minimum change is about 0.3 (0.1 is not enough.) I.e., spaceRange * unit = 2000/3000
//		int nbrOfPoints = 100;
		
//		// for 15-clusters data
		int spaceRange = 1; // based on original range, how many times you want to enlarge
		double unit = 2000; // for 15-clusters dataset, the possible minimum data distance between two points (observed from data)
		int nbrOfPoints = 10;
		
//		// for 5-clusters data
//		int spaceRange = 30000; // based on original range, how many times you want to enlarge
//		double unit = 0.02; // for 5-clusters dataset, the possible minimum data distance between two points is 0.01
//	    int nbrOfPoints = 40;
		
		// for 2-halves data
//		int spaceRange = 22500; // based on original range, how many times you want to enlarge
//		double unit = 0.2; // for 5-clusters dataset, the possible minimum data distance between two points is 0.01
//	    int nbrOfPoints = 80;
		
		
		//这个文件是enlarge 最原始的3 个dataset 到30k的size
		//对于获得 budget = 0.1到2.0之间的private dataset, 需要用这些放大到30k的true file, run adaptive grid, 生成siggrid, 
		//然后synthesize data points in each grid.
		
		// input file
		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/SyntheticDataGenerator/data_grids/4OriginalFiles/15-clusters-original-wlabel.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		// output file
		PrintWriter out = new PrintWriter(
				"E:/D_disk/workspace/SyntheticDataGenerator/synthetic_datasets/deritative-15-clusters-50k-unit-0.01.csv");

		String strLine = null;
		String[] mystring = new String[3];

		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			mystring = strLine.split(",");
			// System.out.println("mystring length is: " + mystring.length);

			if (mystring.length != 3) {
				throw new Exception("data must be in the format of 'x,y,label'");
			}

			double x = Double.parseDouble(mystring[0]);
			double y = Double.parseDouble(mystring[1]);
//			int label = Integer.parseInt(mystring[2]);

			String temp = mystring[2];
			String tempLabel = temp.substring(1);
			int label = Integer.parseInt(tempLabel);

			List<Tuple> r = synthesize(x, y, spaceRange, unit, nbrOfPoints);

			for (Tuple t : r) {
				out.println(t.getValue(0) + "," + t.getValue(1) + ",C" + label);
			}

		}
		in.close();
		out.close();
	}

	public static List<Tuple> synthesize(double x, double y, int range,
			double unit, int nbrOfPoints) {
		ArrayList<Tuple> r = new ArrayList<Tuple>();
		double xr = x * range;
		double yr = y * range;

		double xmin = xr ;
		double xmax = xr + unit * range;
		double ymin = yr ;
		double ymax = yr + unit * range;
		double delta = 1;
		for (int i = 0; i < nbrOfPoints; i++) {
			Tuple t = UniformRandom.getRandomTuple(xmin, xmax, ymin, ymax,
					delta);

			r.add(t);
		}

		return r;
	}
}
