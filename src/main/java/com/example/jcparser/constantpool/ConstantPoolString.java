package com.example.jcparser.constantpool;

public final class ConstantPoolString extends ConstantPoolEntry {
	private final int stringIndex;

	public ConstantPoolString(int offset, int idx, ConstantTag constantTag,
	                          int stringIndex) {
		super(offset, idx, constantTag);
		this.stringIndex = stringIndex;
	}

	public int getStringIndex() {
		return stringIndex;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
