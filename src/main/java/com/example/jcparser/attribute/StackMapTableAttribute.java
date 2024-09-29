package com.example.jcparser.attribute;

import com.example.jcparser.attribute.stackmapframe.StackMapFrame;

import java.util.List;

import static com.example.jcparser.Parser.*;


public class StackMapTableAttribute extends Attribute {
	private final U2 numberOf;
	private final List<StackMapFrame> entries;

	public StackMapTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                              List<StackMapFrame> entries) {
		super(constants, nameIndex, length);
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
