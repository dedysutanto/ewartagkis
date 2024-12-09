package org.gkiserpong.ewartagkis

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.core.view.isVisible
import org.gkiserpong.ewartagkis.BuildConfig
//import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.android.synthetic.main.activity_second_new.*
import java.util.*

class SecondActivity : AppCompatActivity() {

    private var howManyClick = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N)
        {
            setContentView(R.layout.activity_second_new_lollipop)
        } else {
            setContentView(R.layout.activity_second_new)
        }

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val txtVersion = findViewById<TextView>(R.id.txt_version)
        val versionName = BuildConfig.VERSION_NAME
        txtVersion.text = getString(R.string.copyright, currentYear, versionName)

        ivGKI.setOnClickListener() {
            howManyClick += 1

            if ( howManyClick == 3 ) {

                val anim = RotateAnimation(
                    0.0f,
                    360.0f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
                )
                anim.setInterpolator(LinearInterpolator())
                anim.repeatCount = 3
                anim.duration = 700

                ivGKI.startAnimation((anim))

                howManyClick = 0

                if ( cvLiturgi.isVisible )
                {
                    cvLiturgi.visibility = View.GONE
                } else {
                    cvLiturgi.visibility = View.VISIBLE
                }
            }
        }


        cvEwarta.setOnClickListener() {
            //cvEwarta.setCardBackgroundColor(0x00FFFFF)
            val intent = Intent(this, EwartaActivity::class.java)
            startActivity(intent)
        }

        cvPelangi.setOnClickListener() {
            val intent = Intent(this, MajalahPelangiActivity::class.java)
            startActivity(intent)
        }

        cvIbadah.setOnClickListener() {
            val intent = Intent(this, JadwalIbadah1Activity::class.java)
            startActivity(intent)
        }

        cvPendeta.setOnClickListener() {
            val intent = Intent(this, JadwalPendeta1Activity::class.java)
            startActivity(intent)
        }

        cvHubungi.setOnClickListener() {
            val intent = Intent(this, HubungiKami1Activity::class.java)
            startActivity(intent)
        }

        cvLiturgi.setOnClickListener() {
            val intent = Intent(this, LiturgiActivity::class.java)
            startActivity(intent)
        }

        cvBKJ.setOnClickListener() {
            val intent = Intent(this, BukuKehidupanActivity::class.java)
            startActivity(intent)
        }

        cvKatekisasi.setOnClickListener() {
            val intent = Intent(this, KatekisasiActivity::class.java)
            startActivity(intent)
        }

        cvWebsite.setOnClickListener() {
            val intent = Intent(this, WebsiteActivity::class.java)
            startActivity(intent)
        }

        cvYoutube.setOnClickListener() {
            openYoutubeLink("UC8v6CsBT5NKk8yMPiTiUojg")
        }
    }

    fun openYoutubeLink(youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UC8v6CsBT5NKk8yMPiTiUojg"))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UC8v6CsBT5NKk8yMPiTiUojg"))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            this.startActivity(intentBrowser)
        }

    }

}


