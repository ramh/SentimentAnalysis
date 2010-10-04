import java.util.List;

import weka.core.Instances;


public interface FeatureExtractor {
	public Instances extractFeatures(List<Tweet> tweets);
}
