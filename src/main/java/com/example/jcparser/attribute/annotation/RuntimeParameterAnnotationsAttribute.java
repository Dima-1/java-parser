package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.attribute.Attribute;

public class RuntimeParameterAnnotationsAttribute extends Attribute {
	private final U1 numberOf;
	private final RuntimeAnnotationsAttribute[] parameterAnnotations;

	public RuntimeParameterAnnotationsAttribute(U2 nameIndex, U4 length, U1 numberOf,
	                                            RuntimeAnnotationsAttribute[] parameterAnnotations) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.parameterAnnotations = parameterAnnotations;
	}

	public U1 getNumberOf() {
		return numberOf;
	}

	public RuntimeAnnotationsAttribute[] getParameterAnnotations() {
		return parameterAnnotations;
	}
}
