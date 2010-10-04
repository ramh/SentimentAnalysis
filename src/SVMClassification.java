import java.util.ArrayList;

import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Instances;


public class SVMClassification {
	
	private SMO classifiers[] = new SMO[4];
	private Instances insts[] = new Instances[4];
	
	public void train(String fileloc) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		
		
		insts[2] = ContextualFeatureExtractor.extractFeatures(tweets);
		classifiers[2] = new SMO();
		classifiers[2].setNumFolds(10);
		try {
			classifiers[2].buildClassifier(insts[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test(String fileloc)
	{
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		Instances tests = ContextualFeatureExtractor.extractFeatures(tweets);
		
		for(int i=0;i<tests.numInstances();i++) {
			try {
				double cld = classifiers[2].classifyInstance(tests.instance(i));
				Attribute attr = tests.attribute(0);
				String cl = attr.value((int) cld);
				System.out.println(cl);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SVMClassification svmc = new SVMClassification();
		svmc.train("D:\\homework\\nlp\\SentimentAnalysis\\src\\data\\train.40000.2009.05.25");
		svmc.test("D:\\homework\\nlp\\SentimentAnalysis\\src\\data\\testdata.manual.2009.05.25");
	}

}
