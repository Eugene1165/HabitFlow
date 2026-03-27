package com.example.habitflow

import android.app.Application
import android.content.Context
import com.kaspersky.kaspresso.runner.KaspressoRunner
import dagger.hilt.android.testing.HiltTestApplication

class CustomTestRunner: KaspressoRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
