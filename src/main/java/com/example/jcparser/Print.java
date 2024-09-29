package com.example.jcparser;

import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;
import com.example.jcparser.attribute.Opcode;
import com.example.jcparser.attribute.stackmapframe.StackFramePrinter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.jcparser.Parser.*;

public class Print {

	public static final boolean PRINT_CONSTANT_POOL = true;
	public static final String YELLOW_STRING = ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET;
	public static final String SP_5 = " ".repeat(5);
	public static final String HEX_2 = " (%02X)";
	private final StackFramePrinter stackFramePrinter = new StackFramePrinter(this);
	private final AttributePrinter attributePrinter = new AttributePrinter(this);
	private final ConstantPrinter constantPrinter = new ConstantPrinter();
	private String OFFSET_FORMAT = "%04X ";

	public StackFramePrinter getStackFramePrinter() {
		return stackFramePrinter;
	}

	public void setLength(long length) {
		OFFSET_FORMAT = "%0" + Long.toHexString(length).length() + "X ";
	}

	public void u1(U1 u1, String title) {
		String hexValue = String.format("%02X", u1.getValue());
		System.out.printf(OFFSET_FORMAT + "%s %s\n",
				u1.getOffset(), hexValue, title);
	}

	public void u2(U2 u2, String title) {
		u2(u2, title, "", false);
	}

	public void u2(U2 u2, String title, String titleColor, boolean addDecimal) {
		String hexValue = String.format("%04X", u2.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String decimalValue = String.format(addDecimal ? " (%02d)" : "", u2.getValue());
		String yellowString = u2.getSymbolic().isEmpty() ? YELLOW_STRING : " " + YELLOW_STRING;
		System.out.printf(OFFSET_FORMAT + "%s " + titleColor + "%s" + ConsoleColors.RESET + "%s" + yellowString + "\n",
				u2.getOffset(), splitHexValue, title, decimalValue, u2.getSymbolic());
	}

	public void u4(U4 u4, String title) {
		String hexValue = String.format("%08X", u4.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String yellowString = u4.getSymbolic().isEmpty() ? YELLOW_STRING : " " + YELLOW_STRING;
		System.out.printf(OFFSET_FORMAT + "%s %s" + yellowString + "\n",
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

	public void accessFlags(U2 u2, Type type) {
		accessFlags(u2, type, "Access flags");
	}

	public void accessFlags(U2 u2, Type type, String title) {
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

	public void opcodes(List<Opcode> opcodes) {
		for (Opcode opcode : opcodes) {
			String arguments = opcode.arguments().length > 0
					? " " + Arrays.stream(opcode.arguments()).mapToObj(num -> String.format("%02X ", num))
					.collect(Collectors.joining()).trim()
					: "";
			System.out.printf(OFFSET_FORMAT + "%02X%s\n", opcode.offset(), opcode.opcode(), arguments);
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
}
