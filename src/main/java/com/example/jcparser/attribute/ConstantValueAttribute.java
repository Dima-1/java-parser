package com.example.jcparser.attribute;

import com.example.jcparser.Parser.ConstantPoolEntry;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

import java.util.List;

public class ConstantValueAttribute extends ConstantPoolAttribute {

	public ConstantValueAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 constantValueIndex) {
		super(constants, nameIndex, length, constantValueIndex);
		ConstantPoolEntry entry = constants.get(constantValueIndex.getValue());
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
