package com.heremapp.presentation.extensions

import androidx.databinding.Observable
import androidx.databinding.ObservableField

/**
 * An [ObservableField] that enforces non-null values (both initial and subsequent values
 * set via [ObservableField.set]). For cases where null values are expected, use the original
 * [ObservableField] class.
 *
 * This class is now necessary because Android DataBinding library's [ObservableField.get] has been
 * annotated with [android.support.annotation.Nullable]. This makes things inconvenient for code
 * that only uses non-null values for [ObservableField]s:
 *
 * ```
 * val observableField = ObservableField("")    // non-null initial value
 *
 * observableField.onChanged { value: String?   // because [ObservableField.get] is now nullable.
 *      if (value?.isEmpty() == true) {         // all calls to [value] must be null-safe calls (?.)
 *          // do something
 *      }
 * }
 * ```
 */
class NonNullObservableField<T : Any>(
        value: T
) : ObservableField<T>(value) {

    override fun get(): T = super.get()!!

    @Suppress("RedundantOverride") // Only allow non-null `value`.
    override fun set(value: T) = super.set(value)
}

/**
 * Non null version of [onChanged]
 */
inline fun <T : Any> NonNullObservableField<T>.onChanged(crossinline callback: (T) -> Unit) {
    addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback.invoke(get())
        }
    })
}

/**
 * Non null version of [createObservableWithState]
 */
fun <T : Any, R : Any> NonNullObservableField<T>.createObservableWithState(derivation: (T) -> R): NonNullObservableField<R> {
    val newObservableField = NonNullObservableField(derivation(this.get()))
    this.onChanged { newValue ->
        newObservableField.set(derivation(newValue))
    }
    return newObservableField
}