package com.heremapp.presentation.permissions

/**
 * Callback for when a permission is granted/denied by the [RequestPermissionsViewModel].
 */
interface RequestPermissionCallback {
    /**
     * Callback from [RequestPermissionsViewModel] when the permission is granted.
     */
    fun onPermissionGranted(resultCode: Int? = null)

    /**
     * Callback from [RequestPermissionsViewModel] when the permission is not granted.
     */
    fun onPermissionDenied(grantResults: IntArray, resultCode: Int? = null)
}