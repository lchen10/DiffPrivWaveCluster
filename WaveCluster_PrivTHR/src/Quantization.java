import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Quantization {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static String quantize(long fxmin, long fymin, long fxmax,
			long fymax, int num_partition, String exeId) throws IOException {
		// TODO Auto-generated method stub

		BufferedWriter out = null;
		String outputFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/private_siggrid_coordinates"
				+ exeId + ".csv";
		// Create file
		FileWriter fstream = new FileWriter(outputFile);
		out = new BufferedWriter(fstream);

		// xmin, ymin
		long[] featuremin = { fxmin, fymin };
		// xmax, ymax
		long[] featuremax = { fxmax, fymax };

		// num_partition
		// int num_partition = 29;

		long grid_x_length = (long) Math.ceil(Math.abs(featuremax[0]
				- featuremin[0])
				/ num_partition) + 1;
		System.out.println("grid_x_length is : " + grid_x_length);
		long grid_y_length = (long) Math.ceil(Math.abs(featuremax[1]
				- featuremin[1])
				/ num_partition) + 1;
		System.out.println("grid_y_length is : " + grid_y_length);

		// long[][] grid = new long[29][29];
		ArrayList<ArrayList<Long>> grids = new ArrayList<ArrayList<Long>>();

		long gridId = 0;
		long xmin = fxmin;
		long ymin = fymin;
		long xmax = xmin + grid_x_length;
		long ymax = ymin + grid_y_length;

		// i represent row number-----from bottom to top
		for (int i = 0; i < num_partition; i++) {

			// j represent col number----from left to right
			for (int j = 0; j < num_partition; j++) {
				gridId++;

				ArrayList<Long> grid = new ArrayList<Long>();
				grid.add(gridId);
				grid.add(xmin);
				grid.add(ymin);
				grid.add(xmax);
				grid.add(ymax);

				grids.add(grid);

				xmin += grid_x_length;
				xmax += grid_x_length;
			}
			xmin = fxmin;
			xmax = xmin + grid_x_length;
			ymin += grid_y_length;
			ymax += grid_y_length;
		}

		System.out.println("start printing:==================");
		for (int k = 0; k < grids.size(); k++) {
			System.out.println(grids.get(k).toString());
			out.write( grids.get(k).get(1) + ", "
					+ grids.get(k).get(2) + ", " + grids.get(k).get(3) + ", "
					+ grids.get(k).get(4) + ", ");
			out.newLine();

		}

		out.flush();
		out.close();
		return outputFile;
	}
}
