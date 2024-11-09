package com.example.jcparser.constantpool;

import org.apache.commons.text.StringEscapeUtils;

public final class ConstantPoolUtf8 extends ConstantPoolEntry {
	private final String utf8;

	public ConstantPoolUtf8(int offset, int idx, ConstantTag constantTag, String utf8) {
		super(offset, idx, constantTag);
		this.utf8 = StringEscapeUtils.escapeJava(utf8);
	}

	public String getUtf8() {
		return utf8;
	}

	@Override
	public void format(ConstantFormater formater) {
		super.format(formater);
		formater.format(this);
	}
}
