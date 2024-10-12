package com.example.jcparser.attribute;

import com.example.jcparser.Parser.*;

import java.util.List;

public class EnclosingMethodAttribute extends Attribute {
	private final U2 classIndex;
	private final U2 methodIndex;

	public EnclosingMethodAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex,
	                                U4 attributeLength, U2 classIndex, U2 methodIndex) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.classIndex = classIndex;
		this.methodIndex = methodIndex;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getClassIndex() {
		return classIndex;
	}

	public U2 getMethodIndex() {
		return methodIndex;
	}
}
