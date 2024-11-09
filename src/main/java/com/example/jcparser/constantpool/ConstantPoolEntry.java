package com.example.jcparser.constantpool;

public class ConstantPoolEntry implements ConstantFormater.Formatter<ConstantFormater> {
	private final int offset;
	private final int idx;
	private final ConstantTag constantTag;

	public ConstantPoolEntry(int offset, int idx, ConstantTag constantTag) {
		this.offset = offset;
		this.idx = idx;
		this.constantTag = constantTag;
	}

	public int getOffset() {
		return offset;
	}

	public int getIdx() {
		return idx;
	}

	public ConstantTag getConstantTag() {
		return constantTag;
	}

	@Override
	public void format(ConstantFormater formater) {
		formater.format(this);
	}
}
