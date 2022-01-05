package org.jenkinsci.plugins.parameterizedscheduler;

import hudson.model.ParametersDefinitionProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ParameterParserTest {

	@Mock
	private ParametersDefinitionProperty mockParametersDefinitionProperty;

	@Test
	public void test_nullReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();
		assertEquals(Collections.emptyMap(), testObject.parse(null));
	}

	@Test
	public void test_EmptyStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		assertEquals(Collections.emptyMap(), testObject.parse(""));
		assertEquals(Collections.emptyMap(), testObject.parse("     "));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Malformed_NoEquals_StringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();
		assertEquals(Collections.emptyMap(), testObject.parse("namevalue"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_Malformed_ExtraSemicolon_StringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<>();
		expected.put("name", "value");
		assertEquals(expected, testObject.parse("name=value;;"));
	}

	@Test
	public void test_OneParamStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<>();
		expected.put("name", "value");
		assertEquals(expected, testObject.parse("name=value"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_TrimsSpacesString() {
		ParameterParser testObject = new ParameterParser();
		assertEquals(Collections.singletonMap("name", "value"), testObject.parse(" name = value; ;;"));
	}

	@Test
	public void test_TwoParamsStringReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<>();
		expected.put("name", "value");
		expected.put("name2", "value2");
		assertEquals(expected, testObject.parse("name2=value2;name=value"));
	}

	@Test
	public void test_TwoParamsStringWithSpaceReturns_emptyMap() {
		ParameterParser testObject = new ParameterParser();

		HashMap<String, String> expected = new HashMap<>();
		expected.put("name", "value");
		expected.put("name2", "value2");
		assertEquals(expected, testObject.parse("name2=value2; name=value"));
	}

	@Test
	public void test_ValueContainsEquals_emptyMap() {
		ParameterParser testObject = new ParameterParser();
		assertEquals(Collections.singletonMap("name", "value=contains=equals"), testObject.parse("name=value=contains=equals"));
	}

	@Test
	public void checkSanity_HappyPath() {
		ParameterParser testObject = new ParameterParser();

		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(Collections.singletonList("name"));
		assertNull(testObject.checkSanity("* * * * *%name=value", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_NotDefined_ProjectParameter() {
		ParameterParser testObject = new ParameterParser();

		List<String> list = Collections.singletonList("not name");
		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(list);
		assertEquals(Messages.ParameterizedTimerTrigger_UndefinedParameter("[name]", list.toString()),
				testObject.checkSanity("* * * * *%name=value", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_TrailingSemiColon_IsTrimmed() {
		ParameterParser testObject = new ParameterParser();

		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(
				Arrays.asList("env", "freckled"));
		assertNull(testObject.checkSanity("* * * * *%env=eight;freckled=flase;", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_NoParametersIsNoBigDeal() {
		ParameterParser testObject = new ParameterParser();

		assertNull(testObject.checkSanity("* * * * *%", mockParametersDefinitionProperty));
		assertNull(testObject.checkSanity("* * * * *", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_duplicateParamName() {
		ParameterParser testObject = new ParameterParser();
		assertTrue(testObject.checkSanity("* * * * *%name=value;name=value2", mockParametersDefinitionProperty).startsWith("Duplicate key"));
	}

	@Test
	public void checkSanity_UnmatchedEquals() {
		ParameterParser testObject = new ParameterParser();
		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(
				Arrays.asList("name", "name2"));
		assertEquals(Messages.ParameterizedTimerTrigger_EmptyParameter(Collections.singletonList("name2")),
				testObject.checkSanity("* * * * *%name=value;name2=", mockParametersDefinitionProperty));
	}

	@Test
	public void checkSanity_NullParameters() {
		ParameterParser testObject = new ParameterParser();
		assertEquals(Messages.ParameterizedTimerTrigger_UndefinedParameter(Collections.singletonList("name"), Collections.emptyList()),
				testObject.checkSanity("* * * * *%name=value", null));
	}

	@Test
	public void test_paramValue_with_percent() {
		ParameterParser testObject = new ParameterParser();
		HashMap<String, String> expected = new HashMap<>();
		expected.put("name", "value");
		expected.put("percent", "10%");
		assertEquals(expected, testObject.parse("name=value;percent=10%"));
	}

	@Test
	public void checkSanity_paramValueWithPercent() {
		ParameterParser testObject = new ParameterParser();
		List<String> list = new ArrayList<>();
		list.add("percent");
		list.add("name");
		Mockito.when(mockParametersDefinitionProperty.getParameterDefinitionNames()).thenReturn(list);
		assertNull(testObject.checkSanity("* * * * *%percent=10%;name=value", mockParametersDefinitionProperty));
	}

}
