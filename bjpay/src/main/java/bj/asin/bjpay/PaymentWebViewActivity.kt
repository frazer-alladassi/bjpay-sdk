package bj.asin.bjpay


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class PaymentWebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        val url = buildUrlFromIntent(intent)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidInterface")
        webView.loadUrl(url)
    }

    private fun buildUrlFromIntent(intent: Intent): String {
        val base = "http://10.71.2.60:8088"
        val params = listOf(
            "totalamount=${intent.getIntExtra("totalamount", 0)}",
            "currency=${intent.getStringExtra("currency")}",
            "description=${intent.getStringExtra("description")}",
            "callbackurl=${intent.getStringExtra("callbackurl")}",
            "apikey=${intent.getStringExtra("apikey")}",
            "customdata=${intent.getStringExtra("customdata")}",
            "partnerid=${intent.getStringExtra("partnerid")}",
        ).joinToString("&")
        return "$base/?$params"
    }

    inner class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun onPaymentSuccess(transactionId: String) {
            sendResult("SUCCESS", transactionId)
        }

        @JavascriptInterface
        fun onPaymentFailure(transactionId: String) {
            sendResult("FAILED", transactionId)
        }

        @JavascriptInterface
        fun closeWebView() {
            if (context is Activity) {
                context.runOnUiThread {
                    context.finish()
                }
            }
        }
    }

    private fun sendResult(status: String, transactionId: String?) {
        val intent = Intent().apply {
            putExtra("status", status)
            putExtra("transactionId", transactionId)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}