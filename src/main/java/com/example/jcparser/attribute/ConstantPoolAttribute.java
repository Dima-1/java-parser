package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class ConstantPoolAttribute extends Attribute {
	private final U2 constantPoolIndex;

	public ConstantPoolAttribute(U2 nameIndex, U4 length, U2 constantPoolIndex) {
		super(nameIndex, length);
		this.constantPoolIndex = constantPoolIndex;
	}

	public U2 getConstantPoolIndex() {
		return constantPoolIndex;
	}
}
