package com.heremapp.utility.rx

import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.FragmentEvent
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Converts an Observable with a [Optional] event type into one with a not Optional event type,
 * while ignoring the null events.
 *
 * Prevents us from having to use .filter { !it.isNull }.map { it.get() } everywhere.
 */
fun <T : Any> Observable<Optional<T>>.dropNull(): Observable<T> {
    return filter { !it.isNull }.map { it.get() }
}

/**
 * Converts an Observable with an Optional event type into one that applies given function to the next non-null,
 * returning Observable.empty() if a null Optional is emitted by the source.
 */
fun <T, S : Any> Observable<Optional<T>>.switchMapDropNull(onNextNonNull: (T) -> Observable<S>): Observable<S> {
    return switchMap { optionalItem ->
        optionalItem.valueOrNull?.let(onNextNonNull) ?: Observable.empty()
    }
}

fun <T : Any> Flowable<T>.bindUntil(
    lifecycle: Flowable<ActivityEvent>,
    event: ActivityEvent = ActivityEvent.DESTROY
): Flowable<T> {
    return this.takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Flowable<T>.bindUntil(
    lifecycle: Flowable<FragmentEvent>,
    event: FragmentEvent = FragmentEvent.DESTROY
): Flowable<T> {
    return this.takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Observable<T>.bindUntil(
    lifecycle: Observable<ActivityEvent>,
    event: ActivityEvent = ActivityEvent.DESTROY
): Observable<T> {
    return this.takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Observable<T>.bindUntil(
    lifecycle: Observable<FragmentEvent>,
    event: FragmentEvent = FragmentEvent.DESTROY
): Observable<T> {
    return this.takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Single<T>.bindUntil(
    lifecycle: Observable<ActivityEvent>,
    event: ActivityEvent = ActivityEvent.DESTROY
): Observable<T> {
    return this.toObservable().takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Single<T>.bindUntil(
    lifecycle: Observable<FragmentEvent>,
    event: FragmentEvent = FragmentEvent.DESTROY
): Observable<T> {
    return this.toObservable().takeUntil(lifecycle.takeFirst { it == event })
}

fun <T : Any> Flowable<T>.takeFirst(predicate: (nextItem: T) -> Boolean): Flowable<T> {
    return this.takeUntil { predicate(it) }.takeLast(1)
}

fun <T : Any> Observable<T>.takeFirst(predicate: (nextItem: T) -> Boolean): Observable<T> {
    return this.takeUntil { predicate(it) }.takeLast(1)
}