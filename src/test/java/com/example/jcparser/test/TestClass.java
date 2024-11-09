package com.example.jcparser.test;

import java.lang.annotation.*;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class TestClass {

	public static final float FLOAT_CONSTANT_VALUE = 3.3333f;
	public static final double DOUBLE_CONSTANT_VALUE = 2.2222;
	public static final String STRING_CONSTANT_VALUE = "local string";
	private static final long LONG_CONSTANT_VALUE = (long) Integer.MAX_VALUE * 4;

	public static void main(String[] args) {
		TestClass testClassVar = new TestClass();
		testClassVar.testMethod();
		String res = testClassVar.testMethodWithParameters(1, "MethodWithParameters", "param2");
		System.out.println(res);
		TestProvider provider = TestProvider.getInstance();
		TestService service = provider.serviceImpl();
		service.print("Test service");
		TestSealed testRecord = new TestRecord(1, "Test Record");
		testRecord.print();
	}

	@Deprecated(forRemoval = true)
	private int oldCodeMethod() {
		return 10;
	}

	@Target({ElementType.TYPE_USE, ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@interface NonNull {
	}

	@TestInvisibleAnnotation(hName = "New invisible name", hId = 2)
	private void testMethod() {
		class TestLocalClass {

			private final String str;

			public TestLocalClass(String str) {
				this.str = str;
			}

			public String getStr() {
				return str;
			}
		}
		TestInnerClass testInnerClass = new TestInnerClass(10);
		long aLong = (long) testInnerClass.getFieldInt() * Integer.MAX_VALUE + LONG_CONSTANT_VALUE;
		System.out.println(aLong);
		float aFloat = FLOAT_CONSTANT_VALUE * 2;
		double aDouble = DOUBLE_CONSTANT_VALUE;
		aDouble += aFloat;
		System.out.println(aDouble);
		TestLocalClass testLocalClass = new TestLocalClass(STRING_CONSTANT_VALUE);
		System.out.println(testLocalClass.getStr());
	}

	@TestAnnotation(name = "New name 1", id = 1)
	@TestAnnotation(name = "New name 2", id = 2)
	private String testMethodWithParameters(@TestAnnotation(name = "Params 1") int i,
	                                        @TestInvisibleAnnotation(hName = "Params 2") final String test,
	                                        @NonNull String p2)
			throws InvalidParameterException {
		if (i == 0) {
			throw new InvalidParameterException(test);
		}
		String res = switch (i) { // TABLESWITCH
			case 0xFF0 -> test;
			case 0xFF2 -> "Two " + test;
			case 0xFF1 -> "One " + test;
			case 0xFF9 -> "Nine " + test; // FFA - LOOKUPSWITCH
			default -> "Default " + i;
		};
		return switch (i) { // LOOKUPSWITCH
			case 64 -> test;
			case 2 -> "Two " + test;
			case 1 -> "One " + test;
			case 32 -> "Thirty two " + test;
			default -> throw new IllegalStateException("Unexpected value: " + i);
		};
	}

	public static class TestInnerClass {
		private final int fieldInt;

		public TestInnerClass(int fieldInt) {
			this.fieldInt = fieldInt;
		}

		public int getFieldInt() {
			int anInt = 11;
			return fieldInt * anInt;
		}
	}

	@Repeatable(TestRepeatableAnnotations.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface TestAnnotation {
		String name() default "default name";

		int id() default 0;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface TestRepeatableAnnotations {
		TestAnnotation[] value();
	}

	@Retention(RetentionPolicy.CLASS)
	@interface TestInvisibleAnnotation {
		String hName() default "default hidden name";

		int hId() default 1;
	}

	public interface TestService {
		void print(String title);
	}

	public static class TestProvider {
		private static TestProvider provider;

		private final ServiceLoader<TestService> loader;

		private TestProvider() {
			loader = ServiceLoader.load(TestService.class);
		}

		public static TestProvider getInstance() {
			if (provider == null) {
				provider = new TestProvider();
			}
			return provider;
		}

		public TestService serviceImpl() {
			TestService service = loader.iterator().next();

			if (service != null) {
				return service;
			} else {
				throw new NoSuchElementException("No implementation for TestProvider");
			}
		}

	}

	public static class ConsolePrintTest implements TestService {
		@Override
		public void print(String title) {
			System.out.println("Console print: " + title);
		}
	}

	record TestRecord(int id, String name) implements TestSealed {

		public void print() {
			System.out.println(name + " " + 1);
		}
	}

	sealed interface TestSealed permits TestRecord {
		void print();
	}
}
