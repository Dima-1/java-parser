package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import org.apache.commons.text.StringEscapeUtils;

public class SourceDebugExtensionAttribute extends Attribute {
	private final String utf8;

	public SourceDebugExtensionAttribute(U2 attributeNameIndex, U4 attributeLength, String utf8) {
		super(attributeNameIndex, attributeLength);
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
