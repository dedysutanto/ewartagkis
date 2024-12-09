package org.gkiserpong.ewartagkis

import android.Manifest
//import android.content.Context
import android.content.Intent
//import android.content.SharedPreferences
//import android.icu.text.SimpleDateFormat
import java.text.SimpleDateFormat
//import android.icu.util.Calendar
import android.os.Build
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
import android.widget.Toast
import com.github.barteksc.pdfviewer.util.FitPolicy
//import com.karumi.dexter.Dexter
//import com.karumi.dexter.MultiplePermissionsReport
//import com.karumi.dexter.PermissionToken
//import com.karumi.dexter.listener.PermissionRequest
//import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //private val PREF_NAME = "eWarta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_main)
        Toast.makeText(this@MainActivity, "Saya Buka Dulu ini", Toast.LENGTH_SHORT)

        //val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : BaseMultiplePermissionsListener() {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    super.onPermissionsChecked(report)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    super.onPermissionRationaleShouldBeShown(permissions, token)
                }
            })

        progress_bar.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
            var answer: String = current.format(formatter)
            Log.d("answer", answer)
        } else {
            var date = Date();
            val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
            val answer: String = formatter.format(date)
            Log.d("answer", answer)
        }

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    val today = Calendar.getInstance()
        //    val YEAR = today.get(Calendar.YEAR)
        //    val MONTH = today.get(Calendar.MONTH)
        //} else {
        //var date = Date()
        //val month_format = SimpleDateFormat("MM")
        //val year_format = SimpleDateFormat("yyyy")
        //val YEAR: String = year_format.format(date)
        //val MONTH: String = month_format.format(date)

        // Get Previous Sunday
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val date_format = SimpleDateFormat("yyyyMMdd")
        val PREV_SUNDAY: String = date_format.format(calendar.getTime())
        val date_format_title = SimpleDateFormat("dd LLLL yyyy")
        val PREV_SUNDAY_STRING: String = date_format_title.format(calendar.getTime())
        //val PREV_YEAR = calendar.get(Calendar.YEAR)
        //val PREV_MONTH = calendar.get(Calendar.MONTH)
        // Get Next Sunday
        calendar.add(Calendar.DATE, 7)
        val NEXT_SUNDAY: String = date_format.format(calendar.getTime())
        val NEXT_SUNDAY_STRING: String = date_format_title.format(calendar.getTime())
        //val NEXT_YEAR = calendar.get(Calendar.YEAR)
        //val NEXT_MONTH = calendar.get(Calendar.MONTH)

        //}
        //val today = LocalDate()
        val eWARTA_URI = "https://ewarta.carisolusi.com/wp-content/ewarta"
        //val eWARTA

        val eWARTA_PREV = eWARTA_URI + "/eWarta-" + PREV_SUNDAY + ".pdf"
        val eWARTA_NEXT = eWARTA_URI + "/eWarta-" + NEXT_SUNDAY + ".pdf"
        //FileLoader.deleteWith(this).fromDirectory("eWarta", FileLoader.DIR_INTERNAL).deleteAllFiles()

        FileLoader.with(this)
            .load(eWARTA_NEXT)
            .fromDirectory("eWarta", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    progress_bar.visibility = View.GONE
                    val pdfFile = p1!!.body

                    val actionBar = supportActionBar
                    actionBar!!.title = "eWarta - " + NEXT_SUNDAY_STRING

                    /*
                    val editor = sharedPref.edit()

                    editor.putString("lastSunday", NEXT_SUNDAY)
                    editor.putString("lastSundayString", NEXT_SUNDAY_STRING)
                    editor.putString("prevSunday", PREV_SUNDAY)
                    editor.putString("prevSundayString", PREV_SUNDAY_STRING)
                    editor.commit()
                    */

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
                            Toast.makeText(this@MainActivity, "Error while opening page " + page, Toast.LENGTH_SHORT)
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

                                /*
                                val editor = sharedPref.edit()


                                editor.putString("lastSunday", PREV_SUNDAY)
                                editor.putString("lastSundayString", PREV_SUNDAY_STRING)
                                //editor.putString("prevSunday", PREV_SUNDAY)
                                editor.commit()
                                */

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
                                        )
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
                                Toast.makeText(this@MainActivity, "" + p1!!.message, Toast.LENGTH_SHORT).show()
                                progress_bar.visibility = View.GONE
                            }
                        })

                }

            })


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
/*
            R.id.nav_previous -> {
                val intent = Intent(this, PreviousActivity::class.java)
                startActivity(intent)
            }
*/
            /*
            R.id.nav_send -> {

            }
            */
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
