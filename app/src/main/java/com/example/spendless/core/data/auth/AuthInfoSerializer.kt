package com.example.spendless.core.data.auth

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AuthInfoSerializer : Serializer<AuthInfoSerializable?> {
    override val defaultValue: AuthInfoSerializable? = null

    override suspend fun readFrom(input: InputStream): AuthInfoSerializable? {
        val jsonString = withContext(Dispatchers.IO) {
            input.use { it.readBytes().decodeToString() }
        }
        return if (jsonString.isBlank()) null else Json.decodeFromString(
            AuthInfoSerializable.serializer(),
            jsonString
        )
    }

    override suspend fun writeTo(t: AuthInfoSerializable?, output: OutputStream) {
        val jsonString = t?.let { Json.encodeToString(AuthInfoSerializable.serializer(), it) } ?: ""
        withContext(Dispatchers.IO) {
            output.use { it.write(jsonString.toByteArray()) }
        }
    }
}