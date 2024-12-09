package org.gkiserpong.ewartagkis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.jakewharton.threetenabp.AndroidThreeTen
import com.krishna.fileloader.BuildConfig
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
//import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var PREV_SUNDAY: String = ""
    private var PREV_SUNDAY_STRING: String = ""
    private var isLatest: Boolean = false
    //private val eWARTA_URI = "https://ewarta.carisolusi.com/wp-content/ewarta"
    // Change in version 3.1
    private val eWARTA_URI = "https://ewarta.gkiserpong.org/wp-content/ewarta"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
        /*
        val txtVersion = findViewById<TextView>(R.id.txt_version)
        val versionName = BuildConfig.VERSION_NAME
        val versionCopyright = getString(R.string.copyright, versionName)
        txtVersion.text = "Version Name: $versionCopyright"
        */
        //Picasso.get().load("https://ewarta.gkiserpong.org/img/cj1.jpg").into(this.iv_naviheader)

        progress_bar.visibility = View.VISIBLE
        fab_next.hide()
        fab_prev.hide()

        if (!isLatest) {
            getLatesteWarta()
        }


        fab_next.setOnClickListener {
            getLatesteWarta()
        }

        fab_prev.setOnClickListener {
            getPreveWarta()
        }


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /*
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        */
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun getNextSundayDate(): Calendar
    {
        val calendarForNextSunday = Calendar.getInstance()
        val today = calendarForNextSunday.get(Calendar.DAY_OF_WEEK)

        if (today != Calendar.SUNDAY) {
            val offset = Calendar.SATURDAY - today + Calendar.SUNDAY
            calendarForNextSunday.add(Calendar.DATE, offset)
        }
        return calendarForNextSunday
    }

    private fun getLatesteWarta() {

        val PREF_NAME = "eWartaSetting"
        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Get Previous Sunday
/*
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val date_format = SimpleDateFormat("yyyyMMdd")
        val date_format_title = SimpleDateFormat("dd LLLL yyyy")
        val PREV_SUNDAY: String = date_format.format(calendar.getTime())
        val PREV_SUNDAY_STRING: String = date_format_title.format(calendar.getTime())

        // Get Next Sunday
        calendar.add(Calendar.DATE, 7)

        val NEXT_SUNDAY: String = date_format.format(calendar.getTime())
        val NEXT_SUNDAY_STRING: String = date_format_title.format(calendar.getTime())
*/

        // Test new function - Start
        val date_format = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        val date_format_title = SimpleDateFormat("dd LLLL yyyy", Locale.ENGLISH)

        val cal = getNextSundayDate()

        val NEXT_SUNDAY: String = date_format.format(cal.getTime())
        val NEXT_SUNDAY_STRING: String = date_format_title.format(cal.getTime())

        cal.add(Calendar.DATE, -7)

        val PREV_SUNDAY: String = date_format.format(cal.getTime())
        val PREV_SUNDAY_STRING: String = date_format_title.format(cal.getTime())
        // Test new function - End


        val eWARTA_PREV = eWARTA_URI + "/eWarta-" + PREV_SUNDAY + ".pdf"
        val eWARTA_NEXT = eWARTA_URI + "/eWarta-" + NEXT_SUNDAY + ".pdf"

        FileLoader.with(this)
            .load(eWARTA_NEXT)
            .fromDirectory("eWarta", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    progress_bar.visibility = View.GONE
                    val pdfFile = p1!!.body

                    val actionBar = supportActionBar
                    actionBar!!.title = "eWarta - " + NEXT_SUNDAY_STRING

                    val editor = sharedPref.edit()

                    editor.putString("lastSunday", NEXT_SUNDAY)
                    editor.putString("lastSundayString", NEXT_SUNDAY_STRING)
                    editor.putString("prevSunday", PREV_SUNDAY)
                    editor.putString("prevSundayString", PREV_SUNDAY_STRING)
                    editor.apply()

                    eWartaV.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onPageChange { page, pageCount ->  }
                        .onPageError { page, t ->
                            Toast.makeText(this@MainActivity, "Error while opening page " + page, Toast.LENGTH_SHORT).show()
                            Log.d("ERROR", " " + t.localizedMessage)
                        }
                        .pageFitPolicy(FitPolicy.WIDTH)
                        //.onRender { nbPages, pageWidth, pageHeight ->
                        //    eWartaV.fitToWidth()
                        //}
                        .enableAnnotationRendering(true)
                        .pageFling(true)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .load()
                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    progress_bar.visibility = View.GONE

                    FileLoader.with(this@MainActivity)
                        .load(eWARTA_PREV)
                        .fromDirectory("eWarta", FileLoader.DIR_INTERNAL)
                        .asFile(object : FileRequestListener<File> {
                            override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                                progress_bar.visibility = View.GONE
                                val pdfFile = p1!!.body

                                val actionBar = supportActionBar
                                actionBar!!.title = "eWarta - " + PREV_SUNDAY_STRING

                                val editor = sharedPref.edit()
                                editor.clear()
                                editor.putString("lastSunday", PREV_SUNDAY)
                                editor.putString("lastSundayString", PREV_SUNDAY_STRING)
                                editor.apply()


                                eWartaV.fromFile(pdfFile)
                                    .password(null)
                                    .defaultPage(0)
                                    .enableSwipe(true)
                                    .swipeHorizontal(true)
                                    .enableDoubletap(true)
                                    //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                                    //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                                    //.onPageChange { page, pageCount ->  }
                                    .onPageError { page, t ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error while opening page " + page,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("ERROR", " " + t.localizedMessage)
                                    }
                                    .pageFitPolicy(FitPolicy.WIDTH)
                                    //.onRender { nbPages, pageWidth, pageHeight ->
                                    //    eWartaV.fitToWidth()
                                    //}
                                    .enableAnnotationRendering(true)
                                    .pageFling(true)
                                    .pageSnap(true)
                                    .autoSpacing(true)
                                    .load()

                            }

                            override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                                Toast.makeText(this@MainActivity, "Sorry eWARTA_PREV: " + p1!!.message, Toast.LENGTH_SHORT).show()
                                progress_bar.visibility = View.GONE
                            }
                        })

                }

            })

        fab_next.hide()
        fab_prev.show()
        isLatest = true

    }

    private fun getPreveWarta() {

        val PREF_NAME = "eWartaSetting"
        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val lastSunday = sharedPref.getString("lastSunday", "")
        //val lastSundayString = sharedPref.getString("lastSundayString", "")
        val prevSunday = sharedPref.getString("prevSunday", "")
        val prevSundayString = sharedPref.getString("prevSundayString", "")

        progress_bar.visibility = View.VISIBLE

        if (!sharedPref.contains("prevSunday")) {

            val date_format = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
            val date_format_title = SimpleDateFormat("dd LLLL yyyy", Locale.ENGLISH)

            val date = date_format.parse(lastSunday.toString())!!

            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.add(Calendar.DATE, -7)
            //calendar.add(Calendar.DATE, -6)

            // This if to fix bug, if today is SUNDAY
            // PREV SUNDAY won't work
            //if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            //    calendar.add(Calendar.DATE, -6)

            /*
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            */
            //val date_format = SimpleDateFormat("yyyyMMdd")
            PREV_SUNDAY = date_format.format(calendar.getTime())
            PREV_SUNDAY_STRING = date_format_title.format(calendar.getTime())
        } else {
            PREV_SUNDAY = prevSunday.toString()
            PREV_SUNDAY_STRING = prevSundayString.toString()
        }
        val eWARTA_URI = "https://ewarta.carisolusi.com/wp-content/ewarta"
        //val eWARTA

        val eWARTA_PREV = eWARTA_URI + "/eWarta-" + PREV_SUNDAY + ".pdf"

        FileLoader.with(this)
            .load(eWARTA_PREV)
            .fromDirectory("eWarta", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    progress_bar.visibility = View.GONE
                    val pdfFile = p1!!.body

                    val actionBar = supportActionBar
                    actionBar!!.title = "eWarta - " + PREV_SUNDAY_STRING

                    eWartaV.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onPageChange { page, pageCount ->  }
                        .onPageError { page, t ->
                            Toast.makeText(this@MainActivity, "Error while opening page " + page, Toast.LENGTH_SHORT).show()
                            Log.d("ERROR", " " + t.localizedMessage)
                        }
                        .pageFitPolicy(FitPolicy.WIDTH)
                        //.onRender { nbPages, pageWidth, pageHeight ->
                        //    eWartaV.fitToWidth()
                        //}
                        .enableAnnotationRendering(true)
                        .pageFling(true)
                        .pageSnap(true)
                        .autoSpacing(true)
                        .load()
                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    Toast.makeText(this@MainActivity, "Sorry eWARTA_PREV: " + p1!!.message, Toast.LENGTH_SHORT).show()
                    progress_bar.visibility = View.GONE
                }
            })
        fab_next.show()
        fab_prev.hide()
        isLatest = false
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            //R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {

            R.id.nav_jadwal_ibadah -> {
                val intent = Intent(this, JadwalIbadah1Activity::class.java)
                //intent.putExtra("keyIdentifier", value)
                startActivity(intent)
            }

            R.id.nav_jadwal_pendeta -> {
                val intent = Intent(this, JadwalPendeta1Activity::class.java)
                startActivity(intent)
            }

            R.id.nav_hubungi_kami -> {

                val intent = Intent(this, HubungiKami1Activity::class.java)
                startActivity(intent)
            }

            R.id.nav_majalah_pelangi -> {

                val intent = Intent(this, MajalahPelangiActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_ewarta -> {

                val intent = Intent(this, EwartaActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_second -> {

                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }

            /*
            R.id.nav_event_tahunan -> {

                val intent = Intent(this, EventTahunanActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            */
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
