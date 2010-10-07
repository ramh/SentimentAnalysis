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
	
	public void setupAttributes(List<String> tags)
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
			for(int fnum = 0;fnum < tweets.size(); fnum+=100) {
				out = new PrintWriter(new BufferedWriter(new FileWriter("twitterlines" + fnum/100)));
				for(int i=0;i<100 && fnum + i < tweets.size();i++) {
					Tweet t = tweets.get(fnum + i);
					String text = t.text;
					text = text.replaceAll("\\@\\p{Graph}*", "");
					text = text.replaceAll("\\p{Graph}*\\d\\p{Graph}*", "");
					text = text.replaceAll("\\p{Punct}", "");
					text = text.replaceAll("\\s+", " ");
					text = text.trim();
					out.println(text);
				}
				out.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}
/*
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
*/
		
			ArrayList<String> tags = new ArrayList<String>();
			for(int fnum = 0;fnum < tweets.size(); fnum+=100) {
				boolean filenotfound = false;
				BufferedReader in = null;
				try {
					in = new BufferedReader(new FileReader("twitterlines" + fnum/100 + ".simpparse"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				for(int i=0;i<100 && fnum + i < tweets.size();i++) {
					String firstline = null;
					try {
						firstline = in.readLine();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(firstline.length() == 0) {
						filenotfound = true;
						continue;
					}
					else {
						String[] tagsplit = firstline.split(",");
						for(String tag : tagsplit) {
							if(!tags.contains(tag))
								tags.add(tag);
						}
					}
				}
			}
			if(attrs == null)
				setupAttributes(tags);
			
			Instances feats = new Instances("Contextual Features", attrs, tweets.size());
			
			try {
				for(int fnum = 0;fnum < tweets.size(); fnum+=100) {
					boolean emptyfile = false;
					BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader("twitterlines" + fnum/100 + ".simpparse"));
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					for(int i=0;i<100 && fnum + i < tweets.size();i++) {
						Tweet t = tweets.get(i + fnum);
						String firstline = in.readLine();
						if(firstline.length() == 0) {
							emptyfile = true;
						}
						
						Instance inst = new Instance(1.0, new double[attrs.size()]);
						inst.setDataset(feats);

						inst.setValue(0, t.sentiment);

						if(!emptyfile) {
							String line = in.readLine();
							if(line.length() > 0) {
								String[] counts = line.split(",");
		
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
						}
					}
				}
				return feats;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		return null;
	}
	
	public static void main(String[] args) {
        ArrayList<Tweet> tweets = TweetFileParser.parseFile("data/train.40000.2009.05.25");
        SemanticFeatureExtractor sfe = new SemanticFeatureExtractor();
        sfe.extractFeatures(tweets);
	}
}

