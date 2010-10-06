import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class BaselineFeatureExtractor implements FeatureExtractor {	
	private FastVector attrs;
	private FastVector frequent_words;
	private static final int OFFSET = 1;
	
	private void setupAttributes(List<Tweet> tweets)
	{
		
		attrs = new FastVector();
		// Determine attributes
		
		FastVector sentvals = new FastVector();
		sentvals.addElement("Negative"); sentvals.addElement("Neutral"); sentvals.addElement("Positive"); 
		Attribute sentclass = new Attribute("Sentiment", sentvals);
		attrs.addElement(sentclass);
		
		frequent_words = get_frequent_words(tweets);
		for(int i=0; i<frequent_words.size(); i++)
		{
			String word = (String) frequent_words.elementAt(i);
			Attribute attr = new Attribute(word);
			attrs.addElement(attr);
		}
	}
		
	public Instances extractFeatures(List<Tweet> tweets)
	{
		if(attrs == null)
			setupAttributes(tweets);
		
		Instances feats = new Instances("Baseline Features", attrs, tweets.size());
		feats.setClassIndex(0);
		// Record features
		
		StringTokenizer st;
		String word;
		for(Tweet t: tweets)
		{
			Instance inst = new Instance(1.0, new double[attrs.size()]);
			inst.setDataset(feats);
			inst.setValue(0, t.sentiment);
			st = new StringTokenizer(t.text);
			while(st.hasMoreElements()) {
				word = (String) st.nextElement();
				if(frequent_words.contains(word))
					inst.setValue(frequent_words.indexOf(word)+OFFSET, 1);
			}
			feats.add(inst);
		}
		
		 ArffSaver saver = new ArffSaver();
		 saver.setInstances(feats);
		 try {
			saver.setFile(new File("output/baseline.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return feats;
	}
	
	private static FastVector get_frequent_words(List<Tweet> tweets)
	{
		HashMap<String, Integer> word_frequency = new HashMap<String, Integer>();
		Iterator<Tweet> itr = tweets.iterator();
		Tweet current_tweet;
		String text;
		StringTokenizer wt;
		String word;
		int count = 0;
		while (itr.hasNext()) {
			current_tweet = itr.next();
			text = current_tweet.text;
			wt = new StringTokenizer(text);
			while(wt.hasMoreTokens()) {
				word = wt.nextToken();
				count++;
				if(!word_frequency.containsKey(word))
					word_frequency.put(word, 1);
				else 
					word_frequency.put(word, word_frequency.get(word)+1);
			}
		}
		
		// Sort the Hashmap values to get max count values
		HashMap<String, Integer> sorted_word_frequency = sortHashMapOnValues(word_frequency);
		
		// Get array of 5000 max count words
		Collection<String> c = sorted_word_frequency.keySet();
		Iterator<String> it = c.iterator();
		FastVector frequent_words = new FastVector();
		//ArrayList<String> frequent_words = new ArrayList<String>();
		while(it.hasNext()) {
			frequent_words.addElement((String) it.next());
		}
		
		return frequent_words;
	}
	
	// Sort HashMap based on Values
	private static HashMap<String, Integer> sortHashMapOnValues(HashMap<String, Integer> input) {
	    Map<String, Integer> tempMap = new HashMap<String, Integer>();
	    for (String wsState : input.keySet()){
	        tempMap.put(wsState,input.get(wsState));
	    }

	    List<String> mapKeys = new ArrayList<String>(tempMap.keySet());
	    List<Integer> mapValues = new ArrayList<Integer>(tempMap.values());
	    HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	    TreeSet<Integer> sortedSet = new TreeSet<Integer>(mapValues);
	    Object[] sortedArray = sortedSet.toArray();
	    int size = sortedArray.length;
	    for (int i=0; i<size; i++){
	        sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), 
	                      (Integer)sortedArray[i]);
	    }
	    return sortedMap;
	}
	
}
