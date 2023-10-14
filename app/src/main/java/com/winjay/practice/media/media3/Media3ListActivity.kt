package com.winjay.practice.media.media3

import com.winjay.practice.common.BaseListActivity

class Media3ListActivity : BaseListActivity() {
    override fun getMainMap(): LinkedHashMap<String?, Class<*>?> {
        return object : LinkedHashMap<String?, Class<*>?>() {
            init {
                put("MediaSessionService", Media3SessionActivity::class.java)
                put("MediaLibraryService", Media3LibraryActivity::class.java)
            }
        }
    }
}