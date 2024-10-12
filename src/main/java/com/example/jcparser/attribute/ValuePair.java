package com.example.jcparser.attribute;

import com.example.jcparser.Parser;

public class ValuePair {
	Parser.U2 elementNameIndex;
	ElementValue elemenValue;

	public ValuePair(Parser.U2 elementNameIndex, ElementValue elemenValue) {
		this.elementNameIndex = elementNameIndex;
		this.elemenValue = elemenValue;
	}
}
