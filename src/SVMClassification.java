import java.util.ArrayList;

import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Instances;


public class SVMClassification {
	private FeatureExtractor featexts[] = {new BaselineFeatureExtractor(),
										   new ContextualFeatureExtractor()};
	private SMO classifiers[] = new SMO[featexts.length];
	private Instances insts[] = new Instances[featexts.length];
	
	public void train(String fileloc) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		
		/* Full actual code
		for(int i=0;i<featexts.length;i++) {
			insts[i] = featexts[i].extractFeatures(tweets);
			if(i > 0)
				insts[i] = Instances.mergeInstances(insts[i], insts[i-1]);
			classifiers[i] = new SMO();
			String[] opts = {"-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0", "-V -1", "-W 1", "-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""};
			try {
				classifiers[i].setOptions(opts);
				classifiers[i].buildClassifier(insts[i]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		*/
		
		// Testing code:
		//featexts[0] = new BaselineFeatureExtractor();
		featexts[2] = new ContextualFeatureExtractor();
		
		
		insts[2] = featexts[2].extractFeatures(tweets);
		classifiers[2] = new SMO();
		String[] opts = {"-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0", "-V -1", "-W 1", "-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""};
		try {
			classifiers[2].setOptions(opts);
			classifiers[2].buildClassifier(insts[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test(String fileloc)
	{
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		
		/* Full actual code
		for(int i=0;i<featexts.length;i++) {
			insts[i] = featexts[i].extractFeatures(tweets);
			if(i > 0)
				insts[i] = Instances.mergeInstances(insts[i], insts[i-1]);
			for(int j=0;j<tests.numInstances();j++) {
				try {
					double cld = classifiers[i].classifyInstance(tests.instance(j));
					Attribute attr = tests.attribute(0);
					String cl = attr.value((int) cld);
					System.out.println(cl);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		*/
		
		Instances tests = featexts[2].extractFeatures(tweets);
		
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
		svmc.train("data/train.40000.2009.05.25");
		svmc.test("data/testdata.manual.2009.05.25");
	}

}
