package com.heremapp.ui.uielements.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.heremapp.presentation.base.BaseToolbarViewModel

/**
 * Application base toolbar that has a viewModel.
 */
class BaseToolbarView(context: Context?, attrs: AttributeSet?) : Toolbar(context, attrs) {

    private lateinit var viewModel: BaseToolbarViewModel

    fun setViewModel(viewModel: BaseToolbarViewModel) {
        this.viewModel = viewModel
        setNavigationOnClickListener { viewModel.onNavigationItemClicked() }
    }
}