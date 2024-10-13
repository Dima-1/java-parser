package com.example.jcparser.attribute.annotation;

import com.example.jcparser.Parser.U2;

public class ValuePair {
	U2 elementNameIndex;
	ElementValue elementValue;

	public ValuePair(U2 elementNameIndex, ElementValue elementValue) {
		this.elementNameIndex = elementNameIndex;
		this.elementValue = elementValue;
	}
}
