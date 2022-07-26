package com.nimkat.app.models

class DataHolder<T>(val data: T?, val message: String?, val status: DataStatus) {

    companion object {
        fun <T> error(message: String? = "Error!"): DataHolder<T> = DataHolder(null, message, DataStatus.Error)
        fun <T> success(data: T): DataHolder<T> = DataHolder(data, null, DataStatus.Success)
        fun <T> loadingNext(data: T?): DataHolder<T> = DataHolder(data, null, DataStatus.LoadingNext)
        fun <T> loading(message: String? = "Loading..."): DataHolder<T> = DataHolder(null, message, DataStatus.Loading)
        fun <T> pure(): DataHolder<T> = DataHolder(null, null, DataStatus.Pure)
        fun <T> needCompletion(data: T): DataHolder<T> = DataHolder(data, null, DataStatus.NeedCompletion)
        fun <T> needLogin(): DataHolder<T> = DataHolder(null, null, DataStatus.NeedLogin)
    }
}

enum class DataStatus {
    Pure, Error, Loading, LoadingNext, Success , NeedCompletion , NeedLogin
}