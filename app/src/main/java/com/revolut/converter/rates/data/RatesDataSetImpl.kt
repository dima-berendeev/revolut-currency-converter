package com.revolut.converter.rates.data

import android.content.Context
import androidx.room.*
import com.revolut.converter.rates.model.RatesDataSet
import com.revolut.converter.rates.type.CurrencyCode
import com.revolut.converter.util.logDebug
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

private const val RATES_DATA_SET_TAG = "RATES_DATA_SET"

class RatesDataSetImpl @Inject constructor(private val context: Context) : RatesDataSet {

    private val ratesDao by lazy { RatesDatabase.getDatabase(context).getRatesDao() }

    override fun save(
        baseCurrency: CurrencyCode,
        currencyMap: Map<CurrencyCode, BigDecimal>
    ): Completable {
        return Observable.fromIterable(currencyMap.entries)
            .concatMapCompletable { entry ->
                val rateEntity = RateEntity(
                    baseCurrency = baseCurrency.asString,
                    correspondingCurrency = entry.key.asString,
                    ratio = entry.value.toString()
                )
                ratesDao.insert(rateEntity)
            }.doOnComplete {
                logDebug(
                    RATES_DATA_SET_TAG,
                    "Cash for currency '${baseCurrency.asString}' was updated"
                )
            }
    }

    override fun get(baseCurrency: CurrencyCode): Single<Map<CurrencyCode, BigDecimal>> {
        return ratesDao.getRates(baseCurrency = baseCurrency.asString)
            .map { entityList: List<RateEntity> ->
                entityList.map { entity ->
                    CurrencyCode(entity.correspondingCurrency) to entity.ratio.toBigDecimal()
                }.toMap()
            }
    }

    override fun getCacheSize(): Single<Int> {
        return ratesDao.getRowCount()
    }
}


@Entity(tableName = "rates_table", primaryKeys = ["base_currency", "corresponding_currency"])
private class RateEntity(
    @ColumnInfo(name = "base_currency") val baseCurrency: String,
    @ColumnInfo(name = "corresponding_currency") val correspondingCurrency: String,
    @ColumnInfo(name = "ratio") val ratio: String
)

@Dao
private interface RatesDao {
    @Query("SELECT COUNT(*) FROM rates_table")
    fun getRowCount(): Single<Int>

    @Query("SELECT * from rates_table WHERE base_currency=:baseCurrency")
    fun getRates(baseCurrency: String): Single<List<RateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rateEntity: RateEntity): Completable
}

@Database(entities = [RateEntity::class], version = 2, exportSchema = false)
private abstract class RatesDatabase : RoomDatabase() {

    abstract fun getRatesDao(): RatesDao

    companion object {
        @Volatile
        private var INSTANCE: RatesDatabase? = null

        fun getDatabase(context: Context): RatesDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RatesDatabase::class.java,
                    "rates_database.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
