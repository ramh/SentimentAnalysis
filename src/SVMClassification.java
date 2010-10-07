import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.Instances;

public class SVMClassification {
	private FeatureExtractor featexts[] = {new BaselineFeatureExtractor(),
										   new LinguisticFeatureExtractor(),
										   new ContextualFeatureExtractor()};
	//private SMO classifiers[] = new SMO[featexts.length];
	private Instances traininsts[] = new Instances[featexts.length];
	private Instances testinsts[] = new Instances[featexts.length];
	
	public SVMClassification(String trainfile, String testfile) {
		ArrayList<Tweet> traintweets = TweetFileParser.parseFile(trainfile);
		ArrayList<Tweet> testtweets = TweetFileParser.parseFile(testfile);
		
		for(int i=0;i<featexts.length;i++) {
			traininsts[i] = featexts[i].extractFeatures(traintweets);
			if(i > 0) {
				traininsts[i] = Instances.mergeInstances(traininsts[i], traininsts[i-1]);
			}
			testinsts[i] = featexts[i].extractFeatures(testtweets);
			if(i > 0) {
				testinsts[i] = Instances.mergeInstances(testinsts[i], testinsts[i-1]);
			}
		}
	}
		
	public void crossValidation(int numfolds) {

		for(int i=0;i<featexts.length;i++) {
			Random rand = new Random(8);
			Instances randData = new Instances(traininsts[i]);
			randData.randomize(rand);
			randData.stratify(10);
			
			for(int n = 0; n < 10; n++) {
				Instances train = randData.trainCV(numfolds, n);
				Instances test = randData.testCV(numfolds, n);

				SMO classifier = new SMO();
				String[] opts = {"-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0", "-V -1", "-W 1", "-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""};
				try {
					classifier.setOptions(opts);
					classifier.buildClassifier(train);
					System.out.println("***************************************");
					System.out.println("Cross Validation " + n + ":");
					testAndReport(classifier, test);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}
	}
		
	public void testValidation() {

		for(int i=0;i<featexts.length;i++) {
			Instances train = traininsts[i];
			Instances test = testinsts[i];
			SMO classifier = new SMO();
			String[] opts = {"-C 1.0", "-L 0.0010", "-P 1.0E-12", "-N 0", "-V -1", "-W 1", "-K \"weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0\""};
			try {
				classifier.setOptions(opts);
				classifier.buildClassifier(train);
				System.out.println("***************************************");
				System.out.println("Test Validation:");
				testAndReport(classifier, test);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	public void testAndReport(SMO classifier, Instances test) {
		int accuracy = 0;
		int nonneutral = 0;
		for (int i = 0; i < test.numInstances(); i++) {
			try {
				double cld = classifier.classifyInstance(test.instance(i));
				Attribute attr = test.attribute(0);
				String cl = attr.value((int) cld);
				double actd = test.instance(i).value(0);
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
		System.out.println("Number of test instances: " + test.numInstances());
		System.out.println("Nonneutral: " + nonneutral);
		System.out.println("Accuracy:");
		System.out.println(accuracy + " // " + nonneutral);
		System.out.println(accuracy / (double)nonneutral);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SVMClassification svmc = new SVMClassification("data/train.40000.2009.05.25",
				                                       "data/testdata.manual.2009.05.25");
		svmc.crossValidation(10);
		svmc.testValidation();
	}

}
