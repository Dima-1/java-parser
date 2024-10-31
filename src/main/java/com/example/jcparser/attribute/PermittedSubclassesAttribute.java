package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U2Array;
import com.example.jcparser.Parser.U4;

public class PermittedSubclassesAttribute extends Attribute {
	private final U2Array classes;

	public PermittedSubclassesAttribute(U2 nameIndex, U4 length, U2Array classes) {
		super(nameIndex, length);
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
