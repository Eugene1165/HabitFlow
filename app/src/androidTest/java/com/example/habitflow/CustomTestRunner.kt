package com.example.habitflow

import android.app.Application
import android.content.Context import android.os.Bundle
import com.kaspersky.kaspresso.runner.KaspressoRunner
import dagger.hilt.android.testing.HiltTestApplication
import io.qameta.allure.android.AllureAndroidLifecycle
import io.qameta.allure.kotlin.Allure

class CustomTestRunner: KaspressoRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)
        Allure.lifecycle = AllureAndroidLifecycle()
    }

    override fun onDestroy() {
        AllureCopyListener().testRunFinished(null)
        super.onDestroy()
    }
}
