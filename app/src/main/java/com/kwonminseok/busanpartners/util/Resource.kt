package com.kwonminseok.busanpartners.util
// T는 제네릭 타입  * 제네릭 타입으로 클래스를 만들면, 입력 파라미터 타입이 다르더라도 하나의 클래스로 사용이 가능함.
sealed class Resource<T> ( // Resource<List<Product>>
    val data: T?= null, // data = List<Product>>
    val message: String?= null
        ) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(message = message)
    class Loading<T>: Resource<T>()
    class Unspecified<T>: Resource<T>()
//    class ProgressNull<T>: Resource<T>()
}
