package org.stjs.generator.writer.fields;

import static org.stjs.generator.utils.GeneratorTestHelper.assertCodeContains;
import static org.stjs.generator.utils.GeneratorTestHelper.generate;

import org.junit.Test;
import org.stjs.generator.JavascriptGenerationException;

public class FieldsGeneratorTest {
	@Test
	public void testInstanceField() {
		assertCodeContains(Fields1.class, "x : null");
	}

	@Test
	public void testInstanceFieldAssigned() {
		assertCodeContains(Fields2.class, "x : 2");
	}

	@Test
	public void testInstanceFieldAssignedNegative() {
		assertCodeContains(Fields2b.class, "x : -2");
	}

	@Test
	public void testMultipleInstanceField() {
		assertCodeContains(Fields3.class, "x : 2, y : 3");
	}

	@Test
	public void testStaticField() {
		assertCodeContains(Fields4.class, "x : 2");
	}

	@Test(expected = JavascriptGenerationException.class)
	public void testForbidInstanceFieldInit() {
		generate(Fields6.class);
	}

	@Test
	public void testAllowStaticFieldInit() {
		assertCodeContains(Fields7.class, "x : {}");
	}

	@Test(expected = JavascriptGenerationException.class)
	public void testForbidInstanceFieldInitWithNonLiterals() {
		generate(Fields8.class);
	}

	@Test
	public void testParameterizedType() {
		assertCodeContains(Fields9.class, "field : null");
	}

	@Test
	public void testGeneric() {
		assertCodeContains(Fields10.class, "field : null");
	}

	@Test
	public void testAccessOuterStaticField() {
		assertCodeContains(Fields11.class, "a = Fields11.FIELD;");
	}

	@Test
	public void testPrototypeProperty() {
		assertCodeContains(Fields12.class, "clazz=(String).prototype;");
	}

	@Test
	public void testPrivateFinalBooleanBug() {
		assertCodeContains(Fields13.class, "value : false,");
	}
}
