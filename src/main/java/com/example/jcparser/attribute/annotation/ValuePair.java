package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.AttributePrinter;

public record ValuePair(U2 elementNameIndex, ElementValue elementValue) implements Print.Printable<AttributePrinter> {

	@Override
	public void print(AttributePrinter printer) {
		printer.print(this);
	}
}
