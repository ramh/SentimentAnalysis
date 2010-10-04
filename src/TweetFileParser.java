import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TweetFileParser {
	public static ArrayList<Tweet> parseFile(String filename) {
		File f = new File(filename);
		ArrayList<Tweet> result = new ArrayList<Tweet>();
		try { 
			BufferedReader in = new BufferedReader(new FileReader(f));
			String str;
			String[] parts;
			while ((str = in.readLine()) != null) 
			{        
				parts = str.split(";;");
				for(int i=0; i<parts.length; i++)
					parts[i] = parts[i].trim();

				int sentiment = Integer.parseInt(parts[0]);
				int id = Integer.parseInt(parts[1]);
				SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
				Date time;
				try {
					time = sdf.parse(parts[2]);
				} catch (ParseException e) {
					time = new Date();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String subject = parts[3];
				String user = parts[4];
				String text = parts[5];

				result.add(new Tweet(sentiment, id, time, user, subject, text));
			}

			in.close(); 
		} catch (IOException e) { e.printStackTrace();} 
		return result;
	}
	
	public static void main(String[] args) {
		ArrayList<Tweet> tweets = TweetFileParser.parseFile("D:\\homework\\nlp\\SentimentAnalysis\\src\\data\\train.40000.2009.05.25");
		for(int i=19980;i<20020;i++)
		{
			System.out.println(tweets.get(i));
		}
	}
}
