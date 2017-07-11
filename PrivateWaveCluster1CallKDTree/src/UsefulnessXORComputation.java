import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import weka.core.Instances;

public class UsefulnessXORComputation {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		File outputFolder = new File("E:/D_disk/workspace/PrivateWaveCluster1/data_experiments");
		String[] files = outputFolder.list();
		HashMap<String, String> filemap = new HashMap<String, String>();
		for (String file : files) {
			if (file.startsWith("siggrid-true")) {
				String subfix = file.substring(12);
				String pairFilename = "siggrid-private" + subfix;
				filemap.put(file, pairFilename);
			}
		}
		
		String matlabInputfile = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/accuracyXORComputation.txt";
		PrintWriter f0 = new PrintWriter(new FileWriter(matlabInputfile));

		double AccuracyRatioTotal = 0;
		int pairCount = 0;
		for (Entry<String, String> entry : filemap.entrySet()) {

			String trueFileName = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/" + entry.getKey();
			String privateFileName = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/" + entry.getValue();
			f0.println("=============================================================\n");
			f0.println("true: " + trueFileName + "\n");
			f0.println("private: " + privateFileName + "\n");

			BufferedReader reader1 = new BufferedReader(new FileReader(
					trueFileName));
			BufferedReader reader2 = new BufferedReader(new FileReader(
					privateFileName));

			int IsSameClassOrnot = 0;
			ArrayList<String> trueLabels = new ArrayList<String>();
			ArrayList<Integer> trueLabelsMapping = new ArrayList<Integer>();

			String line = null;
			int count2 = 0;
			while ((line = reader1.readLine()) != null) {
				count2++;
				if(count2 > 101)
				{
					String[] split = line.split(" ");
					// f0.println(split[0] + "," + split[1]);
					trueLabels.add(split[1]);
				}
				
			}

			for (int i = 0; i < trueLabels.size(); i++) {
				for (int j = i + 1; j < trueLabels.size(); j++) {
					if (trueLabels.get(i).equals(trueLabels.get(j))) {
						IsSameClassOrnot = 1;
						trueLabelsMapping.add(IsSameClassOrnot);
					} else {
						IsSameClassOrnot = 0;
						trueLabelsMapping.add(IsSameClassOrnot);
					}
				}
			}

			// for(int i = 0; i < trueLabelsMapping.size(); i++)
			// {
			// System.out.print(trueLabelsMapping.get(i) + ", ");
			// }

			ArrayList<String> privateLabels = new ArrayList<String>();
			ArrayList<Integer> privateLabelsMapping = new ArrayList<Integer>();

			int count1 = 0;
			while ((line = reader2.readLine()) != null) {
				count1++;
				if(count1 > 101)
				{
					String[] split = line.split(" ");
					// f0.println(split[0] + "," + split[1]);
					privateLabels.add(split[1]);
				}
				
			}

			for (int i = 0; i < privateLabels.size(); i++) {
				for (int j = i + 1; j < privateLabels.size(); j++) {
					if (privateLabels.get(i).equals(privateLabels.get(j))) {
						IsSameClassOrnot = 1;
						privateLabelsMapping.add(IsSameClassOrnot);
					} else {
						IsSameClassOrnot = 0;
						privateLabelsMapping.add(IsSameClassOrnot);
					}
				}
			}

			// System.out.println(" ");
			// for(int i = 0; i < privateLabelsMapping.size(); i++)
			// {
			// System.out.print(privateLabelsMapping.get(i) + ", ");
			// }

			ArrayList<Integer> XOR = new ArrayList<Integer>();
			int xor = 0;
			int count = 0;
			for (int i = 0; i < trueLabelsMapping.size(); i++) {
				if (trueLabelsMapping.get(i) == privateLabelsMapping.get(i)) {
					// System.out.println("yes" + i);

					xor = 0;
					XOR.add(xor);
				} else {
					xor = 1;
					XOR.add(xor);
					count++;
				}
			}

			// for(int i = 0; i < XOR.size(); i++)
			// {
			// System.out.println("XOR is: " + XOR.get(i));
			// }

			f0.println(" count is: " + count);

			int total = XOR.size();
			f0.println(" total is: " + total);
			double ratio = (double) count / (double) total;
			f0.println("count ratio is: " + ratio);
			double XORratio = 1- ratio;
			f0.println("XORratio is: " + XORratio);
			
			pairCount++;
			AccuracyRatioTotal += ratio;
		}
		
		double avgAccuracyRatio = AccuracyRatioTotal / (double)pairCount;
		double avgXORAccuracyRatio = 1 - avgAccuracyRatio;
		
		f0.println("********************************************************\n");
		f0.println("avg. accuracy is: " + avgAccuracyRatio);
		f0.println("avgXORAccuracyRatio is: " + avgXORAccuracyRatio);
		f0.close();
		
		
	}

}
