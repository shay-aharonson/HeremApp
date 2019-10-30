package com.heremapp.ui.base

/**
 * Result from Activity#onRequestPermissionResult()
 */
@Suppress("ArrayInDataClass")
data class RequestPermissionResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray)