package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeAnnotationsAttribute extends Attribute {
	protected final U2 numberOf;
	protected final Annotation[] annotations;

	public RuntimeAnnotationsAttribute(U2 nameIndex, U4 length, U2 numberOf, Annotation[] annotations) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.annotations = annotations;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public record Annotation(U2 typeIndex, U2 lengthOfPair, ValuePair[] valuePairs)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
