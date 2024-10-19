package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeVisibleAnnotationsAttribute extends Attribute {
	private final U2 numberOf;
	private final RuntimeVisibleAnnotation[] annotations;

	public RuntimeVisibleAnnotationsAttribute(U2 attributeNameIndex, U4 attributeLength, U2 numberOf,
	                                          RuntimeVisibleAnnotation[] annotations) {
		super(attributeNameIndex, attributeLength);
		this.numberOf = numberOf;
		this.annotations = annotations;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public RuntimeVisibleAnnotation[] getAnnotations() {
		return annotations;
	}

	public record RuntimeVisibleAnnotation(U2 typeIndex, U2 lengthOfPair,
	                                       ValuePair[] valuePairs) implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
