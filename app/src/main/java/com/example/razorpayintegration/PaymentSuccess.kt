package com.example.razorpayintegration

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.razorpayintegration.ui.theme.RazorpayIntegrationTheme

class PaymentSuccess : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RazorpayIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val tid : String? = intent.getStringExtra("tid")
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ){
                        Image(painter = painterResource(id = R.drawable.check), modifier = Modifier.padding(10.dp), contentDescription = "")
                        Text(text = "Payment Successful", fontSize = 25.sp, modifier = Modifier.padding(10.dp), fontWeight = FontWeight.W700)
                        Text(text = "Transaction ID: $tid", modifier = Modifier.padding(10.dp))
                    }
                }
            }
        }
    }
}
