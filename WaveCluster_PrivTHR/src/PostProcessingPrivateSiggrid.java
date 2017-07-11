import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;


public class PostProcessingPrivateSiggrid {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws IOException, MatlabConnectionException, MatlabInvocationException {
		// TODO Auto-generated method stub
		
		int half_num_partition = 32;
		
//		String privatesiggrid = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/siggrid-private-50%-64-15-clusters-train-0-0.txt";
		String privatesiggrid = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/siggrid-private-6%-64-0.txt";
		BufferedReader reader = new BufferedReader(new FileReader(privatesiggrid));
		String line = reader.readLine();	
		
		
		FileWriter fstream = new FileWriter(
				"C:/Users/Ling Chen/Desktop/WaveCluster/siggrid2points.csv");
		BufferedWriter out = new BufferedWriter(fstream);

				
		int count = 0;
		while ((line = reader.readLine()) != null) {
					String[] split = line.split(" ");
					int gridID = Integer.parseInt(split[0].trim());
//					count++;

					int sig = Integer.parseInt(split[1].trim());
					if(sig != 0)
					{
						if(gridID % half_num_partition == 0)
						{
							int y = gridID / half_num_partition;
							int x = half_num_partition;
							count++;
							out.write(x + "," + y + "\n");
//							System.out.println("x is: " + x + "; y is: " + y);
						}
						else if (gridID % half_num_partition != 0)
						{
							int y = (int) (Math.ceil((double)(gridID / half_num_partition)) + 1);
							int x = gridID - (half_num_partition * (y-1));
							count++;
//							System.out.println("x is: " + x + "; y is: " + y);
							out.write(x + "," + y + "\n");
						}
						
					}
					
					
		}
//		System.out.println("line # is: " + count);
		reader.close();
		out.close();
		
		
		MatlabProxyFactoryOptions.Builder builder = new MatlabProxyFactoryOptions.Builder();
		builder.setMatlabLocation("C:/Program Files/MATLAB/R2012a/bin/matlab.exe");
		builder.setMatlabStartingDirectory(new File(
				"C:/Users/Ling Chen/Desktop/WaveCluster/"));
		MatlabProxyFactory factory = new MatlabProxyFactory(builder.build());
		MatlabProxy proxy = factory.getProxy();
		
		
		String datafile = "siggrid2points.csv";
		int num_partition = 8;
		String densityThreshold = "6%";
		
		proxy.eval("a = csvread('" + datafile + "')");
		// Print a value of the array into the MATLAB Command Window
		int figureId = 1;
		proxy.eval("figure(" + figureId + ")");
		proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells] = WaveCluster(a, [], "
				+ num_partition
				+ ", '"
				+ densityThreshold
				+ "', 1, 'haar', 0"
				+ ")");
		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("gscatter(a(:,1), a(:,2),cluster_labels)");
		proxy.eval("title('PostProcessing by WaveCluster: Density-" + densityThreshold
				+ ", NumOfPartition-"
				+ Integer.toString(num_partition) + "')");
		
		
		
		
		proxy.disconnect();
		
		
		
		
		
		
		
		
//		File outputFolder = new File("E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments");
//		String[] files = outputFolder.list();
//		HashMap<String, String> filemap = new HashMap<String, String>();
//		for (String file : files) {
//			if (file.startsWith("wekaclassfile-true")) {
//				String subfix = file.substring(18);
//				String pairFilename = "wekaclassfile-private" + subfix;
//				filemap.put(file, pairFilename);
//			}
//		}
		
		
		

	}

}
