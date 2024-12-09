package org.gkiserpong.ewartagkis

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.jakewharton.threetenabp.AndroidThreeTen
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_bukukehidupan.*
import org.gkiserpong.ewartagkis.adapter.JsonListAdapter
import org.json.JSONArray
import org.json.JSONObject
//import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.File
import java.lang.Exception
import java.net.URL
import java.util.*

class BukuKehidupanActivity : AppCompatActivity() {

    //private var PREV_SUNDAY: String = ""
    //private var PREV_SUNDAY_STRING: String = ""
    //private var isLatest: Boolean = false

    //private var arrayList_details: ArrayList<Model> = ArrayList();
    //private val eWARTA_URI = "https://ewarta.carisolusi.com/wp-content/ewarta"
    private val URLAddress = "https://ewarta.gkiserpong.org/wp-content/bkj/bkj.json"
    // Change in version 3.1
    //private val PELANGI_URI = "https://ewarta.gkiserpong.org/wp-content/pelangi"
    var arrayList_details = ArrayList<HashMap<String, String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContentView(R.layout.activity_bukukehidupan)

        val actionBar = supportActionBar
        actionBar!!.title = "Buku Kehidupan Jemaat"
        actionBar.setDisplayHomeAsUpEnabled(true)

        //Picasso.get().load("https://ewarta.gkiserpong.org/img/cj1.jpg").into(this.iv_naviheader)

        pbBukuKehidupan.visibility = View.VISIBLE

        try {
            FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URLAddress)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getJSON(URLAddress)


        //HTTPAsyncTask().execute(URLAddress)

    }

    inner class HTTPAsyncTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String?): String {
            return URL(urls[0]).readText()
        }
        override fun onPostExecute(result: String?) {
            getLatestBkj(result)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getJSON(URIAddress: String) {


        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE

        FileLoader.with(this)
            .load(URIAddress)
            .fromDirectory("json", FileLoader.DIR_INTERNAL)
            .asFile(object: FileRequestListener<File> {
                @Override
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    val jsonObject =  p1!!.body
                    getLatestBkj(jsonObject.readText())

                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    Toast.makeText(this@BukuKehidupanActivity, "Sorry, can't load: " + URIAddress + " " + p1!!.message, Toast.LENGTH_LONG).show()
                    pbBukuKehidupan.visibility = View.GONE
                }
            })

    }


    fun parseJSON(result: String?) {
        //fun parseJSON(jsonObj: JSONObject) {

        val jsonObj = JSONObject(result!!)



        var json_array: JSONArray = jsonObj.getJSONArray("info")
        //var i:Int = 0
        var size: Int = json_array.length()
        arrayList_details = ArrayList()

        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE

        for (i in size - 1 downTo 0) {
            var jsonObjectdetail = json_array.getJSONObject(i)
            //var model:Model = Model()
            var map = HashMap<String, String>()
            //model.id = json_objectdetail.getString("id")
            map["id"] = jsonObjectdetail.getString("id")
            //model.pdf = json_objectdetail.getString("pdf")
            map["pdf"] = jsonObjectdetail.getString("pdf")
            //model.cover = json_objectdetail.getString("cover")
            map["cover"] = jsonObjectdetail.getString("cover")
            arrayList_details.add(map)
        }
/*
        findViewById<ListView>(R.id.listView).adapter =
            JsonListAdapter(
                this@BukuKehidupanActivity,
                arrayList_details
            )
*/

    }

    private fun getLatestBkj(result: String?) {

        val json_contact: JSONObject = JSONObject(result!!)

        var json_array: JSONArray = json_contact.getJSONArray("info")
        //var i:Int = 0
        var size:Int = json_array.length()
        arrayList_details = ArrayList()

        for(i in 0..size-1) {
            var jsonObjectdetail: JSONObject = json_array.getJSONObject(i)
            //var model:Model = Model()
            var map = HashMap<String, String>()
            //model.id = json_objectdetail.getString("id")
            map["id"] = jsonObjectdetail.getString("id")
            //model.pdf = json_objectdetail.getString("pdf")
            map["pdf"] = jsonObjectdetail.getString("pdf")
            //model.cover = json_objectdetail.getString("cover")
            map["cover"] = jsonObjectdetail.getString("cover")
            arrayList_details.add(map)
        }

        //var PELANGI_LAST = arrayList_details.last()
        val actionBar = supportActionBar
        //actionBar!!.title = "Majalah Pelangi - " + PELANGI_LAST.dataitem.get("id")
        actionBar!!.title = "Buku Kehidupan Jemaat " + arrayList_details.last()["id"]
        actionBar.setDisplayHomeAsUpEnabled(true)


        FileLoader.with(this)
            //.load(PELANGI_LAST + ".pdf")
            .load(arrayList_details.last()["pdf"])
            .fromDirectory("bkj", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    pbBukuKehidupan.visibility = View.GONE

                    val pdfFile = p1!!.body

                    BukuKehidupanV.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onPageChange { page, pageCount ->  }
                        .onPageError { page, t ->
                            Toast.makeText(this@BukuKehidupanActivity, "Error while opening page " + page, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@BukuKehidupanActivity, "Sorry Buku Kehidupan Jemaat: " + p1!!.message, Toast.LENGTH_LONG).show()
                    pbBukuKehidupan.visibility = View.GONE
                }

            })

    }

}

