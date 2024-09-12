
import java.util.List;

public class Attribute implements Print.Printable<Print.AttributePrinter> {
	private final Parser.U2 nameIndex;
	private final String name;
	private final Parser.U4 length;

	public Attribute(List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex, Parser.U4 length) {
		this.nameIndex = nameIndex;
		name = constants.get(nameIndex.getValue() - 1).getAdditional(constants);
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

	public static class SourceFileAttribute extends Attribute {
		private final Parser.U2 sourceFileIndex;

		public SourceFileAttribute(List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex,
		                           Parser.U4 length, Parser.U2 sourceFileIndex) {
			super(constants, nameIndex, length);
			this.sourceFileIndex = sourceFileIndex;
		}

		@Override
		public void print(Print.AttributePrinter printer) {
			super.print(printer);
			printer.print(this);
		}

		public Parser.U2 getSourceFileIndex() {
			return sourceFileIndex;
		}
	}

	public static class NestMembersAttribute extends Attribute {
		private final Parser.U2 numberOfClasses;
		private final Parser.U2[] classes;

		public NestMembersAttribute(List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex,
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

		public InnerClassesAttribute(List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex,
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

		public BootstrapMethodsAttribute(List<Parser.ConstantPoolRecord> constants, Parser.U2 nameIndex,
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
