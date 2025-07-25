package bj.asin.bjpay

import android.app.Activity
import android.content.Intent
import com.google.gson.Gson

object BjPay {

    private var listener: ((Status, String?) -> Unit)? = null

    enum class Status {
        SUCCESS, FAILED
    }

    @JvmStatic
    fun get(): BjPay = this

    @JvmStatic
    fun setListener(callback: (Status, String?) -> Unit) {
        this.listener = callback
    }

    @JvmStatic
    @JvmOverloads
    fun requestPayment(
        context: Activity,
        totalAmount: Int,
        apikey: String,
        callbackUrl: String,
        currency: String = "XOF",
        description: String = "",
        customData: Map<String, String>? = null,
        partnerId: String = ""
    ) {
        val intent = Intent(context, PaymentWebViewActivity::class.java).apply {
            putExtra("totalamount", totalAmount)
            putExtra("currency", currency)
            putExtra("description", description)
            putExtra("callbackurl", callbackUrl)
            putExtra("apikey", apikey)
            putExtra("customdata", Gson().toJson(customData))
            putExtra("partnerid", partnerId)
        }
        context.startActivityForResult(intent, 1001)
    }

    @JvmStatic
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            val status = data?.getStringExtra("status")
            val transactionId = data?.getStringExtra("transactionId")
            when (status) {
                "SUCCESS" -> listener?.invoke(Status.SUCCESS, transactionId)
                "FAILED" -> listener?.invoke(Status.FAILED, transactionId)
            }
        }
    }
}