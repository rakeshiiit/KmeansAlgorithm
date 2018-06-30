package k.means;
import java.io.BufferedReader; 
import java.io.IOException; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * K-Means Algorithm
 * @author User
 */
public class KMeans {

	private static ArrayList<ArrayList<HashMap<String,Object>>> clusters = new ArrayList<ArrayList<HashMap<String,Object>>>();
	private static ArrayList<HashMap<String,Object>> incrementalData = new ArrayList<HashMap<String,Object>>();
	private static ArrayList<HashMap<String,Object>> centroids;
	private static HashMap<String,Object> singleRecord;
	private static ArrayList<HashMap<String,Object>> readFromCSV(String fileName) 
	{ 
		ArrayList<HashMap<String,Object>> dataSet = new ArrayList<HashMap<String,Object>>();
		Path pathToFile = Paths.get(fileName);
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) 
		{ 
			// read the first line from the text file 
			String line = br.readLine(); 
			String[] columnNames = null;
			String[] datatype = null;
			int typeflag = 0;
			// loop until all lines are read 
			if(line != null)
			{
				columnNames = line.split(",");
				line = br.readLine();
			}
			while (line != null) { 

				//System.out.println(line);
				String[] attributes = line.split(","); 
				int length = attributes.length;
				datatype = new String[length];
				singleRecord = new HashMap<String,Object>(columnNames.length);
				for(int i = 0;i<length;i++)
				{
					singleRecord.put(columnNames[i],attributes[i]);
				}
				dataSet.add(singleRecord);
				line = br.readLine(); 
			}
			//clusters.add(dataSet);
			return dataSet;
		} 
		catch (IOException ioe) { 
			ioe.printStackTrace();
			return null;
		}

	}

	public static void KMeansAlgorithm(int k)
	{
		ArrayList<HashMap<String,Object>> dataSet = clusters.get(0);
		ArrayList<ArrayList<HashMap<String,Object>>> newClusters;
		centroids = randomClusterSelection(clusters,k);
		int m = 100;
		while(m>0)
		{
			newClusters = new ArrayList<ArrayList<HashMap<String,Object>>>();
			for(int i = 0; i < k; i++)
			{
				ArrayList<HashMap<String,Object>> clusterData = new ArrayList<HashMap<String,Object>>();
				clusterData.add(centroids.get(i));
				newClusters.add(clusterData);
			}
			for(int i = 0;i<dataSet.size() - k;i++)
			{
				int toCluster = findCluster(dataSet.get(i),centroids);
				newClusters.get(toCluster).add(dataSet.get(i));
			}
			System.out.println("==============================");
			printClusters(newClusters);
			clusters = newClusters;
			centroids = calculateCluster();
			m--;


		}

	}

	public static ArrayList<HashMap<String,Object>> calculateCluster()
	{

		ArrayList<HashMap<String,Object>> centroids = new ArrayList<HashMap<String,Object>>();
		for(int i=0; i<clusters.size(); i++)
		{
			HashMap<String,Object> clusterData = new HashMap<String,Object>();
			ArrayList<HashMap<String,Object>> clustData = clusters.get(i);
			for(int j=0; j<clustData.size(); j++)
			{
				HashMap<String,Object> tempData = clustData.get(j);
				for (Map.Entry<String, Object> entry : tempData.entrySet()) {

					//System.out.println(key + " : " + value);
					if(!entry.getKey().equals("STATION") && !entry.getKey().equals("STATION_NAME"))
					{
						String key = entry.getKey();
						Double value = Double.parseDouble(entry.getValue().toString());
						if(clusterData.containsKey(key))
						{
							clusterData.put(key,Double.parseDouble(clusterData.get(key).toString()) + value);
						}
						else
						{
							clusterData.put(key,value);
						}

					}
					else
					{
						clusterData.put(entry.getKey(), entry.getValue());
					}
				}

			}
			for (Map.Entry<String, Object> entry : clusterData.entrySet()) {
				if(!entry.getKey().equals("STATION") && !entry.getKey().equals("STATION_NAME"))
				{
					clusterData.put(entry.getKey(), Double.parseDouble(entry.getValue().toString())/clustData.size());
				}


			}
			centroids.add(clusterData);    		
		}

		return centroids;


	}


	public static void printClusters(ArrayList<ArrayList<HashMap<String,Object>>> newClusters)
	{
		for(int i = 0; i < newClusters.size(); i++)
		{

			System.out.println(newClusters.get(i).size());
		}
	}

	public static int findCluster(HashMap<String,Object> data,ArrayList<HashMap<String,Object>> centroids)
	{
		int minIndex = 0;double minMean = Double.MAX_VALUE;
		for(int i = 0;i<centroids.size();i++)
		{
			HashMap<String,Object> clusterData = centroids.get(i);
			double temp = calculateMeans(data,clusterData);
			if(minMean > temp)
			{
				minMean = temp;
				minIndex = i;
			}

		}

		return minIndex;
	}

	public static double calculateMeans(HashMap<String,Object> p1,HashMap<String,Object> p2)
	{
		double ssd = 0,diff = 0;
		for (Map.Entry<String, Object> entry : p1.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			//System.out.println(key + " : " + value);
			if(!entry.getKey().equals("STATION") && !entry.getKey().equals("STATION_NAME"))
			{
				diff = Double.parseDouble(entry.getValue().toString()) - Double.parseDouble(p2.get(entry.getKey()).toString());
				ssd += (diff*diff);
			}
		}

		return ssd;

	}
	public static ArrayList<HashMap<String,Object>> randomClusterSelection(ArrayList<ArrayList<HashMap<String,Object>>> clusters,int k)
	{
		ArrayList<HashMap<String,Object>> dataSet = clusters.get(0);
		ArrayList<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		Random randomGenerator = new Random();
		int next,length;
		length = dataSet.size();
		for(int i=0;i<k;i++)
		{

			next = randomGenerator.nextInt(dataSet.size() - k);
			HashMap<String,Object> temp = dataSet.get(next);
			dataSet.set(next,dataSet.get(dataSet.size() - k));
			dataSet.set(dataSet.size() - k,temp);
			result.add(temp);
			//System.out.println("======================================================");
			//System.out.println(next);
			//printDataSetItem(next);
		}

		return result;
	}

	public static void printDataSetItem(int index)
	{
		HashMap<String,Object> temp = clusters.get(0).get(index);
		for (Map.Entry<String, Object> entry : temp.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			System.out.println(key + " : " + value);
		}
	}

	public static void printDataSet()
	{
		ArrayList<HashMap<String,Object>> dataSet = clusters.get(0);
		Iterator<HashMap<String,Object>>iterator = dataSet.iterator();
		while (iterator.hasNext()) {
			HashMap<String,Object> temp = iterator.next();
			for (Map.Entry<String, Object> entry : temp.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				//System.out.println(key + " : " + value);

			}
			System.out.println("");


		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// TODO code application logic here
		ArrayList<HashMap<String,Object>> dataSet = readFromCSV("C:\\Users\\champ\\Downloads\\cleanbook.csv");
		clusters.add(dataSet);
		//printDataSet();
		KMeansAlgorithm(2);
		incrementalData = readFromCSV("C:\\Users\\champ\\Downloads\\incremental.csv");
		Iterator<HashMap<String,Object>>iterator = incrementalData.iterator();
		while (iterator.hasNext()) {
			HashMap<String,Object> temp = iterator.next();
			for (Map.Entry<String, Object> entry : temp.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(key + " : " + value);

			}
			int toCluster = findCluster(temp,centroids);
			System.out.println("alotted to cluster  " + toCluster);
	}
}

}