package com.durov.ratebar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.durov.composeratebar.RateBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rate = remember { mutableStateOf(0f) }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RateBar(
                    iconSelected = R.drawable.ic_icon_selected,
                    iconUnSelected = R.drawable.ic_icon_unselected,
                    onRateChanged = {
                        rate.value = it
                    }
                )
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Current rete: ${rate.value}"
                )
            }
        }
    }
}
