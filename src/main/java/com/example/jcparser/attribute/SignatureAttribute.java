package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class SignatureAttribute extends ConstantPoolAttribute {

	public SignatureAttribute(U2 nameIndex, U4 length, U2 signatureIndex) {
		super(nameIndex, length, signatureIndex);
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
