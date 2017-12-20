package com.sunrun.bill.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BillDbOptException extends Exception {

	private static final long serialVersionUID = 424926622333362289L;
	private String message;
	private String stackTrace;
	private Throwable t;

	public Throwable getCause() {
		return this.t;
	}

	public String toString() {
		return getMessage();
	}

	public String getMessage() {
		return this.message;
	}

	public void printStackTrace() {
		System.err.print(this.stackTrace);
	}

	public void printStackTrace(PrintStream paramPrintStream) {
		printStackTrace(new PrintWriter(paramPrintStream));
	}

	public void printStackTrace(PrintWriter paramPrintWriter) {
		paramPrintWriter.print(this.stackTrace);
	}

	public BillDbOptException(String paramString) {
		super(paramString);
		this.message = paramString;
		this.stackTrace = paramString;
	}
	
	public BillDbOptException(Throwable paramThrowable) {
		super(paramThrowable.getMessage());
		this.t = paramThrowable;
		StringWriter localStringWriter = new StringWriter();
		paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
		this.stackTrace = localStringWriter.toString();
	}

	public BillDbOptException(String paramString, Throwable paramThrowable) {
		super(paramString + "; nested exception is "
				+ paramThrowable.getMessage());
		this.t = paramThrowable;
		StringWriter localStringWriter = new StringWriter();
		paramThrowable.printStackTrace(new PrintWriter(localStringWriter));
		this.stackTrace = localStringWriter.toString();
	}
}
