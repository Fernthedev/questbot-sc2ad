package questbot.moshi

import com.squareup.moshi.*
import java.util.*


class MoshiUUIDAdapter : JsonAdapter<UUID?>() {
    @FromJson
    override fun fromJson(reader: JsonReader): UUID? {
        val string = reader.nextString()
        return UUID.fromString(string)
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: UUID?) {
        val string: String? = value?.toString()
        writer.value(string)
    }
}
