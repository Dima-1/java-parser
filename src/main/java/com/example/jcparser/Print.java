package com.example.jcparser;

import com.example.jcparser.Parser.*;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;
import com.example.jcparser.attribute.opcode.Opcode;
import com.example.jcparser.attribute.opcode.OpcodePrinter;
import com.example.jcparser.attribute.stackmapframe.StackFramePrinter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.jcparser.AccessFlag.getAccessFlags;

public class Print {

	public static final String YELLOW_STRING = ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET;
	public static final String HEX_2 = " (%02X)";
	public static final int SPACES_IN_INTENT = 6;
	private final Options options;
	private final OpcodePrinter opcodePrinter = new OpcodePrinter(this);
	private final StackFramePrinter stackFramePrinter = new StackFramePrinter(this);
	private final AttributePrinter attributePrinter = new AttributePrinter(this);
	private final ConstantFormater constantFormater = new ConstantFormater();
	private String OFFSET_FORMAT = "%04X ";
	private int indent;

	public Print(Options options) {
		this.options = options;
	}

	public OpcodePrinter getOpcodePrinter() {
		return opcodePrinter;
	}

	public StackFramePrinter getStackFramePrinter() {
		return stackFramePrinter;
	}

	public void setLength(long length) {
		OFFSET_FORMAT = "%0" + Long.toHexString(length).length() + "X ";
	}

	public void u1(Parser.U1 u1, String title) {
		u1(u1, title, false);
	}

	public void u1(Parser.U1 u1, String title, boolean addDecimal) {
		String hexValue = String.format("%02X", u1.getValue());
		String value;
		String valueFormat = addDecimal ? " %s" : "";
		value = String.format(addDecimal ? "(%02d)" : "", u1.getValue());
		if (u1 instanceof CharU1) {
			valueFormat += " " + YELLOW_STRING;
			value += Character.toString(u1.value);
		}
		System.out.printf(OFFSET_FORMAT + "%s    %s" + valueFormat + "\n", u1.getOffset(), hexValue,
				title.indent(getIndents()).stripTrailing(), value);
	}

	public void u2(Parser.U2 u2, String title) {
		u2(u2, title, "", false);
	}

	public void u2(Parser.U2 u2, String title, String titleColor, boolean addDecimal) {
		String hexValue = String.format("%04X", u2.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String decimalValue = String.format(addDecimal ? " (%02d)" : "", u2.getValue());
		String constantString = "";
		if (u2.getCpe() != null) {
			constantString = constantFormater.formatNewOnlyString(u2.getCpe());
		}
		String yellowString = constantString.isEmpty() ? "%s" : " %s";
		System.out.printf(OFFSET_FORMAT + "%s " + titleColor + "%s" + ConsoleColors.RESET + "%s" + yellowString + "\n",
				u2.getOffset(), splitHexValue, title.indent(getIndents()).stripTrailing(), decimalValue, constantString);
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

	public void u4(Parser.U4 u4, String title) {
		String hexValue = String.format("%08X", u4.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		System.out.printf(OFFSET_FORMAT + "%s %s\n",
				u4.getOffset(), splitHexValue, title.indent(getIndents() - 6).stripTrailing());
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
		if (!options.needConstants()) {
			return;
		}
		for (int i = 1; i < constants.size(); i++) {
			ConstantPoolEntry cpe = constants.get(i);
			cpe.format(constantFormater);
			constantPoolEntry(constantFormater);
			if (cpe.getConstantTag().isTwoEntriesTakeUp()) {
				i++;
			}
		}
	}

	void constantPoolEntry(ConstantFormater constantFormater) {
		System.out.println(constantFormater.formatedString);
		constantFormater.printGeneral = true;
	}

	public void accessFlags(Parser.U2 u2, AccessFlag.Type type) {
		String flags = getAccessFlags(u2.getValue(), type);
		String title = type.getTitle() + " access flags";
		System.out.printf(OFFSET_FORMAT + "%s %s" + YELLOW_STRING + "\n", u2.getOffset(),
				getSplitHexValue(String.format("%04X", u2.getValue())), title.indent(getIndents()).stripTrailing(), flags);
	}

	public void attributes(List<Attribute> attributes) {
		indent++;
		for (Attribute attr : attributes) {
			attr.print(attributePrinter);
		}
		indent--;
	}

	public void opcode(Opcode opcode, String arguments, String label, String instruction) {
		String strArguments = Arrays.stream(opcode.strArguments())
				.map(constantFormater::formatNewOnlyString).collect(Collectors.joining());
		String strArgFormat = strArguments.isEmpty() ? "" : " %s";
		System.out.printf(OFFSET_FORMAT + "%02X%-12s %5s %s" + strArgFormat + "\n", opcode.offset(), opcode.opcode(),
				arguments, label, instruction, strArguments);
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

	public interface Formatter<T> {
		void format(T printer);
	}

	public class ConstantFormater {
		private String formatedString;
		private boolean printGeneral = true;

		public void format(ConstantPoolEntry cpe) {
			if (printGeneral) {
				String cpName = cpe.getConstantTag().name().replaceFirst("^CONSTANT_", "");
				formatedString = String.format(OFFSET_FORMAT + " %4X %19s", cpe.getOffset(), cpe.getIdx(), cpName);
				printGeneral = false;
			}
		}

		public void format(ConstantPoolUtf8 cpe) {
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
			format(cpe.getStringIndex(), cpe.constants);
		}

		private void format(int idx, List<ConstantPoolEntry> cpe) {
			if (options.needRefs()) {
				formatedString += String.format(HEX_2, idx);
			}
			cpe.get(idx).format(this);
		}

		void format(ConstantPoolMethodRef cpe) {
			format(cpe.getClassIndex(), cpe.constants);
			format(cpe.getNameAndTypeIndex(), cpe.constants);
		}

		void format(ConstantPoolNameAndType cpe) {
			format(cpe.getNameIndex(), cpe.constants);
			format(cpe.getDescriptorIndex(), cpe.constants);
		}

		void format(ConstantPoolDynamic cpe) {
			formatedString += String.format(HEX_2, cpe.getBootstrapMethodAttrIndex());
			format(cpe.getNameAndTypeIndex(), cpe.constants);
		}

		void format(ConstantPoolMethodHandle cpe) {
			formatedString += " " + ConstantPoolMethodHandle.MHRef.values()[cpe.getReferenceKind()].name()
					.replaceFirst("REF_", "");
			format(cpe.getReferenceIndex(), cpe.constants);
		}

		public String formatNewOnlyString(ConstantPoolEntry cpe) {
			printGeneral = false;
			formatedString = "";
			cpe.format(this);
			return formatedString.stripLeading();
		}
	}
}
