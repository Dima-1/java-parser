package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class ConstantValueAttribute extends ConstantPoolAttribute {

	public ConstantValueAttribute(U2 nameIndex, U4 length, U2 constantValueIndex) {
		super(nameIndex, length, constantValueIndex);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getConstantValueIndex() {
		return getConstantPoolIndex();
	}
}
