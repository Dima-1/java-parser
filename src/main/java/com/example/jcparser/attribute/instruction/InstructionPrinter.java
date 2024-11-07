package com.example.jcparser.attribute.instruction;

import com.example.jcparser.ConsoleColors;
import com.example.jcparser.Parser;
import com.example.jcparser.Parser.U1;
import com.example.jcparser.Parser.U4;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
import com.example.jcparser.attribute.LineNumberTableAttribute;
import com.example.jcparser.attribute.LocalVariableAttribute;
import com.example.jcparser.attribute.LocalVariableTableAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstructionPrinter {
	private final Print print;

	public InstructionPrinter(Print print) {
		this.print = print;
	}

	public void instruction(CodeAttribute attr) {
		List<InstructionUI> additionalInstructions = new ArrayList<>();
		List<Instruction> instructions = attr.getInstructions();
		for (Instruction instruction : instructions) {

			String mnemonic = InstructionSet.getInstruction(instruction.opcode()).getMnemonic().toUpperCase();
			InstructionSet.Type type = InstructionSet.getOperandsType(instruction.opcode());
			String strOperands = "";
			switch (type) {
				case LOCAL_VAR_IDX -> strOperands = cpToString(getVariableOperand(instruction, attr));
				case CP_IDX, CP_IDX_BYTE -> strOperands = cpToString(getCPOperands(instruction, type));
				case BRANCH_OFFSET -> strOperands = getBranchOffset(instruction);
				case LOOKUPSWITCH, TABLESWITCH -> processSwitch(attr, instruction, additionalInstructions, type);
			}
			String lineNumber = getLineNumber(instruction, attr);
			print.instruction(instruction, lineNumber, mnemonic, strOperands);
			for (InstructionUI addInstruction : additionalInstructions) {
				print.instruction(addInstruction.instruction, "", addInstruction.description, "");
			}
			additionalInstructions.clear();
		}
	}

	private void processSwitch(CodeAttribute attr, Instruction instruction, List<InstructionUI> tmpInstructions,
	                           InstructionSet.Type type) {
		int startPC = attr.getCodeLength().getOffset() + U4.BYTES;
		int offset = instruction.offset();
		int padding = Instruction.getFirstBytePadding(offset, startPC);
		offset += U1.BYTES;
		if (padding > 0) {
			tmpInstructions.add(new InstructionUI(new Instruction(offset, 0, new int[padding - 1]), "; padding"));
		}
		int[] operands = instruction.operands();
		tmpInstructions.add(createGotoInstruction("; default", operands, padding, offset, 0));
		padding += U4.BYTES;
		if (type == InstructionSet.Type.LOOKUPSWITCH) {
			tmpInstructions.add(createIntInstruction("; number of :", operands, padding, offset));
			padding += U4.BYTES;
			for (int i = padding; i < operands.length; i += U4.BYTES * 2) {
				String match = Integer.toHexString(getIntFromBytes(operands, i)).toUpperCase();
				tmpInstructions.add(createGotoInstruction("; case " + match, operands, i, offset, U4.BYTES));
			}
		} else {
			int from = getIntFromBytes(operands, padding);
			tmpInstructions.add(createIntInstruction("; from :", operands, padding, offset));
			padding += U4.BYTES;
			tmpInstructions.add(createIntInstruction("; to   :", operands, padding, offset));
			padding += U4.BYTES;
			for (int i = padding; i < operands.length; i += U4.BYTES) {
				String match = Integer.toHexString(from + (i - padding) / U4.BYTES).toUpperCase();
				tmpInstructions.add(createGotoInstruction("; case " + match, operands, i, offset, 0));
			}
		}
	}

	private InstructionUI createIntInstruction(String title, int[] operands, int padding, int offset) {
		int[] tmpOperands = Arrays.copyOfRange(operands, padding + 1, padding + U4.BYTES);
		String description = title + Integer.toHexString(getIntFromBytes(operands, padding)).toUpperCase();
		return new InstructionUI(new Instruction(offset + padding, operands[padding], tmpOperands), description);
	}

	private InstructionUI createGotoInstruction(String title, int[] operands, int padding,
	                                            int offset, int wider) {
		int gotoOffset = getIntFromBytes(operands, padding + wider) + offset - 1;
		int[] tmpOperands = Arrays.copyOfRange(operands, padding + 1, padding + U4.BYTES + wider);
		String description = title + " : GOTO :" + String.format(print.getOffsetFormat(), gotoOffset);
		return new InstructionUI(new Instruction(offset + padding, operands[padding], tmpOperands), description);
	}

	private String getBranchOffset(Instruction instruction) {
		return ":" + String.format(print.getOffsetFormat(), instruction.offset() + getSortFromBytes(instruction));
	}

	private static int getSortFromBytes(Instruction instruction) {
		return instruction.operands()[0] << 8 | instruction.operands()[1];
	}

	private static int getIntFromBytes(int[] operands, int padding) {
		return getIntFromBytes(Arrays.copyOfRange(operands, padding, padding + U4.BYTES));
	}

	private static int getIntFromBytes(int[] bytes) {
		return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
	}

	private String cpToString(List<Parser.ConstantPoolEntry> cpeOperands) {
		Print.ConstantFormater constantFormater = print.getConstantFormater();
		return cpeOperands.stream().map(constantFormater::formatNewOnlyString).collect(Collectors.joining(" "));
	}

	private String getLineNumber(Instruction instruction, CodeAttribute attr) {
		String lineNumber = null;
		for (Attribute attribute : attr.getAttributes()) {
			if (attribute instanceof LineNumberTableAttribute lineNumberTableAttr) {
				int startPC = attr.getCodeLength().getOffset() + U4.BYTES;
				lineNumber = Arrays.stream(lineNumberTableAttr.getLineNumberTable())
						.filter(ln -> (ln.startPC().getValue() + startPC) == instruction.offset())
						.map(ln -> "LN" + ln.lineNumber().getValue()).findFirst().orElse("");
				break;
			}
		}
		return lineNumber;
	}

	private List<Parser.ConstantPoolEntry> getCPOperands(Instruction instruction, InstructionSet.Type type) {
		List<Parser.ConstantPoolEntry> cpeOperands = new ArrayList<>();
		Print.ConstantFormater constantFormater = print.getConstantFormater();
		List<Parser.ConstantPoolEntry> constantPool = constantFormater.getConstantPool();
		if (type == InstructionSet.Type.CP_IDX) {
			cpeOperands.add(constantPool.get(getSortFromBytes(instruction)));
		} else {
			cpeOperands.add(constantPool.get(instruction.operands()[0]));
		}
		return cpeOperands;
	}

	private List<Parser.ConstantPoolEntry> getVariableOperand(Instruction instruction, CodeAttribute attr) {
		List<Parser.ConstantPoolEntry> varOperand = new ArrayList<>();
		for (Attribute attribute : attr.getAttributes()) {
			if (attribute instanceof LocalVariableTableAttribute variableAttr) {
				LocalVariableAttribute.LocalVariable localVariable
						= getLocalVariable(variableAttr, instruction, attr.getCodeLength().getOffset() + U4.BYTES);
				if (localVariable != null) {
					varOperand.add(localVariable.descriptorIndex().getCpe());
					varOperand.add(localVariable.nameIndex().getCpe());
				}
				break;
			}
		}
		return varOperand;
	}

	private LocalVariableAttribute.LocalVariable getLocalVariable(LocalVariableTableAttribute variableAttribute,
	                                                              Instruction instruction, int startPC) {
		String mnemonic = InstructionSet.getInstruction(instruction.opcode()).getMnemonic();
		int idx = mnemonic.contains("_") ? Integer.parseInt(mnemonic.split("_")[1]) : instruction.operands()[0];
		return Arrays.stream(variableAttribute.getLocalVariables())
				.filter(lv -> lv.index().getValue() == idx
						&& lv.startPC().getValue() <= instruction.offset() - startPC
						&& lv.startPC().getValue() + lv.length().getValue() > instruction.offset() - startPC)
				.findFirst().orElse(null);
	}

	record InstructionUI(Instruction instruction, String description) {
		InstructionUI {
			description = ConsoleColors.CYAN + description + ConsoleColors.RESET;
		}
	}
}
