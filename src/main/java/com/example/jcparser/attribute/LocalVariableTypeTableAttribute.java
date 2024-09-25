package com.example.jcparser.attribute;

import com.example.jcparser.Parser;

import java.util.List;

public class LocalVariableTypeTableAttribute extends LocalVariableAttribute {
	public LocalVariableTypeTableAttribute(List<Parser.ConstantPoolEntry> constantPool, Parser.U2 attributeNameIndex, 
	                                       Parser.U4 attributeLength, Parser.U2 numberOf, 
	                                       LocalVariable[] localVariables) {
		super(constantPool, attributeNameIndex, attributeLength, numberOf, localVariables);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
