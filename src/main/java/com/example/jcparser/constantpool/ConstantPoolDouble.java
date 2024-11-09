package com.example.jcparser.constantpool;

public class ConstantPoolDouble extends ConstantPoolEntry {
	private final double value;

	public ConstantPoolDouble(int offset, int idx, ConstantTag constantTag, double value) {
		super(offset, idx, constantTag);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
