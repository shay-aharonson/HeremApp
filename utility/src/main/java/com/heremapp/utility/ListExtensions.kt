package com.heremapp.utility

/**
 * [MutableList] extension that applies a given predicate on every item in the list, if the result is true that item is added to
 * a filtered list, otherwise a non filtered list. After all items are parsed the method returns updates the list to
 * contain only the non filtered items, the function also returns a list of the filtered items.
 *
 * @return a list of filtered items.
 */
fun <T> MutableList<T>.splitFiltered(predicate: (T) -> Boolean): List<T> {
    val filtered = mutableListOf<T>()
    val nonFiltered = mutableListOf<T>()

    this.forEach { item ->
        if (predicate(item))
            filtered.add(item)
        else
            nonFiltered.add(item)
    }

    this.clear()
    this.addAll(nonFiltered)
    return filtered
}