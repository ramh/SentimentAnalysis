import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class SemanticFeatureExtractor implements FeatureExtractor{
	private static final int NUMBASEATTR = 1;

	private static final String ASSERTDIR = "/nethome/khawkins3/assert-v0.14b";
	
	private FastVector attrs;
	private ArrayList<String> tagslist;
	
	public void setupAttributes(String[] tags)
	{
		attrs = new FastVector();
		// Determine attributes
		
		FastVector sentvals = new FastVector();
		sentvals.addElement("Negative"); sentvals.addElement("Neutral"); sentvals.addElement("Positive"); 
		Attribute sentclass = new Attribute("Sentiment", sentvals);
		attrs.addElement(sentclass);
		
		tagslist = new ArrayList<String>();
		for(String tag : tags)
		{
			Attribute attr = new Attribute(tag);
			attrs.addElement(attr);
			tagslist.add(tag);
		}
	}

	public Instances extractFeatures(List<Tweet> tweets)
	{
		try {
			PrintWriter out;
			out = new PrintWriter(new BufferedWriter(new FileWriter("twitterlines.lines")));
			for(Tweet t : tweets)
				out.println(t.text);
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}

		String[] cmd = {"." + ASSERTDIR + "/semanticparsing", "twitterlines.lines"};
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			BufferedReader in = new BufferedReader(new FileReader("twitterlines.simpparse"));
			if(attrs == null)
				setupAttributes(in.readLine().split(","));
			else
				in.readLine();
			
			Instances feats = new Instances("Contextual Features", attrs, tweets.size());
			
			for(Tweet t: tweets)
			{
				Instance inst = new Instance(1.0, new double[attrs.size()]);
				inst.setDataset(feats);
				
				inst.setValue(0, t.sentiment);
				
				String[] counts = in.readLine().split(",");
				
				for(String count : counts)
				{
					String[] countsplit = count.split(":");
					String tag = countsplit[0];
					int freq = Integer.parseInt(countsplit[1]);
					if(tagslist.contains(tag))
					{
						int attrind = tagslist.indexOf(tag)+NUMBASEATTR;
						inst.setValue(attrind, (double) freq);
					}
					
				}			
				
				feats.add(inst);
			}
			
			return feats;
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
	
	public static void main(String[] args) {
	}
}

