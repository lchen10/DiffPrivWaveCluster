import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Utils;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

/**
 * Performs a single run of cross-validation.
 * 
 * Command-line parameters:
 * <ul>
 * <li>-t filename - the dataset to use</li>
 * <li>-x int - the number of folds to use</li>
 * <li>-s int - the seed for the random number generator</li>
 * <li>-c int - the class index, "first" and "last" are accepted as well; "last"
 * is used by default</li>
 * <li>-W classifier - classname and options, enclosed by double quotes; the
 * classifier to cross-validate</li>
 * </ul>
 * 
 * Example command-line:
 * 
 * <pre>
 * java CrossValidationSingleRun -t anneal.arff -c last -x 10 -s 1 -W "weka.classifiers.trees.J48 -C 0.25"
 * </pre>
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidationSingleRunUsingGridTraining {
	
//////this VERSION SEEMS is updated, 比之copy2_of 版本，多了好多commment, copy2_of版本是旧的，此java file是删除很多comment。
// Copy_2_of_CrossValidationSingleRunUsingGridTrainingSimplified.java那个文件是这个版的重复，内容都是一样，实验结果也是一样的。
	// 只是删除了很多comment而已。
	/**
	 * Performs the cross-validation. See Javadoc of class for information on
	 * command-line parameters.
	 * 
	 * @param args
	 *            the command-line parameters
	 * @throws Excecption
	 *             if something goes wrong
	 */

	public static void main(String[] args) throws Exception {

		
		// 这里需要更新original 文件名
		BufferedReader reader = new BufferedReader(
				new FileReader("E:/D_disk/workspace/WaveCluster_PrivTHR/dataset/deritative-15-clusters-30k-unit-0.01.arff"));
//		BufferedReader reader = new BufferedReader(
//				new FileReader("E:/D_disk/workspace/WaveCluster_PrivTHR/dataset/deritative-spiral-30k-unit-0.01.arff"));
//		BufferedReader reader = new BufferedReader(
//				new FileReader("E:/D_disk/workspace/WaveCluster_PrivTHR/dataset/deritative-aggregation-30k-unit-0.01.arff"));
		Instances data = new Instances(reader);
		System.out.println(data);

		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);

		int seed = 1;
		int folds = 10;

		Random rand = new Random(seed);
		Instances randData = new Instances(data);
		randData.randomize(rand);
		randData.stratify(folds);

		MatlabProxyFactoryOptions.Builder builder = new MatlabProxyFactoryOptions.Builder();
		builder.setMatlabLocation("C:/Program Files/MATLAB/R2012a/bin/matlab.exe");
		builder.setMatlabStartingDirectory(new File(
				"C:/Users/Ling Chen/Desktop/WaveClusterSet/WaveCluster_code_PrivTHR/"));
		MatlabProxyFactory factory = new MatlabProxyFactory(builder.build());
		MatlabProxy proxy = factory.getProxy();

		for (int r = 0; r < 1; r++) {

			for (int n = 0; n < folds; n++) {
				Instances train = randData.trainCV(folds, n);
				Instances test = randData.testCV(folds, n);
				ArffSaver saver1 = new ArffSaver();

			
				
				///需要更新几个参数，num_partition, density, 文件名
				//15-clusters
				int num_partition = 48;
				String density_threshold = "60%";
				double xmin = 20406;
				double ymin = 51397;
				double xmax = 963731;
				double ymax = 972296;
				double epsilon4count = 2.0;
						
				//spiral datasets
//				int num_partition = 40;
//				String density_threshold = "10%";
//				double xmin = 90045;
//				double ymin = 87019;
//				double xmax = 964418;
//				double ymax = 955379;
//				double epsilon4count = 2.0;
					
				//aggregation datasets
//				int num_partition = 50;
//				String density_threshold = "15%";
//				double xmin = 100504;
//				double ymin = 58503;
//				double xmax = 1097098;
//				double ymax = 875093;
//				double epsilon4count = 2.0;
//				
				
				
	
				
				
				
				double epsilon4PValue = 0.3; //其实也不会被用到
				double delta = 1000;// 其实可以不用，不需要synthesize private pts
				
				
				String trainFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-train-"
						+ r + "-" + n + ".arff";
				saver1.setInstances(train);
				saver1.setFile(new File(trainFile));
				saver1.writeBatch();

				String trainFileCSV = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-train-"
						+ r + "-" + n + ".csv";
				CSVSaver csvsaver1 = new CSVSaver();
				csvsaver1.setInstances(train);
				csvsaver1.setFile(new File(trainFileCSV));
				csvsaver1.writeBatch();

				////////////////////
				////////////////////Here is the WAVECLUSTER algorithm, which output truegrid & privategrid
				////////////////////In this function, there is a lot of output file generated
				////////////////////
				// need to update the paramters: partition, density, xmin, ymin, xmax, ymax, delta, epsilon budget countMatrix, epsilon budget for P-Percentile
				// If it is approach1, no matter what value "epsilon budget for P-Percentile" is, its resulting result won't be used. It does not affect the final results.
				int privateclustertotal = WaveCluster(proxy, trainFileCSV, r, n, num_partition, density_threshold, xmin,ymin, xmax, ymax, delta, epsilon4count, epsilon4PValue);

				System.out.println("private cluster total : " + privateclustertotal);
				
				// this file stores the waveclustering results
				String testFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-test-"
						+ r + "-" + n + ".csv";

				CSVSaver csvsaver2 = new CSVSaver();
				csvsaver2.setInstances(test);
				csvsaver2.setFile(new File(testFile));
				csvsaver2.writeBatch();
				
				////////////修改test data, 更新参数num_partition, xmin, ymin, xmax, ymax/////////////////	
				///////////////////////////////////////////////////////////////
				////////////////////// how to change test data point to grids.
				String exeID = r + "-" + n ;
				Point2GridForTest(proxy, testFile,	num_partition, xmin,ymin, xmax, ymax, exeID);
				
				String point2GridTestFileWLabel = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/data2gridForTestDataWLabel-" + exeID + ".csv";
				///////////////////////////////////////////////////////////////
				
				
				

				String testprocessfile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-test-"
						+ r + "-" + n + "-processed.csv";

				/////////////////////////////////////
				///////////////////////////////////// rewrite private test case
				RewriteClassLabelForCSVFile(privateclustertotal, point2GridTestFileWLabel,
						testprocessfile);
				
				String trueTrainMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-train-true-"
						+ r + "-" + n + "-cluster.csv";
				
//				////////////修改trueTrain data, 更新参数num_partition/////////////////
//				// need to update parameter num_partition
//				String inputTrueTrainFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-true-" + density_threshold + "-" + num_partition + "-15-clusters-train-" + r + "-" + n + ".txt";
//				String trueTrainMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/Grid2Point-siggrid-true-"+ density_threshold + "-" + num_partition +"-15-clusters-" + r + "-" + n + ".csv";
//				Grid2PointForTrain(inputTrueTrainFile, trueTrainMergeLabelFile, num_partition);
				
				
				Instances truetrainData = DataSource
						.read(trueTrainMergeLabelFile);
				truetrainData.setClassIndex(truetrainData.numAttributes() - 1);
				String wekatruefile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/wekaclassfile-true-"
						+ r + "-" + n + ".txt";
				
				String traintestprocessfile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-truetest-"
						+ r + "-" + n + "-processed.csv";

				//////////////////////////////////////
				////////////////////////////////////// rewrite true test data
				int t = truetrainData.numClasses();
				System.out.println("truetrainData.numClasses():" + t);
				RewriteClassLabelForCSVFile(truetrainData.numClasses(), point2GridTestFileWLabel,
						traintestprocessfile);

				Instances testCSV = DataSource.read(traintestprocessfile);
				testCSV.setClassIndex(testCSV.numAttributes() - 1);
				
				/////////////////////////
				// TrueCase weka Validate
				WekaValidate(truetrainData, testCSV, wekatruefile);

				String privateMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-private-synthesized-"
						+ r + "-" + n + ".csv";
				
				////////////修改private train data, 更新参数num_partition/////////////////
				// covert private grid to point, need to update num_partition
//				String inputPrivateTrainFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-private-" + density_threshold + "-" + num_partition + "-15-clusters-train-" + r + "-" + n + ".txt";
//				String privateMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/Grid2Point-siggrid-private-" + density_threshold + "-" + num_partition + "-15-clusters-" + r + "-" + n + ".csv";
//				Grid2PointForTrain(inputPrivateTrainFile, privateMergeLabelFile, num_partition);
				

				Instances privatetrainData = DataSource
						.read(privateMergeLabelFile);
				privatetrainData
						.setClassIndex(privatetrainData.numAttributes() - 1);

				String wekaprivatefile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/wekaclassfile-private-"
						+ r + "-" + n + ".txt";

				Instances testCSVprocessed = DataSource.read(testprocessfile);
				testCSVprocessed
						.setClassIndex(testCSVprocessed.numAttributes() - 1);
				
				///////////////////////
				//Private Case, Weka Validate
				WekaValidate(privatetrainData, testCSVprocessed,
						wekaprivatefile);

				System.out.println("Round#: " + r + "Number # " + n
						+ ": =================" + "\n");
				System.out.println("Train set:" + "\n");
				System.out.println(train);
				System.out.println("Test set:" + "\n");
				System.out.println(test);

			}
		}
		reader.close();
		proxy.disconnect();
	}

	public static void RewriteClassLabelForCSVFile(int privateclustertotal,
			String testFile, String testprocessfile)
			throws FileNotFoundException, IOException {
		BufferedReader testcsvreader = new BufferedReader(new FileReader(
				testFile));
		String line = null;
		testcsvreader.readLine(); // skip the first line
		PrintWriter f0 = new PrintWriter(new FileWriter(testprocessfile));
		f0.println("x-value,y-value,class");
		int clusterid = 0;
		while ((line = testcsvreader.readLine()) != null) {
			String[] split = line.split(",");
			if (clusterid < privateclustertotal) {
				f0.println(split[0] + "," + split[1] + ",C" + clusterid);
			} else {
				f0.println(split[0] + "," + split[1] + ",?");
			}
			clusterid++;
		}
		testcsvreader.close();
		f0.close();
	}

	public static void WekaValidate(Instances train, Instances test, String file)
			throws Exception {

		// // J48

		//
		// // options[0] = "-C";
		// // options[1] = "0.25";
		// // options[2] = "-M";
		// // options[3] = "2";

		J48 cls = new J48(); // new instance of tree
		String[] options = new String[1];
		options[0] = "-U"; // -u represents unpruned tree
		cls.setOptions(options); // set the options
		PrintWriter out = new PrintWriter(new FileWriter(file));
		try {
			cls.buildClassifier(train);

			// NaiveBayes cls = new NaiveBayes();
			// cls.buildClassifier(train);

			for (int i = 0; i < test.numInstances(); i++) {
				double pred = cls.classifyInstance(test.instance(i));
				System.out.print("ID: " + test.instance(i).value(0));
				System.out.print(", actual: "
						+ test.classAttribute().value(
								(int) test.instance(i).classValue()));
				System.out.println(", pred: " + (int) pred);
				System.out.println(", predicted: "
						+ test.classAttribute().value((int) pred));

				double x = test.instance(i).value(0);
				double y = test.instance(i).value(1);
				out.print(x + ",");
				out.print(y + ",");
				out.print(test.classAttribute().value(
						(int) test.instance(i).classValue())
						+ ",");
				out.println(test.classAttribute().value((int) pred));

			}
			out.close();
		} catch (UnsupportedAttributeTypeException e) {
			for (int i = 0; i < test.numInstances(); i++) {
				System.out.print("ID: " + test.instance(i).value(0));
				System.out.print(", actual: "
						+ test.classAttribute().value(
								(int) test.instance(i).classValue()));
				System.out.print("Unary Prediction: C1");
				double x = test.instance(i).value(0);
				double y = test.instance(i).value(1);
				out.print(x + ",");
				out.print(y + ",");
				out.print(test.classAttribute().value(
						(int) test.instance(i).classValue())
						+ ",");
				out.println("C1");

			}
			out.close();
		}
	}

	public static int WaveCluster(MatlabProxy proxy, String trainFile, int r,
			int i, int p_partition, String p_threshold, double p_xmin, double p_ymin, double p_xmax, double p_ymax, double delta, double epsilonForCountMatrix, double epsilonForPercentile) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(trainFile));
		String line = null;
		reader.readLine(); // skip the first line
		String matlabInputfile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-train-"
				+ r + "-" + i + "-processed.csv";
		PrintWriter f0 = new PrintWriter(new FileWriter(matlabInputfile));

		while ((line = reader.readLine()) != null) {
			String[] split = line.split(",");
			f0.println(split[0] + "," + split[1]);
		}
		reader.close();
		f0.close();

		String[] densityThresholdArray = new String[] { p_threshold };
		int[] num_partitionArray = new int[] { p_partition };
		int num_partition = num_partitionArray[0];
		String densityThreshold = densityThresholdArray[0];
		int figureId = 1;

		String datafile = matlabInputfile;
		// String exeID = i + "-" + densityThreshold + "-"
		// + Integer.toString(num_partition);
		String exeID = densityThreshold + "-" + Integer.toString(num_partition)
				+ "-15-clusters-train-" + r + "-" + i;
		String spyTitle = "Sigcell: Density-" + densityThreshold
				+ ", NumOfPartition-" + Integer.toString(num_partition);
		proxy.eval("a = csvread('" + datafile + "')");
		// Print a value of the array into the MATLAB Command Window
		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells] = WaveCluster(a, [], "
				+ num_partition
				+ ", '"
				+ densityThreshold
				+ "', 1, 'bior2.2', 1,'"
				+ exeID
				+ "','"
				+ "True:"
				+ spyTitle
				+ "'" + ")");
		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("gscatter(a(:,1), a(:,2),cluster_labels)");
		proxy.eval("title('True: Density-" + densityThreshold
				+ ", NumOfPartition-" + Integer.toString(num_partition) + "')");
		// proxy.eval("a = csvread('sampledata.csv')");
		// Print a value of the array into the MATLAB Command Window
		proxy.eval("WaveCluster_Preprocess_Private(a, [], " + num_partition
				+ ", '" + densityThreshold + "', 1, 'bior2.2', 1,'" + exeID + "')");
		System.out.println("finish output positive wavedata!");

		System.out.println(" ");
		System.out.println("===============Private Percentile Value for "
				+ exeID + "=================");
		// String densityThreshold = densityThresholdArray[a];
		String[] density = densityThreshold.split("%");
		String density_val = density[0];

		double privatePercentile = ExponentialMech.noisyPercentileValue(exeID,
				Double.parseDouble(density_val), epsilonForPercentile);
		System.out.println("finish private percentile: " + privatePercentile);

		
		// proxy.eval("figure");
		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("[cluster_labels, clustergrid, counts, datacellindices, wdata, sigcells, tempprivatewdata] = WaveCluster_Private(a, [], "
				+ num_partition
				+ ", '"
				+ densityThreshold
				+ "', 1, 'bior2.2', 1,'"
				+ exeID
				+ "', "
				+ privatePercentile
				+ ",'" + "Private:" + spyTitle + "'," + epsilonForCountMatrix + ")");

		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("gscatter(a(:,1), a(:,2), cluster_labels)");
		proxy.eval("title('Private: Density-" + densityThreshold
				+ ", NumOfPartition-" + Integer.toString(num_partition) + "')");
		System.out.println("finish private cluster! ");

		
		// which is the waveclustering results. format: x-value, y-value,
		// classlabel
		
		
		////////////修改trueTrain data, 更新参数num_partition/////////////////
		// need to update parameter num_partition
		String inputTrueTrainFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-true-" + p_threshold + "-" + p_partition + "-15-clusters-train-" + r + "-" + i + ".txt";
		String trueTrainMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/Grid2Point-siggrid-true-"+ p_threshold + "-" + p_partition +"-15-clusters-" + r + "-" + i + ".csv";
		Grid2PointForTrain(inputTrueTrainFile, trueTrainMergeLabelFile, num_partition);
		
		
		BufferedReader reader2 = new BufferedReader(new FileReader(
				trueTrainMergeLabelFile));
		line = null;
		line = reader2.readLine(); // skip the first line
	
		
		String trueMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/15-clusters-train-true-"
				+ r + "-" + i + "-cluster.csv";
		PrintWriter f1 = new PrintWriter(new FileWriter(trueMergeLabelFile));
