module com.example.jcparser.module {
	exports com.example.jcparser;
	exports com.example.jcparser.attribute;
	exports com.example.jcparser.attribute.annotation;
	exports com.example.jcparser.attribute.stackmapframe;
	exports com.example.jcparser.attribute.instruction;
	exports com.example.jcparser.constantpool;
	requires org.apache.commons.text;
}