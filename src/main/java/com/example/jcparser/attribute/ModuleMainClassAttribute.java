package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class ModuleMainClassAttribute extends ConstantPoolAttribute {

	public ModuleMainClassAttribute(U2 nameIndex, U4 length, U2 mainClassIndex) {
		super(nameIndex, length, mainClassIndex);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getMainClassIndex() {
		return getConstantPoolIndex();
	}
}
