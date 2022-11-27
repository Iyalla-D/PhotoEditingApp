package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@Suppress("DEPRECATION")
class EditActivity : AppCompatActivity() {
    private val canvas: Canvas = Canvas()
    private var paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 6f
    }
    private var path = Path()
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private lateinit var _editimageview: ImageView
    private lateinit var _drawingPen: ImageButton
    private lateinit var _rotateImage: ImageButton
    private lateinit var _confirmImage: ImageButton
    private var sentPhoto: Uri? = null
    private var mDefaultColor = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        mDefaultColor = 0
        sentPhoto = intent.data
        _editimageview = findViewById(R.id.editimage)
        with(_editimageview) { setImageURI(sentPhoto) }

        _drawingPen = findViewById(R.id.drawButton)
        _rotateImage = findViewById(R.id.rotateImageButton)
        _confirmImage = findViewById(R.id.checkmark)

        _drawingPen.setOnClickListener {
            _editimageview.isEnabled = true

            openColorPickerDialogue()

            _editimageview.setOnTouchListener { _, event ->
                val bitmapCopy: Bitmap = _editimageview.drawToBitmap()
                motionTouchEventX = event.x
                motionTouchEventY = event.y
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.reset()
                        path.moveTo(motionTouchEventX, motionTouchEventY)
                        currentX = motionTouchEventX
                        currentY = motionTouchEventY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        path.quadTo(currentX,
                            currentY,
                            (motionTouchEventX + currentX) / 2,
                            (motionTouchEventY + currentY) / 2)
                        currentX = motionTouchEventX
                        currentY = motionTouchEventY
                    }
                    MotionEvent.ACTION_UP -> {
                        path.reset()
                    }
                }
                canvas.setBitmap(bitmapCopy)
                canvas.drawPath(path, paint)
                _editimageview.setImageBitmap(bitmapCopy)
                true
            }
        }

        _rotateImage.setOnClickListener {
            _editimageview.isEnabled = false
            val matrix = Matrix()
            matrix.postRotate(90F)
            val imageBitmap = _editimageview.drawToBitmap()
            val rotated = Bitmap.createBitmap(imageBitmap,
                0,
                0,
                imageBitmap.width,
                imageBitmap.height,
                matrix,
                true)
            _editimageview.setImageBitmap(rotated)
        }

        _confirmImage.setOnClickListener{
            val intent = Intent(this, Save_ShareActivity::class.java)
            intent.data = getImageUri(_editimageview.drawToBitmap())
            startActivity(intent)
        }
    }
    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("temp", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    private fun openColorPickerDialogue() {
        val colorPickerDialogue = AmbilWarnaDialog(this, mDefaultColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {

                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    mDefaultColor = color
                    paint.color = mDefaultColor
                }
            })
        colorPickerDialogue.show()
    }
}

    
