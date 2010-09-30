import java.io.FileWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TweetClassifierSVM {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		final String PREFIX = "tweet_";
		FileWriter fw = new FileWriter("evaluation.txt", true);
		fw.write("seq,pctCorrectVal,errorRateVal,BuildTime,ValTime\n");

		// creates training and validation sets
		DataSource trainDS = new DataSource("datasets/" + PREFIX + "_Tr.arff");
		DataSource valDS = new DataSource("datasets/" + PREFIX + "_Val.arff");
		Instances train = trainDS.getDataSet();
		if (train.classIndex() == -1) 
			train.setClassIndex(train.numAttributes() - 1);

		Instances eval = valDS.getDataSet();
		if (eval.classIndex() == -1) 
			eval.setClassIndex(eval.numAttributes() - 1);

		SMO svm = new SMO();
		PolyKernel kernel = new PolyKernel();
		kernel.setExponent(1);
		svm.setKernel(kernel);
		svm.setC(2.0);

		//train
		System.out.println("Building...");
		long time1 = System.currentTimeMillis(); 
		svm.buildClassifier(train);
		long time2 = System.currentTimeMillis(); 

		System.out.println("Evaluating...");
		Evaluation evaluation = new Evaluation(train);
		long evalValTime1 = System.currentTimeMillis();
		evaluation.evaluateModel(svm, eval);
		long evalValTime2 = System.currentTimeMillis() - evalValTime1;
		int classes = eval.classAttribute().numValues();
		double truePos = 0l;
		double falsePos = 0l;
		double falseNeg = 0l;
		for (int i = 0; i < classes; i++) {
			truePos += evaluation.numTruePositives(i);
			falsePos += evaluation.numFalsePositives(i);
			falseNeg += evaluation.numFalseNegatives(i);
		}

		double precision = truePos / (truePos + falsePos);
		double recall = truePos / (truePos + falseNeg);
		double f = (2 * precision * recall) / (precision + recall);
		System.out.println(truePos + "  " + falsePos + "  " + falseNeg + "   P: " + precision + "    R: " + recall + "  f: " + f);

		fw.write("1" + ", " + (evaluation.pctCorrect() / 100.0) + ", " + evaluation.errorRate() + "," + (time2 - time1) + ", " + evalValTime2 + "\n");
		fw.flush();

		fw.close();
		System.out.println("Done");
	}
}