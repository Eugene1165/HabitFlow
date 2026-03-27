package com.example.habitflow

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.Result
import org.junit.runner.notification.RunListener

class AllureCopyListener: RunListener() {
    override fun testRunFinished(result: Result?) {
        super.testRunFinished(result)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val src = context.filesDir.resolve("allure-results")
        val dst = context.getExternalFilesDir(null)?.resolve("allure-results")
        if(src.exists() && dst != null) src.copyRecursively(dst, overwrite = true)
    }
}
