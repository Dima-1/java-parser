package com.example.jcparser;

import java.util.List;

import static com.example.jcparser.Parser.ConstantTag.CONSTANT_Class;

public class Attribute implements Print.Printable<Print.AttributePrinter> {
	private final Parser.U2 nameIndex;
	private final String name;
	private final Parser.U4 length;

	public Attribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex, Parser.U4 length) {
		this.nameIndex = nameIndex;
		name = constants.get(nameIndex.getValue() - 1).getAdditional();
		this.length = length;
	}

	public Parser.U2 getNameIndex() {
		return nameIndex;
	}

	public String getName() {
		return name;
	}

	public Parser.U4 getLength() {
		return length;
	}

	@Override
	public void print(Print.AttributePrinter printer) {
		printer.print(this);
	}

	public static class ConstantPoolAttribute extends Attribute {
		private final Parser.U2 constantPoolIndex;

		public ConstantPoolAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                             Parser.U4 length, Parser.U2 constantPoolIndex) {
			super(constants, nameIndex, length);
			this.constantPoolIndex = constantPoolIndex;
		}

		public Parser.U2 getConstantPoolIndex() {
			return constantPoolIndex;
		}
	}

	public static class ConstantValueAttribute extends ConstantPoolAttribute {

		public ConstantValueAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                              Parser.U4 length, Parser.U2 constantValueIndex) {
			super(constants, nameIndex, length, constantValueIndex);
			Parser.ConstantPoolEntry entry = constants.get(constantValueIndex.getValue() - 1);
			if (!entry.getConstantTag().isConstantValueAttribute()) {
				throw new RuntimeException(String.format("%04X ConstantValue = %s", constantValueIndex.getOffset(), entry.getConstantTag()));
			}
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getConstantValueIndex() {
			return getConstantPoolIndex();
		}
	}

	public static class ExceptionsAttribute extends Attribute {
		private final Parser.U2 numberOf;
		private final Parser.U2[] exceptions;

		public ExceptionsAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex, Parser.U4 length,
		                           Parser.U2 numberOf, Parser.U2[] exceptions) {
			super(constants, nameIndex, length);
			this.numberOf = numberOf;
			this.exceptions = exceptions;
			for (Parser.U2 exception : exceptions) {
				Parser.ConstantPoolEntry entry = constants.get(exception.getValue() - 1);
				if (entry.getConstantTag() != CONSTANT_Class) {
					throw new RuntimeException(String.format("%04X ExceptionValue = %s", exception.getOffset(), entry.getConstantTag()));
				}
			}
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getNumberOf() {
			return numberOf;
		}

		public Parser.U2[] getExceptions() {
			return exceptions;
		}
	}

	public static class SourceFileAttribute extends ConstantPoolAttribute {

		public SourceFileAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                           Parser.U4 length, Parser.U2 sourceFileIndex) {
			super(constants, nameIndex, length, sourceFileIndex);
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getSourceFileIndex() {
			return getConstantPoolIndex();
		}
	}

	public static class SignatureAttribute extends ConstantPoolAttribute {

		public SignatureAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                          Parser.U4 length, Parser.U2 signatureIndex) {
			super(constants, nameIndex, length, signatureIndex);
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getSignatureIndex() {
			return getConstantPoolIndex();
		}
	}

	public static class NestMembersAttribute extends Attribute {
		private final Parser.U2 numberOfClasses;
		private final Parser.U2[] classes;

		public NestMembersAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                            Parser.U4 length, Parser.U2 numberOfClasses, Parser.U2[] classes) {
			super(constants, nameIndex, length);
			this.numberOfClasses = numberOfClasses;
			this.classes = classes;
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getNumberOfClasses() {
			return numberOfClasses;
		}

		public Parser.U2[] getClasses() {
			return classes;
		}
	}

	public static class InnerClassesAttribute extends Attribute {
		private final Parser.U2 numberOf;
		private final InnerClass[] innerClasses;

		public InnerClassesAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                             Parser.U4 length, Parser.U2 numberOf, InnerClass[] innerClasses) {
			super(constants, nameIndex, length);
			this.numberOf = numberOf;
			this.innerClasses = innerClasses;
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getNumberOf() {
			return numberOf;
		}

		public InnerClass[] getInnerClasses() {
			return innerClasses;
		}
	}

	public static class BootstrapMethodsAttribute extends Attribute {
		private final Parser.U2 numberOf;
		private final BootstrapMethod[] bootstrapMethods;

		public BootstrapMethodsAttribute(List<Parser.ConstantPoolEntry> constants, Parser.U2 nameIndex,
		                                 Parser.U4 length, Parser.U2 numberOf, BootstrapMethod[] bootstrapMethods) {
			super(constants, nameIndex, length);
			this.numberOf = numberOf;
			this.bootstrapMethods = bootstrapMethods;
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getNumberOf() {
			return numberOf;
		}

		public BootstrapMethod[] getBootstrapMethods() {
			return bootstrapMethods;
		}
	}

	public record BootstrapMethod(int index, Parser.U2 bootstrapMethodRef,
	                              Parser.U2[] bootstrapArguments) implements Print.Printable<Print.AttributePrinter> {

		@Override
		public void print(Print.AttributePrinter printer) {
			printer.print(this);
		}
	}

	public record InnerClass(Parser.U2 innerClassInfoIndex, Parser.U2 outerClassInfoIndex, Parser.U2 innerNameIndex,
	                         Parser.U2 innerClassAccessFlags) implements Print.Printable<Print.AttributePrinter> {

		@Override
		public void print(Print.AttributePrinter printer) {
			printer.print(this);
		}
	}
}
