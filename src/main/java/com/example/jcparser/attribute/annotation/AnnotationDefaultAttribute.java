package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class AnnotationDefaultAttribute extends Attribute {
	private final ElementValue elementValue;

	public AnnotationDefaultAttribute(U2 attributeNameIndex, U4 attributeLength, ElementValue elementValue) {
		super(attributeNameIndex, attributeLength);
		this.elementValue = elementValue;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public ElementValue getElementValue() {
		return elementValue;
	}
}
