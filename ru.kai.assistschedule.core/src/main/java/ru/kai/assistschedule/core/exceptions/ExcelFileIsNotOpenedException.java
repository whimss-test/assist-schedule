package ru.kai.assistschedule.core.exceptions;

public class ExcelFileIsNotOpenedException extends Exception {
	private static final long serialVersionUID = 4898126402844285011L;

	public ExcelFileIsNotOpenedException() {
		super();
	}

	public ExcelFileIsNotOpenedException(String message, Throwable arg0) {
		super(message, arg0);
	}

	public ExcelFileIsNotOpenedException(String message) {
		super(message);
	}

	public ExcelFileIsNotOpenedException(Throwable arg0) {
		super(arg0);
	}
	
	

}
