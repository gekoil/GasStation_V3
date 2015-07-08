package BL;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class MyObjectFilter implements Filter {
	
	private Object filter;
	
	public MyObjectFilter(Object toFilter) {
		this.filter = toFilter;
	}
	
	public boolean isLoggable(LogRecord rec) {
		if (rec.getParameters() != null) {
			Object temp = rec.getParameters()[0];
			return this.filter == temp;
		}
		else
			return false;
	}
}
