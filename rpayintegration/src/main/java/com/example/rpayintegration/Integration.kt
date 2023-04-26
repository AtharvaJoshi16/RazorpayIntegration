package com.example.rpayintegration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DisplayContext
import android.util.Patterns
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

/*
    validateData() - validates all the payment data provided by the user.
     makePayment() - initiates a razorpay payment intent only when all the data is validated successfully,
                       else returns a DataValidation object with errors
*/
class Integration() : PaymentResultListener {
    private var dataValidated: Boolean = false
    fun makePayment(activity: Activity, name: String, amount:String, description: String,
                            contact: String, email:String, rpay_test_id : String, currency: String) {
        if(dataValidated){
            val amt = (amount.toFloat() * 100).roundToInt().toString()
            val checkout = Checkout()
            checkout.setKeyID(rpay_test_id)
            val obj = JSONObject()
            try {
                obj.put("name",name)
                obj.put("description",description)
                obj.put("amount",amt)
                obj.put("email",email)
                obj.put("contact",contact)
                obj.put("currency",currency)
                checkout.open(activity,obj)
            } catch (e: JSONException){
                println("ERROR:  "+ e.message)
                e.printStackTrace()
            }
        }
    }

    fun validateData(name: String,amount: String,contact: String,
            email: String,rpay_test_id: String) : Any {
        val emailPattern = Patterns.EMAIL_ADDRESS
        if(name == "") {
            return DataValidation("name","Name cannot be empty")
        }
        if(name.contains("[0-9!\\\"#\$%&'()*+,-./:;\\\\\\\\<=>?@\\\\[\\\\]^_`{|}~]".toRegex())) {
            return DataValidation("name","Digits or Special characters not allowed in Name")
        }
        if(amount== "") {
            return DataValidation("amount","Amount cannot be empty")
        }
        if(amount.contains("[A-Za-z!\\\"#\$%&'()*+,-./:;\\\\\\\\<=>?@\\\\[\\\\]^_`{|}~]".toRegex())){
            return DataValidation("amount","Amount should be a number")
        }
        if(contact == "") {
            return DataValidation("contact","Contact Details cannot be empty")
        }
        if(contact.length != 10){
            return DataValidation("contact","Invalid contact number length")
        }
        if(contact.contains("[A-Za-z!\\\"#\$%&'()*+,-./:;\\\\\\\\<=>?@\\\\[\\\\]^_`{|}~]".toRegex())){
            return DataValidation("contact","Contact Details should only contain numbers")
        }
        if(email == "") {
            return DataValidation("email","Email cannot be empty")
        }
        if(!emailPattern.matcher(email).matches()) {
            return DataValidation("email","Invalid Email")
        }
        if(rpay_test_id == "") {
            return DataValidation("rpay_test_id","Razorpay test ID cannot be empty")
        }
        this.dataValidated = true
        return true
    }

    override fun onPaymentSuccess(p0: String?) {
    }

    override fun onPaymentError(p0: Int, p1: String?) {
    }

}