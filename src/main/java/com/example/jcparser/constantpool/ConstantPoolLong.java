package com.example.jcparser.constantpool;

public class ConstantPoolLong extends ConstantPoolEntry {
	private final long value;

	public ConstantPoolLong(int offset, int idx, ConstantTag constantTag, long value) {
		super(offset, idx, constantTag);
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
