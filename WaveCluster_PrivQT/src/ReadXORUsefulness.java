import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class ReadXORUsefulness {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileWriter fstream = new FileWriter(
				"E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/READ_accuracyXORComputation.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		
		BufferedReader reader = new BufferedReader(new FileReader("E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/accuracyXORComputation.txt"));
		String line = null;
//		reader.readLine(); // skip the first line
//		String matlabInputfile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/15-clusters-train-"
//				+ r + "-" + i + "-processed.csv";
//		PrintWriter f0 = new PrintWriter(new FileWriter(matlabInputfile));

		while ((line = reader.readLine()) != null) {
			if (line.startsWith("XORratio is") || line.startsWith("avgXORAccuracyRatio is"))
			{
				String[] split = line.split(":");
				String value = split[1].trim();
				out.write(value);
				out.write("\t");
			}
//			String[] split = line.split(",");
//			f0.println(split[0] + "," + split[1]);
		}
		reader.close();
		out.close();
//		f0.close();
		

	}

}
