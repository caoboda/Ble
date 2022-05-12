@file:Suppress("unused")

package com.example.ble.util

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.*
import java.nio.charset.StandardCharsets

object DataUtils {
    //private val dataStore: DataStore<Preferences> = Utils.getApp().createDataStore(fileName = "DataStore")
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore_data")

    fun readDataBool(key: Preferences.Key<Boolean>, default: Boolean? = false): Flow<Boolean?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key] ?: default
    }

    fun readDataInt(key: Preferences.Key<Int>, default: Int? = 0): Flow<Int?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key] ?: default
    }

    fun readDataFloat(key: Preferences.Key<Float>, default: Float? = 0f): Flow<Float?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key] ?: default
    }

    fun readDataLong(key: Preferences.Key<Long>, default: Long? = 0): Flow<Long?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key] ?: default
    }

    fun readDataSerializable(key: Preferences.Key<String>, default: Serializable? = null): Flow<Serializable?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key]?.let { deSerialize(it) as Serializable? } ?: default
    }

    fun readDataParcelable(key: Preferences.Key<String>, default: ByteArray? = null): Flow<ByteArray?> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key]?.let { Base64.decode(it, 0) } ?: default
    }

    private fun serialize(o: Any): String {
        var result = ""
        try {
            val bos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(bos)
            oos.writeObject(o)
            result = String(Base64.encode(bos.toByteArray(), Base64.NO_WRAP), StandardCharsets.UTF_8)
            oos.flush()
            oos.close()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun marshall(parcelable: Parcelable): ByteArray {
        val parcel = Parcel.obtain()
        parcel.setDataPosition(0)
        parcelable.writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        LogUtils.d("ParcelableTest", "bytes = " + bytes.toString() + "parcel" + parcel.toString())
        parcel.recycle()
        return bytes
    }


    private fun deSerialize(str: String): Any? {
        var obj: Any? = null
        if (str == "") {
            return null
        }
        try {
            val base64Bytes = Base64.decode(str.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
            val bis = ByteArrayInputStream(base64Bytes)
            val ois = ObjectInputStream(bis)
            obj = ois.readObject()
            bis.close()
            ois.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return obj
    }

    inline fun <reified T : Parcelable> ByteArray.deserializeParcelable(): T {
        val parcel = Parcel
            .obtain()
            .apply {
                unmarshall(this@deserializeParcelable, 0, size)
                setDataPosition(0)
            }

        return parcelableCreator<T>()
            .createFromParcel(parcel)
            .also {
                parcel.recycle()
            }
    }

    inline fun <reified T : Parcelable> parcelableCreator(): Parcelable.Creator<T> {
        val creator = T::class.java.getField("CREATOR").get(null)
        @Suppress("UNCHECKED_CAST")
        return creator as Parcelable.Creator<T>
    }

    fun readData(key: Preferences.Key<String>, default: String = ""): Flow<String> = Utils.getApp().dataStore.data.catch {
        // 当读取数据遇到错误时，如果是 `IOException` 异常，发送一个 emptyPreferences 来重新使用
        // 但是如果是其他的异常，最好将它抛出去，不要隐藏问题
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        preferences[key] ?: default
    }

    suspend fun saveDataBool(key: Preferences.Key<Boolean>, value: Boolean) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }

    suspend fun saveDataFloat(key: Preferences.Key<Float>, value: Float) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }

    suspend fun saveDataInt(key: Preferences.Key<Int>, value: Int) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }

    suspend fun saveDataLong(key: Preferences.Key<Long>, value: Long) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }

    suspend fun saveDataSerializable(key: Preferences.Key<String>, value: Serializable) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = serialize(value)
        }
    }

    suspend fun saveDataParcelable(key: Preferences.Key<String>, value: Parcelable) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = Base64.encodeToString(marshall(value), 0)
        }
    }

    suspend fun saveData(key: Preferences.Key<String>, value: String) {
        Utils.getApp().dataStore.edit { mutablePreferences ->
            mutablePreferences[key] = value
        }
    }
}