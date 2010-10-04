import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class ARFFCreator {

	public static void main(String[] args) throws Exception {

		final String PREFIX = "tweet_";
		final double p = 0.9;   // probability of a tweet to be assigned to the training set


		Random random = new Random();

		System.out.println("Loading words in a Map");
		Map<String, Integer> wordId = new HashMap<String, Integer>();
		FileReader fr1 = new FileReader("wordFrequenciesAllTweets.baseline");
		BufferedReader br1 = new BufferedReader(fr1);
		String line1 = null;
		while ((line1 = br1.readLine()) != null) {
			if (line1.length() == 0)
				continue;
			String[] words = line1.split(",");

			if (words[0].length() == 0)
				continue;

			wordId.put(words[0], Integer.parseInt(words[1])); 
		}
		br1.close();
		fr1.close();

		List results;

		int count = 0;
		FileWriter fwT = null;  // training dataset
		FileWriter fwV = null;  // validation dataset

		fwT = new FileWriter("datasets/" + PREFIX + "_Tr.arff");
		fwT.write("@relation TweetCategory\n\n");
		for (int i = 0; i < wordId.size(); i++)
			fwT.write("@attribute Att" + i + " numeric\n");

		fwT.write("@attribute Class { 1,2 }\n");
		fwT.write("\n@data\n");
		fwT.flush();

		fwV = new FileWriter("datasets/" + PREFIX + "_Val.arff", true);

		results = getIDandWords();

		for (Object val : results) {

			// decides whether to add this instance to the training or validation set
			FileWriter fw = null;
			if (random.nextDouble() < p)
				fw = fwT;
			else
				fw = fwV;

			Map<String, Double> wordFrequency = new HashMap<String, Double>();
			Vector vector = (Vector)val;
			Integer id = (Integer)vector.get(0);


			count++;

			if (count % 50 == 0)
				System.out.println(count);



			String body = (String)vector.get(1);
			String[] words = body.split(" ");

			//  read the tweet strings
			for (String word : words) {
				Double freq = wordFrequency.get(word);
				if (freq == null) 
					wordFrequency.put(word, new Double(1));
				else {
					freq++;
					wordFrequency.put(word, freq);
				}
			}

			//normalize frequencies
			Double wordCount = new Double(wordFrequency.size());
			for (Map.Entry<String, Double> entry : wordFrequency.entrySet()) {
				Double normalizedFreq = entry.getValue() / wordCount;
				entry.setValue(normalizedFreq);
			}

			StringBuffer buffer = new StringBuffer();

			buffer.append("{");

			// write the instance values
/*			List<IndexValueHolder> indexValues = new LinkedList<IndexValueHolder>();
			for (Map.Entry<String, Double> entry : wordFrequency.entrySet()) {
				Integer index = wordId.get(entry.getKey());
				if (index != null)  
					indexValues.add(new IndexValueHolder(index, entry.getValue()));
			}
			Collections.sort(indexValues);
			for (IndexValueHolder indexValue : indexValues)
				buffer.append("" + indexValue.index + " " + indexValue.value + ", ");*/

			// writes the category id
			buffer.append("" + wordId.size() + " " + id + "}\n");

			fw.write(buffer.toString());
			wordFrequency = null;
			vector = null;

		}

		fwT.flush();
		fwV.flush();
		fwT.close();
		fwV.close();        

	}


	private static List<Vector<String>> getIDandWords() {
		List<Vector<String>> values = new LinkedList<Vector<String>>(); 
		// read the data file and load it in a list of vectors
		File f = new File("out/tweetCategories.csv");
		try { 
			BufferedReader in = new BufferedReader(new FileReader(f)); 
			String str; 
			Integer i=0;
			Vector<String> v;
			while ((str = in.readLine()) != null) 
			{        
				// read the tweet files with frowny/smiley category and the strings occurring in the tweet 
				// use NLP tools extract as many features as you wish

			} 
			in.close(); 
		} catch (IOException e) { e.printStackTrace();} 

		return values;

	}


}
