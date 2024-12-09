package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class JadwalIbadah1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal_ibadah1)
        val actionBar = supportActionBar
        actionBar!!.title = "Jadwal Ibadah"
    }
}
