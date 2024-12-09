package org.gkiserpong.ewartagkis

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
import kotlinx.android.synthetic.main.activity_liturgi.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class LiturgiActivity : AppCompatActivity() {

    private val URLAddress = "https://ewarta.gkiserpong.org/wp-content/liturgi/liturgi.json"
    private val internalStorage = "liturgi"
    var arrayList_details = ArrayList<HashMap<String, String>>()

    var jsonObjectString = ""

    private var isLatest: Boolean = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liturgi)

        val actionBar = supportActionBar


        actionBar!!.title = "Liturgi"

        actionBar.setDisplayHomeAsUpEnabled(true)


        //Picasso.get().load("https://ewarta.gkiserpong.org/img/cj1.jpg").into(this.iv_naviheader)

        //val actionBar = supportActionBar

        //actionBar.setDisplayHomeAsUpEnabled(true)

        try {
            FileLoader.deleteWith(this).fromDirectory("json", FileLoader.DIR_INTERNAL).deleteFiles(URLAddress)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (! isLatest) {

            getJSONLatestPDF(URLAddress, true)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getJSONLatestPDF(URIAddress: String, latestOne: Boolean) {


        //findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE

        pbLiturgi.visibility = View.VISIBLE

        FileLoader.with(this)
            .load(URIAddress)
            .fromDirectory("json", FileLoader.DIR_INTERNAL)
            .asFile(object: FileRequestListener<File> {
                @Override
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    val jsonObject =  p1!!.body
                    jsonObjectString = jsonObject.readText()
                    extractJson(jsonObjectString, latestOne)
                    getLatestPDF()

                }

                override fun onError(p0: FileLoadRequest?, p1: Throwable?) {
                    Toast.makeText(this@LiturgiActivity, "Sorry JSON can't be retrieved: " + URIAddress + " " + p1!!.message, Toast.LENGTH_LONG).show()
                    pbLiturgi.visibility = View.GONE
                }
            })

    }

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

    private fun getLatestPDF() {

        val actionBar = supportActionBar

        val parser = SimpleDateFormat("yyyyMMddHH", Locale.ENGLISH)

        val date_format_title = SimpleDateFormat("dd LLLL yyyy", Locale.ENGLISH)
        val TITLE_STRING = date_format_title.format(parser.parse(arrayList_details.last()["id"]!!)!!)

        actionBar!!.title = "Liturgi - " + TITLE_STRING

        actionBar.setDisplayHomeAsUpEnabled(true)


        loadPDF(arrayList_details.last()["pdf"]!!, internalStorage)

    }


    private fun loadPDF(pdfURI: String, pdfStorage: String) {

        FileLoader.with(this)
            .load(pdfURI)
            .fromDirectory(pdfStorage, FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File> {
                override fun onLoad(p0: FileLoadRequest?, p1: FileResponse<File>?) {
                    pbLiturgi.visibility = View.GONE
                    val pdfFile = p1!!.body


                    LiturgiV.fromFile(pdfFile)
                        .password(null)
                        .defaultPage(0)
                        .enableSwipe(true)
                        .swipeHorizontal(true)
                        .enableDoubletap(true)
                        //.onDraw { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->  }
                        //.onPageChange { page, pageCount ->  }
                        .onPageError { page, t ->
                            Toast.makeText(this@LiturgiActivity, "Error while opening page " + page, Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@LiturgiActivity, "Sorry PDF error: " + arrayList_details.last()["pdf"] + " " + p1!!.message, Toast.LENGTH_LONG).show()
                    pbLiturgi.visibility = View.GONE
                }

            })

    }

}
