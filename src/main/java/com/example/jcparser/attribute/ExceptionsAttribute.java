package com.example.jcparser.attribute;

import com.example.jcparser.Print;

import java.util.List;

import com.example.jcparser.Parser.*;

public class ExceptionsAttribute extends Attribute {
	private final U2Array exceptions;

	public ExceptionsAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2Array exceptions) {
		super(constants, nameIndex, length);
		this.exceptions = exceptions;
		for (U2 exception : exceptions.array()) {
			ConstantPoolEntry entry = constants.get(exception.getValue() - 1);
			if (!entry.getConstantTag().isConstantClass()) {
				String message = String.format("%04X ExceptionValue = %s", exception.getOffset(), entry.getConstantTag());
				throw new RuntimeException(message);
			}
		}
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
