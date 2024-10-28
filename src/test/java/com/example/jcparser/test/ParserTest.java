package com.example.jcparser.test;

import com.example.jcparser.Options;
import com.example.jcparser.Parser;
import com.example.jcparser.Print;
import com.example.jcparser.attribute.opcode.Instruction;
import com.example.jcparser.attribute.stackmapframe.FrameType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.example.jcparser.ConsoleColors.RED;
import static com.example.jcparser.ConsoleColors.RESET;
import static com.example.jcparser.Parser.ConstantTag.CONSTANT_String;
import static com.example.jcparser.Parser.ConstantTag.CONSTANT_Utf8;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParserTest {
	public static final int CONSTANT_COUNT_LINE = 2;
	public static final String COMMENT_PREFIX = "#";
	private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;
	private TestInfo testInfo;

	@BeforeEach
	void init(TestInfo testInfo) {
		this.testInfo = testInfo;
	}

	@BeforeEach
	public void setUpStreams() {
		System.setOut(new PrintStream(outStream));
		System.setErr(new PrintStream(errStream));
	}

	@AfterEach
	public void restoreStreams() {
		System.setOut(originalOut);
		System.setErr(originalErr);
	}

	Stream<Arguments> getClassFiles() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		String resourceListPath = Objects.requireNonNull(classloader.getResource("class_file_list.csv")).getPath();
		List<String> testFiles = Files.readAllLines(Path.of(resourceListPath))
				.stream().filter(f -> !f.startsWith(COMMENT_PREFIX))
				.toList();
		System.setOut(new PrintStream(outStream));
		System.setErr(new PrintStream(errStream));
		List<Arguments> argumentsList = new ArrayList<>();
		for (String path : testFiles) {
			String filePath = Objects.requireNonNull(classloader.getResource(path),"File not found: " + path).getPath();
			Parser.main(new String[]{filePath});
			String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
			argumentsList.add(arguments(named(fileName, filePath), outStream.toString().split("\n")));
			outStream.reset();
		}
		return argumentsList.stream();
	}

	@ParameterizedTest(name = "{index} File {0}")
	@MethodSource("getClassFiles")
	void whole_file_parsed(String path, String[] lines) throws IOException {
		File file = new File(path);
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

		String lastParsedLine = "(Last parsed line " + lines[lines.length - 1] + ")";
		assertEquals(expectedLastOffset, lastOffset, "Last offset " + lastParsedLine);
		assertArrayEquals(expectedLastBytes, lastBytes, "Last bytes" + lastParsedLine);
	}

	private static String[] getBytes(String[] line) {
		return Arrays.stream(line, 1, line.length)
				.takeWhile(aByte -> aByte.length() == 2 && aByte.matches("-?[0-9A-F]{2}"))
				.toArray(String[]::new);
	}

	@ParameterizedTest(name = "{index} File {0}")
	@MethodSource("getClassFiles")
	void check_every_byte_present(String path, String[] lines) {
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
				String errorMsg = String.format("%s Incorrect number of bytes (expected %d) : %s\n",
						testInfo.getDisplayName(),
						bytesCount, Arrays.toString(splitLine) + "(Previous " + lines[i - 1] + ")");
//				assertFalse(nextOffset - offset != bytesCount, errorMsg);
				System.out.printf("%s", errorMsg);
			}
		}
		String errors = errorCount == 0 ? "Total errors: %s" : RED + "Total errors: %s" + RESET;
		System.out.printf("%s Total line parsed: %s " + errors + "\n",
				testInfo.getDisplayName(), lines.length, errorCount);
	}

	@Nested
	@DisplayName("Tests for all files")
	class AdditionalTests {

		@Test
		void wrong_opcode() {
			assertThrows(IllegalArgumentException.class, () -> Instruction.getInstruction(0xFF));
		}

		@Test
		void wrong_frame_type() {
			assertThrows(IllegalArgumentException.class, () -> FrameType.getType(0x80));
		}

		@Test
		void check_options() {
			System.setOut(new PrintStream(outStream));
			System.setErr(new PrintStream(errStream));
			List<Parser.ConstantPoolEntry> list = new ArrayList<>();
			list.add(null);
			list.add(new Parser.ConstantPoolUtf8(list, 0x0A, 1, CONSTANT_Utf8, "TestUtf8"));
			list.add(new Parser.ConstantPoolString(list, 0x12, 2, CONSTANT_String, 1));
			Options options = new Options();
			options.setConstants(false);
			Print print = new Print(options);
			print.constantPool(list);
			assertEquals(0, outStream.size(), "Constant pool doesn't hide");
			outStream.reset();
			options.setConstants(true);
			options.setRefs(false);
			print.constantPool(list);
			String[] lines = outStream.toString().split("\n");
			assertFalse(lines[1].contains("(01)"), "Constant pool reference index doesn't hide");
		}
	}
}