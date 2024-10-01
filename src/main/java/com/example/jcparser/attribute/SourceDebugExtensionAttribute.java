package com.example.jcparser.attribute;

import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class SourceDebugExtensionAttribute extends Attribute {
	private final String utf8;

	public SourceDebugExtensionAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex,
	                                     U4 attributeLength, String utf8) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.utf8 = StringEscapeUtils.escapeJava(utf8);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public String getUtf8() {
		return utf8;
	}
}
