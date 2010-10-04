import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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


public class ContextualFeatureExtractor {
	private static final int NUMBASEATTR = 2;
	private static final int[] HOURCATEGORY = {3,3,3,3,0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2,3,3};
	private static final String[] TIMENAME = {"Morning","Afternoon","Evening","Night"};
	private static final int MINEMOTCOUNT = 4;
	Instances extractFeatures(List<Tweet> tweets)
	{
		FastVector attrs = new FastVector();
		// Determine attributes
		
		FastVector sentvals = new FastVector();
		sentvals.addElement("Negative"); sentvals.addElement("Neutral"); sentvals.addElement("Positive"); 
		Attribute sentclass = new Attribute("Sentiment", sentvals);
		attrs.addElement(sentclass);
		
		FastVector hourvals = new FastVector();
		hourvals.addElement(TIMENAME[0]); hourvals.addElement(TIMENAME[1]);
		hourvals.addElement(TIMENAME[2]); hourvals.addElement(TIMENAME[3]);
		Attribute timeofday = new Attribute("TimeOfDay", hourvals);
		attrs.addElement(timeofday);
		
		// Find frequent emoticons
		Pattern emotpat = Pattern.compile("\\p{Graph}*\\p{Punct}\\p{Graph}*");
		HashMap<String,Integer> emots = new HashMap<String,Integer>(100);
		for(Tweet t: tweets)
		{
			Matcher emotmat = emotpat.matcher(t.text);
			while (emotmat.find())
			{
				String curemot = emotmat.group();
				int curcount = emots.get(curemot);
				curcount++;
				emots.put(curemot, curcount);
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
		ArrayList<String> emotslist = new ArrayList<String>();
		for(String emot : goodemots)
		{
			Attribute attr = new Attribute(emot);
			attrs.addElement(attr);
			emotslist.add(emot);
		}
		
		Instances feats = new Instances("Contextual Features", attrs, tweets.size());
		feats.setClass(sentclass);
		// Record features
		
		for(Tweet t: tweets)
		{
			Instance inst = new Instance(attrs.size());
			inst.setClassValue(t.sentiment);
			Calendar cal = Calendar.getInstance();
			cal.setTime(t.time);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int hrcat = HOURCATEGORY[hour];
			inst.setValue(timeofday, TIMENAME[hrcat]);
			
			Matcher emotmat = emotpat.matcher(t.text);
			while (emotmat.find())
			{
				String curemot = emotmat.group();
				if(emotslist.contains(curemot))
				{
					int attrind = emotslist.indexOf(curemot)+NUMBASEATTR;
					double val = inst.value(attrind);
					val += 1.0;
					inst.setValue(attrind, val);
				}
			}			
			
			
			inst.setDataset(feats);
			feats.add(inst);
		}
		
		
		return feats;
	}
}
