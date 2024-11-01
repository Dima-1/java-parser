package com.example.jcparser.attribute.instruction;

public record Instruction(int offset, int opcode, int[] operands) {
}

