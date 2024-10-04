package com.example.jcparser.attribute;

import com.example.jcparser.AccessFlag;
import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.stackmapframe.StackMapFrame;

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
		print.u2(attr.getExceptionTableLength(), "Exceptions table length");
		for (ExceptionsAttribute.Exception exception : attr.getExceptions()) {
			exception.print(this);
		}
		print.u2(attr.getNumberOf(), "Attributes table length", "", true);
		print.attributes(attr.getAttributes());
	}

	void print(StackMapTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of stack maps", "", true);
		for (StackMapFrame stackMapFrame : attr.getEntries()) {
			stackMapFrame.print(print.getStackFramePrinter());
		}
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
			print.u2(exceptions[i], String.format("%5X ", i) + "Exception");
		}
	}

	void print(InnerClassesAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of inner classes", "", true);
		print.incIndent();
		for (InnerClassesAttribute.InnerClass innerClass : attr.getInnerClasses()) {
			innerClass.print(this);
		}
		print.decIndent();
	}

	void print(EnclosingMethodAttribute attr) {
		print.u2(attr.getClassIndex(), "Attribute class index");
		print.u2(attr.getMethodIndex(), "Attribute method index");
	}

	void print(SourceFileAttribute attr) {
		print.u2(attr.getSourceFileIndex(), "Attribute source file index");
	}

	void print(SourceDebugExtensionAttribute attr) {
		print.debugInfo(attr.getLength().getOffset() + Parser.U4.getSize(), attr.getUtf8());
	}

	void print(LineNumberTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of lines", "", true);
		print.incIndent();
		for (LineNumberTableAttribute.LineNumber lineNumber : attr.getLineNumberTable()) {
			lineNumber.print(this);
		}
		print.decIndent();
	}

	void print(LineNumberTableAttribute.LineNumber lineNumber) {
		print.u2(lineNumber.startPC(), "Start PC");
		print.u2(lineNumber.lineNumber(), "Line number", "", true);
	}

	void print(LocalVariableTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of local variables", "", true);
		print.incIndent();
		for (LocalVariableTableAttribute.LocalVariable localVariable : attr.getLocalVariables()) {
			localVariable.print(this);
		}
		print.decIndent();
	}

	void print(LocalVariableAttribute.LocalVariable localVariable) {
		print.u2(localVariable.startPC(), "Start PC");
		print.u2(localVariable.length(), "Length", "", true);
		print.u2(localVariable.nameIndex(), "Name index");
		print.u2(localVariable.descriptorIndex(), localVariable.descriptorTitle() + " index");
		print.u2(localVariable.index(), "Index", "", true);
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
			print.u2(classes[i], String.format("%5X ", i) + "Nest");
		}
	}

	void print(InnerClassesAttribute.InnerClass innerClass) {
		var formatedIndex = String.format("%5X ", innerClass.index());
		print.decIndent();
		print.u2(innerClass.innerClassInfoIndex(), formatedIndex + "Inner class");
		print.incIndent();
		print.u2(innerClass.outerClassInfoIndex(), "Outer class");
		print.u2(innerClass.innerNameIndex(), "Inner name index");
		print.accessFlags(innerClass.innerClassAccessFlags(), AccessFlag.Type.INNERCLASS);
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
		var formatedIndex = String.format("%5X ", bootstrapMethod.index());
		print.u2(bootstrapMethod.bootstrapMethodRef(), formatedIndex + "Bootstrap method");
		print.u2(bootstrapMethod.numberOf(), "Number of arguments", "", true);
		for (Parser.U2 u2 : bootstrapMethod.bootstrapArguments()) {
			print.u2(u2, "Argument");
		}
	}
}
