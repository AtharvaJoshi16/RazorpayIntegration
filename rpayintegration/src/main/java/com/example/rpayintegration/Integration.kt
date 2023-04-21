package com.example.rpayintegration

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.DisplayContext
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

class Integration(private val context: Context) : PaymentResultListener {
    private val paymentCallback : PaymentCallback = context as PaymentCallback

    public interface PaymentCallback {
        fun paymentSuccess(tid: String?)
        fun paymentFailure(errorCode: String?)
    }

    fun makePayment(activity: Activity, name: String, amount:String, description: String,
                            contact: String, email:String, rpay_test_id : String, currency: String) {
       // rounding off the amount.
        val amt = (amount.toFloat() * 100).roundToInt().toString()
        val checkout = Checkout()
        checkout.setKeyID(rpay_test_id)
        //checkout.setImage(R.drawable.payment)
        val obj = JSONObject()
        try {
            obj.put("name",name)
            obj.put("description",description)
            obj.put("amount",amt)
            obj.put("email",email)
            //obj.put("order_id", "order_DBJOWzybf0sJbb");
            obj.put("contact",contact)
            obj.put("currency",currency)
            checkout.open(activity,obj)
        } catch (e: JSONException){
            println("ERROR:  "+ e.message)
            e.printStackTrace()
        }
    }


    override fun onPaymentSuccess(p0: String?) {
        paymentCallback.paymentSuccess(p0)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        paymentCallback.paymentFailure(p1)
    }
}