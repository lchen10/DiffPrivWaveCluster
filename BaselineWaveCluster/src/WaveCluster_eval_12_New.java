import java.io.File;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

public class WaveCluster_eval_12_New {

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
		
		// it does not matter about the budget and its distribution, since the budget already been used in synthetic data generating
		double epsilon = 2.0;
		String[] densityThresholdArray = new String[] { "15%" };
		int[] num_partitionArray = new int[] { 50 };
//		String truedatafile = "deritative-15-clusters-30k-unit-0.01.csv";
//		String truedatafile = "deritative-spiral-30k-unit-0.01.csv";
		String truedatafile = "deritative-aggregation-30k-unit-0.01.csv";
		
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
					
					String privatedatafile = "E:/D_disk/workspace/AdaptiveGridGenerationBeingCalledByJava/synthesis_data/aggregation-budget-" + epsilon + "-" + i + ".csv";
					proxy.eval("b = csvread('" + privatedatafile + "')");
					
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
					
					
					
					// private wavecluster, using dataset b
					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells] = WaveCluster_private_baseline(b, [], "
							+ num_partition
							+ ", '"
							+ densityThreshold
							+ "', 1, 'haar', 0,'"
							+ exeID
							+ "','"
							+ "Private:"
							+ spyTitle + "'" + ")");
					proxy.eval("figure(" + figureId + ")");
					figureId++;
					proxy.eval("gscatter(b(:,1), b(:,2),cluster_labels)");
					proxy.eval("title('Private: Density-" + densityThreshold
							+ ", NumOfPartition-"
							+ Integer.toString(num_partition) + "')");
					
					
					
					
					
					
					System.out.println("finish private cluster! ");

				}
			}
		}

		System.out.println("finished");
		// Disconnect the proxy from MATLAB
		proxy.disconnect();

	}

}
