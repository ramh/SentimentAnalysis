import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
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
	
	private void setupAttributes(List<Tweet> tweets)
	{
		attrs = new FastVector();
		// Determine attributes
		FastVector sentvals = new FastVector();
		sentvals.addElement("Negative"); sentvals.addElement("Neutral"); sentvals.addElement("Positive"); 
		Attribute sentclass = new Attribute("Sentiment", sentvals);
		attrs.addElement(sentclass);
		
		pos_relations = get_all_pos_relations(tweets, attrs.size());
		Attribute pos_attr = new Attribute("POSRelations", pos_relations);
		attrs.addElement(pos_attr);
	}
	
	@Override
	public Instances extractFeatures(List<Tweet> tweets) {
		// TODO Auto-generated method stub	
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
			Instance inst = new Instance(attrs.size());
			inst.setClassValue(t.sentiment);

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
					inst.setValueSparse(pos_relations.indexOf(x.reln().toString()), 1);
			}
			
			inst.setDataset(feats);
			feats.add(inst);
		}
		
		return feats;
	}
	
	private static FastVector get_all_pos_relations(List<Tweet> tweets, int attr_size)
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
		FastVector pos_relations = new FastVector();
		
		for(Tweet t: tweets)
		{		
			Instance inst = new Instance(attr_size);
			inst.setClassValue(t.sentiment);

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
				pos_relations.addElement(x.toString());
			}
		}		
		return pos_relations;
	}
}
