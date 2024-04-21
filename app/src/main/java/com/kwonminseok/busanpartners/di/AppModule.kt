package com.kwonminseok.busanpartners.di

import AppDatabase
import UserDao
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kwonminseok.busanpartners.repository.FirebaseUserRepository
import com.kwonminseok.busanpartners.repository.FirebaseUserRepositoryImpl
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//싱글톤 패턴은 객체의 인스턴스를 한개만 생성되게 하는 패턴입니다.
// https://velog.io/@seongwon97/%EC%8B%B1%EA%B8%80%ED%86%A4Singleton-%ED%8C%A8%ED%84%B4%EC%9D%B4%EB%9E%80
//이러한 패턴은 주로 프로그램 내에서 하나로 공유를 해야하는 객체가 존재할 때 해당 객체를 싱글톤으로 구현하여
// 모든 유저 또는 프로그램들이 해당 객체를 공유하며 사용하도록 할 때 사용됩니다.
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: StorageReference,

    ): FirebaseUserRepository = FirebaseUserRepositoryImpl(auth, firestore, storage)


    @Provides
    @Singleton // 여기저기 호출하고 다니지만 사실상 1개를 돌려쓰는 중임...
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

//    @Provides
//    @Singleton // 여기저기 호출하고 다니지만 사실상 1개를 돌려쓰는 중임...
//    fun provideFirebaseDB() = FirebaseDatabase.getInstance().reference
//
//
    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideCloudFunction() = Firebase.functions
//
//    // 뷰모델에서 호출할 때마다 여기 있는 거 가져다가 쓰는 듯????
//
//    @Provides
//    // 여기서는 introduction에서만 사용하기 때문에 별도의 싱글톤을 만들 필요가 없다.
//    fun provideIntroductionSP(
//        application: Application
//    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)
//
//    @Provides
//    @Singleton
//    fun provideFirebaseCommon(
//        firebaseAuth: FirebaseAuth,
//        firestore: FirebaseFirestore
//    ) = FireBaseCommon(firestore,firebaseAuth)
//
//
    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference
//
//
//    @Provides
//    @Singleton
//    fun provideQueryProducts() = FirebaseFirestore.getInstance()
//        .collection(PRODUCTS_COLLECTION)
////        .orderBy(NAME_PROPERTY, ASCENDING)
////        .limit(PAGE_SIZE.toLong())
//
//    @Provides
//    @Singleton
//    fun provideQueryProductsByName() = FirebaseFirestore.getInstance()
//        .collection(PRODUCTS_COLLECTION)
////        .orderBy("pageNumber")
//        .limit(PAGE_SIZE.toLong())


}