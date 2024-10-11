package com.example.jcparser.attribute;

import java.util.List;

import static com.example.jcparser.Parser.*;

public class RuntimeVisibleAnnotationsAttribute extends Attribute {
	private final U2 numberOf;
	private final RuntimeVisibleAnnotation[] annotations;

	public RuntimeVisibleAnnotationsAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex,
	                                          U4 attributeLength, U2 numberOf, RuntimeVisibleAnnotation[] annotations) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.numberOf = numberOf;
		this.annotations = annotations;
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public RuntimeVisibleAnnotation[] getAnnotations() {
		return annotations;
	}

	public static class RuntimeVisibleAnnotation {
		public RuntimeVisibleAnnotation(U2 typeIndex, U2 lengthOfPair, U2 nameIndex) {
		}
	}
}
