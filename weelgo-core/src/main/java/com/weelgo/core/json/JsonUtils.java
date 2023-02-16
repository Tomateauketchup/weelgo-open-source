package com.weelgo.core.json;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public static void serializeIntoJsonFile(Object obje, File f) throws Exception {
		serializeIntoJsonFile(obje, f, true);
	}

	public static void serializeIntoJsonFile(Object obje, File f, boolean prettyPrinter) throws Exception {
		ObjectMapper mapper = createJsonObjectMapper();
		if (prettyPrinter)
			mapper.writerWithDefaultPrettyPrinter().writeValue(f, obje);
		else
			mapper.writer().writeValue(f, obje);
	}

	public static void serializeIntoJson(Object obje, OutputStream output) throws Exception {
		if (output != null) {
			try {
				ObjectMapper mapper = createJsonObjectMapper();

				mapper.writer().writeValue(output, obje);
			} finally {
				IOUtils.closeQuietly(output);
			}
		}
	}

	public static String serializeIntoJsonString(Object obje) throws Exception {
		return serializeIntoJsonString(obje, false);
	}

	public static String serializeIntoPrettyJsonString(Object obje) throws Exception {
		return serializeIntoJsonString(obje, true);
	}

	public static String serializeIntoJsonString(Object obje, boolean prettyPrint) throws Exception {
		ObjectMapper mapper = createJsonObjectMapper();
		if (prettyPrint) {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obje);
		} else {
			return mapper.writer().writeValueAsString(obje);
		}
	}
	
	public static byte[] serializeIntoJsonByteArray(Object obje, boolean prettyPrint) throws Exception {
		ObjectMapper mapper = createJsonObjectMapper();
		if (prettyPrint) {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(obje);
		} else {
			return mapper.writer().writeValueAsBytes(obje);
		}
	}

	public static <T> T deserializeJsonString(String str, Class<T> valueType) throws Exception {
		if (str != null && str.isEmpty() == false) {
			ObjectMapper mapper = createJsonObjectMapper();
			return mapper.readValue(str, valueType);
		}
		return null;
	}

	public static <T> T deserializeJsonFile(File f, Class<T> valueType) throws Exception {
		ObjectMapper mapper = createJsonObjectMapper();
		return mapper.readValue(FileUtils.readFileToByteArray(f), valueType);
	}

	public static <T> T deserializeJson(InputStream input, Class<T> valueType) throws Exception {
		if (input != null) {
			try {
				ObjectMapper mapper = createJsonObjectMapper();
				return mapper.readValue(input, valueType);
			} finally {
				IOUtils.closeQuietly(input);
			}
		}
		return null;
	}

	public static ObjectMapper createJsonObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		configureJsonObjectMapper(objectMapper);
		// objectMapper.enableDefaultTyping();
		// objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,
		// false);
		// objectMapper.setDateFormat(new
		// SimpleDateFormat(Constants.JSON_DATE_FORMAT));
		// objectMapper.setSerializationInclusion(Include.NON_EMPTY);
		return objectMapper;
	}

	public static void configureJsonObjectMapper(ObjectMapper o) {
		if (o != null) {
			o.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			o.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
			o.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			o.setDateFormat(new SimpleDateFormat(JSON_DATE_FORMAT));
			o.setSerializationInclusion(Include.NON_EMPTY);

		}
	}

}
