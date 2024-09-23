package com.example.jcparser.attribute;

import com.example.jcparser.Print;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class LocalVariableTableAttribute extends Attribute {
	private final U2 numberOf;
	private final LocalVariable[] localVariables;

	public LocalVariableTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                                   LocalVariable[] localVariables) {
		super(constants, nameIndex, length);
		this.numberOf = numberOf;
		this.localVariables = localVariables;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public LocalVariable[] getLocalVariables() {
		return localVariables;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public record LocalVariable(U2 startPC, U2 length, U2 nameIndex, U2 descriptorIndex, U2 index) 
			implements Print.Printable<AttributePrinter> {
		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
