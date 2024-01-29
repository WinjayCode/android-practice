package com.winjay.mirrorcast.opengl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import com.winjay.mirrorcast.opengl.annotations.DoNotStrip
import com.winjay.mirrorcast.opengl.grafika.FullFrameRect
import com.winjay.mirrorcast.opengl.grafika.Texture2dProgram
import com.winjay.mirrorcast.util.LogUtil
import java.lang.ref.WeakReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@DoNotStrip
@SuppressLint("ClickableViewAccessibility")
class RemoteDisplayGLView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    private val renderer = Renderer(this)
    private val TAG: String = "RemoteDisplayGLView"

    init {
        setEGLContextClientVersion(2);
        setRenderer(renderer)
        preserveEGLContextOnPause = true
        renderMode = RENDERMODE_WHEN_DIRTY

//        setOnTouchListener(ViewOnTouchListener())
    }

    fun onDestroy() {
        renderer.destroy()
    }

    fun getSurface(): Surface? {
        return renderer.getSurface()
    }

    class Renderer(view: RemoteDisplayGLView) : GLSurfaceView.Renderer {
        private val TAG: String = "Renderer"

        private val viewRef = WeakReference<RemoteDisplayGLView>(view)

        private var screen: FullFrameRect? = null
        private var texture: RenderableTexture? = null

        override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
            LogUtil.d(TAG);
            val program = Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT)
            screen = FullFrameRect(program).apply {
                // delete texture if exists
                deleteTexture()
                texture = createTexture(this)
                // update receiver surface
//                CarLife.receiver().setSurface(texture?.surface)
            }
            LogUtil.d(TAG, "RemoteDisplayView onSurfaceCreated " + texture ?: "")
        }

        override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
            LogUtil.d(TAG);
            GLES20.glViewport(0, 0, width, height)

            texture?.setDefaultBufferSize(width, height)
//            CarLife.receiver().onSurfaceSizeChanged(width, height)

            LogUtil.d(TAG, "RemoteDisplayView onSurfaceChanged width=" + width + " height=" + height)
        }

        override fun onDrawFrame(gl: GL10) {
            LogUtil.d(TAG);
            texture?.let {
                it.updateTexImage()
                screen?.drawFrame(it.textureId, it.transform)
            }
        }

        fun destroy() {
            LogUtil.d(TAG);
            texture?.let {
                it.release(true)
                texture = null
                // clear receiver surface
//                CarLife.receiver().setSurface(null)
            }
        }

        private fun requestRender() {
            viewRef.get()?.requestRender()
        }

        private fun createTexture(screen: FullFrameRect): RenderableTexture {
            val texture = RenderableTexture(screen.createTextureObject(), 0, 0)
            texture.onFrameAvailableListener = SurfaceTexture.OnFrameAvailableListener {
                requestRender()
            }
            return texture
        }

        private fun deleteTexture() {
            texture?.let {
                it.release(true)
                texture = null
            }
        }

        fun getSurface(): Surface? {
            return texture?.surface
        }
    }

//    override fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int) {
//        post {
//            val ratio = videoWidth.toFloat() / videoHeight
//            val screenWidth = context.applicationContext.resources.displayMetrics.widthPixels
//            val screenHeight = context.applicationContext.resources.displayMetrics.heightPixels
//            val (destWidth, destHeight) = ScaleUtils.inside(screenWidth, screenHeight, ratio)
//            layoutParams = layoutParams.apply {
//                width = destWidth
//                height = destHeight
//            }
//        }
//    }

    /*inner class ViewOnTouchListener() : OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            CarLife.receiver().onTouchEvent(event)
            return true
        }

    }*/

}