package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_katekisasi.*
import org.gkiserpong.ewartagkis.adapter.JadwalIbadahAdapter
import org.gkiserpong.ewartagkis.adapter.JsonListAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class KatekisasiActivity : AppCompatActivity() {

    //private var arrayList_details = ArrayList<HashMap<String, String>>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_katekisasi)

        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.title_activity_katekisasi)
        actionBar.setDisplayHomeAsUpEnabled(true)

        //loader.visibility = View.VISIBLE

        val URIAddress = "https://ewarta.gkiserpong.org/wp-content/katekisasi/index.html"

        val mWebView = findViewById<WebView>(R.id.webview);
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(URIAddress)
                return true
            }
        }
        mWebView.loadUrl(URIAddress)
        //mWebView.webViewClient = HelloWebViewClient()
        //mWebView.setWebContentsDebuggingEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
