package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print.Printable;

import java.util.List;

public class Attribute implements Printable<AttributePrinter> {
	private final Parser.U2 nameIndex;
	private final String name;
	private final Parser.U4 length;

	public Attribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex, Parser.U4 length) {
		this.nameIndex = nameIndex;
		name = constants.get(nameIndex.getValue()).getAdditional();
		this.length = length;
	}

	public Parser.U2 getNameIndex() {
		return nameIndex;
	}

	public String getName() {
		return name;
	}

	public Parser.U4 getLength() {
		return length;
	}

	@Override
	public void print(AttributePrinter printer) {
		printer.print(this);
	}
}
