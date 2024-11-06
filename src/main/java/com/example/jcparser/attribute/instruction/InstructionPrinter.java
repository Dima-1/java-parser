package com.example.jcparser.attribute.instruction;

import com.example.jcparser.Parser;
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
				case CP_IDX, CP_IDX_BYTE -> strOperands = cpToString(getCPOperands(instruction));
				case BRANCH_OFFSET -> strOperands = getBranchOffset(instruction);
				case LOOKUPSWITCH -> processSwitch(attr, instruction, additionalInstructions);
			}
			String lineNumber = getLineNumber(instruction, attr);
			print.instruction(instruction, lineNumber, mnemonic, strOperands);
			for (InstructionUI addInstruction : additionalInstructions) {
				print.instruction(addInstruction.instruction, "", addInstruction.description, "");
			}
			additionalInstructions.clear();
		}
	}

	private static void processSwitch(CodeAttribute attr, Instruction instruction, List<InstructionUI> additionalInstructions) {
		int startPC = attr.getCodeLength().getOffset() + Parser.U4.BYTES;
		int instructionOffset = instruction.offset();
		int padding = Instruction.getFirstBytePadding(instructionOffset, startPC);
		int[] tOperands = new int[0];
		if (padding != 0) {
			if (padding > 1) {
				tOperands = new int[padding - 1];
			}
			instructionOffset += Parser.U1.BYTES;
			additionalInstructions.add(new InstructionUI(new Instruction(instructionOffset, 0, tOperands), "//padding"));
		}
		int[] operands = instruction.operands();
		tOperands = Arrays.copyOfRange(operands, padding + 1, padding + 4);
		int defaultGoto = getIntFromBytes(Arrays.copyOfRange(operands, padding, padding + 4)) + instruction.offset();
		String label = String.format("%2X ", defaultGoto);
		additionalInstructions.add(new InstructionUI(new Instruction(instructionOffset + padding, operands[padding], tOperands),
				"//default GOTO :" + label));
		padding += 4;
		tOperands = Arrays.copyOfRange(operands, padding + 1, padding + 4);
		label = String.valueOf(getIntFromBytes(Arrays.copyOfRange(operands, padding, padding + 4)));
		additionalInstructions.add(new InstructionUI(new Instruction(instructionOffset + padding, operands[padding], tOperands),
				"//number of :" + label));
		padding += 4;
		for (int i = padding; i < operands.length; i += Parser.U4.BYTES * 2) {
			int match = getIntFromBytes(Arrays.copyOfRange(operands, i, i + 4));
			int gotoOffset = getIntFromBytes(Arrays.copyOfRange(operands, i + 4, i + 8)) + instruction.offset();
			label = String.format("%2X ", gotoOffset);
			tOperands = Arrays.copyOfRange(operands, i + 1, i + 8);
			additionalInstructions.add(new InstructionUI(new Instruction(instructionOffset + i, operands[i], tOperands),
					"//" + match + ":label GOTO :" + label));
		}
	}

	private String getBranchOffset(Instruction instruction) {
		return ":" + String.format("%2X ", instruction.offset() + getSortFromBytes(instruction));
	}

	private static int getSortFromBytes(Instruction instruction) {
		return instruction.operands()[0] << 8 | instruction.operands()[1];
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
				int startPC = attr.getCodeLength().getOffset() + Parser.U4.BYTES;
				lineNumber = Arrays.stream(lineNumberTableAttr.getLineNumberTable())
						.filter(ln -> (ln.startPC().getValue() + startPC) == instruction.offset())
						.map(ln -> "LN" + ln.lineNumber().getValue()).findFirst().orElse("");
				break;
			}
		}
		return lineNumber;
	}

	private List<Parser.ConstantPoolEntry> getCPOperands(Instruction instruction) {
		InstructionSet.Type type = InstructionSet.getOperandsType(instruction.opcode());
		List<Parser.ConstantPoolEntry> cpeOperands = new ArrayList<>();
		Print.ConstantFormater constantFormater = print.getConstantFormater();
		List<Parser.ConstantPoolEntry> constantPool = constantFormater.getConstantPool();
		if (type == InstructionSet.Type.CP_IDX) {
			cpeOperands.add(constantPool.get(getSortFromBytes(instruction)));
		} else if (type == InstructionSet.Type.CP_IDX_BYTE) {
			cpeOperands.add(constantPool.get(instruction.operands()[0]));
		}
		return cpeOperands;
	}

	private List<Parser.ConstantPoolEntry> getVariableOperand(Instruction instruction, CodeAttribute attr) {
		List<Parser.ConstantPoolEntry> varOperand = new ArrayList<>();
		for (Attribute attribute : attr.getAttributes()) {
			if (attribute instanceof LocalVariableTableAttribute variableAttr) {
				LocalVariableAttribute.LocalVariable localVariable
						= getLocalVariable(variableAttr, instruction, attr.getCodeLength().getOffset() + Parser.U4.BYTES);
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

	}
}
