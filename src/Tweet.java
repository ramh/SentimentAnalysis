import java.util.Date;

public class Tweet {
	public String sentiment;
	public int id;
	public Date time;
	public String user, subject, text;
	
	public Tweet(int sentiment, int id, Date time, String user, String subject,
			String text) {
		super();
		if(sentiment == 0)
			this.sentiment = "Negative";
		if(sentiment == 2)
			this.sentiment = "Neutral";
		if(sentiment == 4)
			this.sentiment = "Positive";
		this.id = id;
		this.time = time;
		this.user = user;
		this.subject = subject;
		this.text = text;
	}

}
