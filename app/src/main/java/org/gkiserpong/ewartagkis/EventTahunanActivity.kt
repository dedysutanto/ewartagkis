package org.gkiserpong.ewartagkis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EventTahunanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_tahunan)
        val actionBar = supportActionBar
        //actionBar!!.title = "Event Tahunan"
        actionBar!!.title = resources.getString(R.string.title_activity_event_tahunan)

        actionBar.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
