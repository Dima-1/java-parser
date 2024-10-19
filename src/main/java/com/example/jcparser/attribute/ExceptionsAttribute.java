package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U2Array;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class ExceptionsAttribute extends Attribute {
	private final U2Array exceptions;

	public ExceptionsAttribute(U2 nameIndex, U4 length, U2Array exceptions) {
		super(nameIndex, length);
		this.exceptions = exceptions;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2Array getExceptions() {
		return exceptions;
	}

	public record Exception(U2 startPc, U2 endPc, U2 handlerPc,
	                        U2 catchType) implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