//		String trueClusterLabel = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/cluster-true-" + exeID
//				+ ".txt";
		String trueClusterLabel = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-true-" + p_threshold + "-" + p_partition + "-15-clusters-train-" + r + "-" + i + ".txt";
		BufferedReader reader3 = new BufferedReader(new FileReader(
				trueClusterLabel));
		reader3.readLine(); // skip first line
		f1.println("x-value,y-value,class");
		int clusterid = 1;

		// in case connected-component labeling algorithm will merge some
		// neighboring clusters. so
		// the cluster labels will be 1, 2,3,4, 6,8. 5,6 are missing.
		Map<Integer, Integer> idmap = new HashMap<Integer, Integer>();
		while ((line = reader2.readLine()) != null) {
//			String labelString = reader3.readLine();
			String siggrid = reader3.readLine();
			String[] splitsiggrid = siggrid.split(" ");
//			System.out.println("labelString is: " + labelString);
			int label = Integer.parseInt(splitsiggrid[1].trim());
//			System.out.println("Label is: " + label);
//			if (label == 0)
//				continue; // remove noisy points
			
			if (!idmap.containsKey(label)) {
				if(label == 0)
				{
					int special_clusterid = 0;
					idmap.put(label, special_clusterid);
				}
				else
				{
					idmap.put(label, clusterid);
					clusterid++;
				}
				
//				System.out.println("Add new Label, ClusterID is: " + clusterid);
				
			}

			String[] split = line.split(",");
			f1.println(split[0] + "," + split[1] + ",C" + idmap.get(label));
//			System.out.println("split[0] is: " + split[0] + ", split[1] is: " + split[1] + ", idmap.get(label) is: " + idmap.get(label));
		}
		reader2.close();
		reader3.close();
		f1.close();

		
		
		//下面是如何生成private train data, 应该如true train data 一样的
			
