package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class JadwalPendeta1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal_pendeta1)
        val actionBar = supportActionBar
        actionBar!!.title = "Jadwal Pendeta"
    }
}
