package com.heremapp.presentation.permissions

/**
 * Implementor should be able to check for permissions and request them.
 */
interface PermissionsProvider {

    /**
     * Request all necessary permissions that are not granted already.
     */
    fun requestPermissions(grantResults: IntArray? = null)

    /**
     * Check if necessary permissions are granted.
     */
    fun arePermissionsGranted(): Boolean
}