package com.winjay.mirrorcast.opengl

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.view.Surface
import com.winjay.mirrorcast.util.LogUtil
import java.util.concurrent.atomic.AtomicInteger

class RenderableTexture(val textureId: Int,
                        val width: Int,
                        val height: Int,
                        val scale: Float = 1.0f,
                        val xOffset: Int = 0,
                        val yOffset: Int = 0,
                        // padding， 左上右下
                        val padding: IntArray = intArrayOf(0, 0, 0, 0))
    : SurfaceTexture.OnFrameAvailableListener {
    private var needUpdateTex = AtomicInteger(0)
    @Volatile
    private var isReleased = false
    private var activated = false
    private val TAG:String = "RenderableTexture"

    val texture: SurfaceTexture = SurfaceTexture(textureId)
    val transform = FloatArray(16)

    val surface: Surface = Surface(texture)

    var onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener? = null

    init {
        if (width != 0 && height != 0) {
            setDefaultBufferSize(width, height)
        }
        texture.setOnFrameAvailableListener(this)
        LogUtil.d(TAG, "RenderableTexture monitor create")
    }

    fun setDefaultBufferSize(width: Int, height: Int) {
        texture.setDefaultBufferSize(width, height)
    }

    fun activate() {
        activated = true
    }

    fun deactivate() {
        activated = false
    }

    fun isActivated(): Boolean {
        return activated
    }

    fun updateTexImage(): Boolean {
        var updated = false
        if (!isReleased) {
            while (needUpdateTex.get() > 0 && needUpdateTex.getAndDecrement() > 0) {
                updated = true
                try {
                    texture.updateTexImage()
                    texture.getTransformMatrix(transform)
                } catch (e: Exception) {
                    LogUtil.e(TAG, "RenderableTexture $this updateTexImage exception $e");
                }
            }
        }
        return updated
    }

    fun release(deleteTexture: Boolean = true) {
        if (!isReleased) {
            isReleased = true

            if (deleteTexture) {
                GLES20.glDeleteTextures(0, intArrayOf(textureId), 0)
            }
            texture.release()
            surface.release()
            LogUtil.d(TAG, "RenderableTexture monitor release $deleteTexture $this");
        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        needUpdateTex.incrementAndGet()
        onFrameAvailableListener?.onFrameAvailable(surfaceTexture)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is RenderableTexture) {
            return false
        }
        return other.textureId == textureId
    }

    override fun hashCode(): Int {
        return textureId
    }

    override fun toString(): String {
        return super.toString() + " $textureId width $width height $height available frames $needUpdateTex"
    }
}