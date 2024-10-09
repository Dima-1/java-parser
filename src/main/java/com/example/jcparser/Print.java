package com.example.jcparser;

import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;
import com.example.jcparser.attribute.Opcode;
import com.example.jcparser.attribute.stackmapframe.StackFramePrinter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.jcparser.AccessFlag.getAccessFlags;
import static com.example.jcparser.Parser.*;

public class Print {

	public static final boolean PRINT_CONSTANT_POOL = true;
	public static final String YELLOW_STRING = ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET;
	public static final String HEX_2 = " (%02X)";
	public static final int SPACES_IN_INTENT = 6;
	private final StackFramePrinter stackFramePrinter = new StackFramePrinter(this);
	private final AttributePrinter attributePrinter = new AttributePrinter(this);
	private final ConstantPrinter constantPrinter = new ConstantPrinter();
	private String OFFSET_FORMAT = "%04X ";
	private int indent;

	public StackFramePrinter getStackFramePrinter() {
		return stackFramePrinter;
	}

	public void setLength(long length) {
		OFFSET_FORMAT = "%0" + Long.toHexString(length).length() + "X ";
	}

	public void u1(U1 u1, String title) {
		u1(u1, title, false);
	}

	public void u1(U1 u1, String title, boolean addDecimal) {
		String hexValue = String.format("%02X", u1.getValue());
		String decimalValue = String.format(addDecimal ? " (%02d)" : "", u1.getValue());
		System.out.printf(OFFSET_FORMAT + "%s    %s %s\n", u1.getOffset(), hexValue,
				title.indent(getIndents()).stripTrailing(), decimalValue);
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
				u2.getOffset(), splitHexValue, title.indent(getIndents()).stripTrailing(), decimalValue, u2.getSymbolic());
	}

	public void u2WithIndex(int index, Parser.U2 u2, String title) {
		String formatedIndex = String.format("%5X ", index);
		decIndent();
		u2(u2, formatedIndex + title);
		incIndent();
	}

	public void u2array(Parser.U2Array u2Array, String title, String itemTitle) {
		u2(u2Array.numberOf(), title, "", true);
		incIndent();
		Parser.U2[] array = u2Array.array();
		for (int i = 0; i < array.length; i++) {
			u2WithIndex(i, array[i], itemTitle);
		}
		decIndent();
	}

	public void u4(U4 u4, String title) {
		String hexValue = String.format("%08X", u4.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String yellowString = u4.getSymbolic().isEmpty() ? YELLOW_STRING : " " + YELLOW_STRING;
		System.out.printf(OFFSET_FORMAT + "%s %s" + yellowString + "\n",
				u4.getOffset(), splitHexValue, title.indent(getIndents() - 6).stripTrailing(), u4.getSymbolic());
	}

	public void debugInfo(int offset, String info) {
		System.out.printf(OFFSET_FORMAT + "%s\n", offset, info);
	}

	private static StringBuilder getSplitHexValue(String hexValue) {
		StringBuilder splitHexValue = new StringBuilder(hexValue);
		for (int i = 2; i < splitHexValue.length(); i += 3) {
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

	public void accessFlags(U2 u2, AccessFlag.Type type) {
		String flags = getAccessFlags(u2.getValue(), type);
		String title = type.getTitle() + " access flags";
		System.out.printf(OFFSET_FORMAT + "%s %s" + YELLOW_STRING + "\n", u2.getOffset(),
				getSplitHexValue(String.format("%04X", u2.getValue())), title.indent(getIndents()).stripTrailing(), flags);
	}

	public void attributes(Map<String, Attribute> attributes) {
		indent++;
		for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
			Attribute attr = entry.getValue();
			attr.print(attributePrinter);
		}
		indent--;
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

	public void incIndent() {
		indent++;
	}

	public void decIndent() {
		indent--;
	}

	private int getIndents() {
		return SPACES_IN_INTENT * indent;
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
			formatedString += " " + String.format(YELLOW_STRING, cpe.getUtf8());
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
