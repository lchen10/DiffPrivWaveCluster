import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

public class ChangeFormat {

	/**
	 * @param args
	 * @throws IOException
	 * @throws MatlabInvocationException 
	 * @throws MatlabConnectionException 
	 */
	public static void main(String[] args) throws IOException, MatlabInvocationException, MatlabConnectionException {
		// TODO Auto-generated method stub
//		String inputFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/siggrid-private-10%-50-15-clusters-train-0-0.txt";
//		String outputFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/siggrid-private2Point-10%-50-15-clusters-train-0-0.txt";
//
//		Grid2Point(inputFile, outputFile, 50);
		
		MatlabProxyFactoryOptions.Builder builder = new MatlabProxyFactoryOptions.Builder();
		builder.setMatlabLocation("C:/Program Files/MATLAB/R2012a/bin/matlab.exe");
		builder.setMatlabStartingDirectory(new File(
				"C:/Users/Ling Chen/Desktop/WaveCluster_approach1_improved/"));
		MatlabProxyFactory factory = new MatlabProxyFactory(builder.build());
		MatlabProxy proxy = factory.getProxy();
		
		String datafile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/15-clusters-test-0-0.csv";
		
//		proxy.eval("a = csvread('" + datafile + "')");
	
		double xmin = 86;
		double ymin = 83;
		double xmax = 935;
		double ymax = 904;
		int num_cells = 25;
//		proxy.eval("[datacells] = data2gridForTestData(" + xmin + "," + xmax + "," + ymin + "," + ymax +", a, []," + num_cells + ")");
			
		String exeID = "0-1";
		Point2GridForTest(proxy, datafile,	50, xmin, ymin, xmax, ymax, exeID);
		
		
		proxy.disconnect();
	}

	public static void Grid2PointForTrain(String GridInputFile,
			String GridPointOutPutFile, int p_partition) throws IOException {
		BufferedReader reader = new BufferedReader(
				new FileReader(GridInputFile));
		String line = reader.readLine();

		int half_num_partition = p_partition / 2;
		int count = 0;

		FileWriter fstream = new FileWriter(GridPointOutPutFile);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write("x-value,y-value,class" + "\n");

		while ((line = reader.readLine()) != null) {
			String[] split = line.split(" ");
			int gridID = Integer.parseInt(split[0].trim());
			// count++;

			int sig = Integer.parseInt(split[1].trim());

			if (gridID % half_num_partition == 0) {
				int y = gridID / half_num_partition;
				int x = half_num_partition;
				count++;
				out.write(x + "," + y + "," + "C" + sig + "\n");
				// System.out.println("x is: " + x + "; y is: " + y);
			} else if (gridID % half_num_partition != 0) {
				int y = (int) (Math
						.ceil((double) (gridID / half_num_partition)) + 1);
				int x = gridID - (half_num_partition * (y - 1));
				count++;
				// System.out.println("x is: " + x + "; y is: " + y);
				out.write(x + "," + y + "," + "C"  + sig + "\n");
			}

		}

		// System.out.println("line # is: " + count);
		reader.close();
		out.close();

	}
	
	public static void Point2GridForTest(MatlabProxy proxy, String TestInputFile,
			int p_partition, double xmin, double ymin, double xmax, double ymax, String exeID) throws MatlabInvocationException, IOException
	{	
		BufferedReader reader = new BufferedReader(
				new FileReader(TestInputFile));
		String line = reader.readLine();

		String tempFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/TestDataTemp-" + exeID + ".txt";
		FileWriter fstream = new FileWriter(tempFile);
		BufferedWriter out = new BufferedWriter(fstream);
		

		while ((line = reader.readLine()) != null) {
			String[] split = line.split(",");
			double x = Double.parseDouble(split[0].trim());
			double y = Double.parseDouble(split[1].trim());
			
			out.write(x + "," + y + "\n");
		}
		
		reader.close();
		out.close();
		
		proxy.eval("a = csvread('" + tempFile + "')");
		int num_cells = p_partition / 2;
		proxy.eval("[datacells] = data2gridForTestData(" + xmin + "," + xmax + "," + ymin + "," + ymax +", a, []," + num_cells + ",'" + exeID  + "')");
		
		
		// rewrite testgrid file
		String data2gridTestFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/data2gridForTestData-" + exeID + ".txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(data2gridTestFile));
		
		String data2gridTestFileWLabel = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/data2gridForTestDataWLabel-" + exeID + ".csv";
		FileWriter fstream1 = new FileWriter(data2gridTestFileWLabel);
		BufferedWriter out1 = new BufferedWriter(fstream1);
		
		String line1 = null;
		out1.write("x-value,y-value,class" + "\n");
		while ((line1 = reader1.readLine()) != null) {
			String[] split1 = line1.split(" ");
			
			double x = Double.parseDouble(split1[0].trim());
			double y = Double.parseDouble(split1[1].trim());
			
			out1.write(x + "," + y + ", C1" + "\n");
			
		}
		
		reader1.close();
		out1.close();
	}
	
	
}
