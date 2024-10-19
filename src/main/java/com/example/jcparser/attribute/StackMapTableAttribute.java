package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.stackmapframe.StackMapFrame;

import java.util.List;


public class StackMapTableAttribute extends Attribute {
	private final U2 numberOf;
	private final List<StackMapFrame> entries;

	public StackMapTableAttribute(U2 nameIndex, U4 length, U2 numberOf, List<StackMapFrame> entries) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.entries = entries;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public List<StackMapFrame> getEntries() {
		return entries;
	}


}
