package com.example.jcparser.attribute.instruction;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;
import com.example.jcparser.attribute.ExceptionsAttribute;

import java.util.List;

public class CodeAttribute extends Attribute {
	private final U2 maxStack;
	private final U2 maxLocals;
	private final U4 codeLength;
	private final List<Instruction> instructions;
	private final U2 exceptionTableLength;
	private final ExceptionsAttribute.Exception[] exceptions;
	private final U2 numberOf;
	private final List<Attribute> attributes;

	public CodeAttribute(U2 nameIndex, U4 length, U2 maxStack, U2 maxLocals, U4 codeLength, List<Instruction> instructions,
	                     U2 exceptionTableLength, ExceptionsAttribute.Exception[] exceptions, U2 numberOf,
	                     List<Attribute> attributes) {
		super(nameIndex, length);
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.codeLength = codeLength;
		this.instructions = instructions;
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

	public List<Instruction> getInstructions() {
		return instructions;
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

	public List<Attribute> getAttributes() {
		return attributes;
	}
}
