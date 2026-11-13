package es.altamirano.common.secure

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Un fichero cifrado con una clave AES del Android Keystore, que no sale del chip: el
 * equivalente al Llavero de iOS. `EncryptedSharedPreferences` está obsoleto; esto no necesita
 * ninguna librería. Se escribe a un temporal y se renombra: nunca queda a medias, que con el
 * `refresh_token` de Netatmo (cambia en cada renovación) sería perder la sesión.
 *
 * Va en `noBackupFilesDir`: en otro móvil la clave no existe y no se podría leer.
 * [alias] es el nombre de la clave en el Keystore, uno por app (**no cambiarlo**: lo ya guardado
 * dejaría de leerse).
 */
class SecureFile(context: Context, name: String, private val alias: String) {
    private val file = File(context.noBackupFilesDir, name)

    private fun key(): SecretKey {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (ks.getKey(alias, null) as? SecretKey)?.let { return it }
        val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        gen.init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build())
        return gen.generateKey()
    }

    /** El texto guardado, o null si no hay nada o no se puede descifrar. Formato: [nº de bytes del IV][IV][cifrado]. */
    @Synchronized
    fun read(): String? = try {
        if (!file.exists()) null else {
            val bytes = file.readBytes()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes, 1, bytes[0].toInt()))
            String(cipher.doFinal(bytes, 1 + bytes[0], bytes.size - 1 - bytes[0]))
        }
    } catch (e: Exception) {
        null
    }

    @Synchronized
    fun write(text: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val iv = cipher.iv
        val body = cipher.doFinal(text.toByteArray())
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeBytes(byteArrayOf(iv.size.toByte()) + iv + body)
        tmp.renameTo(file)
    }

    @Synchronized
    fun clear() { file.delete() }
}
