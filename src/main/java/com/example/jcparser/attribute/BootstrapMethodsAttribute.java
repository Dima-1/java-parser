package com.example.jcparser.attribute;

import com.example.jcparser.Print;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class BootstrapMethodsAttribute extends Attribute {
	private final U2 numberOf;
	private final BootstrapMethod[] bootstrapMethods;

	public BootstrapMethodsAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                                 BootstrapMethod[] bootstrapMethods) {
		super(constants, nameIndex, length);
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

	public record BootstrapMethod(int index, U2 bootstrapMethodRef, U2 numberOf,
	                                     U2[] bootstrapArguments) implements Print.Printable<AttributePrinter> {
	
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
