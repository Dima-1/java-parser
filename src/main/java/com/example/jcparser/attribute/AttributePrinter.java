package com.example.jcparser.attribute;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.Type;
import com.example.jcparser.attribute.stackmapframe.StackMapFrame;

public class AttributePrinter {
	private final Print print;
	public int indent = 0;

	public AttributePrinter(Print print) {
		this.print = print;
	}

	void print(Attribute attr) {
		print.u2(attr.getNameIndex(), getIndent() + "Attribute name index");
		print.u4(attr.getLength(), getIndentOver(-1) + "Attribute length");
	}

	void print(ConstantValueAttribute attr) {
		print.u2(attr.getConstantValueIndex(), getIndent() + "Attribute constant value index");
	}

	void print(CodeAttribute attr) {
		print.u2(attr.getMaxStack(), getIndent() + "Attribute max stack");
		print.u2(attr.getMaxLocals(), getIndent() + "Attribute max local");
		print.u4(attr.getCodeLength(), getIndentOver(-1) + "Code length");
		print.opcodes(attr.getOpcodes());
		print.u2(attr.getExceptionTableLength(), getIndent() + "Exceptions table length");
		for (ExceptionsAttribute.Exception exception : attr.getExceptions()) {
			exception.print(this);
		}
		print.u2(attr.getNumberOf(), getIndent() + "Attributes table length", "", true);
		print.attributes(attr.getAttributes());
	}

	void print(StackMapTableAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of stack maps", "", true);
		for (StackMapFrame stackMapFrame : attr.getEntries()) {
			stackMapFrame.print(print.getStackFramePrinter());
		}
	}

	void print(ExceptionsAttribute.Exception attr) {
		print.u2(attr.startPc(), getIndent() + "Attribute exception start pc");
		print.u2(attr.endPc(), getIndent() + "Attribute exception end pc");
		print.u2(attr.handlerPc(), getIndent() + "Attribute handler start pc");
		print.u2(attr.catchType(), getIndent() + "Exception handler class");
	}

	void print(ExceptionsAttribute attr) {
		Parser.U2[] exceptions = attr.getExceptions();
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of exceptions", "", true);
		for (int i = 0; i < exceptions.length; i++) {
			print.u2(exceptions[i], getIndent() + String.format("%5X ", i) + "Exception");
		}
	}

	void print(InnerClassesAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of inner classes", "", true);
		indent++;
		for (InnerClassesAttribute.InnerClass innerClass : attr.getInnerClasses()) {
			innerClass.print(this);
		}
		indent--;
	}

	void print(EnclosingMethodAttribute attr) {
		print.u2(attr.getClassIndex(), getIndent() + "Attribute class index");
		print.u2(attr.getMethodIndex(), getIndent() + "Attribute method index");
	}

	void print(SourceFileAttribute attr) {
		print.u2(attr.getSourceFileIndex(), getIndent() + "Attribute source file index");
	}

	void print(SourceDebugExtensionAttribute attr) {
		print.debugInfo(attr.getLength().getOffset() + Parser.U4.getSize(), attr.getUtf8());
	}

	void print(LineNumberTableAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of lines", "", true);
		indent++;
		for (LineNumberTableAttribute.LineNumber lineNumber : attr.getLineNumberTable()) {
			lineNumber.print(this);
		}
		indent--;
	}

	void print(LineNumberTableAttribute.LineNumber lineNumber) {
		print.u2(lineNumber.startPC(), getIndent() + "Start PC");
		print.u2(lineNumber.lineNumber(), getIndent() + "Line number", "", true);
	}

	void print(LocalVariableTableAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of local variables", "", true);
		indent++;
		for (LocalVariableTableAttribute.LocalVariable localVariable : attr.getLocalVariables()) {
			localVariable.print(this);
		}
		indent--;
	}

	void print(LocalVariableAttribute.LocalVariable localVariable) {
		print.u2(localVariable.startPC(), getIndent() + "Start PC");
		print.u2(localVariable.length(), getIndent() + "Length", "", true);
		print.u2(localVariable.nameIndex(), getIndent() + "Name index");
		print.u2(localVariable.descriptorIndex(), getIndent() + localVariable.descriptorTitle() + " index");
		print.u2(localVariable.index(), getIndent() + "Index", "", true);
	}

	void print(LocalVariableTypeTableAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of local variable types", "", true);
		for (LocalVariableAttribute.LocalVariable localVariable : attr.getLocalVariables()) {
			localVariable.print(this);
		}
	}

	void print(NestMembersAttribute attr) {
		Parser.U2[] classes = attr.getClasses();
		print.u2(attr.getNumberOfClasses(), getIndent() + "Attribute number of classes", "", true);
		for (int i = 0; i < classes.length; i++) {
			print.u2(classes[i], getIndent() + String.format("%5X ", i) + "Nest");
		}
	}

	void print(InnerClassesAttribute.InnerClass innerClass) {
		var formatedIndex = String.format(getIndentOver(-1) + "%5X ", innerClass.index());
		print.u2(innerClass.innerClassInfoIndex(), formatedIndex + "Inner class");
		print.u2(innerClass.outerClassInfoIndex(), getIndent() + "Outer class");
		print.u2(innerClass.innerNameIndex(), getIndent() + "Inner name index");
		print.accessFlags(innerClass.innerClassAccessFlags(), Type.CLASS, getIndent() + "Inner class access flags");
	}

	void print(SignatureAttribute attr) {
		print.u2(attr.getSignatureIndex(), getIndent() + "Attribute signature index");
	}

	void print(BootstrapMethodsAttribute attr) {
		print.u2(attr.getNumberOf(), getIndent() + "Attribute number of bootstrap methods", "", true);
		for (BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod : attr.getBootstrapMethods()) {
			bootstrapMethod.print(this);
		}
	}

	void print(BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod) {
		var formatedIndex = String.format("%5X ", bootstrapMethod.index());
		print.u2(bootstrapMethod.bootstrapMethodRef(), formatedIndex + "Bootstrap method");
		print.u2(bootstrapMethod.numberOf(), getIndent() + "Number of arguments", "", true);
		for (Parser.U2 u2 : bootstrapMethod.bootstrapArguments()) {
			print.u2(u2, getIndent() + "Argument");
		}
	}

	private String getIndent() {
		return " ".repeat(6 * indent);
	}

	private String getIndentOver(int val) {
		return " ".repeat(6 * (indent + val));
	}
}
