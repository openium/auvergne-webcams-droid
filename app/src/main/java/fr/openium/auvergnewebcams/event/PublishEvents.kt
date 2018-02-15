package fr.openium.auvergnewebcams.event

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by laura on 08/12/2017.
 */
abstract class PublishEvents<T> {
    private val subject: PublishSubject<T> = PublishSubject.create()
    val obs: Observable<T>
        get() {
            return subject
        }

    fun set(t: T) {
        subject.onNext(t)
    }
}