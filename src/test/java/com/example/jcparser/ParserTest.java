package com.example.jcparser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	private final String[] args = new String[]{System.getenv("FILE")};

	@Test
	void whole_file_parsed() throws IOException {

		Parser.main(args);

		File file = new File(args[0]);
		String[] lines = outContent.toString().split("\n");
		String[] lastLine = lines[lines.length - 1].split(" ");
		String lastOffset = lastLine[0];
		String[] lastBytes = Arrays.stream(lastLine, 1, lastLine.length)
				.takeWhile(aByte -> aByte.length() == 2 && aByte.matches("-?[0-9A-F]{2}"))
				.toArray(String[]::new);

		int length = lastBytes.length;
		long offset = file.length() - length;
		byte[] byteArray = new byte[length];
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
			randomAccessFile.seek(offset);
			randomAccessFile.read(byteArray, 0, length);
		}
		String[] expectedLastBytes = new String[length];
		for (int i = 0; i < byteArray.length; i++) {
			byte b = byteArray[i];
			expectedLastBytes[i] = String.format("%02X", b);
		}
		String expectedLastOffset = Long.toHexString(offset).toUpperCase();

		assertEquals(expectedLastOffset, lastOffset);
		assertArrayEquals(expectedLastBytes, lastBytes);
	}
}