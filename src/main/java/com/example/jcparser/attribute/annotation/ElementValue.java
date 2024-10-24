package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.CharU1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.AttributePrinter;

public record ElementValue(CharU1 tag, U2 u2First, U2 u2Second,
                           RuntimeVisibleAnnotationsAttribute.Annotation annotation,
                           com.example.jcparser.attribute.annotation.ElementValue[] elementValues)
		implements Print.Printable<AttributePrinter> {

	@Override
	public void print(AttributePrinter printer) {
		printer.print(this);
	}
}
