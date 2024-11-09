package com.example.jcparser.constantpool;

public class ConstantPoolInteger extends ConstantPoolEntry {
	private final int value;

	public ConstantPoolInteger(int offset, int idx, ConstantTag constantTag, int value) {
		super(offset, idx, constantTag);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
