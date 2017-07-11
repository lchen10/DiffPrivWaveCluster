import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class DataGenerator {

	public static void main(String[] args) throws IOException {

		for(int k = 1; k < 10; k++)
		{
			FileInputStream in = new FileInputStream(
					"E:/D_disk/workspace/SyntheticDataGenerator/data_grids/Concave-5clusters-grids-10-" + k + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			BufferedWriter out = null;
			// Create file
			FileWriter fstream = new FileWriter(
					"E:/D_disk/workspace/SyntheticDataGenerator/synthetic_datasets/Concave-5clusters-grids-10-" + k + ".csv");

			double delta = 2;
			out = new BufferedWriter(fstream);

			String strLine = null;
			String[] mystring = new String[5];

			while ((strLine = br.readLine()) != null) {

				strLine = strLine.trim();
				mystring = strLine.split(",");
				// System.out.println("mystring length is: " + mystring.length);

				if (mystring.length != 5) {
					// System.out.println("test");
					continue;
				}
				// If all the values are interger
//				int count = Integer.parseInt(mystring[0]);
//				int xmin = Integer.parseInt(mystring[1]);
//				int ymin = Integer.parseInt(mystring[2]);
//				int xmax = Integer.parseInt(mystring[3]);
//				int ymax = Integer.parseInt(mystring[4]);
				
				// if all the values are double
				double count_double = Double.parseDouble(mystring[0]);
				int count = (int) Math.round(count_double);
				double xmin = Double.parseDouble(mystring[1]);
				double ymin = Double.parseDouble(mystring[2]);
				double xmax = Double.parseDouble(mystring[3]);
				double ymax = Double.parseDouble(mystring[4]);
				
				
//				int cluster_label = Integer.parseInt(mystring[5]);
				

				if (count > 0) {
					for (int i = 0; i < count; i++) {
						Tuple t = UniformRandom.getRandomTuple(xmin, xmax, ymin,
								ymax, delta);

//						out.write(t.toString() + "C" + cluster_label);
						double x = t.getValue(0);
						double y = t.getValue(1);
						out.write(x + ", " + y);
						out.newLine();
						// System.out.println(t.toString());
					}
				}

			}
			in.close();
			out.flush();
			out.close();
		}
		

		// double tt = 45.70001;
		// double dd = 45.42357;
		// int ttt = (int) Math.round(tt);
		// int ddd = (int) Math.round(dd);
		// System.out.println("round(45.7): " + ttt);
		// System.out.println("round(45.4): " + ddd);

	}

}
