package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HubungiKami1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hubungikami1)
        val actionBar = supportActionBar
        actionBar!!.title = "Hubungi Kami"
    }
}
