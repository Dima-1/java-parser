package com.example.jcparser.attribute;

import com.example.jcparser.Print;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class ExceptionsAttribute extends Attribute {
	private final U2 numberOf;
	private final U2[] exceptions;

	public ExceptionsAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                           U2[] exceptions) {
		super(constants, nameIndex, length);
		this.numberOf = numberOf;
		this.exceptions = exceptions;
		for (U2 exception : exceptions) {
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

	public U2 getNumberOf() {
		return numberOf;
	}

	public U2[] getExceptions() {
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
