package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeInvisibleAnnotationsAttribute extends RuntimeAnnotationsAttribute {
	public RuntimeInvisibleAnnotationsAttribute(U2 attributeNameIndex, U4 attributeLength, U2 numberOf,
	                                            Annotation[] annotations) {
		super(attributeNameIndex, attributeLength, numberOf, annotations);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
