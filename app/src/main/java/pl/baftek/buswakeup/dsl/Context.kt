package pl.baftek.buswakeup.dsl

import android.content.Context
import pl.baftek.buswakeup.data.AppDatabase

fun Context.db(): AppDatabase {
    return AppDatabase.getInstance(this)
}