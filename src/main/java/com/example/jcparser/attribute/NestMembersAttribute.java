package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class NestMembersAttribute extends Attribute {
	private final U2Array classes;

	public NestMembersAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2Array classes) {
		super(constants, nameIndex, length);
		this.classes = classes;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2Array getClasses() {
		return classes;
	}
}
