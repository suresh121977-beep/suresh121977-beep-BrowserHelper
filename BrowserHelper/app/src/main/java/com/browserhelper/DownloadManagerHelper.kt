
package com.browserhelper
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
object DownloadManagerHelper {
    fun start(context:Context, url:String, userAgent:String?, contentDisposition:String?, mimeType:String?){
        try{
            val fileName=URLUtil.guessFileName(url,contentDisposition,mimeType)
            val req=DownloadManager.Request(Uri.parse(url))
            req.setTitle(fileName)
            if(!userAgent.isNullOrBlank()) req.addRequestHeader("User-Agent",userAgent)
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"BrowserHelper/$fileName")
            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(req)
            Toast.makeText(context,"Downloading: $fileName",Toast.LENGTH_SHORT).show()
        }catch(e:Exception){ Toast.makeText(context,"Failed: ${e.message}",Toast.LENGTH_LONG).show() }
    }
}
