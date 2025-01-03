package day01

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob

class HistorianHysteriaVM(
    parentCoroutineScope: CoroutineScope,
) {
    private val coroutineScope = CoroutineScope(SupervisorJob(parentCoroutineScope.coroutineContext[Job]))
    
}