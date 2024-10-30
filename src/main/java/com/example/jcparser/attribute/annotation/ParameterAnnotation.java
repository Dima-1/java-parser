package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.AttributePrinter;

public record ParameterAnnotation(U2 numberOf, RuntimeAnnotationsAttribute.Annotation[] annotations, boolean visible)
		implements Print.Printable<AttributePrinter> {

	@Override
	public void print(AttributePrinter printer) {
		printer.print(this);
	}
}
