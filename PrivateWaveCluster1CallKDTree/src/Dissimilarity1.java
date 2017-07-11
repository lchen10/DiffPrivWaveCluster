import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Dissimilarity1 {

	public static HashMap<Integer, Integer> clusters(String filename)
			throws IOException {
		// "E:/cluster.txt"
		FileInputStream in = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		String[] mystring = new String[2];

		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		int key = 0;
		// String value = null;
		ArrayList<Integer> value = new ArrayList<Integer>();

		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			mystring = strLine.split("\\s+");
			// System.out.println("mystring length is: " + mystring.length);

			double pointID = Double.parseDouble(mystring[0]);
			int ptIDint = (int) pointID;
			// String pointIDstring = Integer.toString(ptIDint);
			// System.out.println("pointID is: " + ptIDint);
			double clusterID = Double.parseDouble(mystring[1]);
			if (clusterID > 0) {
				clusterID = 1;
			}
			map.put(ptIDint, (int) clusterID);

		}
		in.close();

		return map;

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		FileWriter fstream = new FileWriter(
				"E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/Dissimilarity_1.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		
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

		int exeTime = 10;
		
		double result = 0.0;
		
		double result_sum = 0.0;
		
		for (Entry<String, String> entry : filemap.entrySet()) {
//			boolean isTransposed = false;		
			
			String trueFileName = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/" + entry.getKey();
			String privateFileName = "E:/D_disk/workspace/PrivateWaveCluster1/data_experiments/" + entry.getValue();
			out.write("true file: " + trueFileName+"\n");
			out.write("private file: " + privateFileName+"\n");
			out.write("\n");
			// System.out.println("=============================================================\n");
			// out.write("true: " + trueFileName + "\n");
			// out.write("private: " + privateFileName + "\n");
			//
			HashMap<Integer, Integer> trueClusters = clusters(trueFileName);
			HashMap<Integer, Integer> privateClusters = clusters(privateFileName);

			int sizeOfTrue = 0;
			int unionOfTF = 0;
			int intersectionOfTF = 0;

			for (Entry<Integer, Integer> trueEntry : trueClusters.entrySet()) {
				boolean trueSignificant = trueEntry.getValue() == 1 ? true : false;
				boolean privateSignificant = privateClusters.get(trueEntry.getKey()) == 1 ? true : false;
				
				if (trueSignificant) {
					sizeOfTrue++;
				}

				if(trueSignificant || privateSignificant){
					unionOfTF++;
				}
				
				if(trueSignificant && privateSignificant){
					intersectionOfTF++;
				}
				
			}
			
			int numerator = unionOfTF - intersectionOfTF;

			result = (unionOfTF - intersectionOfTF) / (double)sizeOfTrue;
			
			out.write("sizeOfTrue is: " + sizeOfTrue +"\n");
			out.write("unionOfTF is: " + unionOfTF +"\n");
			out.write("intersectionOfTF is: " + intersectionOfTF +"\n");
			
			double delta = unionOfTF - intersectionOfTF;
			out.write("(unionOfTF - intersectionOfTF) is: " + delta +"\n");
			out.write("((S_True Union S_private)- (S_True Intersection S_Private))/S_True is: " + result +"\n");
			// out.write("\n");
			// out.write("the size of true clusters: " + trueClusters.size() +
			// "\n");
			// out.write("the size of private clusters: " +
			// privateClusters.size() + "\n");
			//
			// out.write("\n");
			out.write("\n");
			out.write("\n");
			out.write("\n");
			out.write("\n");
			
			result_sum += result;

		}
		
		double Avg_dissimilarity_1 = result_sum / exeTime;
		out.write("\n");
		out.write("\n");
		out.write("============================================================================");
		out.write("\n");
		out.write("Avg_dissimilarity_1 is: " + Avg_dissimilarity_1);
		
		out.flush();
		out.close();


	}

}
