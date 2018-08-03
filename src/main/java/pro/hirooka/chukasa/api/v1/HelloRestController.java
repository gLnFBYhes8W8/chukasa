package pro.hirooka.chukasa.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.hirooka.chukasa.domain.model.api.ChukasaResponse;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequestMapping("${api.root}/hello")
@RestController
public class HelloRestController {

    @GetMapping("")
    public ChukasaResponse hello(){
        final Instant instant = Instant.ofEpochMilli(new Date().getTime());
        final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        return new ChukasaResponse(zonedDateTime.format(dateTimeFormatter) + "|" + zonedDateTime.toEpochSecond());
    }
}