//		String quantizationfile = Quantization.quantize(p_xmin, p_ymin, p_xmax, p_ymax,
//				p_partition/2, exeID);
//		String privateGridLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-private-"
//				+ exeID + ".txt";
//		BufferedReader reader4 = new BufferedReader(new FileReader(
//				privateGridLabelFile));
//		reader4.readLine();
//		BufferedReader reader5 = new BufferedReader(new FileReader(
//				quantizationfile));
		String synthesizedPointsLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-private-synthesized-"
				+ r + "-" + i + ".csv";
		PrintWriter f2 = new PrintWriter(new FileWriter(
				synthesizedPointsLabelFile));
		f2.println("x-value,y-value,class");
		
		////////////修改private train data, 更新参数num_partition/////////////////
		// covert private grid to point, need to update num_partition
		String inputPrivateTrainFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/siggrid-private-"
				+ exeID + ".txt";
		String privateMergeLabelFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/Grid2Point-siggrid-private-" + p_threshold + "-" + p_partition + "-15-clusters-" + r + "-" + i + ".csv";
		Grid2PointForTrain(inputPrivateTrainFile, privateMergeLabelFile, num_partition);
		
		
		BufferedReader reader4 = new BufferedReader(new FileReader(
				privateMergeLabelFile));
		line = null;
		line = reader4.readLine(); // skip the first line
		
		
		BufferedReader reader5 = new BufferedReader(new FileReader(
				inputPrivateTrainFile));
		reader5.readLine();
	
		
		int pclusterid = 1;
		HashMap<Integer, Integer> pidmap = new HashMap<Integer, Integer>();

