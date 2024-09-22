package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class SignatureAttribute extends ConstantPoolAttribute {

	public SignatureAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 signatureIndex) {
		super(constants, nameIndex, length, signatureIndex);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getSignatureIndex() {
		return getConstantPoolIndex();
	}
}
