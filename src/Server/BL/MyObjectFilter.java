package BL;

import Interfaces.LoggableClass;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MyObjectFilter implements Filter {
	
	private LoggableClass filter;
	
	public MyObjectFilter(LoggableClass toFilter) {
		this.filter = toFilter;
	}
	
	public boolean isLoggable(LogRecord rec) {
		return rec.getMessage().contains(filter.getClass().getCanonicalName() + " ID " + filter.getId());
	}
}
