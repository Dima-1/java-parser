package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;

import java.util.List;

import com.example.jcparser.Parser.*;

public class MethodParameterAttribute extends Attribute {
	private final Parser.U1 numberOf;
	private final MethodParameterAttribute.MethodParameter[] methodParameters;

	public MethodParameterAttribute(List<ConstantPoolEntry> constantPool, U2 attributeNameIndex, U4 attributeLength,
	                                Parser.U1 numberOf, MethodParameterAttribute.MethodParameter[] methodParameters) {
		super(constantPool, attributeNameIndex, attributeLength);
		this.numberOf = numberOf;
		this.methodParameters = methodParameters;
	}

	@Override
	public void print(AttributePrinter printer) {
		super.print(printer);
		printer.print(this);
	}

	public U1 getNumberOf() {
		return numberOf;
	}

	public MethodParameter[] getMethodParameters() {
		return methodParameters;
	}

	public record MethodParameter(int index, U2 nameIndex, U2 accessFlag)
			implements Print.Printable<AttributePrinter> {

		@Override
		public void print(AttributePrinter printer) {
			printer.print(this);
		}
	}
}
