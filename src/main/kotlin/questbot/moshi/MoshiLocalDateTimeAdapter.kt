package questbot.moshi

import com.squareup.moshi.*
import java.time.LocalDateTime


class MoshiLocalDateTimeAdapter : JsonAdapter<LocalDateTime?>() {
    @FromJson
    override fun fromJson(reader: JsonReader): LocalDateTime? {
        val string = reader.nextString()
        return LocalDateTime.parse(string)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        val string: String? = value?.toString()
        writer.value(string)
    }
}
