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

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

public class TestJ48Classification {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String TrainFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/Grid2Point-siggrid-true-50%-64-15-clusters-0-1.csv";
		String TestFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/data2gridForTestDataWLabel-0-1.csv";
		Instances trainCSV = DataSource.read(TrainFile);
		trainCSV.setClassIndex(trainCSV.numAttributes() - 1);

		Instances testCSV = DataSource.read(TestFile);
		testCSV.setClassIndex(testCSV.numAttributes() - 1);

		J48 cls = new J48(); // new instance of tree
		String[] options = new String[1];
		options[0] = "-U"; // -u represents unpruned tree
		cls.setOptions(options); // set the options

		cls.buildClassifier(trainCSV);

		Instance crew = testCSV.instance(0);
        double[] distributionForInstance = cls.distributionForInstance(crew);
//        System.out.println("distribution is: " + distributionForInstance);

        //output predictions
        // 
        System.out.println("starting printing.....");
        System.out.println("probability length is: " + distributionForInstance.length);
        for(int j=0; j<distributionForInstance.length; j++)
        {
            System.out.println("Probability of class "+
                                crew.value(0)+ ", " + crew.value(1) +  " : "+Double.toString(distributionForInstance[j]));
        }
		
        
        String matlabInputfile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/prediction.txt";
		PrintWriter f0 = new PrintWriter(new FileWriter(matlabInputfile));
        
        

		for (int i = 0; i < testCSV.numInstances(); i++) {
			double pred = cls.classifyInstance(testCSV.instance(i));
			System.out.print("ID: " + testCSV.instance(i).value(0));
			System.out.print(", actual: "
					+ testCSV.classAttribute().value(
							(int) testCSV.instance(i).classValue()));
			System.out.println(", pred: " + (int) pred);
			System.out.println(", predicted: "
					+ testCSV.classAttribute().value((int) pred));
			
			
//			f0.println("ID: " + testCSV.instance(i).value(0));
//			f0.println(", actual: "
//					+ testCSV.classAttribute().value(
//							(int) testCSV.instance(i).classValue()));
//			f0.println(", pred: " + (int) pred);
//			f0.println(", predicted: "
//					+ testCSV.classAttribute().value((int) pred));
			
			
			
			
			

//			double x = testCSV.instance(i).value(0);
//			double y = testCSV.instance(i).value(1);
			// out.print(x + ",");
			// out.print(y + ",");
			// out.print(test.classAttribute().value(
			// (int) test.instance(i).classValue())
			// + ",");
			// out.println(test.classAttribute().value((int) pred));
			
			 //load training instances
//	        Instances test=...

	    
//	        //decide which instance you want to predict
//	        int s1=2;

	        //get the predicted probabilities 
//	        cls.distributionForInstance(testCSV.instance(i));
			

//			String exeID = 0 + "-" + 1 ;
//			String trueTrainMergeLabelFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/15-clusters-train-true-"
//					+ 0 + "-" + 1 + "-cluster.csv";
//			String point2GridTestFileWLabel = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/data2gridForTestDataWLabel-" + exeID + ".csv"; 
//			
//			String testprocessfile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/LingChenTest.csv";
//			
//			RewriteClassLabelForCSVFile(TrainFile, TestFile, testprocessfile);
			
			
		}

	}
	
	public static void RewriteClassLabelForCSVFile(String trainFile,
			String testFile, String testprocessfile)
			throws FileNotFoundException, IOException {
		
		//先读取TrainFile, 建立一个hashmap, 对应按顺序读取的cluster label(cluster label为key)
		BufferedReader traincsvreader = new BufferedReader(new FileReader(
				trainFile));
		String trainLine = null;
		traincsvreader.readLine(); // skip the first line
		
		HashMap<String, Integer> idLabelMap = new HashMap<String, Integer>();
		int id = 0;
		while((trainLine = traincsvreader.readLine()) != null)
		{
			String[] trainsplit = trainLine.split(",");
			String trainLabel = trainsplit[2].trim();
			
			if(!idLabelMap.containsKey(trainLabel))
			{
				idLabelMap.put(trainLabel, id);
				id++;
			}
			
		}
		
		//为方便排序，将hashmap中的array装到ArrayList<ArrayList>>中，并且置cluster label为第二项
		ArrayList<ArrayList<Object>> trainLabels = new ArrayList<ArrayList<Object>> ();
		for(Map.Entry entry : idLabelMap.entrySet())
		{
			System.out.println("Key is: " + entry.getKey() + ", value is:" + entry.getValue());
			ArrayList<Object> map = new ArrayList<Object>();
			
			map.add(entry.getValue());
			map.add(entry.getKey());
			trainLabels.add(map);
		}
		
		//对arraylist<arraylist<>> 按照cluster label对应的id (0, 1,2,3)排序
		Collections.sort(trainLabels, new ArrayListComparator ());
		
		for(int t = 0; t < trainLabels.size(); t++)
		{
			ArrayList<Object> o = trainLabels.get(t);
			int x = (Integer) o.get(0);
			String y = (String) o.get(1);
			System.out.println("x is: " + x + ", y is: " + y);
		}
		
		//重写testFile,前面的clusterlabel按照顺序读取出来，剩下的赋?职
		BufferedReader testcsvreader = new BufferedReader(new FileReader(
				testFile));
		String line = null;
		testcsvreader.readLine(); // skip the first line
		PrintWriter f0 = new PrintWriter(new FileWriter(testprocessfile));
		f0.println("x-value,y-value,class");
		int clusterid = 0;
		while ((line = testcsvreader.readLine()) != null) {
			String[] split = line.split(",");
			if (clusterid < trainLabels.size()) {
				ArrayList<Object> mappedLabel = trainLabels.get(clusterid);
				String label = ((String) mappedLabel.get(1)).trim();
				f0.println(split[0] + "," + split[1] + "," + label);
			} else {
				f0.println(split[0] + "," + split[1] + ",?");
			}
			clusterid++;
		}
		
		traincsvreader.close();
		testcsvreader.close();
		f0.close();
	}
	
	
	static final class ArrayListComparator implements Comparator<ArrayList<Object>> {  
		  public int compare(ArrayList<Object> list1, ArrayList<Object> list2) {  
		    return ((Integer) list1.get(0)).compareTo((Integer) list2.get(0));  
		  }  
		}  
	
	
	
}