//		String noisyCountForGrids = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/private-wdataMatrix-"
//				+ exeID + ".txt";
//		BufferedReader reader6 = new BufferedReader(new FileReader(
//				noisyCountForGrids));

		while ((line = reader4.readLine()) != null) {
//			String[] split = line.split(" ");
//			int clusterlabel = Integer.parseInt(split[1]);
			
			String siggrid = reader5.readLine();
			String[] splitsiggrid = siggrid.split(" ");
//			System.out.println("labelString is: " + labelString);
			int label = Integer.parseInt(splitsiggrid[1].trim());
			
//			System.out.println("Label is: " + label);
//			if (label == 0)
//				continue; // remove noisy points
			
			if (!pidmap.containsKey(label)) {
				if(label == 0)
				{
					int special_pclusterid = 0;
					pidmap.put(label, special_pclusterid);
				}
				else
				{
					pidmap.put(label, pclusterid);
					pclusterid++;
				}
				
//				System.out.println("Add new Label, ClusterID is: " + clusterid);
				
			}

			String[] split = line.split(",");
			f2.println(split[0] + "," + split[1] + ",C" + pidmap.get(label));
			
			
			
			
			
			
			
//			String qline = reader5.readLine();
//			double noisycount = Double.parseDouble(reader6.readLine()) * 2;
//			if (clusterlabel == 0) // remove noisy points
//				continue;
//			String[] linesplit = qline.split(",");
//			int xmin = Integer.parseInt(linesplit[0].trim());
//			int ymin = Integer.parseInt(linesplit[1].trim());
//			int xmax = Integer.parseInt(linesplit[2].trim());
//			int ymax = Integer.parseInt(linesplit[3].trim());


//			int roundcount = (int) Math.round(noisycount);
//			if (roundcount > 0) {
//				if (!pidmap.containsKey(clusterlabel)) {
//					pidmap.put(clusterlabel, pclusterid);
//					pclusterid++;
//				}

//				SynthesizePoints(roundcount, delta, xmin, ymin, xmax, ymax,
//						pidmap.get(clusterlabel), f2);
//			}
			
		
		}

		reader4.close();
