
package com.browserhelper
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
object VaultManager {
    private fun prefs(c:Context) = EncryptedSharedPreferences.create(c, "vault", MasterKey.Builder(c).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(), EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    fun save(c:Context, site:String, user:String, pass:String){ prefs(c).edit().putString(site+"|"+user, pass).apply() }
    fun getAll(c:Context): Map<String,String> = prefs(c).all.mapNotNull { (k,v) -> if(v is String) k to v else null }.toMap()
}
object PasswordGenerator {
    fun generate(len:Int=20, upper:Boolean=true, lower:Boolean=true, digits:Boolean=true, symbols:Boolean=true): String{
        var chars=""; if(upper) chars+="ABCDEFGHIJKLMNOPQRSTUVWXYZ"; if(lower) chars+="abcdefghijklmnopqrstuvwxyz"; if(digits) chars+="0123456789"; if(symbols) chars+="!@#\$%^&*()-_=+[]{}|;:,.<>?"
        return (1..len).map { chars.random() }.joinToString("")
    }
}
