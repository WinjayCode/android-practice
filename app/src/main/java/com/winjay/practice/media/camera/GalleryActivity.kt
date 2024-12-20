package com.winjay.practice.media.camera

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import com.winjay.practice.common.BaseActivity
import com.winjay.practice.databinding.ActivityGalleryBinding
import com.winjay.practice.utils.FileUtil
import com.winjay.practice.utils.LogUtil
import java.io.FileNotFoundException


/**
 * open system gallery
 */
class GalleryActivity : BaseActivity() {
    private lateinit var binding: ActivityGalleryBinding

    val OPEN_GALLERY_REQUEST_CODE: Int = 1

    override fun useViewBinding(): Boolean {
        return true
    }

    override fun viewBinding(): View {
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.openGallery.setOnClickListener {
            pickMediaFromGallery()
        }
    }

    private fun pickMediaFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/* video/*")
        // 允许选择多个文件
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        // 设置最大选择文件数量，例如设置为5
        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 5)

        // 获取SD卡根目录的File对象
        val sdCardRoot = Environment.getExternalStorageDirectory()
        LogUtil.d("dwj", "sdCardRoot=${sdCardRoot.path}")
        // 将File对象转换为Uri
        val sdCardRootUri = Uri.fromFile(sdCardRoot)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, sdCardRootUri)
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.clipData != null) {
                val clipData = data.clipData
                for (i in 0 until clipData!!.itemCount) {
                    val uri = clipData!!.getItemAt(i).uri
                    // 处理每个文件的URI
                    handleUri(uri)
                }
            } else if (data.data != null) {
                val uri = data.data
                // 处理单个文件的URI
                if (uri != null) {
                    handleUri(uri)
                }
            }
        }
    }

    private fun handleUri(uri: Uri) {
        try {
            // 检查MIME类型
            val mimeType = contentResolver.getType(uri)
            LogUtil.d("dwj", "mimeType=${mimeType}")
            if (mimeType != null) {
                if (mimeType.startsWith("image")) {
                    // 用户选择了图片
                    LogUtil.d("dwj", "Image selected")
                    // 处理图片
                    val inputStream = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.galleryIv.setImageBitmap(bitmap)
                } else if (mimeType.startsWith("video")) {
                    // 用户选择了视频
                    LogUtil.d("dwj", "Video selected")
                    // 处理视频
                }
            }

            LogUtil.d("dwj", "path=${FileUtil.getPathFromUri(this, uri)}")
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }
    }
}