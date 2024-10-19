package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U2Array;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class BootstrapMethodsAttribute extends Attribute {
	private final U2 numberOf;
	private final BootstrapMethod[] bootstrapMethods;

	public BootstrapMethodsAttribute(U2 nameIndex, U4 length, U2 numberOf, BootstrapMethod[] bootstrapMethods) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.bootstrapMethods = bootstrapMethods;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public BootstrapMethod[] getBootstrapMethods() {
		return bootstrapMethods;
	}

	public record BootstrapMethod(int index, U2 bootstrapMethodRef, U2Array bootstrapArguments)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
