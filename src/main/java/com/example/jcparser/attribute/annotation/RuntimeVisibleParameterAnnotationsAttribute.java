package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

public class RuntimeVisibleParameterAnnotationsAttribute extends RuntimeParameterAnnotationsAttribute {
	public RuntimeVisibleParameterAnnotationsAttribute(U2 nameIndex, U4 length, U1 numberOf,
	                                                   RuntimeAnnotationsAttribute[] parameterAnnotations) {
		super(nameIndex, length, numberOf, parameterAnnotations);
	}
}