//		reader5.close();
		reader5.close();
		f2.close();

//		return pclusterid - 1;
		return pclusterid;
	}

//	public static void SynthesizePoints(int count, double delta, int xmin,
//			int ymin, int xmax, int ymax, int cluster_label, PrintWriter out) {
//
//		if (count > 0) {
//			for (int i = 0; i < count; i++) {
//				Tuple t = UniformRandom.getRandomTuple(xmin, xmax, ymin, ymax,
//						delta);
//
//				out.println(t.toString().trim() + ("C" + cluster_label).trim());
//				// System.out.println(t.toString());
//			}
//		}
//
//	}
	
	
	public static void Grid2PointForTrain(String GridInputFile,
			String GridPointOutPutFile, int p_partition) throws IOException {
		BufferedReader reader = new BufferedReader(
				new FileReader(GridInputFile));
		String line = reader.readLine();

		int half_num_partition = p_partition / 2;
//		int count = 0;

		FileWriter fstream = new FileWriter(GridPointOutPutFile);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write("x-value,y-value,class" + "\n");

		
//		String temptrackingFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/TEMP-TRACKING-" + r + "-" + n + ".txt";
//		FileWriter fstream_track = new FileWriter(temptrackingFile);
//		BufferedWriter out_track = new BufferedWriter(fstream_track);
		
		
		
		while ((line = reader.readLine()) != null) {
			String[] split = line.split(" ");
			int gridID = Integer.parseInt(split[0].trim());
			// count++;
			int sig = Integer.parseInt(split[1].trim());

//			out_track.write("gridID: " + gridID + ", sig: " + Integer.toString(sig) + "\n");
			if (gridID % half_num_partition == 0) {
				int y = gridID / half_num_partition;
				int x = half_num_partition;
//				count++;
				out.write(x + "," + y + "," + "C" + Integer.toString(sig) + "\n");
//				out_track.write("gridID % half_num_partition == 0");
//				out_track.write(x + "," + y + "," + "C" + Integer.toString(sig) + "\n");
				// System.out.println("x is: " + x + "; y is: " + y);
			} else if (gridID % half_num_partition != 0) {
				int y = (int) (Math
						.ceil((double) (gridID / half_num_partition)) + 1);
				int x = gridID - (half_num_partition * (y - 1));
//				count++;
				// System.out.println("x is: " + x + "; y is: " + y);
//				out_track.write("gridID % half_num_partition != 0");
				out.write(x + "," + y + "," + "C"  + Integer.toString(sig) + "\n");
//				out_track.write(x + "," + y + "," + "C" + Integer.toString(sig) + "\n");
			}

		}

		// System.out.println("line # is: " + count);
		reader.close();
		out.close();
//		out_track.close();

	}
	
	public static void Point2GridForTest(MatlabProxy proxy, String TestInputFile,
			int p_partition, double xmin, double ymin, double xmax, double ymax, String exeID) throws MatlabInvocationException, IOException
	{	
		BufferedReader reader = new BufferedReader(
				new FileReader(TestInputFile));
		String line = reader.readLine();

		String tempFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/TestDataTemp-" + exeID + ".txt";
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
		String data2gridTestFile = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/data2gridForTestData-" + exeID + ".txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(data2gridTestFile));
		
		String data2gridTestFileWLabel = "E:/D_disk/workspace/WaveCluster_PrivTHR/data_experiments/data2gridForTestDataWLabel-" + exeID + ".csv";
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