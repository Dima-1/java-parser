package com.example.jcparser.attribute;

import java.util.List;

import com.example.jcparser.Parser.*;

public class SourceFileAttribute extends ConstantPoolAttribute {

	public SourceFileAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 sourceFileIndex) {
		super(constants, nameIndex, length, sourceFileIndex);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getSourceFileIndex() {
		return getConstantPoolIndex();
	}
}
