import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class CleanData {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		FileInputStream in = new FileInputStream(
				"E:/D_disk/workspace/BaselineWaveCluster/dataset/AdaptiveGrids/spiral/Spiral-adaptive-grids-10.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		FileWriter fstream = new FileWriter(
				"E:/D_disk/workspace/BaselineWaveCluster/dataset/AdaptiveGrids/spiral/Spiral-AdaptiveGrids-10.csv");
		BufferedWriter out = new BufferedWriter(fstream);

		String strLine = null;
		while ((strLine = br.readLine()) != null) {

			strLine = strLine.trim();
			String[] split = strLine.split(",");
			double d1 = Double.parseDouble(split[0]);
			double d2 = Double.parseDouble(split[1]);
			out.write(d1 + ", " + d2 + "\n");
		}
		
		in.close();
		br.close();
		out.close();
		
		
		

	}

}
