package com.example.jcparser.attribute;

import java.util.List;
import java.util.Map;

import static com.example.jcparser.Parser.*;

public class CodeAttribute extends Attribute {
	private final U2 maxStack;
	private final U2 maxLocals;
	private final U4 codeLength;
	private final U2 exceptionTableLength;
	private final ExceptionsAttribute.Exception[] exceptions;
	private final U2 numberOf;
	private final Map<String, Attribute> attributes;

	public CodeAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 maxStack, U2 maxLocals,
	                     U4 codeLength, U2 exceptionTableLength, ExceptionsAttribute.Exception[] exceptions, U2 numberOf,
	                     Map<String, Attribute> attributes) {
		super(constants, nameIndex, length);
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.codeLength = codeLength;
		this.exceptionTableLength = exceptionTableLength;
		this.exceptions = exceptions;
		this.numberOf = numberOf;
		this.attributes = attributes;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getMaxStack() {
		return maxStack;
	}

	public U2 getMaxLocals() {
		return maxLocals;
	}

	public U4 getCodeLength() {
		return codeLength;
	}

	public U2 getExceptionTableLength() {
		return exceptionTableLength;
	}

	public ExceptionsAttribute.Exception[] getExceptions() {
		return exceptions;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public Map<String, Attribute> getAttributes() {
		return attributes;
	}
}