import java.util.LinkedList;


public class ErrorStream {
	
	public final int WARNING = 0;
	public final int ERROR = 1;
	
	public static class Notification {
		int mType;
		int mLine;
		String mMessage;
		
		public Notification(int type, int line, String message) {
			mType = type;
			mLine = line;
			mMessage = message;
		}
		
	}
	
	public LinkedList<Notification> mNotifications = new LinkedList<Notification>();
	
	public void addSymanticError(int line, String errorMsg) {
		mNotifications.add(new Notification(ERROR, line, errorMsg));
	}

}
