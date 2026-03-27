package com.example.habitflow

import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.alluresupport.addAllureSupport

open class BaseAllureTestCase: TestCase(
    kaspressoBuilder = Kaspresso.Builder.simple().apply{
        addAllureSupport()
    }
) {

}

