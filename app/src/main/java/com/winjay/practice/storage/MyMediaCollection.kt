package com.winjay.practice.storage

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Size
import com.winjay.practice.utils.LogUtil

class MyMediaCollection {

    //图片实体类
    data class ImageBean(
        val uri: Uri,
        val name: String,
        val mimeType: String,
        val size: Int
    )

    //视频实体类
    data class VideoBean(
        val uri: Uri,
        val name: String,
        val mimeType: String,
        val duration: Int,
        val size: Int
    )

    //音频实体类
    data class AudioBean(
        val uri: Uri,
        val name: String,
        val mimeType: String,
        val duration: Int,
        val size: Int
    )


    /**
     * Query [ 图片媒体集 ] 包括： DCIM/ 和 Pictures/ 目录
     */
    fun queryImageCollection(context: Context): MutableList<ImageBean> {
        LogUtil.d(TAG, "########### 图片媒体集 ############")
        val imageBeanList = mutableListOf<ImageBean>()
        //定义内容解析器
        val contentResolver = context.contentResolver
        //指定查询的列名
        val photoColumns = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE
        )
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //指定查询哪张表的URI
            photoColumns, // 指定查询的列明
            null, //指定where的约束条件
            null, //为where中的占位符提供具体的值
            null // 指定查询结果的排序方式
        )

        val count = cursor!!.count
        LogUtil.d(TAG, "imageCollection count: --> $count")

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE))
                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                val size =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                )
                LogUtil.d(
                    TAG,
                    "imageCollection id =$id\ntitle = $title\nmime_type: =$mimeType\nsize: =\t$size\ncontentUri: =\t$contentUri\n"
                )
                val imageBean = ImageBean(
                    uri = contentUri,
                    name = title,
                    mimeType = mimeType,
                    size = size.toInt()
                )
                imageBeanList += imageBean

            }
            cursor.close()
        }
        return imageBeanList
    }

    /**
     * Query [ 视频媒体集 ] 包括： DCIM/, Movies/, 和 Pictures/ 目录
     */
    fun queryVideoCollection(context: Context): MutableList<VideoBean> {
        LogUtil.d(TAG, "########### 视频媒体集 ############")
        val videoBeanList = mutableListOf<VideoBean>()
        val contentResolver = context.contentResolver
        val videoColumns = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.SIZE
        )
        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoColumns,
            null,
            null,
            null
        )

        val count = cursor!!.count
        LogUtil.d(TAG, "videoCollection count: --> $count")

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                val size =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                )
                LogUtil.d(
                    TAG,
                    "imageCollection id =$id\ntitle = $title\nmime_type: =$mimeType\nduration=$duration\nsize: =\t$size\ncontentUri: =\t$contentUri\n"
                )
                val videoBean = VideoBean(
                    uri = contentUri,
                    name = title,
                    mimeType = mimeType,
                    duration = duration,
                    size = size
                )
                videoBeanList += videoBean

            }
            cursor.close()
        }
        return videoBeanList
    }

    /**
     * Query [ 音频媒体集 ] 包括： Alarms/, Audiobooks/, Music/, Notifications/, Podcasts/, 和 Ringtones/ 目录
     * 以及 Music/ 和 Movies/ 目录中的音频文件
     */
    fun queryAudioCollection(context: Context): MutableList<AudioBean> {
        LogUtil.d(TAG, "########### 音频媒体集 ############")
        val audioBeanList = mutableListOf<AudioBean>()
        val contentResolver = context.contentResolver
        val videoColumns = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.SIZE
        )
        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            videoColumns,
            null,
            null,
            null
        )

        val count = cursor!!.count
        LogUtil.d(TAG, "audioCollection count: --> $count")

        cursor.use {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val title =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                val mimeType =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE))
                val duration =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                val size =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                )
                LogUtil.d(
                    TAG,
                    "audioCollection id =$id\ntitle = $title\nmime_type: =$mimeType\nduration=$duration\nsize: =\t$size\ncontentUri: =\t$contentUri\n"
                )
                val audioBean = AudioBean(
                    uri = contentUri,
                    name = title,
                    mimeType = mimeType,
                    duration = duration,
                    size = size
                )
                audioBeanList += audioBean

            }
            cursor.close()
        }
        return audioBeanList
    }

    /**
     *  Insert [ 图片媒体集 ]
     */
    fun insertImageToCollection(context: Context, disPlayName: String) {
        LogUtil.d(
            TAG,
            "insertImageToCollection() called with: context = $context, disPlayName = $disPlayName"
        )
        val contentResolver = context.contentResolver
        //在主要外部存储设备上查找所有图片文件 (API <= 28 使用 VOLUME_EXTERNAL 代替)
        val imageCollection = MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        val contentValues = ContentValues().apply {
            //配置图片的显示名称
            put(MediaStore.Images.Media.DISPLAY_NAME, disPlayName)
            //配置图片的状态为：等待中...
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        //开始插入图片
        val imageUri = contentResolver.insert(imageCollection, contentValues)
        imageUri.let {
            contentResolver.openFileDescriptor(imageUri!!, "w", null).use {
                AssetHelper.copyAssetSingleFileToMedia(context, disPlayName, it!!)
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(imageUri, contentValues, null, null)
                it.close()
            }
        }
    }

    /**
     *  Insert [ 视频媒体集 ]
     */
    fun insertVideoToCollection(context: Context, disPlayName: String) {
        LogUtil.d(
            TAG,
            "insertVideoToCollection() called with: context = $context, disPlayName = $disPlayName"
        )
        val contentResolver = context.contentResolver
        //在主要外部存储设备上查找所有视频文件 (API <= 28 使用 VOLUME_EXTERNAL 代替)
        val videoCollection = MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        val contentValues = ContentValues().apply {
            //配置视频的显示名称
            put(MediaStore.Video.Media.DISPLAY_NAME, disPlayName)
            //配置视频的状态为：等待中...
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        //开始插入视频
        val videoUri = contentResolver.insert(videoCollection, contentValues)
        videoUri.let {
            contentResolver.openFileDescriptor(videoUri!!, "w", null).use {
                AssetHelper.copyAssetSingleFileToMedia(context, disPlayName, it!!)
                contentValues.clear()
                contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                contentResolver.update(videoUri, contentValues, null, null)
                it.close()
            }
        }
    }

    /**
     *  Insert [ 音频媒体集 ]
     */
    fun insertAudioToCollection(context: Context, disPlayName: String) {
        LogUtil.d(
            TAG,
            "insertAudioToCollection() called with: context = $context, disPlayName = $disPlayName"
        )
        val contentResolver = context.contentResolver
        //在主要外部存储设备上查找所有音频文件 (API <= 28 使用 VOLUME_EXTERNAL 代替)
        val audioCollection = MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        val contentValues = ContentValues().apply {
            //配置音频的显示名称
            put(MediaStore.Audio.Media.DISPLAY_NAME, disPlayName)
            //配置音频的状态为：等待中...
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        //开始插入音频
        val audioUri = contentResolver.insert(audioCollection, contentValues)
        audioUri.let {
            contentResolver.openFileDescriptor(audioUri!!, "w", null).use {
                AssetHelper.copyAssetSingleFileToMedia(context, disPlayName, it!!)
                contentValues.clear()
                contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                contentResolver.update(audioUri, contentValues, null, null)
                it.close()
            }
        }
    }

    /**
     *  Update [ 图片媒体集 ]
     */
    fun updateImageCollection(context: Context, id: Long, newName: String) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Images.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, newName)
        }
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageUri.let {
            context.contentResolver.openFileDescriptor(imageUri, "w")?.use {
                val result =
                    contentResolver.update(imageUri, contentValues, selection, selectionArgs)
                LogUtil.d(TAG, "updateImageCollection() called : $result")
            }
        }
    }

    /**
     *  Update [ 视频媒体集 ]
     */
    fun updateVideoCollection(context: Context, id: Long, newName: String) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Video.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, newName)
        }
        val videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
        videoUri.let {
            context.contentResolver.openFileDescriptor(videoUri, "w")?.use {
                val result =
                    contentResolver.update(videoUri, contentValues, selection, selectionArgs)
                LogUtil.d(TAG, "updateVideoCollection() called : $result")
                it.close()
            }
        }

    }

    /**
     *  Update [ 音频媒体集 ]
     */
    fun updateAudioCollection(context: Context, id: Long, newName: String) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, newName)
        }
        val audioUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        audioUri.let {
            context.contentResolver.openFileDescriptor(audioUri, "w")?.use {
                val result =
                    contentResolver.update(audioUri, contentValues, selection, selectionArgs)
                LogUtil.d(TAG, "updateAudioCollection() called : $result")
                it.close()
            }
        }

    }

    /**
     *  Delete [ 图片媒体集 ]
     */
    fun deleteImageCollection(context: Context, id: Long) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Images.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageUri.let {
            context.contentResolver.openFileDescriptor(imageUri, "w")?.use {
                val result = contentResolver.delete(imageUri, selection, selectionArgs)
                LogUtil.d(TAG, "deleteImageCollection() called : $result")
                it.close()
            }
        }
    }

    /**
     *  Delete [ 视频媒体集 ]
     */
    fun deleteVideoCollection(context: Context, id: Long) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Video.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
        videoUri.let {
            context.contentResolver.openFileDescriptor(videoUri, "w")?.use {
                val result = contentResolver.delete(videoUri, selection, selectionArgs)
                LogUtil.d(TAG, "deleteVideoCollection() called : $result")
                it.close()
            }
        }

    }

    /**
     *  Delete [ 音频媒体集 ]
     */
    fun deleteAudioCollection(context: Context, id: Long) {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val audioUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        audioUri.let {
            context.contentResolver.openFileDescriptor(audioUri, "w")?.use {
                val result = contentResolver.delete(audioUri, selection, selectionArgs)
                LogUtil.d(TAG, "deleteAudioCollection() called : $result")
                it.close()
            }
        }

    }

    /**
     * 加载特定媒体文件的缩略图
     */
    fun loadThumbnail(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
        val size = Size(width, height)
        return context.contentResolver.loadThumbnail(
            uri, size, null
        )
    }

    /**
     * 获取图片位置元数据信息
     */
//    fun getImageLocationMataData(context: Context, imageUri: Uri) {
//        // 获取位置数据使用 ExifInterface 库.
//        // 如果 ACCESS_MEDIA_LOCATION 没有被授予，将会发生异常
//        val uri = MediaStore.setRequireOriginal(imageUri)
//        context.contentResolver.openInputStream(uri)?.use {
//            ExifInterface(it).run {
//                //如果经纬度为Null，将会回退到坐标(0,0)
//                this.latLong?.let {
//                    LogUtil.d(TAG, "getImageLocationMataData() called : Latitude =  ${latLong?.get(0)}")
//                    LogUtil.d(TAG, "getImageLocationMataData() called : Longitude = ${latLong?.get(1)}")
//                }
//            }
//        }
//    }


    companion object {
        private const val TAG = "MyMediaCollection"
    }
}