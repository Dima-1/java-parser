package com.example.jcparser;

import com.example.jcparser.Parser.*;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.AttributePrinter;
import com.example.jcparser.attribute.instruction.Instruction;
import com.example.jcparser.attribute.instruction.InstructionPrinter;
import com.example.jcparser.attribute.instruction.InstructionSet;
import com.example.jcparser.attribute.stackmapframe.StackFramePrinter;
import com.example.jcparser.constantpool.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.jcparser.AccessFlag.getAccessFlags;

public class Print {

	public static final String YELLOW_STRING = ConsoleColors.YELLOW + "%s" + ConsoleColors.RESET;
	public static final String HEX_2 = " (%02X)";
	public static final int SPACES_IN_INTENT = 6;
	private final Options options;
	private final InstructionPrinter instructionPrinter = new InstructionPrinter(this);
	private final StackFramePrinter stackFramePrinter = new StackFramePrinter(this);
	private final AttributePrinter attributePrinter = new AttributePrinter(this);
	private final ConstantFormater constantFormater = new ConstantFormater(this);
	private String OFFSET_FORMAT = "%04X ";
	private int indent;

	public Print(Options options) {
		this.options = options;
	}

	public Options getOptions() {
		return options;
	}

	public InstructionPrinter getInstructionPrinter() {
		return instructionPrinter;
	}

	public StackFramePrinter getStackFramePrinter() {
		return stackFramePrinter;
	}

	public ConstantFormater getConstantFormater() {
		return constantFormater;
	}

	public void setOffsetWidth(long length) {
		OFFSET_FORMAT = "%0" + Long.toHexString(length).length() + "X ";
	}

	public String getOffsetFormat() {
		return OFFSET_FORMAT;
	}

	public void u1(Parser.U1 u1, String title, boolean addDecimal) {
		u1(u1, title, addDecimal, "");
	}

	public void u1(U1 u1, String title, boolean addDecimal, String stringValue) {
		String hexValue = String.format("%02X", u1.getValue());
		String value;
		String valueFormat = addDecimal ? " %s" : "%s";
		String parenthesesType = stringValue.isEmpty() ? "[%02d]" : "(%02d)";
		value = String.format(addDecimal ? parenthesesType : "", u1.getValue());
		if (!stringValue.isEmpty()) {
			valueFormat += " " + YELLOW_STRING;
		}
		System.out.printf(OFFSET_FORMAT + "%s    %s" + valueFormat + "\n", u1.getOffset(), hexValue,
				title.indent(getIndents()).stripTrailing(), value, stringValue);
	}

	public void u2(Parser.U2 u2, String title) {
		u2(u2, title, false);
	}

	public void u2(Parser.U2 u2, String title, boolean addDecimal) {
		u2(u2, title, "", addDecimal);
	}

	public void u2(Parser.U2 u2, String title, String titleColor, boolean addDecimal) {
		String hexValue = String.format("%04X", u2.getValue());
		StringBuilder splitHexValue = getSplitHexValue(hexValue);
		String valueFormat = addDecimal ? " %s" : "%s";
		String decimalValue = String.format(addDecimal ? "[%02d]" : "", u2.getValue());
		String constantString = "";
		if (u2.getCpe() != null) {
			constantString = constantFormater.formatNewOnlyString(u2.getCpe());
		}
		String yellowString = constantString.isEmpty() ? "%s" : " %s";
		String coloredTitle = ConsoleColors.addColor(titleColor, title);
		System.out.printf(OFFSET_FORMAT + "%s " + "%s" + valueFormat + yellowString + "\n",
				u2.getOffset(), splitHexValue, coloredTitle.indent(getIndents()).stripTrailing(), decimalValue, constantString);
	}

	public void u2WithIndex(int index, Parser.U2 u2, String title) {
		String formatedIndex = String.format("%5X ", index);
		decIndent();
		u2(u2, formatedIndex + title);
		incIndent();
	}

	public void u2array(Parser.U2Array u2Array, String title, String itemTitle) {
		u2(u2Array.numberOf(), title, true);
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
		System.out.println(constantFormater.getFormatedString());
		constantFormater.setPrintGeneral(true);
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

	public void instruction(Instruction instruction, String label, String mnemonic, String strOperands) {
		InstructionSet.Type type = InstructionSet.getOperandsType(instruction.opcode());
		String hexOperands = instruction.operands().length > 0
				&& type != InstructionSet.Type.LOOKUPSWITCH && type != InstructionSet.Type.TABLESWITCH
				? " " + Arrays.stream(instruction.operands()).mapToObj(num -> String.format("%02X", num))
				.collect(Collectors.joining(" "))
				: "";
		String strArgFormat = strOperands.isEmpty() ? "" : " %s";
		String labelFormat = hexOperands.length() < 15 ? " %8s" : "%s";
		System.out.printf(OFFSET_FORMAT + "%02X%-12s" + labelFormat + " %s" + strArgFormat + "\n",
				instruction.offset(), instruction.opcode(), hexOperands, label, mnemonic, strOperands);
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

}
