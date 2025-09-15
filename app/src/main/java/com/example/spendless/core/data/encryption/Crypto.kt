package com.example.spendless.core.data.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object Crypto {
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val KEY_ALIAS = "secret"

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getKey(): SecretKey {
        // Retrieves or generates a secret key
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        // Generates a new key if not already present
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false) // Explicitly set, for clarity we are not using biometric
                    .build()
            )
        }.generateKey()
    }


    suspend fun encrypt(bytes: ByteArray): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Initializes the cipher in encrypt mode and encrypts data
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(bytes)
            iv + encrypted
        } catch (e: Exception) {
            throw SecurityException("Encryption failed", e)
        }
    }

    suspend fun decrypt(bytes: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        try {
            // Extracts IV and decrypts the data
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = bytes.copyOfRange(0, cipher.blockSize)
            val data = bytes.copyOfRange(cipher.blockSize, bytes.size)
            cipher.init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
            cipher.doFinal(data)
        } catch (e: Exception) {
            throw SecurityException("Decryption failed", e)
        }
    }
}