package com.example.jcparser.attribute;

import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;

import java.util.List;

public class RecordAttribute extends Attribute {
	private final U2 numberOf;
	private final ComponentInfo[] components;

	public RecordAttribute(U2 nameIndex, U4 length, U2 numberOf, ComponentInfo[] components) {
		super(nameIndex, length);
		this.numberOf = numberOf;
		this.components = components;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U2 getNumberOf() {
		return numberOf;
	}

	public ComponentInfo[] getComponents() {
		return components;
	}

	public record ComponentInfo(U2 nameIndex, U2 descriptorIndex, U2 numberOf, List<Attribute> attributes) {
	}
}
