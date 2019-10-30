package com.heremapp.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import com.heremapp.presentation.permissions.RequestPermissionCallback
import com.heremapp.utility.messaging.MessageHandler.Companion.log
import com.heremapp.utility.rx.mainThread
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Base Activity implementation.
 */
open class BaseActivity : RxAppCompatActivity() {

    val activityResults: Observable<ActivityResult> by lazy {
        activityResultsSubject.compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
    }

    val requestPermissionsResults: Observable<RequestPermissionResult> by lazy {
        requestPermissionsResultsSubject.compose(RxLifecycle.bindUntilEvent(lifecycle(), ActivityEvent.DESTROY))
    }

    private val activityResultsSubject: PublishSubject<ActivityResult> = PublishSubject.create()
    private val requestPermissionsResultsSubject: PublishSubject<RequestPermissionResult> = PublishSubject.create()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultsSubject.onNext(ActivityResult(requestCode, resultCode, data))
    }

    /**
     * Emit a permission response event to all subscribers.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        requestPermissionsResultsSubject.onNext(RequestPermissionResult(requestCode, permissions, grantResults))
    }

    /**
     * Subscribe to permission request results of the given requestCode with the given callback.
     */
    @SuppressLint("CheckResult")
    fun addOnPermissionResultCallback(requestCode: Int, callback: RequestPermissionCallback) {
        requestPermissionsResults
            .filter { requestCode == it.requestCode }
            .mainThread(lifecycle())
            .subscribe({ (_, _, grantResults) ->
                if (grantResults.isEmpty() || grantResults.contains(PERMISSION_DENIED))
                    callback.onPermissionDenied(grantResults)
                else
                    callback.onPermissionGranted()
        }, ::log)
    }
}