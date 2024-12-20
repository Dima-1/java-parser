package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class LocalVariableAttribute extends Attribute {
	private final U2 numberOf;
	private final LocalVariable[] localVariables;

	public LocalVariableAttribute(U2 nameIndex, U4 length, U2 numberOf, LocalVariable[] localVariables) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.localVariables = localVariables;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public LocalVariable[] getLocalVariables() {
		return localVariables;
	}

	public record LocalVariable(U2 startPC, U2 length, U2 nameIndex, U2 descriptorIndex, U2 index,
	                            String descriptorTitle) implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
