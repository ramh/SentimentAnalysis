import java.util.Calendar;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class ContextualFeatureExtractor {
	private int[] HOURCATEGORY = {3,3,3,3,0,0,0,0,0,0,1,1,1,1,1,1,2,2,2,2,2,2,3,3};
	private String[] TIMENAME = {"Morning","Afternoon","Evening","Night"};
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
			inst.setDataset(feats);
			feats.add(inst);
		}
		
		
		return feats;
	}
}
