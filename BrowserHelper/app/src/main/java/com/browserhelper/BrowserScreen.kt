
package com.browserhelper
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

@Composable
fun BrowserScreen(){
    var url by remember { mutableStateOf("https://www.google.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var scanResult by remember { mutableStateOf<JSONObject?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()){
        Row(Modifier.fillMaxWidth().padding(6.dp)){
            OutlinedTextField(value=url, onValueChange={url=it}, modifier=Modifier.weight(1f), singleLine=true, label={Text("URL")})
            Button(onClick={ webView?.loadUrl(if(url.startsWith("http")) url else "https://www.google.com/search?q=${Uri.encode(url)}") }, modifier=Modifier.padding(start=4.dp)){ Text("Go") }
        }
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement=Arrangement.SpaceEvenly){
            Button(onClick={ webView?.evaluateJavascript(PageScanner.SCAN_JS){ res ->
                try{
                    val clean=res.trim().removeSurrounding(""").replace("\\"",""").replace("\\\\","\\")
                    // Actually WebView returns JSON stringified twice, handle
                    val jsonStr = if(res.startsWith("\"")) JSONObject(res.removeSurrounding("\"").replace("\\"",""")).toString() else res
                    // fallback parse
                    scanResult = try{ JSONObject(res.let{ android.webkit.URLUtil.guessFileName("","","") ; org.json.JSONObject.quote(it) }) } catch(e:Exception){ null }
                    // Simple: evaluate again with proper callback
                    webView?.evaluateJavascript("(${PageScanner.SCAN_JS})"){ r2 ->
                        try{
                            val s = r2.replace("\\\"",""").trim()
                            // Remove outer quotes added by evaluateJavascript
                            var inner = s
                            if(inner.startsWith("\"") && inner.endsWith("\"")) inner = JSONObject().apply{}.let{ inner.substring(1, inner.length-1).replace("\\"",""").replace("\\\\","\\") }
                            scanResult = JSONObject(inner)
                            showSheet=true
                        }catch(e:Exception){}
                    }
                }catch(e:Exception){}
            }}){ Text("📥 Files") }
            Button(onClick={ webView?.url?.let{ BookmarkStore.save(webView!!.context, webView!!.title ?: it, it); Toast.makeText(webView!!.context,"Bookmarked",Toast.LENGTH_SHORT).show() } }){ Text("🔖 Save") }
            Button(onClick={ webView?.reload() }){ Text("↻") }
        }
        AndroidView(modifier=Modifier.weight(1f).fillMaxWidth(), factory={ ctx ->
            WebView(ctx).apply{
                webView=this
                settings.javaScriptEnabled=true; settings.domStorageEnabled=true
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this,true)
                webViewClient=object:WebViewClient(){ override fun onPageStarted(v:WebView?,u:String?,f:Bitmap?){ if(u!=null) url=u } }
                setDownloadListener{ dlUrl, ua, cd, mime, len -> DownloadManagerHelper.start(context, dlUrl, ua, cd, mime) }
                loadUrl(url)
                layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        })

        if(showSheet && scanResult!=null){
            AlertDialog(onDismissRequest={showSheet=false}, title={Text("Found on page")},
            text={
                val files = scanResult!!.optJSONArray("files")
                val images = scanResult!!.optJSONArray("images")
                val tables = scanResult!!.optJSONArray("tables")
                LazyColumn{
                    item{ Text("Files: ${files?.length()?:0} | Images: ${images?.length()?:0} | Tables: ${tables?.length()?:0}", style=MaterialTheme.typography.titleSmall) }
                    if(files!=null) items((0 until files.length()).toList()){ i ->
                        val o=files.getJSONObject(i); Text("📄 ${o.optString("name")} [${o.optString("ext")}] - ${o.optString("url").take(40)}")
                    }
                }
            },
            confirmButton={
                Button(onClick={
                    // download all files found
                    val files = scanResult!!.optJSONArray("files")
                    if(files!=null) for(i in 0 until files.length()){
                        val o=files.getJSONObject(i); webView?.let{ DownloadManagerHelper.start(it.context, o.optString("url"), null, null, null) }
                    }
                    showSheet=false
                }){ Text("Download All Files") }
            })
        }
    }
}
