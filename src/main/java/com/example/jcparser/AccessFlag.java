package com.example.jcparser;

import java.util.Map;

public class AccessFlag {
	public enum Type {
		CLASS("Class"),
		FIELD("Field"),
		METHOD("Method"),
		PARAMETERS("Parameter"),
		INNERCLASS("Inner class"),
		MODULE("Module"),
		REQUIRES("Requires"),
		EXPORTS("Exports"),
		OPENS("Opens");

		private final String title;

		Type(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	private static Map<Integer, String> getFlagsMap(Type type) {
		return switch (type) {
			case CLASS, INNERCLASS -> Map.ofEntries(
					Map.entry(0x0001, "ACC_PUBLIC"),     // Declared public; may be accessed from outside its package.
					Map.entry(0x0002, "ACC_PRIVATE"),    // Marked private in source. (Nested)
					Map.entry(0x0004, "ACC_PROTECTED"),  // Marked protected in source. (Nested)
					Map.entry(0x0008, "ACC_STATIC"),     // Marked or implicitly static in source. (Nested)
					Map.entry(0x0010, "ACC_FINAL"),      // Declared final; no subclasses allowed.
					Map.entry(0x0020, "ACC_SUPER"),      // Treat superclass methods specially when invoked by the invokespecial instruction.
					Map.entry(0x0200, "ACC_INTERFACE"),  // Is an interface, not a class.
					Map.entry(0x0400, "ACC_ABSTRACT"),   // Declared abstract; must not be instantiated.
					Map.entry(0x1000, "ACC_SYNTHETIC"),  // Declared synthetic; not present in the source code.
					Map.entry(0x2000, "ACC_ANNOTATION"), // Declared as an annotation type.
					Map.entry(0x4000, "ACC_ENUM"),       // Declared as an enum type.
					Map.entry(0x8000, "ACC_MODULE")      // Is a module, not a class or interface.*/
			);
			case FIELD -> Map.of(
					0x0001, "ACC_PUBLIC",    // Declared public; may be accessed from outside its package.
					0x0002, "ACC_PRIVATE",   // Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
					0x0004, "ACC_PROTECTED", // Declared protected; may be accessed within subclasses.
					0x0008, "ACC_STATIC",    // Declared static.
					0x0010, "ACC_FINAL",     // Declared final; never directly assigned to after object construction (JLS §17.5).
					0x0040, "ACC_VOLATILE",  // Declared volatile; cannot be cached.
					0x0080, "ACC_TRANSIENT", // Declared transient; not written or read by a persistent object manager.
					0x1000, "ACC_SYNTHETIC", // Declared synthetic; not present in the source code.
					0x4000, "ACC_ENUM"       // Declared as an element of an enum.
			);
			case METHOD -> Map.ofEntries(
					Map.entry(0x0001, "ACC_PUBLIC"),       // Declared public; may be accessed from outside its package.
					Map.entry(0x0002, "ACC_PRIVATE"),      // Declared private; accessible only within the defining class and other classes belonging to the same nest (§5.4.4).
					Map.entry(0x0004, "ACC_PROTECTED"),    // Declared protected; may be accessed within subclasses.
					Map.entry(0x0008, "ACC_STATIC"),       // Declared static.
					Map.entry(0x0010, "ACC_FINAL"),        // Declared final; must not be overridden (§5.4.5).
					Map.entry(0x0020, "ACC_SYNCHRONIZED"), // Declared synchronized; invocation is wrapped by a monitor use.
					Map.entry(0x0040, "ACC_BRIDGE"),       // A bridge method, generated by the compiler.
					Map.entry(0x0080, "ACC_VARARGS"),      // Declared with variable number of arguments.
					Map.entry(0x0100, "ACC_NATIVE"),       // Declared native; implemented in a language other than the Java programming language.
					Map.entry(0x0400, "ACC_ABSTRACT"),     // Declared abstract; no implementation is provided.
					Map.entry(0x0800, "ACC_STRICT"),       // Declared strictfp; floating-point mode is FP-strict.
					Map.entry(0x1000, "ACC_SYNTHETIC")     // Declared synthetic; not present in the source code.
			);
			case PARAMETERS -> Map.of(
					0x0010, "ACC_FINAL",     // Declared final.
					0x1000, "ACC_SYNTHETIC", // Declared synthetic; not present in the source code.
					0x8000, "ACC_MANDATED"   // Implicitly declared in source code
			);
			case MODULE -> Map.of(
					0x0020, "ACC_OPEN",      // Indicates that this module is open.
					0x1000, "ACC_SYNTHETIC", // Indicates that this module was not explicitly or implicitly declared.
					0x8000, "ACC_MANDATED"   // Indicates that this module was implicitly declared. 
			);
			case REQUIRES, EXPORTS, OPENS -> Map.of(
					0x0020, "ACC_TRANSITIVE",   // Indicates that any module which depends on the current module, implicitly declares a dependence on the module indicated by this entry. 
					0x0040, "ACC_STATIC_PHASE", // Indicates that this dependence is mandatory in the static phase, i.e., at compile time, but is optional in the dynamic phase, i.e., at run time. 
					0x1000, "ACC_SYNTHETIC",    // Indicates that this module was not explicitly or implicitly declared.
					0x8000, "ACC_MANDATED"      // Indicates that this module was implicitly declared. 
			);
		};
	}

	public static String getAccessFlags(int value, Type type) {
		var flagsMap = getFlagsMap(type);
		var res = new StringBuilder();
		for (int bit = 1; bit < 0x100000; bit <<= 1) {
			res.append((String) flagsMap.getOrDefault(value & bit, "")
					.transform(s -> s.isEmpty() ? s : " " + s.replaceFirst("ACC_", "").replace("_", " ")));
		}
		return res.toString().toLowerCase();
	}
}
