package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class ConstantValueAttribute extends ConstantPoolAttribute {

	public ConstantValueAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 constantValueIndex) {
		super(constants, nameIndex, length, constantValueIndex);
		ConstantPoolEntry entry = constants.get(constantValueIndex.getValue() - 1);
		if (!entry.getConstantTag().isConstantValueAttribute()) {
			String message = String.format("%04X ConstantValue = %s", constantValueIndex.getOffset(),
					entry.getConstantTag());
			throw new RuntimeException(message);
		}
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getConstantValueIndex() {
		return getConstantPoolIndex();
	}
}
