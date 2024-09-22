package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class NestMembersAttribute extends Attribute {
	private final U2 numberOfClasses;
	private final U2[] classes;

	public NestMembersAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOfClasses,
	                            U2[] classes) {
		super(constants, nameIndex, length);
		this.numberOfClasses = numberOfClasses;
		this.classes = classes;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOfClasses() {
		return numberOfClasses;
	}

	public U2[] getClasses() {
		return classes;
	}
}
