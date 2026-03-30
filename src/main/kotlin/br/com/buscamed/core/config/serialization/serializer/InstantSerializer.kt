package br.com.buscamed.core.config.serialization.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

/**
 * Serializador customizado do `kotlinx.serialization` para lidar com instâncias de [Instant].
 * 
 * Converte [Instant] para String no formato ISO-8601 e vice-versa,
 * permitindo que DTOs usem classes de data/hora do pacote java.time.
 */
object InstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Instant) = 
        encoder.encodeString(value.toString())
    
    override fun deserialize(decoder: Decoder): Instant = 
        Instant.parse(decoder.decodeString())
}
