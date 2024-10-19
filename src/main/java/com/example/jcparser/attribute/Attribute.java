package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print.Printable;

public class Attribute implements Printable<AttributePrinter> {
	private final Parser.U2 nameIndex;
	private final Parser.U4 length;

	public Attribute(Parser.U2 nameIndex, Parser.U4 length) {
		this.nameIndex = nameIndex;
		this.length = length;
	}

	public Parser.U2 getNameIndex() {
		return nameIndex;
	}

	public Parser.U4 getLength() {
		return length;
	}

	@Override
	public void print(AttributePrinter printer) {
		printer.print(this);
	}
}
