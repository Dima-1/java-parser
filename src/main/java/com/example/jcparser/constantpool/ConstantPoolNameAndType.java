package com.example.jcparser.constantpool;

public final class ConstantPoolNameAndType extends ConstantPoolEntry {
	private final int nameIndex;
	private final int descriptorIndex;

	public ConstantPoolNameAndType(int offset, int idx, ConstantTag constantTag,
	                               int nameIndex, int descriptorIndex) {
		super(offset, idx, constantTag);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
