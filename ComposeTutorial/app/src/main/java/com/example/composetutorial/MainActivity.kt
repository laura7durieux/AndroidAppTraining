package com.example.composetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutorialTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TutorialView()
                }
            }
        }
    }
}



@Composable
fun TutorialView(modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Top,
        modifier = modifier) {
        val image = painterResource(R.drawable.bg_compose_background)
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.tutoTitle),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = stringResource(R.string.para1),
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(start = 16.dp, end=16.dp)
        )

        Text(
            text = stringResource(R.string.para2),
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(start = 16.dp, end=16.dp)
        )

    }
}

@Preview(name = "My preview", showSystemUi = true, showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeTutorialTheme {
        TutorialView()
    }
}