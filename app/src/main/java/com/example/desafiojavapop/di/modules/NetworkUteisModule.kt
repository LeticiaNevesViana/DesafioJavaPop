package com.example.desafiojavapop.di.modules

import com.example.desafiojavapop.util.NetworkUtils
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {

    single { NetworkUtils(androidContext()) }
}
