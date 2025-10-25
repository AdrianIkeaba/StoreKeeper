package com.ghostdev.storekeeperhng.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghostdev.storekeeperhng.data.local.AppDatabase
import com.ghostdev.storekeeperhng.data.repository.ProductRepositoryImpl
import com.ghostdev.storekeeperhng.domain.repository.ProductRepository
import com.ghostdev.storekeeperhng.domain.usecase.*
import com.ghostdev.storekeeperhng.presentation.addedit.AddEditViewModel
import com.ghostdev.storekeeperhng.presentation.detail.DetailViewModel
import com.ghostdev.storekeeperhng.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "storekeeper.db"
        )
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }
    single { get<AppDatabase>().productDao() }
}

val repositoryModule = module {
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single { com.ghostdev.storekeeperhng.data.prefs.ProfilePrefs(androidContext()) }
}

val useCaseModule = module {
    factory { AddProductUseCase(get()) }
    factory { GetProductsUseCase(get()) }
    factory { GetProductUseCase(get()) }
    factory { UpdateProductUseCase(get()) }
    factory { DeleteProductUseCase(get()) }
    factory { SearchProductsUseCase(get()) }
    factory { GetTotalsUseCase(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { (productId: Long?) -> AddEditViewModel(productId, get(), get(), get()) }
    viewModel { (productId: Long) -> DetailViewModel(productId, get(), get()) }
}