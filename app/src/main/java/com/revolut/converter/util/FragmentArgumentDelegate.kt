package com.revolut.converter.util

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> arg() = FragmentArgumentDelegate<T>()

class FragmentArgumentDelegate<T> : ReadWriteProperty<Fragment, T> {

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        thisRef.arguments?.let { arguments ->
            return arguments.getValue(property.name)
        } ?: run {
            throw ArgumentsNotFoundException()
        }
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        if (thisRef.arguments == null) {
            thisRef.arguments = Bundle()
        }
        thisRef.arguments?.putValue(value, property.name)
    }

    @Suppress("UNCHECKED_CAST")
    private fun Bundle.getValue(name: String): T {
        return get(name) as T
    }

    private fun Bundle.putValue(value: T, key: String) {
        when (value) {
            null -> putString(key, null) // Any nullable type will suffice.

            // Scalars
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Byte -> putByte(key, value)
            is Char -> putChar(key, value)
            is Double -> putDouble(key, value)
            is Float -> putFloat(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Short -> putShort(key, value)

            // References
            is Bundle -> putBundle(key, value)
            is CharSequence -> putCharSequence(key, value)
            is Parcelable -> putParcelable(key, value)
            is Binder -> BundleCompat.putBinder(this, key, value)
            is java.io.Serializable -> putSerializable(key, value)

            // Scalar arrays
            is BooleanArray -> putBooleanArray(key, value)
            is ByteArray -> putByteArray(key, value)
            is CharArray -> putCharArray(key, value)
            is DoubleArray -> putDoubleArray(key, value)
            is FloatArray -> putFloatArray(key, value)
            is IntArray -> putIntArray(key, value)
            is LongArray -> putLongArray(key, value)
            is ShortArray -> putShortArray(key, value)

            else -> throw IllegalStateException("Type of property $key is not supported")
        }
    }

    class ArgumentsNotFoundException : IllegalStateException("Fragment should set arguments")
}
