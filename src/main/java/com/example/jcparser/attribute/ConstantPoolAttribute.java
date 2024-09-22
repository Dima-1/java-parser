package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class ConstantPoolAttribute extends Attribute {
	private final U2 constantPoolIndex;

	public ConstantPoolAttribute(List<ConstantPoolEntry> constants, U2 nameIndex, U4 length, U2 constantPoolIndex) {
		super(constants, nameIndex, length);
		this.constantPoolIndex = constantPoolIndex;
	}

	public U2 getConstantPoolIndex() {
		return constantPoolIndex;
	}
}
