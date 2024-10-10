package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class ModuleMainClassAttribute extends ConstantPoolAttribute {

	public ModuleMainClassAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 mainClassIndex) {
		super(constants, nameIndex, length, mainClassIndex);
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
