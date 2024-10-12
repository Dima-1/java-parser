package com.example.jcparser.attribute;

import com.example.jcparser.Parser;

public class ElementValue {
	private final Parser.U1 tag;
	private final Parser.U2 u2First;
	private final Parser.U2 u2Second;

	public ElementValue(Parser.U1 tag, Parser.U2 u2First, Parser.U2 u2Second) {
		this.tag = tag;
		this.u2First = u2First;
		this.u2Second = u2Second;
	}
}
