package com.heremapp.presentation.extensions

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

/**
 * Wraps [ObservableField.addOnPropertyChangedCallback] with a cleaner interface.
 *
 * Why? For whatever reason, [Observable.OnPropertyChangedCallback] is an abstract class instead of an interface,
 * meaning you can't make use of lambda notation for [ObservableField.addOnPropertyChangedCallback].
 * This extension just implements it and exposes a lambda function so that you can.
 *
 * ## Usage
 *
 * Following code shows how to listen on observable field change callback using standard API:
 * ```
 *  observableField.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
 *      override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
 *          val newValue = observableField.get()
 *          // Do operation with new changed value
 *      }
 *  })
 * ```
 *
 *
 * This is how boilerplate code can be minimized using the extension function for doing same task:
 * ```
 * observableField.onChanged { newValue ->
 *    // Do operation with new changed value
 * }
 * ```
 */
inline fun <T> ObservableField<T>.onChanged(crossinline callback: (T?) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback.invoke(get())
        }
    })
}

/**
 * ObservableInt version of [onChanged]
 */
inline fun ObservableInt.onChanged(crossinline callback: (Int) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback.invoke(get())
        }
    })
}

/**
 * Converts one [ObservableField] into another. When the original field changes via [onChanged] listener,
 * the derived field is also updated by applying [derivation] function.
 *
 * Once new "ObservableField" is derived, you should save it and reuse where necessary.
 *
 * ```
 * // Here is an example, where we use "createObservableWithState" extension function to
 * // convert ObservableField<Int> to ObservableField<Boolean>
 *
 * val myNumber: ObservableField<Int> = ObservableField(0)
 *
 * // Now "myNumber" can be converted into new ObservableField<Boolean> based on the
 * // derivation lambda passed to it. Here we derived "isEvenNumber" and "isOddNumber"
 * // ObservableField<Boolean>, which automatically will change when "myNumber" value changes.
 *
 * val isEvenNumber = myNumber.createObservableWithState { it.rem(2) == 0 }
 * val isOddNumber  = myNumber.createObservableWithState { it.rem(2) == 1 }
 * ```

 * @param derivation Transformation function that converts source type to desired type.
 */
fun <T, R> ObservableField<T?>.createObservableWithState(derivation: (T?) -> R): ObservableField<R> {
    val newObservableField = ObservableField(derivation(this.get()))
    this.onChanged { newValue ->
        newObservableField.set(derivation(newValue))
    }
    return newObservableField
}

/**
 * Simpler to its observableField<T>.onChanged at the top of this file:
 *
 * Wraps [ObservableBoolean.addOnPropertyChangedCallback] with a cleaner interface.
 *
 * Why? For whatever reason, [Observable.OnPropertyChangedCallback] is an abstract class instead of an interface,
 * meaning you can't make use of lambda notation for [ObservableBoolean.addOnPropertyChangedCallback].
 * This extension just implements it and exposes a lambda function so that you can.
 *
 * ## Usage
 *
 * Following code shows how to listen on observable field change callback using standard API:
 * ```
 *  observableField.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
 *      override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
 *          val newValue = observableField.get()
 *          // Do operation with new changed value
 *      }
 *  })
 * ```
 *
 *
 * This is how boilerplate code can be minimized using the extension function for doing same task:
 * ```
 * observableBoolean.onChanged { newValue ->
 *    // Do operation with new changed value
 * }
 * ```
 */
inline fun ObservableBoolean.onChanged(crossinline callback: (Boolean) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback.invoke(get())
        }
    })
}