package com.example.jcparser;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParserTest {
	public static final int CONSTANT_COUNT_LINE = 2;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private String[] lines;

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

	@BeforeAll
	public void runParsing() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		Parser.main(args);
		lines = outContent.toString().split("\n");
	}

	@Test
	void whole_file_parsed() throws IOException {
		File file = new File(args[0]);
		String[] lastLine = lines[lines.length - 1].split(" ");
		String lastOffset = lastLine[0];
		String[] lastBytes = getBytes(lastLine);

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

	private static String[] getBytes(String[] line) {
		return Arrays.stream(line, 1, line.length)
				.takeWhile(aByte -> aByte.length() == 2 && aByte.matches("-?[0-9A-F]{2}"))
				.toArray(String[]::new);
	}

	@Test
	void check_every_byte_present() {
		System.setOut(originalOut);
		System.setErr(originalErr);
		assertFalse(lines.length < CONSTANT_COUNT_LINE, "Wrong file size < 2 lines");
		String[] bytes = getBytes(lines[CONSTANT_COUNT_LINE].split(" "));
		int constantCount = Integer.parseInt(bytes[0] + bytes[1], 16);
		int errorCount = 0;
		for (int i = CONSTANT_COUNT_LINE + constantCount; i < lines.length - 1; i++) {
			String[] splitLine = lines[i].split(" ");
			int offset = Integer.parseInt(splitLine[0], 16);
			int nextOffset = Integer.parseInt(lines[i + 1].split(" ")[0], 16);
			int bytesCount = getBytes(splitLine).length;
			if (nextOffset - offset != bytesCount) {
				errorCount++;
				System.out.printf("Incorrect number of bytes : %s\n", Arrays.toString(splitLine)
						+ "(Previous " + lines[i - 1] + ")");
			}
		}
		System.out.println("Total errors: " + errorCount);
	}
}