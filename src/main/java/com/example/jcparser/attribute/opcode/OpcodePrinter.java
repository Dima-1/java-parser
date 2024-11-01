package com.example.jcparser.attribute.opcode;

import com.example.jcparser.Print;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OpcodePrinter {
	private final Print print;

	public OpcodePrinter(Print print) {
		this.print = print;
	}

	public void opcodes(List<Opcode> opcodes) {
		for (Opcode opcode : opcodes) {
			String operands = opcode.operands().length > 0
					? " " + Arrays.stream(opcode.operands()).mapToObj(num -> String.format("%02X ", num))
					.collect(Collectors.joining()).trim()
					: "";
			String instruction = Instruction.getInstruction(opcode.opcode()).getName().toUpperCase();
			print.opcode(opcode, operands, "", instruction);
		}
	}
}
