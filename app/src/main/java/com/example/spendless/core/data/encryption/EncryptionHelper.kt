package com.example.spendless.core.data.encryption

import android.util.Base64

object EncryptionHelper {
    suspend fun encryptedValue(value: String): String {
        val encryptedValue = encryptToBase64(value)
        return encryptedValue
    }

    suspend fun decryptedValue(value: String): String {
        val decryptedValue = decryptFromBase64(value)
        return decryptedValue
    }

    private suspend fun encryptToBase64(value: String): String {
        val encrypted = Crypto.encrypt(value.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private suspend fun decryptFromBase64(value: String): String {
        val decodedBytes = Base64.decode(value, Base64.DEFAULT)
        val decrypted = Crypto.decrypt(decodedBytes)
        return decrypted?.decodeToString() ?: ""
    }
}

