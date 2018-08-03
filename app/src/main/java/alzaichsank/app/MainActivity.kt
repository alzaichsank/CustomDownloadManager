package alzaichsank.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var link_doc : String = "http://codehopedevloper.com/dummy_downloader/docx_exemple_download.docx"
    private var link_pdf : String = "http://codehopedevloper.com/dummy_downloader/pdf_exemple_download.pdf"
    private var link_ppt : String = "http://codehopedevloper.com/dummy_downloader/ppt_exemple_download.pptx"
    private var link_xls : String = "http://codehopedevloper.com/dummy_downloader/xls_exemple_downlod.xlsx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
