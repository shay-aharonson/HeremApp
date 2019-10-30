package com.heremapp.data

import android.annotation.SuppressLint
import android.content.Context
import com.heremapp.data.models.place.Place
import com.heremapp.data.persistedmodels.persistedplace.PersistedOpeningHours
import com.heremapp.data.persistedmodels.persistedplace.PersistedPlace
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manage and provide access to Realm.
 *
 * TODO: Refactor.
 */
@Singleton
class PersistedDataStore @Inject constructor(context: Context) {

    private val places = BehaviorSubject.create<List<Place>>()
//    private val realmScheduler: Scheduler by lazy {
//        val realmHandlerThread = HandlerThread("realmHandlerThread")
//        if (!realmHandlerThread.isAlive)
//            realmHandlerThread.start()
//        AndroidSchedulers.from(realmHandlerThread.looper)
//    }

    private lateinit var realm: Realm
    private lateinit var persistedPlacesObservable: Observable<RealmResults<PersistedPlace>>

    init {
        initPlacesSubject()
        initRealm(context)
    }

    /**
     * Save the given place to realm or update an existing record.
     */
    fun savePlace(place: Place): Single<Place> {
        var result = Place()

        return Single.just(Unit)
//            .subscribeOn(realmScheduler)
//            .unsubscribeOn(realmScheduler)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                realm.executeTransaction {
                    val savedPlace = realm.copyToRealmOrUpdate(
                        place.toPersistedClass()
                            .also { log("Converted Place to PersistentPlace: $it") }
                    )

                    result = Place(savedPlace)
                }
            }
            .map { result }
    }

    /**
     * Delete the [PersistedPlace] and [PersistedOpeningHours] records associated with the given place.
     */
    fun deletePlace(place: Place): Single<Boolean> {
        var result = false

        return Single.just(Unit)
//            .subscribeOn(realmScheduler)
//            .unsubscribeOn(realmScheduler)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                realm.executeTransaction {
                    val placesDeleted = realm.where(PersistedPlace::class.java).equalTo("id", place.id)
                        .findAll()
                        .deleteAllFromRealm()
                    log("Place deleted: $placesDeleted.")

                    val hoursDeleted = realm.where(PersistedOpeningHours::class.java).equalTo("id", place.id)
                        .findAll()
                        .deleteAllFromRealm()
                    log("Hours deleted: $hoursDeleted.")

                    result = placesDeleted && hoursDeleted
                }
            }
            .map { result }
    }

    /**
     * Subscribe to datastore changes, will emit a list of all persisted places upon every change.
     */
    fun subscribeToPlaces(): Observable<List<Place>> {
        return places
    }

    /**
     * Retrieve the last fetched datastore values.
     */
    fun getLoadedPlaces(): List<Place> {
        return places.value ?: emptyList()
    }

    /**
     * Initialize the hot observable on which the persistent data events will be emitted.
     */
    @SuppressLint("CheckResult")
    private fun initPlacesSubject() {
        places
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                log("Places updated:\n${it.joinToString("\n")}")
            }, ::log)
    }

    /**
     * Initialize the Realm database and retrieve all persisted places, while subscribing to the realm results for
     * future changes.
     */
    @SuppressLint("CheckResult")
    private fun initRealm(context: Context) {

        Realm.init(context)
        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .build()
        )
        log("Realm initialized.")

        Observable.just(Unit)
//            .subscribeOn(realmScheduler)
//            .unsubscribeOn(realmScheduler)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                realm = Realm.getDefaultInstance()
                log("Realm instance obtained.")

                realm.executeTransaction {
                    persistedPlacesObservable = realm.where(PersistedPlace::class.java)
                        .findAll()
                        .asFlowable()
                        .toObservable()
                }
            }
            .flatMap { persistedPlacesObservable }
            .subscribe({ realmResults ->
                places.onNext(realmResults.map(::Place))
            }
                , ::log)
    }
}
