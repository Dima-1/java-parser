package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeInvisibleParameterAnnotationsAttribute extends RuntimeParameterAnnotationsAttribute {
	public RuntimeInvisibleParameterAnnotationsAttribute(U2 nameIndex, U4 length, U1 numberOf,
	                                                     ParameterAnnotation[] parameterAnnotations) {
		super(nameIndex, length, numberOf, parameterAnnotations);
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
