package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U2;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;

public class MethodParameterAttribute extends Attribute {
	private final Parser.U1 numberOf;
	private final MethodParameterAttribute.MethodParameter[] methodParameters;

	public MethodParameterAttribute(U2 attributeNameIndex, U4 attributeLength, Parser.U1 numberOf,
	                                MethodParameterAttribute.MethodParameter[] methodParameters) {
		super(attributeNameIndex, attributeLength);
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
