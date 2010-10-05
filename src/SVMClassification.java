import java.util.ArrayList;

import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Instances;

public class SVMClassification {
	private FeatureExtractor featexts[] = new FeatureExtractor[4];
	private SMO classifiers[] = new SMO[4];
	private Instances insts[] = new Instances[4];

	public void train(String fileloc) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		featexts[2] = new ContextualFeatureExtractor();
		//featexts[2] = new BaselineFeatureExtractor();
		//featexts[2] = new LinguisticFeatureExtractor();

		insts[2] = featexts[2].extractFeatures(tweets);
		classifiers[2] = new SMO();
		// String[] opts = {"-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0",
		// "-V -1", "-W 1",
		// "-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""};
		//String[] opts = { "-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0", "-V -1",
		//		"-W 1"};
		//		"-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\"" };
		try {
			//classifiers[2].setOptions(opts);
			classifiers[2].buildClassifier(insts[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test(String fileloc) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile(fileloc);
		Instances tests = featexts[2].extractFeatures(tweets);
		int accuracy = 0;
		int nonneutral = 0;
		for (int i = 0; i < tests.numInstances(); i++) {
			try {
				double cld = classifiers[2].classifyInstance(tests.instance(i));
				Attribute attr = tests.attribute(0);
				String cl = attr.value((int) cld);
				double actd = tests.instance(i).value(0);
				String act = attr.value((int) actd);

				if(!act.equals("Neutral")) {
					nonneutral++;
					System.out.println("Class: " + cl + ", Act: " + act);
					if (cl.equals(act))
						accuracy++;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		System.out.println(tests.numInstances());
		System.out.println(nonneutral);
		System.out.println(accuracy);
		System.out.println("Accuracy:");
		System.out.println(accuracy / (double)nonneutral);
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
