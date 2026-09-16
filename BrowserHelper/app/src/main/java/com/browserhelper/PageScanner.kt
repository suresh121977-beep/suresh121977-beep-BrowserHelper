
package com.browserhelper
object PageScanner {
    // JS that finds files, images, tables
    const val SCAN_JS = """
    (function(){
      function ext(u){ try{ return u.split('.').pop().split('?')[0].toLowerCase(); }catch(e){return ''} }
      const fileExts=["pdf","xls","xlsx","csv","doc","docx","zip","mp3","mp4","jpg","jpeg","png","webp","svg","json","txt"];
      let files=[];
      document.querySelectorAll('a[href]').forEach(a=>{
        let href=a.href; if(!href) return;
        let e=ext(href); if(fileExts.includes(e)) files.push({url:href,name:a.textContent.trim()||href.split('/').pop(),ext:e,type:e.toUpperCase()});
      });
      let images=[...document.querySelectorAll('img')].map(i=>({url:i.src, w:i.naturalWidth, h:i.naturalHeight})).filter(x=>x.url && x.w>80 && x.h>80);
      let tables=[...document.querySelectorAll('table')].map((t,i)=>({index:i, rows:t.rows.length, cols:t.rows[0]?t.rows[0].cells.length:0, html:t.outerHTML}));
      return JSON.stringify({files:files, images:images, tables:tables});
    })();
    """
}
