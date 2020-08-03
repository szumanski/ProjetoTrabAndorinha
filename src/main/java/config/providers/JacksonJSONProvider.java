package config.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import util.Constants;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Provider
@Consumes(Constants.JSON_UTF8)
@Produces(Constants.JSON_UTF8)
public class JacksonJSONProvider extends com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider {

    static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public JacksonJSONProvider() {
        super();
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        _mapperConfig.getDefaultMapper()
                .setDateFormat(dateTimeFormat)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}

//TODO customizar os serializadores??

class JsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        String dateText = parser.getText();

        try {
            if (dateText.length() == 10 ) {
                return JacksonJSONProvider.dateFormat.parse(dateText);
            }
            else{
                return JacksonJSONProvider.dateTimeFormat.parse(dateText);
            }
        } catch (ParseException e) {
            throw new RuntimeException("Can't parse date " + dateText, e);
        }
    }
}

class JsonDateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        String formattedDate = JacksonJSONProvider.dateTimeFormat.format(date);
        gen.writeString(formattedDate);
    }
}
