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
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.razorpayintegration.ui.theme.RazorpayIntegrationTheme
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

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
        val intent = Intent(this,PaymentSuccess::class.java)
        intent.putExtra("tid",p0)
        this.startActivity(intent)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        val intent = Intent(this,PaymentFailure::class.java)
        this.startActivity(intent)
//        if (p1 != null) {
//            Log.d("ERROR",p1)
//        }
//        Toast.makeText(this,"Payment failed: $p1",Toast.LENGTH_LONG).show()
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
                OutlinedTextField(value = amount, onValueChange = {amount=it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter the amount")}, singleLine = true
                )
                OutlinedTextField(value = contact, onValueChange = {contact = it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter contact of the recipient")}, singleLine = true
                )
                OutlinedTextField(value = email, onValueChange = {email = it}, modifier = Modifier.padding(10.dp),
                    placeholder = { Text(text = "Enter email of the recipient")}, singleLine = true
                )
                Button(onClick = { if(validateData(ctx,name.text,amount.text,contact.text,email.text)){
                        pay(activity,name.text,amount.text,description.text,contact.text,email.text)
                }
                }, modifier = Modifier.padding(10.dp)) {
                    Text(text = "PROCEED TO PAY")
                }

            }
        }
    }
}

fun validateData(context : Context,name : String,amount: String,contact: String,email: String) : Boolean {
    val emailPattern = Patterns.EMAIL_ADDRESS
    val contactPattern = Patterns.PHONE
    if(name==""){
        Toast.makeText(context,"Enter name",Toast.LENGTH_SHORT).show()
        return false
    }
    if(emailPattern.matcher(email).matches() && contactPattern.matcher(contact).matches() && amount.toIntOrNull()!=null) {
        return true
    }
    if(!emailPattern.matcher(email).matches()) {
        Toast.makeText(context,"Invalid Email",Toast.LENGTH_SHORT).show()
        return false
    }
    if(!contactPattern.matcher(contact).matches()) {
        Toast.makeText(context,"Invalid Contact",Toast.LENGTH_SHORT).show()
        return false
    }
    if(amount.toIntOrNull() == null) {
        Toast.makeText(context,"Invalid Amount Entered",Toast.LENGTH_SHORT).show()
        return false
    }
    return false
}

fun pay(activity: Activity,name:String,amount: String,description: String,contact:String,email:String) {

    // rounding off the amount.
    val amt = (amount.toFloat() * 100).roundToInt()
    val checkout = Checkout()
    checkout.setKeyID("rzp_test_Opj3ByZgovZmDV")
    checkout.setImage(R.drawable.payment)
    val obj = JSONObject()
    try {
        obj.put("name",name)
        obj.put("description",description)
        obj.put("amount",amt)
        obj.put("email",email)
//        obj.put("order_id","order_HGBasGHF0sbb")
        obj.put("contact",contact)
        obj.put("currency","INR")
        checkout.open(activity,obj)
    } catch (e: JSONException){
        e.printStackTrace()
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
