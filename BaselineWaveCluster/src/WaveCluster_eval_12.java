import java.io.File;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class WaveCluster_eval_12 {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		MatlabProxyFactoryOptions.Builder builder = new MatlabProxyFactoryOptions.Builder();
		builder.setMatlabLocation("C:/Program Files/MATLAB/R2012a/bin/matlab.exe");
		builder.setMatlabStartingDirectory(new File(
				"C:/Users/Ling Chen/Desktop/WaveClusterSet/WaveCluster_approach-Baseline/"));
		MatlabProxyFactory factory = new MatlabProxyFactory(builder.build());
		MatlabProxy proxy = factory.getProxy();
		// Use the proxy as desired
		// Create a 4x3x2 array filled with random values

		///////////////////////////////////////////
		// below is the paramters need to be updated when apply on different dataset or with different budgets
		// if it is approach 1 or 2, paramter double epsilonForPercentile = 0.3; won't have any effect. Its derived value won't be used.
		String[] densityThresholdArray = new String[] { "6%" };
		int[] num_partitionArray = new int[] { 64 };
		
		// it does not matter about the budget and its distribution, since the budget already been used in synthetic data generating
		double epsilonForCountMatrix = 1.0;
		double epsilonForPercentile = 0.3;
		String truedatafile = "Aggregation-enlarge-36-30k.csv";
		
		String privatedatafile_1"E:/D_disk/workspace/AdaptiveGridGenerationBeingCalledByJava/synthesis_data/aggregation-budget-" + eps + "-" + j + ".csv"
		
		String privatedatafile_1 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-1.csv";
		String privatedatafile_2 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-2.csv";
		String privatedatafile_3 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-3.csv";
		String privatedatafile_4 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-4.csv";
		String privatedatafile_5 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-5.csv";
		String privatedatafile_6 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-6.csv";
		String privatedatafile_7 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-7.csv";
		String privatedatafile_8 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-8.csv";
		String privatedatafile_9 = "C:/Users/Ling Chen/Desktop/WaveCluster_approach-Baseline/AdaptiveGrids-1-9/concave/Concave-5clusters-grids-1-9.csv";
		String[] privatedatafile = {privatedatafile_1,privatedatafile_2, privatedatafile_3, privatedatafile_4,privatedatafile_5,privatedatafile_6, privatedatafile_7, privatedatafile_8, privatedatafile_9};
		// String datafile = "SpiralSynthetic2.csv";
		
		int figureId = 1;
		int exeTime = 10;
		for (int i = 0; i < exeTime; i++) {

			for (int a = 0; a < densityThresholdArray.length; a++) {
				String densityThreshold = densityThresholdArray[a];
				for (int b = 0; b < num_partitionArray.length; b++) {
					int num_partition = num_partitionArray[b];
					
					
					String exeID = densityThreshold + "-"
							+ Integer.toString(num_partition) + "-" + i;
					
					String spyTitle = "Sigcell: Density-" + densityThreshold
							+ ", NumOfPartition-"
							+ Integer.toString(num_partition);
					proxy.eval("a = csvread('" + truedatafile + "')");
					proxy.eval("b = csvread('" + privatedatafile[i] + "')");
					// Print a value of the array into the MATLAB Command Window
					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells] = WaveCluster(a, [], "
							+ num_partition
							+ ", '"
							+ densityThreshold
							+ "', 1, 'haar', 0,'"
							+ exeID
							+ "','"
							+ "True:"
							+ spyTitle + "'" + ")");
					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("gscatter(a(:,1), a(:,2),cluster_labels)");
					proxy.eval("title('True: Density-" + densityThreshold
							+ ", NumOfPartition-"
							+ Integer.toString(num_partition) + "')");
					// proxy.eval("a = csvread('sampledata.csv')");
					// Print a value of the array into the MATLAB Command Window
					proxy.eval("WaveCluster_Preprocess_Private(b, [], "
							+ num_partition + ", '" + densityThreshold
							+ "', 1, 'haar', 0,'" + exeID + "')");
					System.out.println("finish output positive wavedata!");

					System.out.println(" ");
					System.out
							.println("===============Private Percentile Value for "
									+ exeID + "=================");
					// String densityThreshold = densityThresholdArray[a];
					String[] density = densityThreshold.split("%");
					String density_val = density[0];

					double privatePercentile = ExponentialMech
							.noisyPercentileValue(exeID,
									Double.parseDouble(density_val), epsilonForPercentile);
					System.out.println("finish private percentile: "
							+ privatePercentile);

					// proxy.eval("figure");
					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells] = WaveCluster_Private(b, [], "
							+ num_partition
							+ ", '"
							+ densityThreshold
							+ "', 1, 'haar', 0,'"
							+ exeID
							+ "', "
							+ privatePercentile
							+ ",'"
							+ "Private:"
							+ spyTitle
							+ "'," + epsilonForCountMatrix + ")");

					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("gscatter(b(:,1), b(:,2), cluster_labels)");
					proxy.eval("title('Private: Density-" + densityThreshold
							+ ", NumOfPartition-"
							+ Integer.toString(num_partition) + "')");
					System.out.println("finish private cluster! ");

				}
			}

		}

		// fjdklfjdslfjdl
		//
		// proxy.eval("a = csvread('sampledata.csv')");
		// // Print a value of the array into the MATLAB Command Window
		// proxy.eval("gscatter(a(:,1), a(:,2), WaveCluster_Private2(a, [], 8, '30%', 1, 'haar', 0,'test'))");

		// Get the array from MATLAB
		// MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
		// MatlabNumericArray array = processor.getNumericArray("array");
		//
		// // Print out the same entry, using Java's 0-based indexing
		// System.out.println("entry: " + array.getRealValue(2, 1, 0));
		//
		// // Convert to a Java array and print the same value again
		// double[][][] javaArray = array.getRealArray3D();
		// System.out.println("entry: " + javaArray[2][1][0]);
		System.out.println("finished");
		// Disconnect the proxy from MATLAB
		proxy.disconnect();
		// proxy.eval(new String("x=5;"));
		// proxy.eval(new String("sqrt(x)"));

		// TODO Auto-generated method stub
		// MatlabControl mc = new MatlabControl();
		// mc.eval(new String("help"));
		// mc.eval(new String("help(plot)"));

	}

}
