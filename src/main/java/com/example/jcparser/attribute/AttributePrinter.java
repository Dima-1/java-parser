package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.Type;

public class AttributePrinter {
	private final Print print;

	public AttributePrinter(Print print) {
		this.print = print;
	}

	void print(Attribute attr) {
		print.u2(attr.getNameIndex(), "Attribute name index");
		print.u4(attr.getLength(), "Attribute length");
	}

	void print(ConstantValueAttribute attr) {
		print.u2(attr.getConstantValueIndex(), "Attribute constant value index");
	}

	void print(CodeAttribute attr) {
		print.u2(attr.getMaxStack(), "Attribute max stack");
		print.u2(attr.getMaxLocals(), "Attribute max local");
		print.u4(attr.getCodeLength(), "Code length");
		print.opcodes(attr.getOpcodes());
		print.u2(attr.getExceptionTableLength(), "Exception table length");
		for (ExceptionsAttribute.Exception exception : attr.getExceptions()) {
			exception.print(this);
		}
		print.u2(attr.getNumberOf(), "Attribute table length");
		print.attributes(attr.getAttributes());
	}

	void print(ExceptionsAttribute.Exception attr) {
		print.u2(attr.startPc(), "Attribute exception start pc");
		print.u2(attr.endPc(), "Attribute exception end pc");
		print.u2(attr.handlerPc(), "Attribute handler start pc");
		print.u2(attr.catchType(), "Exception handler class");
	}

	void print(ExceptionsAttribute attr) {
		Parser.U2[] exceptions = attr.getExceptions();
		print.u2(attr.getNumberOf(), "Attribute number of exceptions", "", true);
		for (int i = 0; i < exceptions.length; i++) {
			print.u2(exceptions[i], String.format("%4X ", i) + "Exception");
		}
	}

	void print(SourceFileAttribute attr) {
		print.u2(attr.getSourceFileIndex(), "Attribute source file index");
	}

	void print(LineNumberTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of lines", "", true);
		for (LineNumberTableAttribute.LineNumber lineNumber : attr.getLineNumberTable()) {
			lineNumber.print(this);
		}
	}

	void print(LineNumberTableAttribute.LineNumber lineNumber) {
		print.u2(lineNumber.startPC(), Print.SP_5 + "Start PC");
		print.u2(lineNumber.lineNumber(), Print.SP_5 + "Line number", "", true);
	}

	void print(LocalVariableTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of local variables", "", true);
		for (LocalVariableTableAttribute.LocalVariable localVariable : attr.getLocalVariables()) {
			localVariable.print(this);
		}
	}

	void print(LocalVariableAttribute.LocalVariable localVariable) {
		print.u2(localVariable.startPC(), Print.SP_5 + "Start PC");
		print.u2(localVariable.length(), Print.SP_5 + "Length", "", true);
		print.u2(localVariable.nameIndex(), Print.SP_5 + "Name index");
		print.u2(localVariable.descriptorIndex(), Print.SP_5 + localVariable.descriptorTitle() + " index");
		print.u2(localVariable.index(), Print.SP_5 + "Index", "", true);
	}

	void print(LocalVariableTypeTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of local variable types", "", true);
		for (LocalVariableAttribute.LocalVariable localVariable : attr.getLocalVariables()) {
			localVariable.print(this);
		}
	}

	void print(NestMembersAttribute attr) {
		Parser.U2[] classes = attr.getClasses();
		print.u2(attr.getNumberOfClasses(), "Attribute number of classes", "", true);
		for (int i = 0; i < classes.length; i++) {
			print.u2(classes[i], String.format("%4X ", i) + "Nest");
		}
	}

	void print(InnerClassesAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of inner classes", "", true);
		for (InnerClassesAttribute.InnerClass innerClass : attr.getInnerClasses()) {
			innerClass.print(this);
		}
	}

	void print(InnerClassesAttribute.InnerClass innerClass) {
		print.u2(innerClass.innerClassInfoIndex(), Print.SP_5 + "Inner class");
		print.u2(innerClass.outerClassInfoIndex(), Print.SP_5 + "Outer class");
		print.u2(innerClass.innerNameIndex(), Print.SP_5 + "Inner name index");
		print.accessFlags(innerClass.innerClassAccessFlags(), Type.CLASS, Print.SP_5 + "Inner class access flags");
	}

	void print(SignatureAttribute attr) {
		print.u2(attr.getSignatureIndex(), "Attribute signature index");
	}

	void print(BootstrapMethodsAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of bootstrap methods", "", true);
		for (BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod : attr.getBootstrapMethods()) {
			bootstrapMethod.print(this);
		}
	}

	void print(BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod) {
		var formatedIndex = String.format("%4X ", bootstrapMethod.index());
		print.u2(bootstrapMethod.bootstrapMethodRef(), formatedIndex + "Bootstrap method");
		print.u2(bootstrapMethod.numberOf(), Print.SP_5 + "Number of arguments", "", true);
		for (Parser.U2 u2 : bootstrapMethod.bootstrapArguments()) {
			print.u2(u2, Print.SP_5 + "Argument");
		}
	}
}
