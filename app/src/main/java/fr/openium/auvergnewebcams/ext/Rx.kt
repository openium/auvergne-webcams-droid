package fr.openium.auvergnewebcams.ext

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by t.coulange on 03/06/2016.
 */
fun <T> Observable<T>.computationS(): Observable<T> {
    return subscribeOn(Schedulers.computation())
}

fun <T> Observable<T>.ioS(): Observable<T> {
    return subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.mainThread(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.fromIOToMain(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.fromIOToMain(): Single<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun Completable.fromIOToMain(): Completable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}