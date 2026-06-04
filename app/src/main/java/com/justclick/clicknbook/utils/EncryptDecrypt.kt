package com.justclick.clicknbook.utils

import android.R.attr.key
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


object EncryptDecrypt {
    private fun getEncryptionKey(context: Context): String {
        val key = "c9XAzmFaC4l5lmdsipTaJqMKjYu2lW00"
        return key
    }

    private fun getSessionEncryptionKey(context: Context): String {
//        return context.getResources().getString(R.String.google_api_key).subString(8);
        return "nwA9gVUzpa0wFostuUWuPZmdiWci63oo"
    }

     fun encryption(text:String, context: Context): String {
         val cipher = Cipher.getInstance("AES/GCM/NoPadding")
//         val key = SecretKeySpec(getEncryptionKey(context).toByteArray(), "AES")
         val key = getOrCreateSecretKey(getEncryptionKey(context))
         cipher.init(Cipher.ENCRYPT_MODE, key)
         val iv = cipher.iv // Get the randomly generated IV
         val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

         // Concatenate IV and ciphertext, then Base64 encode the result for storage/transmission
         val combined = iv + encryptedBytes
         return Base64.encodeToString(combined, Base64.DEFAULT)
    }

     fun encryption2(text:String, context: Context): String {
         val iv = ByteArray(12) // GCM recommended IV size is 12 bytes
         SecureRandom().nextBytes(iv)

         val cipher = Cipher.getInstance("AES/GCM/NoPadding")
         val spec = GCMParameterSpec(128, iv) // 128-bit authentication tag
         val key = SecretKeySpec(getEncryptionKey(context).toByteArray(), "AES")
         cipher.init(Cipher.ENCRYPT_MODE, key, spec)

         val ciphertext = cipher.doFinal(text.toByteArray(Charsets.UTF_8))


         // Combine IV and ciphertext/tag and encode to Base64 for storage/transmission
         // The tag is automatically appended to the ciphertext in Java's GCM implementation
         val combined = ByteArray(iv.size + ciphertext.size)
         System.arraycopy(iv, 0, combined, 0, iv.size)
         System.arraycopy(ciphertext, 0, combined, iv.size, ciphertext.size)

         return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decryption(encryptedData: String, context: Context): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)
        // The IV is the first 16 bytes for AES (128 bits)
        val iv = combined.copyOfRange(0, 16)
        val cipherText = combined.copyOfRange(16, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivSpec = GCMParameterSpec(128, iv) // GCM needs the tag length (128 bits)
//        val key = SecretKeySpec(getEncryptionKey(context).toByteArray(), "AES")
        val key = getOrCreateSecretKey(getEncryptionKey(context))
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        val decryptedBytes = cipher.doFinal(cipherText)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun decryption2(encryptedData: String, context: Context): String {
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)


        // Extract IV (first 12 bytes)
        val iv = ByteArray(12)
        System.arraycopy(decodedData, 0, iv, 0, iv.size)


        // Extract the actual ciphertext and tag
        val ciphertextLength = decodedData.size - 12
        val ciphertext = ByteArray(ciphertextLength)
        System.arraycopy(decodedData, 12, ciphertext, 0, ciphertextLength)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        val key = SecretKeySpec(getEncryptionKey(context).toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        val decryptedBytes = cipher.doFinal(ciphertext)
        return String(decryptedBytes, charset("UTF-8"))
    }


    fun getOrCreateSecretKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (keyStore.containsAlias(alias)) {
            return keyStore.getKey(alias, null) as SecretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).run {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256) // Use 128, 192, or 256 bits
            build()
        }
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
}