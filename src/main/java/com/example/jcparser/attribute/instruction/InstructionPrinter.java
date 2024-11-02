package com.example.jcparser.attribute.instruction;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.Attribute;
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
		List<Instruction> instructions = attr.getInstructions();
		for (Instruction instruction : instructions) {
			String operands = instruction.operands().length > 0
					? " " + Arrays.stream(instruction.operands()).mapToObj(num -> String.format("%02X ", num))
					.collect(Collectors.joining()).trim()
					: "";
			String mnemonic = InstructionSet.getInstruction(instruction.opcode()).getMnemonic().toUpperCase();
			InstructionSet.Type type = InstructionSet.getOperandsType(instruction.opcode());
			String strOperands;
			List<Parser.ConstantPoolEntry> cpeOperands = new ArrayList<>();
			Print.ConstantFormater constantFormater = print.getConstantFormater();
			if (type == InstructionSet.Type.LOCAL_VAR_IDX) {
				cpeOperands = getVariableOperand(instruction, attr);
			} else {
				cpeOperands = getCPOperands(instruction);
			}
			strOperands = cpeOperands.stream().map(constantFormater::formatNewOnlyString).collect(Collectors.joining(" "));
			print.instruction(instruction, operands, "", mnemonic, strOperands);
		}
	}

	private List<Parser.ConstantPoolEntry> getCPOperands(Instruction instruction) {
		InstructionSet.Type type = InstructionSet.getOperandsType(instruction.opcode());
		List<Parser.ConstantPoolEntry> cpeOperands = new ArrayList<>();
		Print.ConstantFormater constantFormater = print.getConstantFormater();
		List<Parser.ConstantPoolEntry> constantPool = constantFormater.getConstantPool();
		if (type == InstructionSet.Type.CP_IDX) {
			cpeOperands.add(constantPool.get(instruction.operands()[0] << 8 | instruction.operands()[1]));
		} else if (type == InstructionSet.Type.CP_IDX_BYTE) {
			cpeOperands.add(constantPool.get(instruction.operands()[0]));
		}
		return cpeOperands;
	}

	private List<Parser.ConstantPoolEntry> getVariableOperand(Instruction instruction, CodeAttribute attr) {
		List<Parser.ConstantPoolEntry> varOperand = new ArrayList<>();
		for (Attribute attribute : attr.getAttributes()) {
			if (attribute instanceof LocalVariableTableAttribute variableAttribute) {
				LocalVariableAttribute.LocalVariable localVariable
						= getLocalVariable(variableAttribute, instruction, attr.getCodeLength().getOffset() + Parser.U4.BYTES);
				if (localVariable != null) {
					varOperand.add(localVariable.descriptorIndex().getCpe());
					varOperand.add(localVariable.nameIndex().getCpe());
				} else {
//					throw new IllegalArgumentException();
				}
			}
		}
		return varOperand;
	}

	private static LocalVariableAttribute.LocalVariable getLocalVariable(LocalVariableTableAttribute variableAttribute,
	                                                                     Instruction instruction, int startPC) {
		int idx;
		String mnemonic = InstructionSet.getInstruction(instruction.opcode()).getMnemonic();
		if (mnemonic.contains("_")) {
			idx = Integer.parseInt(mnemonic.split("_")[1]);
		} else {
			idx = instruction.operands()[0];
		}
		return Arrays.stream(variableAttribute.getLocalVariables())
				.filter(lv -> lv.index().getValue() == idx
						&& lv.startPC().getValue() <= instruction.offset() - startPC
						&& lv.startPC().getValue() + lv.length().getValue() > instruction.offset() - startPC)
				.findFirst().orElse(null);
	}
}
