package com.example.jcparser.attribute;

import java.util.List;

import com.example.jcparser.Parser.*;

public class NestHostAttribute extends ConstantPoolAttribute {

	public NestHostAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 hostClassIndex) {
		super(constants, nameIndex, length, hostClassIndex);
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
