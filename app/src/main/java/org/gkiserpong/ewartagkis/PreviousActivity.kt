package org.gkiserpong.ewartagkis

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PreviousActivity : AppCompatActivity() {

    private val PREF_NAME = "eWarta"
    private var PREV_SUNDAY: String = ""
    private var PREV_SUNDAY_STRING: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous)

        val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val lastSunday = sharedPref.getString("lastSunday", "")
        //val lastSundayString = sharedPref.getString("lastSundayString", "")
        val prevSunday = sharedPref.getString("prevSunday", "")
        val prevSundayString = sharedPref.getString("prevSundayString", "")

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
        if (!sharedPref.contains("prevSunday")) {

            val date_format = SimpleDateFormat("yyyyMMdd")
            val date = date_format.parse(lastSunday.toString())!!

            val calendar = Calendar.getInstance()
            calendar.setTime(date)
            calendar.add(Calendar.DATE, -6)

            // This if to fix bug, if today is SUNDAY
            // PREV SUNDAY won't work
            //if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            //    calendar.add(Calendar.DATE, -6)

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            //val date_format = SimpleDateFormat("yyyyMMdd")
            PREV_SUNDAY = date_format.format(calendar.getTime())

            val date_format_title = SimpleDateFormat("dd LLLL yyyy")
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
                            Toast.makeText(this@PreviousActivity, "Error while opening page " + page, Toast.LENGTH_SHORT)
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
                    Toast.makeText(this@PreviousActivity, "" + p1!!.message, Toast.LENGTH_SHORT).show()
                    progress_bar.visibility = View.GONE
                }
            })

    }
}
