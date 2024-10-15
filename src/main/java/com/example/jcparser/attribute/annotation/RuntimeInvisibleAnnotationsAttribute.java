package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser;
import com.example.jcparser.attribute.AttributePrinter;

import java.util.List;

public class RuntimeInvisibleAnnotationsAttribute extends RuntimeVisibleAnnotationsAttribute {
	public RuntimeInvisibleAnnotationsAttribute(List<Parser.ConstantPoolEntry> constantPool, Parser.U2 attributeNameIndex, Parser.U4 attributeLength, Parser.U2 numberOf, RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation[] annotations) {
		super(constantPool, attributeNameIndex, attributeLength, numberOf, annotations);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
