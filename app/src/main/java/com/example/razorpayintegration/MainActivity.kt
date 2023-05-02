package com.example.razorpayintegration

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.razorpayintegration.ui.theme.RazorpayIntegrationTheme
import com.example.rpayintegration.DataValidation
import com.example.rpayintegration.Integration
import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(),PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RazorpayIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PaymentUI()
                }
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        this.startActivity(Intent(this,PaymentSuccess::class.java).putExtra("tid",p0))
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        this.startActivity(Intent(this,PaymentFailure::class.java).putExtra("err",p0))
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentUI() {
    CheckInternetService()
    val ctx = LocalContext.current
    val activity = ctx as Activity
    var name by remember {
        mutableStateOf(TextFieldValue())
    }
    var description by remember {
        mutableStateOf(TextFieldValue())
    }
    var amount by remember {
        mutableStateOf(TextFieldValue())
    }

    var contact by remember {
        mutableStateOf(TextFieldValue())
    }

    var email by remember {
        mutableStateOf(TextFieldValue())
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Razorpay Integration")})
    }) {
        Box(modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(value = name, onValueChange = {name = it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter name of the payee")}, singleLine = true
                )
                OutlinedTextField(value = description, onValueChange = {description = it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter description")}, singleLine = true
                )
                OutlinedTextField(value = amount, onValueChange = {amount=it}, keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ) , modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter the amount")}, singleLine = true
                )
                OutlinedTextField(value = contact, onValueChange = {contact = it}, keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ) ,modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter contact of the recipient")}, singleLine = true
                )
                OutlinedTextField(value = email, onValueChange = {email = it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter email of the recipient")}, singleLine = true
                )
                Button(onClick = {
                        pay(ctx,activity,name.text,amount.text,description.text,contact.text,email.text)
                }, modifier = Modifier.padding(10.dp)) {
                    Text(text = "PROCEED TO PAY")
                }

            }
        }
    }
}


fun pay(context: Context ,activity: Activity,name:String,amount: String,description: String,contact:String,email:String) {
    val i = Integration()
    val validation : Any = i.validateData(name,amount,contact,email, "rzp_test_Opj3ByZgovZmDV")
    if(validation is Boolean) {
        println("Validated...")
        i.makePayment(activity,name,amount,description,contact,email,"rzp_test_Opj3ByZgovZmDV","INR")
    } else if(validation is DataValidation) {
        println("Validation Failed")
        Toast.makeText(context,"${validation.fieldName} : ${validation.error}",Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CheckInternetService() {
    val ctx = LocalContext.current
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val networkCallback  = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Toast.makeText(ctx,"Connected", Toast.LENGTH_SHORT).show()
            super.onAvailable(network)
        }

        override fun onLost(network: Network) {
            Toast.makeText(ctx,"Check your internet connection", Toast.LENGTH_SHORT).show()
            super.onLost(network)
        }
    }
    val connectivityManager = ctx.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    connectivityManager.requestNetwork(networkRequest, networkCallback)
}
