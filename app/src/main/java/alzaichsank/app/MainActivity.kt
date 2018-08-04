package alzaichsank.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.webkit.URLUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var link_doc : String = "http://codehopedevloper.com/dummy_downloader/docx_exemple_download.docx"
    private var link_pdf : String = "http://codehopedevloper.com/dummy_downloader/pdf_exemple_download.pdf"
    private var link_ppt : String = "http://codehopedevloper.com/dummy_downloader/ppt_exemple_download.pptx"
    private var link_xls : String = "http://codehopedevloper.com/dummy_downloader/xls_exemple_downlod.xlsx"

    //costum downloader
    private  var downloadManager : DownloadManager? = null
    private var refid : Long? = 0
    var uriData: Uri? = null
    var list: ArrayList<Long> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declration downloader
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    !isStoragePermissionGranted()
                } else {
                    TODO("VERSION.SDK_INT < M")
                })
        {
        }

//        lets download
        download_pdf.setOnClickListener{
            download(link_pdf)
        }
        download_doc.setOnClickListener{
            download(link_doc)
        }
        download_ppt.setOnClickListener{
            download(link_ppt)
        }
        download_xls.setOnClickListener{
            download(link_xls)
        }


    }
    //downloader

    fun download(Link :  String) {
        list.clear()
        var nameFile : String = URLUtil.guessFileName(Link, null, null)
//                    Snackbar.make(relativeLayout,nameFile,  Snackbar.LENGTH_LONG).show()
        DownloadChecker(Link,nameFile)
    }


    // check if exist
    fun DownloadChecker(Link : String , name_file : String) {
        val applictionFile = File((Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).toString() + "/" + getString(R.string.app_name) + "/" + name_file))
        if (applictionFile.exists())
        {
//            Snackbar.make(relativeLayout,applictionFile.absolutePath,  Snackbar.LENGTH_LONG).show()
            reOpen(applictionFile.absolutePath)
        }
        else
        {
            val uri = Uri.parse(Link)
            if(uri != null) {
                val request = DownloadManager.Request(uri)

                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                request.setAllowedOverRoaming(false)
                request.setTitle("Download...")
                request.setDescription("Downloading $name_file")
                request.setVisibleInDownloadsUi(true)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        "/" + getString(R.string.app_name) + "/" + name_file)
                refid = downloadManager!!.enqueue(request)
                Log.e("DOWNLOAD ID", "" + refid)

                list.add(refid!!)
            }
        }
    }
    // re open
    fun reOpen(fileName : String){
        try{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val newFilePath = fileName.replace("%20", " ")
            val file = File(newFilePath)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uriData = FileProvider.getUriForFile(this@MainActivity, applicationContext.packageName + ".provider", file)

                // Add in case of if We get Uri from fileProvider

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            }else{
                uriData = Uri.fromFile(file)
            }

            intent.setDataAndType(uriData, "application/pdf")
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // handle no application here....
//                    Snackbar.make(relativeLayout, e.toString(),  Snackbar.LENGTH_LONG).show()
            Snackbar.make(relativeLayout, "No pdf viewing application detected. File saved in download folder!",  Snackbar.LENGTH_LONG).show()
        }
    }
    //on complete

    var onComplete: BroadcastReceiver = object: BroadcastReceiver() {

        @SuppressLint("ServiceCast")
        override fun onReceive(ctxt:Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            Log.e("ID nya", " berapa : $referenceId")
            list.remove(referenceId)

            if (list.isEmpty())
            {

                val mBuilder = NotificationCompat.Builder(this@MainActivity)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("All Download completed")
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(455, mBuilder.build())

                val c = downloadManager!!.query(DownloadManager.Query().setFilterById(refid!!))
                c.moveToFirst()
                val fileUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                val mFile = File(Uri.parse(fileUri).path)
                val fileName : String = mFile.absolutePath

                try{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val newFilePath = fileName.replace("%20", " ")
                    val file = File(newFilePath)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uriData = FileProvider.getUriForFile(this@MainActivity, applicationContext.packageName + ".provider", file)

                        // Add in case of if We get Uri from fileProvider

                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    }else{
                        uriData = Uri.fromFile(file)
                    }

                    intent.setDataAndType(uriData, "application/pdf")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // handle no application here....
//                    Snackbar.make(relativeLayout, e.toString(),  Snackbar.LENGTH_LONG).show()
                    Snackbar.make(relativeLayout, "No pdf viewing application detected. File saved in download folder!",  Snackbar.LENGTH_LONG).show()
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isStoragePermissionGranted():Boolean {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED))
            {
                return true
            }
            else
            {
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        }
        else
        { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults:IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            // permission granted
        }
    }


    //test downloader end
}
