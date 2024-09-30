package com.example.jcparser.attribute;

import com.example.jcparser.Parser;

import java.util.List;

public class EnclosingMethodAttribute extends Attribute {
	private final Parser.U2 classIndex;
	private final Parser.U2 methodIndex;

	public EnclosingMethodAttribute(List<Parser.ConstantPoolEntry> constantPool, Parser.U2 attributeNameIndex,
	                                Parser.U4 attributeLength, Parser.U2 classIndex, Parser.U2 methodIndex) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.classIndex = classIndex;
		this.methodIndex = methodIndex;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public Parser.U2 getClassIndex() {
		return classIndex;
	}

	public Parser.U2 getMethodIndex() {
		return methodIndex;
	}
}
