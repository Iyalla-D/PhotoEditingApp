package com.example.photoeditor

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
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
    private var filterPaint = Paint()
    private var path = Path()
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f

    private lateinit var _editimageview: ImageView
    private lateinit var _zoomImage: ImageView
    private lateinit var _editOptions: HorizontalScrollView
    private lateinit var _filterList: HorizontalScrollView
    private lateinit var _drawingPen: ImageButton
    private lateinit var _rotateImage: ImageButton
    private lateinit var _confirmImage: ImageButton
    private lateinit var _filterImage: ImageButton
    private lateinit var _exitFilter: ImageButton
    private lateinit var _zoomButton: ImageButton
    private lateinit var _addText: ImageButton
    private lateinit var _textBox: EditText
    private lateinit var _flipImage: ImageButton
    private lateinit var _textBoxHint: TextView
    private lateinit var _forwardButton: ImageButton
    private lateinit var _backButton: ImageButton

    private var sentPhoto: Uri? = null

    private lateinit var original:ImageView
    private lateinit var oneIV:ImageView
    private lateinit var threeIV:ImageView
    private lateinit var fourIV:ImageView
    private lateinit var fiveIV:ImageView
    private lateinit var sixIV:ImageView
    private lateinit var eightIV:ImageView
    private lateinit var tenIV:ImageView

    private lateinit var  originalBitmap: Bitmap
    private lateinit var oneBitMap: Bitmap
    private lateinit var threeBitMap:Bitmap
    private lateinit var fourBitMap:Bitmap
    private lateinit var fiveBitMap:Bitmap
    private lateinit var sixBitMap:Bitmap
    private lateinit var eightBitMap:Bitmap
    private lateinit var tenBitMap:Bitmap
    private val bitmapArray = arrayOfNulls<Bitmap>(100)
    var bitmapArrayCount = 0
    var bitmapArrayCurrent = 0



    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        sentPhoto = intent.data
        _editimageview = findViewById(R.id.editimage)
        _editimageview.setImageURI(sentPhoto)

        _drawingPen = findViewById(R.id.drawButton)
        _rotateImage = findViewById(R.id.rotateImageButton)
        _confirmImage = findViewById(R.id.checkmark)
        _filterImage = findViewById(R.id.filterButton)
        _editOptions = findViewById(R.id.editOptions)
        _filterList = findViewById(R.id.filterList)
        _exitFilter = findViewById(R.id.exitFilter)
        _zoomButton = findViewById(R.id.zoomImageButton)
        _zoomImage = findViewById(R.id.zoomImage)
        _addText = findViewById(R.id.addText)
        _textBox = findViewById(R.id.inputText)
        _flipImage = findViewById(R.id.flipImage)
        _textBoxHint = findViewById(R.id.testText)
        _forwardButton = findViewById(R.id.next)
        _backButton = findViewById(R.id.previous)

        original = findViewById(R.id.idOriginal)
        oneIV = findViewById(R.id.idIVOne)
        threeIV = findViewById(R.id.idIVThree)
        fourIV = findViewById(R.id.idIVFour)
        fiveIV = findViewById(R.id.idIVFive)
        sixIV = findViewById(R.id.idIVSix)
        eightIV = findViewById(R.id.idIVEight)
        tenIV = findViewById(R.id.idIVTen)

        _editimageview.post{
            bitmapArray[0] = _editimageview.drawToBitmap()
            bitmapArrayCount++
        }

        _textBoxHint.text = getString(R.string.editboxHint)
        _textBoxHint.visibility=View.INVISIBLE
        _editimageview.isEnabled = false

        _forwardButton.setOnClickListener{
            _zoomImage.visibility = View.INVISIBLE
            _editimageview.visibility = View.VISIBLE
            if(bitmapArrayCurrent!=99&&bitmapArray[bitmapArrayCurrent+1].toString()!="null"){
                _editimageview.setImageBitmap(bitmapArray[bitmapArrayCurrent+1])
                bitmapArrayCurrent++
            }
        }

        _backButton.setOnClickListener{
            _zoomImage.visibility = View.INVISIBLE
            _editimageview.visibility = View.VISIBLE
            if(bitmapArrayCurrent!=0){
                _editimageview.setImageBitmap(bitmapArray[bitmapArrayCurrent-1])
                bitmapArrayCurrent--
            }
        }

        _flipImage.setOnClickListener{
            _editimageview.setImageBitmap(flipPhoto(_editimageview.drawToBitmap()))
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        _addText.setOnClickListener {
            if(_textBox.visibility== View.VISIBLE){
                _textBox.visibility= View.INVISIBLE
                _textBoxHint.visibility=View.INVISIBLE
            }else{
                _textBox.visibility= View.VISIBLE
                _editimageview.isEnabled = true
                _textBoxHint.visibility=View.VISIBLE
                _editimageview.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        // When the user clicks on the image, get the x and y coordinates of the click
                        val x = event.x
                        val y = event.y

                        val image =
                            _editimageview.drawToBitmap().copy(Bitmap.Config.ARGB_8888, true)
                        val textCanvas = Canvas(image)
                        val textPaint = Paint()
                        textPaint.color = Color.BLACK
                        textPaint.textSize = 100F
                        textCanvas.drawText(_textBox.text.toString(), x, y, textPaint)
                        _editimageview.setImageBitmap(image)
                        _textBox.visibility = View.INVISIBLE
                        _textBoxHint.visibility=View.INVISIBLE
                        _editimageview.isEnabled = false
                        addToBitmapArray(_editimageview.drawToBitmap())
                    }
                    true
                }
            }
        }

        _filterImage.setOnClickListener {
            _zoomImage.visibility = View.INVISIBLE
            _editimageview.visibility = View.VISIBLE
            _editOptions.visibility = View.INVISIBLE
            _filterList.visibility = View.VISIBLE
            _exitFilter.visibility = View.VISIBLE

            original.setImageBitmap(_editimageview.drawToBitmap())
            originalBitmap = _editimageview.drawToBitmap()

            oneBitMap = applyTintFilter(_editimageview.drawToBitmap(),Color.BLUE)
            oneIV.setImageBitmap(oneBitMap)

            threeBitMap = applySepiaFilter(_editimageview.drawToBitmap())
            threeIV.setImageBitmap(threeBitMap)

            fourBitMap = applySaturationFilter(_editimageview.drawToBitmap(),10F)
            fourIV.setImageBitmap(fourBitMap)

            fiveBitMap = applyVintageFilter(_editimageview.drawToBitmap())
            fiveIV.setImageBitmap(fiveBitMap)

            applyGrayScaleFilter()
            sixIV.setImageBitmap(sixBitMap)

            eightBitMap = applyContrastFilter(_editimageview.drawToBitmap(), 10F)
            eightIV.setImageBitmap(eightBitMap)

            tenBitMap = invertFilter(_editimageview.drawToBitmap())
            tenIV.setImageBitmap(tenBitMap)
            initializeOnCLickListeners()
        }

        _exitFilter.setOnClickListener{
            _editOptions.visibility = View.VISIBLE
            _filterList.visibility = View.INVISIBLE
            _exitFilter.visibility = View.INVISIBLE
        }

        _zoomButton.setOnClickListener {
            _zoomImage.setImageBitmap(_editimageview.drawToBitmap())
            _editimageview.visibility = View.INVISIBLE
            _zoomImage.visibility = View.VISIBLE
            //_editimageview.isEnabled = true
        }

        _drawingPen.setOnClickListener {
            if(_zoomImage.isVisible){
                _editimageview.setImageBitmap(_zoomImage.drawToBitmap())
            }
            _zoomImage.visibility = View.INVISIBLE
            _editimageview.visibility = View.VISIBLE
            _editimageview.isEnabled = true

            _editimageview.setOnTouchListener { _, event ->
                val bitmapCopy = if(_zoomImage.isVisible){
                    _zoomImage.drawToBitmap()
                } else{
                    _editimageview.drawToBitmap()
                }

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
                if(_zoomImage.isVisible){
                    _zoomImage.setImageBitmap(bitmapCopy)
                }
                else{
                    _editimageview.setImageBitmap(bitmapCopy)

                }

                true
            }
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        _rotateImage.setOnClickListener {
            _zoomImage.visibility = View.INVISIBLE
            _editimageview.visibility = View.VISIBLE
            val bitmap = (_editimageview.drawable as BitmapDrawable).bitmap
            val matrix = Matrix()
            matrix.postRotate(90F)
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            _editimageview.setImageBitmap(rotatedBitmap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        _confirmImage.setOnClickListener{
            val intent = Intent(this, Save_ShareActivity::class.java)
            MainActivity.dbManagerBase!!.insert(_editimageview.drawToBitmap())
            intent.data = getImageUri(_editimageview.drawToBitmap())
            startActivity(intent)
        }
    }

    private fun initializeOnCLickListeners() {
        original.setOnClickListener{
            _editimageview.setImageBitmap(originalBitmap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        oneIV.setOnClickListener {
            _editimageview.setImageBitmap(oneBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        threeIV.setOnClickListener {
            _editimageview.setImageBitmap(threeBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        fourIV.setOnClickListener {
            _editimageview.setImageBitmap(fourBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        fiveIV.setOnClickListener {
            _editimageview.setImageBitmap(fiveBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        sixIV.setOnClickListener {
            _editimageview.setImageBitmap(sixBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        eightIV.setOnClickListener {
            _editimageview.setImageBitmap(eightBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }

        tenIV.setOnClickListener {
            _editimageview.setImageBitmap(tenBitMap)
            addToBitmapArray(_editimageview.drawToBitmap())
        }
    }

    private fun addToBitmapArray(bitmap: Bitmap){
        bitmapArray[bitmapArrayCount] = bitmap
        bitmapArrayCount++
        bitmapArrayCurrent++
    }
    private fun invertFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Create a new Bitmap object to hold the inverted image data
        val invertedBitmap = Bitmap.createBitmap(width, height, bitmap.config)

        // Loop through all pixels in the image
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the pixel color at the current position
                val pixel = bitmap.getPixel(x, y)

                // Invert the colors by subtracting the R, G, and B values from 255
                val invertedR = 255 - Color.red(pixel)
                val invertedG = 255 - Color.green(pixel)
                val invertedB = 255 - Color.blue(pixel)

                // Set the inverted color for the current pixel in the new Bitmap object
                invertedBitmap.setPixel(x, y, Color.rgb(invertedR, invertedG, invertedB))
            }
        }
        return invertedBitmap
    }

    private fun applyContrastFilter(bitmap: Bitmap, contrast: Float): Bitmap{
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through all the pixels in the bitmap
        for (x in 0 until result.width) {
            for (y in 0 until result.height) {
                // Get the current pixel
                val pixel = result.getPixel(x, y)

                // Apply the contrast effect by adjusting the R, G, and B channels
                val red = adjustChannel(Color.red(pixel), contrast)
                val green = adjustChannel(Color.green(pixel), contrast)
                val blue = adjustChannel(Color.blue(pixel), contrast)
                result.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }

        // Return the resulting bitmap
        return result
    }

    private fun adjustChannel(channel: Int, contrast: Float): Int {
        // Clamp the contrast value to the range [-1, 1]
        val contrastClamped = contrast.coerceIn(-1f, 1f)

        // Calculate the adjusted channel value
        val adjusted = (channel - 128) * (1 + contrastClamped) + 128

        // Clamp the adjusted channel value to the range [0, 255]
        return adjusted.toInt().coerceIn(0, 255)
    }

    private fun applySepiaFilter(image: Bitmap): Bitmap {
        // Iterate over all pixels in the image
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                // Get the color of the current pixel
                val color = image.getPixel(x, y)
                // Apply the sepia filter by setting the red, green, and blue channels to weighted averages of their original values
                val r = (0.393 * Color.red(color) + 0.769 * Color.green(color) + 0.189 * Color.blue(color)).toInt()
                val g = (0.349 * Color.red(color) + 0.686 * Color.green(color) + 0.168 * Color.blue(color)).toInt()
                val b = (0.272 * Color.red(color) + 0.534 * Color.green(color) + 0.131 * Color.blue(color)).toInt()
                image.setPixel(x, y, Color.rgb(r, g, b))
            }
        }
        // Return the filtered image
        return image
    }

    private fun applySaturationFilter(bitmap: Bitmap, level: Float):Bitmap {
        // Create a mutable copy of the input image
        val image = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get the width and height of the image
        val width = image.width
        val height = image.height

        // Loop through all the pixels in the image
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the ARGB values for the pixel at (x, y)
                var pixel = image.getPixel(x, y)

                val a = Color.alpha(pixel)

                // Adjust the saturation of the pixel using the level provided
                val hsv = floatArrayOf(0f, 0f, 0f)
                Color.colorToHSV(pixel, hsv)
                hsv[1] = hsv[1] * level
                pixel = Color.HSVToColor(a, hsv)

                // Set the pixel at (x, y) to the adjusted value
                image.setPixel(x, y, pixel)
            }
        }
        // Return the modified image
        return image
    }

    private fun applyVintageFilter(bitmap: Bitmap):Bitmap {
        // Create a mutable copy of the input bitmap
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Loop through all the pixels in the bitmap
        for (x in 0 until result.width) {
            for (y in 0 until result.height) {
                // Get the current pixel
                val pixel = result.getPixel(x, y)

                // Apply a grayscale effect by setting the R, G, and B channels to the average of the R, G, and B channels
                val grayscale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                result.setPixel(x, y, Color.rgb(grayscale, grayscale, grayscale))
            }
        }
        return result
    }

        private fun applyTintFilter(bitmap: Bitmap, color: Int):Bitmap {
        // Create a mutable copy of the input image
        val image = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Get the width and height of the image
        val width = image.width
        val height = image.height

        // Loop through all the pixels in the image
        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the ARGB values for the pixel at (x, y)
                var pixel = image.getPixel(x, y)

                val a = Color.alpha(pixel)
                // Adjust the color of the pixel using the tint color provided
                val hsv = floatArrayOf(0f, 0f, 0f)
                Color.colorToHSV(pixel, hsv)
                val newHsv = floatArrayOf(0f, 0f, 0f)
                Color.colorToHSV(color, newHsv)
                hsv[0] = newHsv[0]
                hsv[1] = newHsv[1]
                pixel = Color.HSVToColor(a, hsv)

                // Set the pixel at (x, y) to the adjusted value
                image.setPixel(x, y, pixel)
            }
        }
        // Return the modified image
        return image
    }
    private fun applyGrayScaleFilter(){
        val colormatrix = ColorMatrix()
        colormatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colormatrix)
        filterPaint.colorFilter = filter
        sixBitMap = Bitmap.createBitmap(_editimageview.drawToBitmap())
        val filterCanvas = Canvas(sixBitMap)
        filterCanvas.drawBitmap(sixBitMap,0f,0f,filterPaint)
    }

    private fun flipPhoto(photo: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        return Bitmap.createBitmap(photo, 0, 0, photo.width, photo.height, matrix, true)
    }

    companion object{
        fun getImageUri(inImage: Bitmap): Uri {

            val tempFile = File.createTempFile("temp", ".jpeg")
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val bitmapData = bytes.toByteArray()

            val fileOutPut = FileOutputStream(tempFile)
            fileOutPut.write(bitmapData)
            fileOutPut.flush()
            fileOutPut.close()
            return Uri.fromFile(tempFile)
        }
    }
}
