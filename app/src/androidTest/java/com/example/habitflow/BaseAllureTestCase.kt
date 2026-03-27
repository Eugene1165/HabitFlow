package com.example.habitflow

import androidx.test.rule.GrantPermissionRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.alluresupport.addAllureSupport
import org.junit.Rule

open class BaseAllureTestCase: TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple().apply{
        addAllureSupport()
    }
) {
    //Это JUnit Rule, который выдаёт разрешение до запуска теста, без показа диалога:
    @get:Rule
    val notificationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

}

