package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeAnnotationsAttribute extends Attribute {
	private final U2 numberOf;
	private final Annotation[] annotations;
	private final boolean visible;

	public RuntimeAnnotationsAttribute(U2 nameIndex, U4 length, U2 numberOf, Annotation[] annotations, boolean visible) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.annotations = annotations;
		this.visible = visible;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}


	public record Annotation(U2 typeIndex, U2 lengthOfPair, ValuePair[] valuePairs)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
