@file:JvmName("Main")

package day01

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.jakewharton.mosaic.runMosaicBlocking

fun main() {
    runMosaicBlocking {
        val coroutineScope = rememberCoroutineScope()
        val vM = remember { HistorianHysteriaVM(coroutineScope) }
        HistorianHysteriaScreen(vM)
    }
}
