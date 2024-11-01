package com.example.jcparser.attribute.opcode;

import com.example.jcparser.Parser.ConstantPoolEntry;

public record Opcode(int offset, int opcode, int[] operands, ConstantPoolEntry[] strArguments) {
}

