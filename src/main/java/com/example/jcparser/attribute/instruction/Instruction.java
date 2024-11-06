package com.example.jcparser.attribute.instruction;

public record Instruction(int offset, int opcode, int[] operands) {
	public static int getFirstBytePadding(int offset, int startCodeCount) {
		return 3 - (offset - startCodeCount) % 4;
	}
}

