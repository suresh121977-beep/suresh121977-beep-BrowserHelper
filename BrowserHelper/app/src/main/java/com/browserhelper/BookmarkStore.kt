
package com.browserhelper
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
data class Bookmark(val title:String, val url:String)
object BookmarkStore {
    private const val PREFS="browser_helper"
    private const val KEY="bookmarks"
    fun save(context:Context,title:String,url:String){
        val p=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE)
        val arr=JSONArray(p.getString(KEY,"[]"))
        for(i in 0 until arr.length()) if(arr.getJSONObject(i).optString("url")==url) return
        val o=JSONObject(); o.put("title",title); o.put("url",url)
        arr.put(o); p.edit().putString(KEY,arr.toString()).apply()
    }
    fun getAll(context:Context):List<Bookmark>{
        val p=context.getSharedPreferences(PREFS,Context.MODE_PRIVATE)
        val arr=JSONArray(p.getString(KEY,"[]"))
        return (0 until arr.length()).map { Bookmark(arr.getJSONObject(it).optString("title"), arr.getJSONObject(it).optString("url")) }
    }
}
