package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class LocalVariableTypeTableAttribute extends LocalVariableAttribute {
	public LocalVariableTypeTableAttribute(U2 attributeNameIndex, U4 attributeLength, U2 numberOf,
	                                       LocalVariable[] localVariables) {
		super(attributeNameIndex, attributeLength, numberOf, localVariables);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
