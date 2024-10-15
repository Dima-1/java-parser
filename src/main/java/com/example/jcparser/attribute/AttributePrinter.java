package com.example.jcparser.attribute;

import com.example.jcparser.AccessFlag;
import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.annotation.*;
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

	void print(ExceptionsAttribute.Exception attr) {
		print.u2(attr.startPc(), "Attribute exception start pc");
		print.u2(attr.endPc(), "Attribute exception end pc");
		print.u2(attr.handlerPc(), "Attribute handler start pc");
		print.u2(attr.catchType(), "Exception handler class");
	}

	void print(StackMapTableAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of stack maps", "", true);
		for (StackMapFrame stackMapFrame : attr.getEntries()) {
			stackMapFrame.print(print.getStackFramePrinter());
		}
	}

	void print(ExceptionsAttribute attr) {
		print.u2array(attr.getExceptions(), "Attribute number of exceptions", "Exception");
	}

	void print(InnerClassesAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of inner classes", "", true);
		print.incIndent();
		for (InnerClassesAttribute.InnerClass innerClass : attr.getInnerClasses()) {
			innerClass.print(this);
		}
		print.decIndent();
	}

	void print(InnerClassesAttribute.InnerClass innerClass) {
		print.u2WithIndex(innerClass.index(), innerClass.innerClassInfoIndex(), "Inner class");
		print.u2(innerClass.outerClassInfoIndex(), "Outer class");
		print.u2(innerClass.innerNameIndex(), "Inner name index");
		print.accessFlags(innerClass.innerClassAccessFlags(), AccessFlag.Type.INNERCLASS);
	}

	void print(EnclosingMethodAttribute attr) {
		print.u2(attr.getClassIndex(), "Attribute class index");
		print.u2(attr.getMethodIndex(), "Attribute method index");
	}

	void print(SignatureAttribute attr) {
		print.u2(attr.getSignatureIndex(), "Attribute signature index");
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
		print.u2WithIndex(lineNumber.index(), lineNumber.startPC(), "Start PC");
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
		print.u2WithIndex(localVariable.index().getValue(), localVariable.startPC(), "Start PC");
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

	public void print(RuntimeVisibleAnnotationsAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of visible annotation", "", true);
		print.incIndent();
		for (RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation visibleAnnotation : attr.getAnnotations()) {
			visibleAnnotation.print(this);
		}
		print.decIndent();
	}

	public void print(RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation attr) {
		print.u2(attr.typeIndex(), "Type of annotation", "", true);
		print.u2(attr.lengthOfPair(), "Number of value pair", "", true);
		for (ValuePair valuePair : attr.valuePairs()) {
			valuePair.print(this);
		}
	}

	public void print(ValuePair valuePair) {
		print.u2(valuePair.elementNameIndex(), "Type of annotation", "", true);
		valuePair.elementValue().print(this);
	}

	public void print(ElementValue elementValue) {
		print.u1(elementValue.tag(), "Tag");
		int tag = elementValue.tag().getValue();
		switch (TagValueItem.getTagValue(tag)) {
			case CONST_VALUE_INDEX -> print.u2(elementValue.u2First(), "Constant value index");
			case ENUM_CONST_VALUE -> {
				print.u2(elementValue.u2First(), "Type name index");
				print.u2(elementValue.u2Second(), "Constant name index");
			}
			case CLASS_INFO_INDEX -> print.u2(elementValue.u2First(), "Class info index");
			case ANNOTATION_VALUE -> elementValue.annotation().print(this);
			case ARRAY_VALUE -> {
				print.u2(elementValue.u2First(), "Number of values");
				ElementValue[] elementValues = elementValue.elementValues();
				for (int i = 0; i < elementValue.u2First().getValue(); i++) {
					elementValues[i].print(this);
				}
			}
		}
	}

	public void print(RuntimeInvisibleAnnotationsAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of invisible annotation", "", true);
		print.incIndent();
		for (RuntimeVisibleAnnotationsAttribute.RuntimeVisibleAnnotation visibleAnnotation : attr.getAnnotations()) {
			visibleAnnotation.print(this);
		}
		print.decIndent();
	}

	void print(BootstrapMethodsAttribute attr) {
		print.u2(attr.getNumberOf(), "Attribute number of bootstrap methods", "", true);
		for (BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod : attr.getBootstrapMethods()) {
			bootstrapMethod.print(this);
		}
	}

	void print(BootstrapMethodsAttribute.BootstrapMethod bootstrapMethod) {
		print.u2WithIndex(bootstrapMethod.index(), bootstrapMethod.bootstrapMethodRef(), "Bootstrap method");
		print.u2array(bootstrapMethod.bootstrapArguments(), "Number of arguments", "Argument");
	}

	void print(MethodParameterAttribute attr) {
		print.u1(attr.getNumberOf(), "Parameter count", true);
		print.incIndent();
		for (MethodParameterAttribute.MethodParameter methodParameter : attr.getMethodParameters()) {
			methodParameter.print(this);
		}
		print.decIndent();
	}

	void print(MethodParameterAttribute.MethodParameter methodParameter) {
		print.u2WithIndex(methodParameter.index(), methodParameter.nameIndex(), "Method parameter");
		print.accessFlags(methodParameter.accessFlag(), AccessFlag.Type.PARAMETERS);
	}

	void print(ModuleAttribute attr) {
		print.u2(attr.getModuleNameIndex(), "Attribute module name index", "", true);
		print.accessFlags(attr.getModuleFlags(), AccessFlag.Type.MODULE);
		print.u2(attr.getModuleVersionIndex(), "Attribute module version index", "", true);
		print.u2(attr.getRequiresCount(), "Requires count", "", true);
		print.incIndent();
		for (ModuleAttribute.Requires require : attr.getRequires()) {
			require.print(this);
		}
		print.decIndent();
		print.u2(attr.getExportsCount(), "Exports count", "", true);
		print.incIndent();
		for (ModuleAttribute.Exports export : attr.getExports()) {
			export.print(this);
		}
		print.decIndent();
		print.u2(attr.getOpensCount(), "Opens count", "", true);
		print.incIndent();
		for (ModuleAttribute.Opens open : attr.getOpens()) {
			open.print(this);
		}
		print.decIndent();
		print.u2array(attr.getUses(), "Uses count", "Uses");
		print.u2(attr.getProvidesCount(), "Provides count", "", true);
		print.incIndent();
		for (ModuleAttribute.Provides provide : attr.getProvides()) {
			provide.print(this);
		}
	}

	void print(ModuleAttribute.Requires attr) {
		print.u2WithIndex(attr.index(), attr.requiresIndex(), "Requires index");
		print.accessFlags(attr.accessFlag(), AccessFlag.Type.REQUIRES);
		print.u2(attr.requiresVersionIndex(), "Requires version index");
	}

	void print(ModuleAttribute.Exports attr) {
		print.u2WithIndex(attr.index(), attr.exportsIndex(), "Export index");
		print.accessFlags(attr.accessFlag(), AccessFlag.Type.EXPORTS);
		print.u2array(attr.exportsToIndex(), "Export to index", "Export");
	}

	void print(ModuleAttribute.Opens attr) {
		print.u2WithIndex(attr.index(), attr.opensIndex(), "Open index");
		print.accessFlags(attr.accessFlag(), AccessFlag.Type.OPENS);
		print.u2array(attr.opensToIndex(), "Open to index", "Open");
	}

	void print(ModuleAttribute.Provides attr) {
		print.u2WithIndex(attr.index(), attr.providesIndex(), "Provides index");
		print.u2array(attr.providesWithIndex(), "Provides with index", "Provides with");
	}

	void print(ModulePackagesAttribute attr) {
		print.u2array(attr.getPackages(), "Attribute number of packages", "Package");
	}

	void print(ModuleMainClassAttribute attr) {
		print.u2(attr.getMainClassIndex(), "Module main class");
	}

	void print(NestHostAttribute attr) {
		print.u2(attr.getHostClassIndex(), "Host class");
	}

	void print(NestMembersAttribute attr) {
		print.u2array(attr.getClasses(), "Attribute number of classes", "Nest");
	}
}
