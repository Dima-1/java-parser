package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeAnnotationsAttribute extends Attribute {
	protected final Parser.U2 numberOf;
	protected final Annotation[] annotations;

	public RuntimeAnnotationsAttribute(Parser.U2 nameIndex, Parser.U4 length, Parser.U2 numberOf, Annotation[] annotations) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.annotations = annotations;
	}

	public Parser.U2 getNumberOf() {
		return numberOf;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public record Annotation(Parser.U2 typeIndex, Parser.U2 lengthOfPair, ValuePair[] valuePairs)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
