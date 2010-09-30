import java.util.Date;

public class Tweet {
	public int sentiment, id;
	public Date time;
	public String user, subject, text;
	
	public Tweet(int sentiment, int id, Date time, String user, String subject,
			String text) {
		super();
		this.sentiment = sentiment;
		this.id = id;
		this.time = time;
		this.user = user;
		this.subject = subject;
		this.text = text;
	}

}
