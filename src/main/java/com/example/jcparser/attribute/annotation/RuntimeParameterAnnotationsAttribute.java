package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;

public class RuntimeParameterAnnotationsAttribute extends Attribute {
	private final U1 numberOf;
	private final ParameterAnnotation[] parameterAnnotations;
	private final boolean visible;

	public RuntimeParameterAnnotationsAttribute(U2 nameIndex, U4 length, U1 numberOf,
	                                            ParameterAnnotation[] parameterAnnotations, boolean visible) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.parameterAnnotations = parameterAnnotations;
		this.visible = visible;
	}

	public U1 getNumberOf() {
		return numberOf;
	}

	public ParameterAnnotation[] getParameterAnnotations() {
		return parameterAnnotations;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}
}
