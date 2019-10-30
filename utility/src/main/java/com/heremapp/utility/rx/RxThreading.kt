package com.heremapp.utility.rx

import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Use for network calls whose results update on the main thread for [Flowable].
 *
 * If a [lifecycle] is passed in, then the returned [Flowable] will complete if the [activityEvent] occurs (defaults
 * to [ActivityEvent.DESTROY]).
 *
 * @return [Flowable] that observes on main thread, and completes on the given lifecycle
 *         event.
 */
fun <T : Any> Flowable<T>.mainThread(
    lifecycle: Flowable<ActivityEvent>? = null,
    activityEvent: ActivityEvent = ActivityEvent.DESTROY
): Flowable<T> {
    var transformed = this.observeOn(AndroidSchedulers.mainThread())
    if (lifecycle != null) {
        transformed = transformed.bindUntil(lifecycle, activityEvent)
    }
    return transformed
}

fun <T : Any> Flowable<T>.mainThread(
    lifecycle: Flowable<FragmentEvent>? = null,
    fragmentEvent: FragmentEvent = FragmentEvent.DESTROY
): Flowable<T> {
    var transformed = this.observeOn(AndroidSchedulers.mainThread())
    if (lifecycle != null) {
        transformed = transformed.bindUntil(lifecycle, fragmentEvent)
    }
    return transformed
}

/**
 * Use for network calls whose results update on the main thread for [Observable].
 *
 * If a [lifecycle] is passed in, then the returned [Observable] will complete if the [activityEvent] occurs (defaults
 * to [ActivityEvent.DESTROY]).
 *
 * @return [Observable] that observes on main thread, and completes on the given lifecycle
 *         event.
 */
fun <T : Any> Observable<T>.mainThread(
    lifecycle: Observable<ActivityEvent>? = null,
    activityEvent: ActivityEvent = ActivityEvent.DESTROY
): Observable<T> {
    var transformed = this.observeOn(AndroidSchedulers.mainThread())
    if (lifecycle != null) {
        transformed = transformed.bindUntil(lifecycle, activityEvent)
    }
    return transformed
}

fun <T : Any> Observable<T>.mainThread(
    lifecycle: Observable<FragmentEvent>? = null,
    fragmentEvent: FragmentEvent = FragmentEvent.DESTROY
): Observable<T> {
    var transformed = this.observeOn(AndroidSchedulers.mainThread())
    if (lifecycle != null) {
        transformed = transformed.bindUntil(lifecycle, fragmentEvent)
    }
    return transformed
}

/**
 * Use for network calls whose results update on the main thread for [Single].
 *
 * The returned [Observable] will complete if the [activityEvent] occurs on the [lifecycle] (defaults to
 * [ActivityEvent.DESTROY]).
 * Note that this converts a [Single] into an [Observable], so that it can complete without emitting any events.
 *
 * @return [Observable] that observes on main thread, and completes on the given activity
 *         lifecycle event
 */
fun <T : Any> Single<T>.mainThread(
    lifecycle: Observable<ActivityEvent>?,
    activityEvent: ActivityEvent = ActivityEvent.DESTROY
): Observable<T> {
    return if (lifecycle != null) {
        this.observeOn(AndroidSchedulers.mainThread())
            .bindUntil(lifecycle, activityEvent)
    } else {
        this.observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }
}

fun <T : Any> Single<T>.mainThread(
    lifecycle: Observable<FragmentEvent>?,
    fragmentEvent: FragmentEvent = FragmentEvent.DESTROY
): Observable<T> {
    return if (lifecycle != null) {
        this.observeOn(AndroidSchedulers.mainThread())
            .bindUntil(lifecycle, fragmentEvent)
    } else {
        this.observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }
}

/**
 * Use for network calls whose results update on the main thread for [Single].
 *
 * @return [Single] that observes on the main thread.
 */
fun <T : Any> Single<T>.mainThread(): Single<T> {
    return this.observeOn(AndroidSchedulers.mainThread())
}