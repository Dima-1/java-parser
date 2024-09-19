package com.example.jcparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jcparser.Attribute.*;
import static com.example.jcparser.Parser.*;

public class Print {

	public static final boolean PRINT_CONSTANT_POOL = true;
	public static final String YELLOW_STRING = ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET;
	public static final String SP_5 = " ".repeat(5);
	public static final String HEX_2 = " (%02X)";

	private final AttributePrinter attributePrinter = new AttributePrinter();
	private final ConstantPrinter constantPrinter = new ConstantPrinter();
	private String OFFSET_FORMAT = "%04X ";

	public void setLength(long length) {
		OFFSET_FORMAT = "%0" + Long.toHexString(length).length() + "X ";
	}

	void u2(U2 u2, String title) {
		u2(u2, title, "", false);
	}

	void u2(U2 u2, String title, String titleColor, boolean addDecimal) {
		String hexValue = String.format("%04X", u2.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String decimalValue = String.format(addDecimal ? "(%02d)" : "", u2.getValue());
		System.out.printf(OFFSET_FORMAT + "%s " + titleColor + "%s" + ConsoleColors.RESET + " %s" + YELLOW_STRING + "\n",
				u2.getOffset(), splitHexValue, title, decimalValue, u2.getSymbolic());
	}

	void u4(U4 u4, String title) {
		String hexValue = String.format("%08X", u4.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		System.out.printf(OFFSET_FORMAT + "%s %s" + YELLOW_STRING + "\n",
				u4.getOffset(), splitHexValue, title, u4.getSymbolic());
	}

	private static StringBuilder getSplitHexValue(String hexValue) {
		StringBuilder splitHexValue = new StringBuilder(hexValue);
		for (var i = 2; i < splitHexValue.length(); i += 3) {
			splitHexValue.insert(i, " ");
		}
		return splitHexValue;
	}

	public void constantPool(List<ConstantPoolEntry> constants) {
		if (!PRINT_CONSTANT_POOL) {
			return;
		}
		for (int i = 0; i < constants.size(); i++) {
			ConstantPoolEntry entry = constants.get(i);
			entry.print(constantPrinter);
			constantPrinter.print();
			if (entry.getConstantTag().isTwoEntriesTakeUp()) {
				i++;
			}
		}
	}

	void accessFlags(U2 u2, Type type) {
		accessFlags(u2, type, "Access flags");
	}

	void accessFlags(U2 u2, Type type, String title) {
		Map<Integer, String> flagsMap = switch (type) {
			case CLASS -> Map.ofEntries(
					Map.entry(0x0001, "ACC_PUBLIC"),     //   Declared public; may be accessed from outside its package.
					Map.entry(0x0002, "ACC_PRIVATE"),    //   Marked private in source. (Nested)
					Map.entry(0x0004, "ACC_PROTECTED"),  //   Marked protected in source. (Nested)
					Map.entry(0x0008, "ACC_STATIC"),     //   Marked or implicitly static in source. (Nested)
					Map.entry(0x0010, "ACC_FINAL"),      //   Declared final; no subclasses allowed.
					Map.entry(0x0020, "ACC_SUPER"),      //   Treat superclass methods specially when invoked by the invokespecial instruction.
					Map.entry(0x0200, "ACC_INTERFACE"),  //   Is an interface, not a class.
					Map.entry(0x0400, "ACC_ABSTRACT"),   //   Declared abstract; must not be instantiated.
					Map.entry(0x1000, "ACC_SYNTHETIC"),  //   Declared synthetic; not present in the source code.
					Map.entry(0x2000, "ACC_ANNOTATION"), //   Declared as an annotation type.
					Map.entry(0x4000, "ACC_ENUM"),       //   Declared as an enum type.
					Map.entry(0x8000, "ACC_MODULE")      //   Is a module, not a class or interface.*/
			);
			case FIELD -> Map.of(
					0x0001, "ACC_PUBLIC",    // Declared public; may be accessed from outside its package.
					0x0002, "ACC_PRIVATE",   // Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
					0x0004, "ACC_PROTECTED", // Declared protected; may be accessed within subclasses.
					0x0008, "ACC_STATIC",    // Declared static.
					0x0010, "ACC_FINAL",     // Declared final; never directly assigned to after object construction (JLS §17.5).
					0x0040, "ACC_VOLATILE",  // Declared volatile; cannot be cached.
					0x0080, "ACC_TRANSIENT", // Declared transient; not written or read by a persistent object manager.
					0x1000, "ACC_SYNTHETIC", // Declared synthetic; not present in the source code.
					0x4000, "ACC_ENUM"       // Declared as an element of an enum.
			);
			case METHOD -> Map.ofEntries(
					Map.entry(0x0001, "ACC_PUBLIC"),       //Declared public; may be accessed from outside its package.
					Map.entry(0x0002, "ACC_PRIVATE"),      //Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
					Map.entry(0x0004, "ACC_PROTECTED"),    //Declared protected; may be accessed within subclasses.
					Map.entry(0x0008, "ACC_STATIC"),       //Declared static.
					Map.entry(0x0010, "ACC_FINAL"),        //Declared final; must not be overridden (§5.4.5).
					Map.entry(0x0020, "ACC_SYNCHRONIZED"), //Declared synchronized; invocation is wrapped by a monitor use.
					Map.entry(0x0040, "ACC_BRIDGE"),       //A bridge method, generated by the compiler.
					Map.entry(0x0080, "ACC_VARARGS"),      //Declared with variable number of arguments.
					Map.entry(0x0100, "ACC_NATIVE"),       //Declared native; implemented in a language other than the Java programming language.
					Map.entry(0x0400, "ACC_ABSTRACT"),     //Declared abstract; no implementation is provided.
					Map.entry(0x0800, "ACC_STRICT"),       //Declared strictfp; floating-point mode is FP-strict.
					Map.entry(0x1000, "ACC_SYNTHETIC")     //Declared synthetic; not present in the source code.
			);
			default -> new HashMap<>();
		};

		String flags = getAccessFlags(u2.getValue(), flagsMap);
		System.out.printf(OFFSET_FORMAT + "%s %s" + YELLOW_STRING + "\n", u2.getOffset(),
				getSplitHexValue(String.format("%04X", u2.getValue())), title, flags);
	}

	private String getAccessFlags(int value, Map<Integer, String> flagsMap) {
		StringBuilder res = new StringBuilder();
		for (int bit = 1; bit < 0x100000; bit <<= 1) {
			String flag = flagsMap.get(value & bit);
			if (flag != null && !flag.isEmpty()) {
				res.append(" ").append(flag.replaceFirst("ACC_", ""));
			}
		}
		return res.toString().toLowerCase();
	}

	public void attributes(Map<String, Attribute> attributes) {
		for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
			Attribute attr = entry.getValue();
			attr.print(attributePrinter);
		}
	}

	public interface Printable<T> {
		void print(T printer);
	}

	public class ConstantPrinter {
		private String formatedString;
		private boolean printGeneral = true;

		void format(ConstantPoolEntry cpe) {
			if (printGeneral) {
				String cpName = cpe.getConstantTag().name().replaceFirst("^CONSTANT_", "");
				formatedString = String.format(OFFSET_FORMAT + " %4X %19s", cpe.getOffset(), cpe.getIdx(), cpName);
				printGeneral = false;
			}
		}

		void format(ConstantPoolUtf8 cpe) {
			formatedString += " " + String.format(YELLOW_STRING, cpe.getUTF8());
		}

		void format(ConstantPoolInteger cpe) {
			formatedString += " " + cpe.getValue();
		}

		void format(ConstantPoolFloat cpe) {
			formatedString += " " + cpe.getValue();
		}

		void format(ConstantPoolLong cpe) {
			formatedString += " " + cpe.getValue();
		}

		void format(ConstantPoolDouble cpe) {
			formatedString += " " + cpe.getValue();
		}

		void format(ConstantPoolString cpe) {
			formatedString += String.format(HEX_2, cpe.getStringIndex());
			cpe.constants.get(cpe.getStringIndex() - 1).print(this);
		}

		void format(ConstantPoolMethodRef cpe) {
			formatedString += String.format(HEX_2, cpe.getClassIndex());
			cpe.constants.get(cpe.getClassIndex() - 1).print(this);
			formatedString += String.format(HEX_2, cpe.getNameAndTypeIndex());
			cpe.constants.get(cpe.getNameAndTypeIndex() - 1).print(this);
		}

		void format(ConstantPoolNameAndType cpe) {
			formatedString += String.format(HEX_2, cpe.getNameIndex());
			cpe.constants.get(cpe.getNameIndex() - 1).print(this);
			formatedString += String.format(HEX_2, cpe.getDescriptorIndex());
			cpe.constants.get(cpe.getDescriptorIndex() - 1).print(this);
		}

		void format(ConstantPoolDynamic cpe) {
			formatedString += String.format(HEX_2, cpe.getBootstrapMethodAttrIndex());
			formatedString += String.format(HEX_2, cpe.getNameAndTypeIndex());
			cpe.constants.get(cpe.getNameAndTypeIndex() - 1).print(this);
		}

		void format(ConstantPoolMethodHandle cpe) {
			formatedString += " " + ConstantPoolMethodHandle.MHRef.values()[cpe.getReferenceKind()].name()
					.replaceFirst("REF_", "");
			formatedString += String.format(HEX_2, cpe.getReferenceIndex());
			cpe.constants.get(cpe.getReferenceIndex() - 1).print(this);
		}

		void print() {
			System.out.println(formatedString);
			printGeneral = true;
		}
	}

	public class AttributePrinter {
		void print(Attribute attr) {
			u2(attr.getNameIndex(), "Attribute name index");
			u4(attr.getLength(), "Attribute length");
		}

		void print(ConstantValueAttribute attr) {
			u2(attr.getConstantValueIndex(), "Attribute constant value index");
		}

		void print(CodeAttribute attr) {
			u2(attr.getMaxStack(), "Attribute max stack");
			u2(attr.getMaxLocals(), "Attribute max local");
			u4(attr.getCodeLength(), "Code length");
			u2(attr.getExceptionTableLength(), "Exception table length");
			for (Attribute.Exception exception : attr.getExceptions()) {
				exception.print(this);
			}
		}

		void print(Attribute.Exception attr) {
			u2(attr.startPc(), "Attribute exception start pc");
			u2(attr.endPc(), "Attribute exception end pc");
			u2(attr.handlerPc(), "Attribute handler start pc");
			u2(attr.catchType(), "Exception handler class");
		}

		void print(ExceptionsAttribute attr) {
			U2[] exceptions = attr.getExceptions();
			u2(attr.getNumberOf(), "Attribute number of exceptions", "", true);
			for (int i = 0; i < exceptions.length; i++) {
				u2(exceptions[i], String.format("%4X ", i) + "Exception");
			}
		}

		void print(SourceFileAttribute attr) {
			u2(attr.getSourceFileIndex(), "Attribute source file index");
		}

		void print(NestMembersAttribute attr) {
			U2[] classes = attr.getClasses();
			u2(attr.getNumberOfClasses(), "Attribute number of classes", "", true);
			for (int i = 0; i < classes.length; i++) {
				u2(classes[i], String.format("%4X ", i) + "Nest");
			}
		}

		void print(InnerClassesAttribute attr) {
			u2(attr.getNumberOf(), "Attribute number of inner classes", "", true);
			for (InnerClass innerClass : attr.getInnerClasses()) {
				innerClass.print(this);
			}
		}

		void print(SignatureAttribute attr) {
			u2(attr.getSignatureIndex(), "Attribute signature index");
		}

		void print(BootstrapMethodsAttribute attr) {
			u2(attr.getNumberOf(), "Attribute number of bootstrap methods", "", true);
			for (BootstrapMethod bootstrapMethod : attr.getBootstrapMethods()) {
				bootstrapMethod.print(this);
			}
		}

		void print(BootstrapMethod bootstrapMethod) {
			var formatedIndex = String.format("%4X ", bootstrapMethod.index());
			u2(bootstrapMethod.bootstrapMethodRef(), formatedIndex + "Bootstrap method");
			u2(bootstrapMethod.numberOf(), SP_5 + "Number of arguments", "", true);
			for (U2 u2 : bootstrapMethod.bootstrapArguments()) {
				u2(u2, SP_5 + "Argument");
			}
		}

		public void print(InnerClass innerClass) {
			u2(innerClass.innerClassInfoIndex(), SP_5 + "Inner class");
			u2(innerClass.outerClassInfoIndex(), SP_5 + "Outer class");
			u2(innerClass.innerNameIndex(), SP_5 + "Inner name index");
			accessFlags(innerClass.innerClassAccessFlags(), Type.CLASS, SP_5 + "Inner class access flags");
		}
	}
}
