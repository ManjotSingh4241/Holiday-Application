package com.netlifymanjot.christmas

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.flask.colorpicker.ColorPickerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.view.ScaleGestureDetector
import java.io.File
import java.io.FileOutputStream

class CreateCards : AppCompatActivity() {

    private lateinit var cardCanvas: FrameLayout
    private val REQUEST_CODE_IMAGE = 101
    private val REQUEST_CODE_BACKGROUND_IMAGE = 102
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cards)

        cardCanvas = findViewById(R.id.cardCanvas)
        val btnChangeBackground = findViewById<ImageButton>(R.id.btnChangeBackground)
        val btnAddText = findViewById<ImageButton>(R.id.btnAddText)
        val btnAddImage = findViewById<ImageButton>(R.id.btnAddImage)
        val btnSave = findViewById<ImageButton>(R.id.btnSave)
        val btnAddSticker = findViewById<ImageButton>(R.id.btnAddSticker)

        // Change Background
        btnChangeBackground.setOnClickListener {
            openBackgroundOptionsDialog()
        }

        // Add Text
        btnAddText.setOnClickListener {
            val textView = TextView(this).apply {
                text = "Double-tap to edit"
                textSize = 18f
                setTextColor(Color.BLACK)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Ensure the TextView is clickable
            textView.isClickable = true

            // Enable drag-and-drop functionality
            textView.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Store the initial touch position
                        v.tag = Pair(event.rawX, event.rawY)
                    }

                    MotionEvent.ACTION_MOVE -> {
                        // Move the view
                        v.x = event.rawX - v.width / 2
                        v.y = event.rawY - v.height / 2
                    }

                    MotionEvent.ACTION_UP -> {
                        // Detect a click by comparing the initial and final touch positions
                        val (startX, startY) = v.tag as Pair<Float, Float>
                        if (Math.abs(startX - event.rawX) < 10 && Math.abs(startY - event.rawY) < 10) {
                            v.performClick() // Trigger the click event
                        }
                    }
                }
                true
            }


            // Add click listener to open the edit dialog
            textView.setOnClickListener {
                Toast.makeText(this, "TextView clicked", Toast.LENGTH_SHORT).show() // Debugging
                openTextEditDialog(textView)
            }

            // Add the TextView to the canvas
            cardCanvas.addView(textView)
        }

        // Add Image
        btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }

        // Add Sticker
        btnAddSticker.setOnClickListener {
            openStickerPickerDialog()
        }

        // Save Canvas
        btnSave.setOnClickListener {
            val bitmap =
                Bitmap.createBitmap(cardCanvas.width, cardCanvas.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            cardCanvas.draw(canvas)

            val savedUri = saveImageToGallery(bitmap)
            if (savedUri != null) {
                Toast.makeText(this, "Card saved to gallery", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save card", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "card_${System.currentTimeMillis()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ChristmasCards")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = contentResolver
        val uri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                    return uri
                }
            }
            contentResolver.delete(it, null, null)
        }

        return null
    }


    private fun openBackgroundOptionsDialog() {
        val options = arrayOf("Add a Photo", "Choose a Color")
        MaterialAlertDialogBuilder(this).setTitle("Change Background")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectBackgroundPhoto()
                    1 -> selectBackgroundColor()
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun selectBackgroundPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_BACKGROUND_IMAGE)
    }

    private fun selectBackgroundColor() {
        val colors = arrayOf(
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#673AB7",
            "#3F51B5",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#8BC34A",
            "#CDDC39",
            "#FFEB3B",
            "#FFC107",
            "#FF9800",
            "#FF5722"
        )

        val dialogView = layoutInflater.inflate(R.layout.dialog_material_color_picker, null)
        val colorPickerContainer = dialogView.findViewById<LinearLayout>(R.id.colorPickerContainer)
        val applyButton = dialogView.findViewById<Button>(R.id.applyButton)

        var selectedColor: String? = null

        colors.forEach { color ->
            val colorView = View(this).apply {
                setBackgroundColor(Color.parseColor(color))
                layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                    setMargins(16, 16, 16, 16)
                }
                setOnClickListener {
                    selectedColor = color
                    // Optional: Highlight selected color
                    colorPickerContainer.children.forEach { child -> child.alpha = 1.0f }
                    this.alpha = 0.5f
                }
            }
            colorPickerContainer.addView(colorView)
        }

        val dialog =
            MaterialAlertDialogBuilder(this).setTitle("Pick a Background Color").setView(dialogView)
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.create()

        applyButton.setOnClickListener {
            selectedColor?.let { color ->
                cardCanvas.setBackgroundColor(Color.parseColor(color))
            } ?: Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> {
                    val uri = data?.data
                    val imageView = ImageView(this).apply {
                        setImageURI(uri)
                        layoutParams = FrameLayout.LayoutParams(300, 300) // Default size
                    }

                    // Initialize scaling and dragging variables
                    var scaleFactor = 1.0f
                    var lastX = 0f
                    var lastY = 0f
                    var isDragging = false

                    // Gesture detector for pinch-to-zoom
                    val scaleGestureDetector = ScaleGestureDetector(this,
                        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                // Incrementally adjust the scale factor
                                scaleFactor *= detector.scaleFactor

                                // Clamp scale factor between 0.5 and 3.0 (reset to initial size if zoomed out too much)
                                scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)

                                // Apply scaling to the ImageView
                                imageView.scaleX = scaleFactor
                                imageView.scaleY = scaleFactor

                                return true
                            }
                        })

                    // Touch listener for dragging and zooming
                    imageView.setOnTouchListener { v, event ->
                        scaleGestureDetector.onTouchEvent(event)

                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                // Store initial touch position for dragging
                                lastX = event.rawX
                                lastY = event.rawY
                                isDragging = true
                            }

                            MotionEvent.ACTION_MOVE -> {
                                if (event.pointerCount == 1 && isDragging) {
                                    // Handle drag movement
                                    val dx = event.rawX - lastX
                                    val dy = event.rawY - lastY
                                    v.x += dx
                                    v.y += dy
                                    lastX = event.rawX
                                    lastY = event.rawY
                                }
                            }

                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                isDragging = false
                            }
                        }
                        true
                    }

                    cardCanvas.addView(imageView)
                }

                REQUEST_CODE_BACKGROUND_IMAGE -> {
                    val uri = data?.data
                    val backgroundImageView = ImageView(this).apply {
                        setImageURI(uri)
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                    cardCanvas.removeAllViews()
                    cardCanvas.addView(backgroundImageView)
                }
            }
        }
    }


    private fun openTextEditDialog(textView: TextView) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_edit_text)
        dialog.setTitle("Edit Text")

        // Find views in the dialog layout
        val editText = dialog.findViewById<EditText>(R.id.editTextContent)
        val sizeSeekBar = dialog.findViewById<SeekBar>(R.id.sizeSeekBar)
        val colorButton = dialog.findViewById<Button>(R.id.btnColorPicker)
        val btnApply = dialog.findViewById<Button>(R.id.btnApply)

        // Ensure views are not null
        if (editText == null || sizeSeekBar == null || colorButton == null || btnApply == null) {
            Toast.makeText(
                this, "Dialog layout views not properly initialized.", Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Set initial values
        editText.setText(textView.text)
        val initialSizeInSp = textView.textSize / resources.displayMetrics.scaledDensity
        sizeSeekBar.max = 50 // Maximum font size
        sizeSeekBar.progress = initialSizeInSp.toInt()

        // Handle SeekBar changes for font size adjustment
        sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.textSize = progress.toFloat() // Update font size dynamically
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Handle color picker
        colorButton.setOnClickListener {
            openColorPickerDialog { selectedColor ->
                textView.setTextColor(selectedColor)
            }
        }


        // Apply changes to TextView
        btnApply.setOnClickListener {
            textView.text = editText.text.toString()
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }

    private fun openColorPickerDialog(onColorSelected: (Int) -> Unit) {
        val colors = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.BLACK,
            Color.WHITE
        )
        val colorNames =
            listOf("Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Black", "White")

        MaterialAlertDialogBuilder(this).setTitle("Pick a Color")
            .setItems(colorNames.toTypedArray()) { _, which ->
                val selectedColor = colors[which]
                // Show the color preview (if you added it to your layout)
                findViewById<View>(R.id.colorPreview)?.setBackgroundColor(selectedColor)
                onColorSelected(selectedColor)
            }.setNegativeButton("Cancel", null).show()
    }


    private fun openStickerPickerDialog() {
        val stickers = listOf(
            R.drawable.sticker_star, // Example stickers (Replace with your actual sticker resources)
            R.drawable.sticker_heart,
            R.drawable.sticker_snowflake,
            R.drawable.sticker_fox,
            R.drawable.sticker_reindeer,
            R.drawable.sticker_rabbit,
            R.drawable.stcker_rabbit,
            R.drawable.sticker_santa_claus,
            R.drawable.sticker_santa_claus_1,
            R.drawable.sticker_santa_claus_2,
            R.drawable.sticker_birthday,
            R.drawable.sticker_gift,
            R.drawable.sticker_gift_1,
            R.drawable.sticker_gift_2,
            R.drawable.sticker_cupcake,
            R.drawable.sticker_christmas_tree,
            R.drawable.stickerflight

        )
        val stickerNames = listOf(
            "Star",
            "Heart",
            "Snowflake",
            "Fox",
            "Reindeer",
            "Rabbit",
            "Rabbit-1",
            "Santa Claus",
            "Santa Claus-1",
            "Santa Claus-2",
            "Birthday",
            "Gift",
            "Gift-1",
            "Gift-2",
            "Cupcake",
            "Christmas Tree",
            "Holiday Flight"
        )

        MaterialAlertDialogBuilder(this).setTitle("Choose a Sticker")
            .setItems(stickerNames.toTypedArray()) { _, which ->
                addStickerToCanvas(stickers[which])
            }.setNegativeButton("Cancel", null).show()
    }

    private fun addStickerToCanvas(stickerResId: Int) {
        val imageView = ImageView(this).apply {
            setImageResource(stickerResId)
            layoutParams = FrameLayout.LayoutParams(200, 200) // Default size for stickers
        }

        imageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    v.x = event.rawX - v.width / 2
                    v.y = event.rawY - v.height / 2
                }
            }
            true
        }

        cardCanvas.addView(imageView)
    }


}