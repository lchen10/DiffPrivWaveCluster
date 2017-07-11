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
public class CrossValidationSingleRun {

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

		BufferedReader reader = new BufferedReader(
				new FileReader(
						"E:/D_disk/workspace/PrivateWaveCluster1/dataset/Aggregation-enlarge-36-trim.arff"));
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


		for (int r = 0; r < 1; r++) {

			for (int n = 0; n < folds; n++) {
				Instances train = randData.trainCV(folds, n);
				Instances test = randData.testCV(folds, n);
				ArffSaver saver1 = new ArffSaver();

				String trainFile = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/15-clusters-train-"
						+ r + "-" + n + ".arff";
				saver1.setInstances(train);
				saver1.setFile(new File(trainFile));
				saver1.writeBatch();

				String trainFileCSV = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/15-clusters-train-"
						+ r + "-" + n + ".csv";
				CSVSaver csvsaver1 = new CSVSaver();
				csvsaver1.setInstances(train);
				csvsaver1.setFile(new File(trainFileCSV));
				csvsaver1.writeBatch();

				String tabtrainFileCSV = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/15-clusters-train-"
						+ r + "-" + n + "-tab.csv";
				RewriteCSVToTabDelimited(trainFileCSV, tabtrainFileCSV);

				String kdoutputfile = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/15-clusters-train-private-"
						+ r + "-" + n + ".csv";
				
				PythonExecutor.ExecuteKDTree(kdoutputfile, 1, tabtrainFileCSV);
				

				// this file stores the waveclustering results
				String testFile = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/15-clusters-test-"
						+ r + "-" + n + ".csv";

				CSVSaver csvsaver2 = new CSVSaver();
				csvsaver2.setInstances(test);
				csvsaver2.setFile(new File(testFile));
				csvsaver2.writeBatch();

				// String testprocessfile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/15-clusters-test-"
				// + r + "-" + n + "-processed.csv";
				//
				// RewriteClassLabelForCSVFile(privateclustertotal, testFile,
				// testprocessfile);
				//
				// String trueTrainMergeLabelFile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/15-clusters-train-true-"
				// + r + "-" + n + "-cluster.csv";
				// Instances truetrainData = DataSource
				// .read(trueTrainMergeLabelFile);
				// truetrainData.setClassIndex(truetrainData.numAttributes() -
				// 1);
				// String wekatruefile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/wekaclassfile-true-"
				// + r + "-" + n + ".txt";
				//
				// String traintestprocessfile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/15-clusters-truetest-"
				// + r + "-" + n + "-processed.csv";
				//
				// RewriteClassLabelForCSVFile(truetrainData.numClasses(),
				// testFile,
				// traintestprocessfile);
				//
				// Instances testCSV = DataSource.read(traintestprocessfile);
				// testCSV.setClassIndex(testCSV.numAttributes() - 1);
				//
				//
				// WekaValidate(truetrainData, testCSV, wekatruefile);
				//
				// String privateMergeLabelFile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/siggrid-private-synthesized-"
				// + r + "-" + n + ".csv";
				//
				// Instances privatetrainData = DataSource
				// .read(privateMergeLabelFile);
				// privatetrainData
				// .setClassIndex(privatetrainData.numAttributes() - 1);
				//
				// String wekaprivatefile =
				// "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/wekaclassfile-private-"
				// + r + "-" + n + ".txt";
				//
				// Instances testCSVprocessed =
				// DataSource.read(testprocessfile);
				// testCSVprocessed
				// .setClassIndex(testCSVprocessed.numAttributes() - 1);
				// WekaValidate(privatetrainData, testCSVprocessed,
				// wekaprivatefile);
				//
				// System.out.println("Round#: " + r + "Number # " + n
				// + ": =================" + "\n");
				// System.out.println("Train set:" + "\n");
				// System.out.println(train);
				// System.out.println("Test set:" + "\n");
				// System.out.println(test);

			}
		}
		reader.close();
	}

	private static void RewriteCSVToTabDelimited(String trainFileCSV,
			String tabtrainFileCSV) throws Exception {
		BufferedReader testcsvreader = new BufferedReader(new FileReader(
				trainFileCSV));
		String line = null;
		testcsvreader.readLine(); // skip the first line
		PrintWriter f0 = new PrintWriter(new FileWriter(tabtrainFileCSV));
		while ((line = testcsvreader.readLine()) != null) {
			String[] split = line.split(",");
			f0.println(split[0] + "\t" + split[1]);
		}
		testcsvreader.close();
		f0.close();
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
		int clusterid = 1;
		while ((line = testcsvreader.readLine()) != null) {
			String[] split = line.split(",");
			if (clusterid <= privateclustertotal) {
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
			int i, int p_partition, String p_threshold, int p_xmin, int p_ymin,
			int p_xmax, int p_ymax, double delta, double epsilonForCountMatrix,
			double epsilonForPercentile) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(trainFile));
		String line = null;
		reader.readLine(); // skip the first line
		String matlabInputfile = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/15-clusters-train-"
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
				+ "', 1, 'haar', 0,'"
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
				+ ", '" + densityThreshold + "', 1, 'haar', 0,'" + exeID + "')");
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
				+ "', 1, 'haar', 0,'"
				+ exeID
				+ "', "
				+ privatePercentile
				+ ",'"
				+ "Private:"
				+ spyTitle
				+ "',"
				+ epsilonForCountMatrix
				+ ")");

		proxy.eval("figure(" + figureId + ")");
		figureId++;
		proxy.eval("gscatter(a(:,1), a(:,2), cluster_labels)");
		proxy.eval("title('Private: Density-" + densityThreshold
				+ ", NumOfPartition-" + Integer.toString(num_partition) + "')");
		System.out.println("finish private cluster! ");

		BufferedReader reader2 = new BufferedReader(new FileReader(
				matlabInputfile));
		line = null;

		// which is the waveclustering results. format: x-value, y-value,
		// classlabel
		String trueMergeLabelFile = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/15-clusters-train-true-"
				+ r + "-" + i + "-cluster.csv";
		PrintWriter f1 = new PrintWriter(new FileWriter(trueMergeLabelFile));
		String trueClusterLabel = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/cluster-true-"
				+ exeID + ".txt";
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
			String labelString = reader3.readLine();
			int label = Integer.parseInt(labelString.trim());
			if (label == 0)
				continue; // remove noisy points
			if (!idmap.containsKey(label)) {
				idmap.put(label, clusterid);
				clusterid++;
			}

			String[] split = line.split(",");
			f1.println(split[0] + "," + split[1] + ",C" + idmap.get(label));
		}
		reader2.close();
		reader3.close();
		f1.close();

		String quantizationfile = Quantization.quantize(p_xmin, p_ymin, p_xmax,
				p_ymax, p_partition / 2, exeID);
		String privateGridLabelFile = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/siggrid-private-"
				+ exeID + ".txt";
		BufferedReader reader4 = new BufferedReader(new FileReader(
				privateGridLabelFile));
		reader4.readLine();
		BufferedReader reader5 = new BufferedReader(new FileReader(
				quantizationfile));
		String synthesizedPointsLabelFile = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/siggrid-private-synthesized-"
				+ r + "-" + i + ".csv";
		PrintWriter f2 = new PrintWriter(new FileWriter(
				synthesizedPointsLabelFile));
		f2.println("x-value,y-value,class");
		int pclusterid = 1;
		HashMap<Integer, Integer> pidmap = new HashMap<Integer, Integer>();

		String noisyCountForGrids = "E:/D_disk/workspace/PrivateWaveCluster1CallKDTree/data_experiments/private-wdataMatrix-"
				+ exeID + ".txt";
		BufferedReader reader6 = new BufferedReader(new FileReader(
				noisyCountForGrids));

		while ((line = reader4.readLine()) != null) {
			String[] split = line.split(" ");
			int clusterlabel = Integer.parseInt(split[1]);
			String qline = reader5.readLine();
			double noisycount = Double.parseDouble(reader6.readLine()) * 2;
			if (clusterlabel == 0) // remove noisy points
				continue;
			String[] linesplit = qline.split(",");
			int xmin = Integer.parseInt(linesplit[0].trim());
			int ymin = Integer.parseInt(linesplit[1].trim());
			int xmax = Integer.parseInt(linesplit[2].trim());
			int ymax = Integer.parseInt(linesplit[3].trim());

			int roundcount = (int) Math.round(noisycount);
			if (roundcount > 0) {
				if (!pidmap.containsKey(clusterlabel)) {
					pidmap.put(clusterlabel, pclusterid);
					pclusterid++;
				}

				SynthesizePoints(roundcount, delta, xmin, ymin, xmax, ymax,
						pidmap.get(clusterlabel), f2);
			}

		}

		reader4.close();
		reader5.close();
		reader6.close();
		f2.close();

		return pclusterid - 1;
	}

	public static void SynthesizePoints(int count, double delta, int xmin,
			int ymin, int xmax, int ymax, int cluster_label, PrintWriter out) {

		if (count > 0) {
			for (int i = 0; i < count; i++) {
				Tuple t = UniformRandom.getRandomTuple(xmin, xmax, ymin, ymax,
						delta);

				out.println(t.toString().trim() + ("C" + cluster_label).trim());
				// System.out.println(t.toString());
			}
		}

	}
}