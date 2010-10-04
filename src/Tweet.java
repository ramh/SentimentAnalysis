import java.util.Calendar;
import java.util.Date;

public class Tweet {
	public String sentiment;
	public int id, hour;
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		this.user = user;
		this.subject = subject;
		this.text = text; 
	}
	
	public String toString() {
		return this.sentiment + " - Hour " + hour + ": " + text;
	}

}
