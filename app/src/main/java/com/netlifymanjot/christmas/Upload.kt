package com.netlifymanjot.christmas

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class Upload : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var uploadButton: Button
    private lateinit var submitButton: Button
    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null

    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // Initialize views
        nameEditText = findViewById(R.id.NameOfImageEditText)
        uploadButton = findViewById(R.id.AddImage)
        submitButton = findViewById(R.id.SubmitButton)
        imageView = findViewById(R.id.imagePreview)

        uploadButton.setOnClickListener { openGallery() }
        submitButton.setOnClickListener { handleSubmit() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun handleSubmit() {
        val name = nameEditText.text.toString()
        if (name.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please provide a name and upload an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload the image to Firebase Storage
        val storageRef = storage.reference.child("uploads/${UUID.randomUUID()}")
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save metadata in Realtime Database
                    saveToDatabase(name, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToDatabase(name: String, imageUrl: String) {
        val databaseRef = database.reference.child("images").push()
        val data = mapOf(
            "name" to name,
            "url" to imageUrl
        )
        databaseRef.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save metadata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
            imageView.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }
}
