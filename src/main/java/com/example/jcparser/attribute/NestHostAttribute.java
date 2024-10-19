package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class NestHostAttribute extends ConstantPoolAttribute {

	public NestHostAttribute(U2 nameIndex, U4 length, U2 hostClassIndex) {
		super(nameIndex, length, hostClassIndex);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getHostClassIndex() {
		return getConstantPoolIndex();
	}
}
