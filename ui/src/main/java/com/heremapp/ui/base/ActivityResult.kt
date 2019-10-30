package com.heremapp.ui.base

import android.content.Intent

/**
 * Result from onActivityResult
 */
data class ActivityResult(val requestCode: Int, val resultCode: Int, val data: Intent?)