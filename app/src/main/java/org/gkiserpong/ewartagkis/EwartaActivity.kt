package org.gkiserpong.ewartagkis

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.jakewharton.threetenabp.AndroidThreeTen
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_ewarta.*
import kotlinx.android.synthetic.main.activity_majalah_pelangi.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class EwartaActivity : AppCompatActivity() {

    private val URLAddress = "https://ewarta.gkiserpong.org/wp-content/ewarta/ewarta.json"
    // Change in version 3.1
    //private val PELANGI_URI = "https://ewarta.gkiserpong.org/wp-content/pelangi"
    //var arrayList_details = ArrayList<LinkedHashMap<Int, LinkedHashMap<String, String>>>()
    var arrayList_details = ArrayList<HashMap<String, String>>()

    var jsonObjectString = ""

    private var isLatest: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_ewarta)

        val actionBar = supportActionBar
        actionBar!!.title = "eWarta"
        actionBar.setDisplayHomeAsUpEnabled(true)


        //Picasso.get().load("https://ewarta.gkiserpong.org/img/cj1.jpg").into(this.iv_naviheader)

        fb_ewarta_next.hide()
        fb_ewarta_prev.hide()
        pbEwarta.visibility = View.VISIBLE


        try {
            FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URLAddress)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //getJSONLatestEwarta(URLAddress, true)

        //getLatestEwarta()

        if (! isLatest) {

            getJSONLatestEwarta(URLAddress, true)
        }

        fb_ewarta_next.setOnClickListener {
            try {
                FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URLAddress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getJSONLatestEwarta(URLAddress, true)
        }

        fb_ewarta_prev.setOnClickListener {
            try {
                FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URLAddress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            getJSONLatestEwarta(URLAddress, false)
        }

        //HTTPAsyncTask().execute(URLAddress)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getJSONLatestEwarta(URIAddress: String, latestOne: Boolean) {


        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE

        pbEwarta.visibility = View.VISIBLE

        FileLoader.with(this)
            .load(URIAddress)
            .fromDirectory("json", FileLoader.DIR_INTERNAL)
            .asFile(object: FileRequestListener<File> {
                @Override
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    val jsonObject =  p1!!.body
                    jsonObjectString = jsonObject.readText()
                    //getLatestPelangi(jsonObject.readText())
                    extractJson(jsonObjectString, latestOne)
                    getLatestEwarta()

                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    Toast.makeText(this@EwartaActivity, "Sorry JSON can't be retrieved: " + URIAddress + p1!!.message, Toast.LENGTH_LONG).show()
                    pbEwarta.visibility = View.GONE
                }
            })

    }

    /*

    private fun getJSONPrevEwarta(URIAddress: String) {


        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE

        FileLoader.with(this)
            .load(URIAddress)
            .fromDirectory("json", FileLoader.DIR_INTERNAL)
            .asFile(object: FileRequestListener<File> {
                @Override
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    val jsonObject =  p1!!.body
                    jsonObjectString = jsonObject.readText()
                    //getLatestPelangi(jsonObject.readText())
                    extractJson(jsonObjectString, false)
                    getLatestEwarta()

                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    Toast.makeText(this@EwartaActivity, "Sorry eWarta JSON can't be retrieved: " + URIAddress + p1!!.message, Toast.LENGTH_SHORT).show()
                    pbEwarta.visibility = View.GONE
                }
            })

    }


     */

    private fun extractJson(result: String?, latestOne: Boolean) {

        //val json_contact: JSONObject = JSONObject(result!!)
        val json_contact = JSONObject(result!!)

        var json_array: JSONArray = json_contact.getJSONArray("info")
        //var i:Int = 0
        var size: Int = json_array.length()
        if (! latestOne) {
            size -= 1
        }

        //arrayList_details = ArrayList()

        for (i in 0 until size) {
            var jsonObjectdetail: JSONObject = json_array.getJSONObject(i)
            //var model:Model = Model()
            var map = LinkedHashMap<String, String>()
            //model.id = json_objectdetail.getString("id")
            map["id"] = jsonObjectdetail.getString("id")
            //model.pdf = json_objectdetail.getString("pdf")
            map["pdf"] = jsonObjectdetail.getString("pdf")
            //model.cover = json_objectdetail.getString("cover")
            map["cover"] = jsonObjectdetail.getString("cover")

            arrayList_details.add(map)
        }

    }

    private fun getLatestEwarta() {

            val actionBar = supportActionBar
            //actionBar!!.title = "Majalah Pelangi - " + PELANGI_LAST.dataitem.get("id")

            val parser = SimpleDateFormat("yyyyMMddHH", Locale.ENGLISH)

            val date_format_title = SimpleDateFormat("dd LLLL yyyy", Locale.ENGLISH)
            val TITLE_STRING = date_format_title.format(parser.parse(arrayList_details.last()["id"]!!)!!)

            //val formatter = DateTimeFormatter.ofPattern("yyyymmdd", Locale.ENGLISH)


            //actionBar!!.title = "eWarta - " + arrayList_details.last()["id"]

            actionBar!!.title = "eWarta - " + TITLE_STRING

            actionBar.setDisplayHomeAsUpEnabled(true)


            loadEwarta(arrayList_details.last()["pdf"]!!)
/*
        if ( ! isLatest ) {
            fb_ewarta_next.hide()
            fb_ewarta_prev.show()
            isLatest = true
        } else {
            fb_ewarta_next.show()
            fb_ewarta_prev.hide()
            isLatest = false
        }
*/
    }

    /*
    private fun getPrevEwarta() {

        if ( isLatest ) {

            val actionBar = supportActionBar
            //actionBar!!.title = "Majalah Pelangi - " + PELANGI_LAST.dataitem.get("id")

            //val date_format_title = SimpleDateFormat("dd LLLL yyyy", Locale.ENGLISH)
            //val TITLE_STRING: String = date_format_title.format(cal.getTime())

            //val formatter = DateTimeFormatter.ofPattern("yyyymmdd", Locale.ENGLISH)



            val INDEXPREV = arrayList_details.size - 1


            actionBar!!.title = "eWarta - " + arrayList_details.last()["id"]

            actionBar.setDisplayHomeAsUpEnabled(true)


            //loadEwarta(arrayList_details.indexOf(INDEXPREV))

            fb_ewarta_next.show()
            fb_ewarta_prev.hide()
            isLatest = false
        }

    }

     */

    private fun loadEwarta(EwartaURI: String) {

        FileLoader.with(this)
            //.load(PELANGI_LAST + ".pdf")
            .load(EwartaURI)
            .fromDirectory("ewarta", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    pbEwarta.visibility = View.GONE
                    val pdfFile = p1!!.body

                    if ( ! isLatest ) {
                        fb_ewarta_next.hide()
                        fb_ewarta_prev.show()
                        isLatest = true
                    } else {
                        fb_ewarta_next.show()
                        fb_ewarta_prev.hide()
                        isLatest = false
                    }


                    EWartaV.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onPageChange { page, pageCount ->  }
                        .onPageError { page, t ->
                            Toast.makeText(this@EwartaActivity, "Error while opening page " + page, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@EwartaActivity, "Sorry PDF error: " + arrayList_details.last()["pdf"] + p1!!.message, Toast.LENGTH_LONG).show()
                    pbEwarta.visibility = View.GONE
                }

            })

    }

}
