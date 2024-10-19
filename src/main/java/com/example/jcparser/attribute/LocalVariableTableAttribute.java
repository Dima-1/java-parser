package com.example.jcparser.attribute;


import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class LocalVariableTableAttribute extends LocalVariableAttribute {

	public LocalVariableTableAttribute(U2 nameIndex, U4 length, U2 numberOf, LocalVariable[] localVariables) {
		super(nameIndex, length, numberOf, localVariables);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
