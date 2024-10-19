package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class SourceFileAttribute extends ConstantPoolAttribute {

	public SourceFileAttribute(U2 nameIndex, U4 length, U2 sourceFileIndex) {
		super(nameIndex, length, sourceFileIndex);
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
