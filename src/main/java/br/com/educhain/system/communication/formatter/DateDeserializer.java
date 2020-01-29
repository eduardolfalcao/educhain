package br.com.educhain.system.communication.formatter;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateDeserializer implements JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		String date = element.getAsString();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		try {
			return format.parse(date);
		} catch (ParseException exp) {
			System.err.println("Failed to parse Date:"+ exp);
			return null;
		}
	}
}
