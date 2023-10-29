@file:Suppress("unused")
    package de.maxbossing.mxpaper.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

/**
 * Serializes [UUID]s
 * If the serialized UUID does not contain dashes, it will reinsert them
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    private val uuidPattern = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})"

    override fun deserialize(decoder: Decoder): UUID {
        var uuid = decoder.decodeString()
        if (uuid.contains("-")) {
            uuid = uuid.replace(uuidPattern, "$1-$2-$3-$4-$5")
        }
        return UUID.fromString(uuid)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}