package com.abdallah_abdelazim.loadapp.widget.loading_button

sealed interface ButtonState {

    object Idle : ButtonState

    object Clicked : ButtonState

    object Completed : ButtonState
}