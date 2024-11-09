package com.example.jcparser.constantpool;

public final class ConstantPoolDynamic extends ConstantPoolEntry {
	private final int bootstrapMethodAttrIndex;
	private final int nameAndTypeIndex;

	public ConstantPoolDynamic(int offset, int idx, ConstantTag constantTag,
	                           int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		super(offset, idx, constantTag);
		this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public int getBootstrapMethodAttrIndex() {
		return bootstrapMethodAttrIndex;
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
