import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;


public class ContextualFeatureExtractor implements FeatureExtractor{
	private static final int NUMBASEATTR = 1;
	private static final int[] HOURCATEGORY = {3,3,3,3,0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2,3,3};
	private static final String[] TIMENAME = {"Morning","Afternoon","Evening","Night"};
	private static final int MINEMOTCOUNT = 100;
	
	private FastVector attrs;
	private Pattern emotpat;
	private ArrayList<String> emotslist;
	
	public void setupAttributes(List<Tweet> tweets)
	{
		attrs = new FastVector();
		// Determine attributes
		
		FastVector hourvals = new FastVector();
		hourvals.addElement(TIMENAME[0]); hourvals.addElement(TIMENAME[1]);
		hourvals.addElement(TIMENAME[2]); hourvals.addElement(TIMENAME[3]);
		Attribute timeofday = new Attribute("TimeOfDay", hourvals);
		attrs.addElement(timeofday);
		
		// Find frequent emoticons
		emotpat = Pattern.compile("\\p{Graph}*\\p{Punct}\\p{Graph}*");
		HashMap<String,Integer> emots = new HashMap<String,Integer>(100);
		for(Tweet t: tweets)
		{
			Matcher emotmat = emotpat.matcher(t.text);
			while (emotmat.find())
			{
				String curemot = emotmat.group();
				if(curemot.length() > 1 && curemot.length() < 5) {
					if(emots.containsKey(curemot)) {
						int curcount = emots.get(curemot);
						curcount++;
						emots.put(curemot, curcount);
					}
					else
						emots.put(curemot, 1);
				}
			}
		}
		Set<Map.Entry<String,Integer>> emotset = emots.entrySet();
		Set<String> emotrem = new HashSet<String>(100);
		for(Map.Entry<String,Integer> emotmap : emotset)
		{
			if(emotmap.getValue() < MINEMOTCOUNT)
				emotrem.add(emotmap.getKey());
		}
		Set<String> goodemots = emots.keySet();
		goodemots.removeAll(emotrem);
		emotslist = new ArrayList<String>();
		for(String emot : goodemots)
		{
			Attribute attr = new Attribute("confeature:" + emot);
			attrs.addElement(attr);
			emotslist.add(emot);
		}
	}

	public Instances extractFeatures(List<Tweet> tweets)
	{
		if(attrs == null)
			setupAttributes(tweets);
		Instances feats = new Instances("Contextual Features", attrs, tweets.size());
		feats.setClassIndex(0);
		// Record features
		
		for(Tweet t: tweets)
		{
			Instance inst = new Instance(1.0, new double[attrs.size()]);
			inst.setDataset(feats);

			int hrcat = HOURCATEGORY[t.hour];
			inst.setValue(0, TIMENAME[hrcat]);
			
			Matcher emotmat = emotpat.matcher(t.text);
			while (emotmat.find())
			{
				String curemot = emotmat.group();
				if(emotslist.contains(curemot))
				{
					int attrind = emotslist.indexOf(curemot)+NUMBASEATTR;
					//double val = inst.value(attrind);
					//val += 1.0;
					inst.setValue(attrind, 1.0);
				}
				
			}			
			
			feats.add(inst);
		}
		
		 ArffSaver saver = new ArffSaver();
		 saver.setInstances(feats);
		 try {
			saver.setFile(new File("output/contextual.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return feats;
	}
	
	public static void main(String[] args) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile("D:\\homework\\nlp\\SentimentAnalysis\\src\\data\\train.40000.2009.05.25");
		ContextualFeatureExtractor cfe = new ContextualFeatureExtractor();
		Instances insts = cfe.extractFeatures(tweets);
		System.out.println(insts.toSummaryString());
		//System.out.println(insts.toString());
	}
}
