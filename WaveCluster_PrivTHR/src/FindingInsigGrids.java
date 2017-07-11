import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FindingInsigGrids {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String trainGridFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/Grid2Point-siggrid-true-20%-16-15-clusters-0-1.csv";
		String WekaValidateFile = "E:/D_disk/workspace/PrivateWaveCluster1_improved/data_experiments/wekaclassfile-true-0-1.txt";
		
		String label = LocateInsigGrid(trainGridFile, WekaValidateFile);
		System.out.println("label is: " + label);
		
	}
	
	public static String LocateInsigGrid(String TrainGrids, String WekaValidateFile) throws IOException
	{
		BufferedReader reader1 = new BufferedReader(new FileReader(
				TrainGrids));
		String line1 = reader1.readLine(); // skip the first line
		ArrayList<ArrayList<Integer>> InSigGridArray = new ArrayList<ArrayList<Integer>> ();
		
		while((line1 = reader1.readLine()) != null)
		{
			String[] split = line1.split(",");
			int x = Integer.parseInt(split[0].trim());
			int y = Integer.parseInt(split[1].trim());
			String label = split[2].trim();
			System.out.println("label is !!!!:  " + label);
			if(label.equals("C0"))
			{
				ArrayList<Integer> coordinate = new ArrayList<Integer>();
				coordinate.add(x);
				coordinate.add(y);
				InSigGridArray.add(coordinate);	
			}
		}
		
		
		BufferedReader reader2 = new BufferedReader(new FileReader(
				WekaValidateFile));
		String line2 = null;
		
		Map<String, Integer> LabelMap = new HashMap<String, Integer>();
		
		int max = 0;
		String maxLabel = null;
		
		while((line2 = reader2.readLine()) != null)
		{
			System.out.println("start");
			String[] split = line2.split(",");
			int x = (int)Double.parseDouble(split[0].trim());
			int y = (int)Double.parseDouble(split[1].trim());
			String label = split[3].trim();
			System.out.println("x is: " + x);
			System.out.println("y is: " + y);
//			System.out.println("come ");
			for(int i = 0; i < InSigGridArray.size(); i++)
			{
//				System.out.println("Hey");
				int gridX = InSigGridArray.get(i).get(0);
				System.out.println("gridX is: " + gridX);
				int gridY = InSigGridArray.get(i).get(1);
				System.out.println("gridY is: " + gridY);
				
				int value = 0;
				if(x == gridX && y == gridY)
				{
					if(!LabelMap.containsKey(label))
					{
						value = 1;
						LabelMap.put(label, value);
						System.out.println("come out");
					}
					else
					{
						System.out.println("come in");
						value = LabelMap.get(label);
						value++;
						LabelMap.put(label, value);
						
						
					}
						
					if(value > max)
					{
						max = value;
						maxLabel = label;
					}
				}
			}
		}
		
		for(Map.Entry me:LabelMap.entrySet())
		{
			System.out.println("Key is: " + me.getKey() + ", " + me.getValue());
		}
		
		
		 
		
		
		reader1.close();
		reader2.close();
		return maxLabel;
	}

}
