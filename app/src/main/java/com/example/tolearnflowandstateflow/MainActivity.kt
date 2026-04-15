package com.example.tolearnflowandstateflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tolearnflowandstateflow.flow.SimpleFlow
import com.example.tolearnflowandstateflow.ui.theme.ToLearnFlowAndStateFlowTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToLearnFlowAndStateFlowTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColdFlowTestScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ColdFlowTestScreen(modifier: Modifier = Modifier) {
    val simpleFlow = remember { SimpleFlow() }
    val scope = rememberCoroutineScope()
    val logs = remember { mutableStateListOf<String>() }
    var runId by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Cold Flow Test UI")
        Text(text = "Click multiple times. Each run restarts from 2, 4, 6.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                runId += 1
                val currentRun = runId
                scope.launch {
                    logs.add("Run #$currentRun start")
                    simpleFlow.run {
                        demoIntFlow()
                            .map { it * 2 }
                            .collect { value ->
                                logs.add("Run #$currentRun -> $value")
                            }
                    }
                    logs.add("Run #$currentRun done")
                }
            }) {
                Text("Start collect")
            }

            OutlinedButton(onClick = { logs.clear() }) {
                Text("Clear")
            }
        }

        HorizontalDivider()
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(logs) { line ->
                Text(text = line)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColdFlowTestPreview() {
    ToLearnFlowAndStateFlowTheme {
        ColdFlowTestScreen()
    }
}