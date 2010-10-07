import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class LinguisticFeatureExtractor implements FeatureExtractor{
	private FastVector attrs;
	private FastVector pos_relations;
	private static final int OFFSET = 1;

	private void setupAttributes(List<Tweet> tweets)
	{
		attrs = new FastVector();
		// Determine attributes

		pos_relations = get_all_pos_relations(tweets);
		for(int i=0; i<pos_relations.size(); i++)
		{
			String word = (String) pos_relations.elementAt(i);
			Attribute attr = new Attribute("lingfeature:" + word);
			attrs.addElement(attr);
		}
	}

	@Override
	public Instances extractFeatures(List<Tweet> tweets) {
		if(attrs == null)
			setupAttributes(tweets);
		
		Instances feats = new Instances("Linguistic Features", attrs, tweets.size());
		feats.setClassIndex(0);
		// Record features

		StringTokenizer st;
		String word;
		List words;
		Tree parse;
		TreebankLanguagePack tlp;
		GrammaticalStructureFactory gsf;
		GrammaticalStructure gs;
		Collection tdl;
		LexicalizedParser lp = new LexicalizedParser("res/englishPCFG.ser.gz");
		lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"});
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();

		for(Tweet t: tweets)
		{	
			Instance inst = new Instance(1.0, new double[attrs.size()]);

			st = new StringTokenizer(t.text);
			words = new ArrayList<String>();
			while(st.hasMoreElements()) {
				word = (String) st.nextElement();
				words.add(word);
			}
			parse = (Tree) lp.apply(words);
			gs = gsf.newGrammaticalStructure(parse);
			tdl = gs.typedDependenciesCollapsed();

			Iterator td_itr = tdl.iterator();
			TypedDependency x;
			while(td_itr.hasNext()) {
				x = (TypedDependency) td_itr.next();
				if(pos_relations.contains(x.reln().toString()))
					inst.setValue(pos_relations.indexOf(x.reln().toString())+OFFSET, 1);
			}

			inst.setDataset(feats);
			feats.add(inst);
		}

		ArffSaver saver = new ArffSaver();
		saver.setInstances(feats);
		try {
			saver.setFile(new File("output/baseline.arff"));
			saver.writeBatch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return feats;
	}

	private static FastVector get_all_pos_relations(List<Tweet> tweets)
	{
		StringTokenizer st;
		String word;
		List words;
		Tree parse;
		TreebankLanguagePack tlp;
		GrammaticalStructureFactory gsf;
		GrammaticalStructure gs;
		Collection tdl;
		LexicalizedParser lp = new LexicalizedParser("res/englishPCFG.ser.gz");
		lp.setOptionFlags(new String[]{"-maxLength", "80", "-retainTmpSubcategories"});
		tlp = new PennTreebankLanguagePack();
		gsf = tlp.grammaticalStructureFactory();
		int postags_count = 0;
		FastVector pos_relations = new FastVector();

		for(Tweet t: tweets)
		{		
			st = new StringTokenizer(t.text);
			words = new ArrayList<String>();
			while(st.hasMoreElements()) {
				word = (String) st.nextElement();
				words.add(word);
			}
			parse = (Tree) lp.apply(words);
			gs = gsf.newGrammaticalStructure(parse);
			tdl = gs.typedDependenciesCollapsed();

			Iterator td_itr = tdl.iterator();
			TypedDependency x;
			while(td_itr.hasNext()) {
				x = (TypedDependency) td_itr.next();
				if(!pos_relations.contains(x.reln().toString())) {
					pos_relations.addElement(x.reln().toString());
					postags_count++;
				}
			}
			if(postags_count > 500)
				break;
		}		
		return pos_relations;
	}
}
