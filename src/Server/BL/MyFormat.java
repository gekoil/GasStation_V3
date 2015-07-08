package BL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyFormat extends Formatter {
	private final String dateFormat = "dd/MM/yyyy HH:mm:ss";

	@Override
	public String format(LogRecord record) {
		String formatStr = "";
		Date curretTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		String newFormat = format.format(curretTime);
		formatStr += newFormat + "\r\n" + record.getMessage() + "\r\n\r\n";
		return formatStr;
	}
}
