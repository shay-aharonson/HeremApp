package com.heremapp.ui.uielements.listeners

/**
 * Allows UI components to request dismissal of the bottom sheet and provides access to the bottom sheet gesture detectors.
 */
interface BottomSheetListener {

    /**
     * Detects swipe gestures within the bottom sheet to expand or minimize.
     */
    val swipeDetector: OnSwipeGestureDetector

    /**
     * Detects swipe from bottom gesture in wrapping layout to display the bottom sheet.
     */
    val bottomSwipeDetector: OnSwipeFromBottomGestureDetector

    /**
     * Request to dismiss the bottom sheet.
     */
    fun requestDismiss(): Boolean

    /**
     * Request to show the bottom sheet.
     */
    fun requestDisplay(): Boolean
}