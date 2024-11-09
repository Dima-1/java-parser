package com.example.jcparser.constantpool;

public final class ConstantPoolMethodRef extends ConstantPoolEntry {
	private final int classIndex;
	private final int nameAndTypeIndex;

	public ConstantPoolMethodRef(int offset, int idx, ConstantTag constantTag,
	                             int classIndex, int nameAndTypeIndex) {
		super(offset, idx, constantTag);
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public int getClassIndex() {
		return classIndex;
	}

	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
