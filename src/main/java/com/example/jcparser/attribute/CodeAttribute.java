package com.example.jcparser.attribute;

import com.example.jcparser.Parser.ConstantPoolEntry;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

import java.util.List;

public class CodeAttribute extends Attribute {
	private final U2 maxStack;
	private final U2 maxLocals;
	private final U4 codeLength;
	private final List<Opcode> opcodes;
	private final U2 exceptionTableLength;
	private final ExceptionsAttribute.Exception[] exceptions;
	private final U2 numberOf;
	private final List<Attribute> attributes;

	public CodeAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 maxStack, U2 maxLocals,
	                     U4 codeLength, List<Opcode> opcodes, U2 exceptionTableLength, ExceptionsAttribute.Exception[] exceptions, U2 numberOf,
	                     List<Attribute> attributes) {
		super(constants, nameIndex, length);
		this.maxStack = maxStack;
		this.maxLocals = maxLocals;
		this.codeLength = codeLength;
		this.opcodes = opcodes;
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

	public List<Opcode> getOpcodes() {
		return opcodes;
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
