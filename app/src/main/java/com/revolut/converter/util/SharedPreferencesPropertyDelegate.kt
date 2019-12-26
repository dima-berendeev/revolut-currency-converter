package com.revolut.converter.util

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class SharedPreferencesPropertyDelegate<T>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValueBlock: () -> T,
    private val afterChanged: (() -> Unit)?,
    private val propertyClass: KClass<*>
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (!sharedPreferences.contains(key)) {
            return defaultValueBlock()
        }

        return when (propertyClass) {
            String::class -> sharedPreferences.getString(key, "") as T
            Int::class -> sharedPreferences.getInt(key, -1) as T
            Long::class -> sharedPreferences.getLong(key, -1L) as T
            Float::class -> sharedPreferences.getFloat(key, -1F) as T
            Boolean::class -> sharedPreferences.getBoolean(key, false) as T
            Set::class -> sharedPreferences.getStringSet(key, emptySet()) as T
            else -> throw IllegalStateException("Cannot put $key")
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        when (propertyClass) {
            String::class -> sharedPreferences.edit().putString(key, value as String?).apply()
            Int::class -> sharedPreferences.edit().putInt(key, value as Int).apply()
            Long::class -> sharedPreferences.edit().putLong(key, value as Long).apply()
            Float::class -> sharedPreferences.edit().putFloat(key, value as Float).apply()
            Boolean::class -> sharedPreferences.edit().putBoolean(key, value as Boolean).apply()
            Set::class -> sharedPreferences.edit().putStringSet(key, value as Set<String>).apply()
            else -> throw IllegalStateException("Cannot put $key")
        }

        afterChanged?.invoke()
    }
}

inline fun <reified T> preferenceProperty(
    sharedPreferences: SharedPreferences,
    key: String,
    noinline afterChanged: (() -> Unit)? = null,
    noinline defaultValueBlock: () -> T
): SharedPreferencesPropertyDelegate<T> {
    return SharedPreferencesPropertyDelegate(
        sharedPreferences = sharedPreferences,
        key = key,
        afterChanged = afterChanged,
        defaultValueBlock = defaultValueBlock,
        propertyClass = T::class
    )
}
