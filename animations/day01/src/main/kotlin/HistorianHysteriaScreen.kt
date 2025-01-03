package day01

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text

@Composable
fun HistorianHysteriaScreen(vm: HistorianHysteriaVM) {
    Column {
        for (s in vm.getList()){
            Text(s)
        }
    }
}