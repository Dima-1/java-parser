package com.example.jcparser.constantpool;

public class ConstantPoolFloat extends ConstantPoolEntry {
	private final float value;

	public ConstantPoolFloat(int offset, int idx, ConstantTag constantTag, float value) {
		super(offset, idx, constantTag);
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
