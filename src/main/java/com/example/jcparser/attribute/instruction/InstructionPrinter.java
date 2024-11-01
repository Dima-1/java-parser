package com.example.jcparser.attribute.instruction;

import com.example.jcparser.Parser;
import com.example.jcparser.Print;

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
			List<Parser.ConstantPoolEntry> cpeOperands = new ArrayList<>();
			Print.ConstantFormater constantFormater = print.getConstantFormater();
			List<Parser.ConstantPoolEntry> constantPool = constantFormater.getConstantPool();
			if (type == InstructionSet.Type.CP_IDX) {
				cpeOperands.add(constantPool.get(instruction.operands()[0] << 8 | instruction.operands()[1]));
			} else if (type == InstructionSet.Type.CP_IDX_BYTE) {
				cpeOperands.add(constantPool.get(instruction.operands()[0]));
			}
			String strOperands = cpeOperands.stream().map(constantFormater::formatNewOnlyString)
					.collect(Collectors.joining());
			print.instruction(instruction, operands, "", mnemonic, strOperands);
		}
	}
}
