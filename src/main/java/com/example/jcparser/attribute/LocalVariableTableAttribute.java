package com.example.jcparser.attribute;


import java.util.List;

import com.example.jcparser.Parser.*;

public class LocalVariableTableAttribute extends LocalVariableAttribute {

	public LocalVariableTableAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 numberOf,
	                                   LocalVariable[] localVariables) {
		super(constants, nameIndex, length, numberOf, localVariables);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
