package spiridonov.no_to_garbage.mainMenu


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_image.*
import spiridonov.no_to_garbage.R
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class AddImageActivity : AppCompatActivity() {
    val CAMERA_CODE = 0
    val GALLERY_CODE = 1
    val Items = arrayOf("Камера", "Галерея")
    var bitmapImage: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_image)
        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val allGarbage = arrayOf(
            getString(R.string.BTN_Jars),
            getString(R.string.BTN_Bottles),
            getString(R.string.BTN_Сontainers),
            getString(R.string.BTN_Box),
            getString(R.string.BTN_GoodClothes),
            getString(R.string.BTN_BadClothes),
            getString(R.string.BTN_Battery),
            getString(R.string.BTN_Paper),
            getString(R.string.BTN_Technic)
        )
        var category = allGarbage[0]


        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                allGarbage
            )
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerImage.adapter = adaptermain
        spinnerImage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {


                category = allGarbage[selectedItemPosition]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        btnUploadImage.setOnClickListener {
            val storageRef = FirebaseStorage.getInstance().reference
            val random = (1000000..9999999).random()

            val imageRef = storageRef.child("Garbage/$category").child("$random.jpeg")
            val baos = ByteArrayOutputStream()
            val bitmap = bitmapImage
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d("addOnSuccessListener", taskSnapshot.metadata.toString())

                    val pDialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    pDialog.progressHelper.barColor = Color.parseColor("#264599")
                    pDialog.titleText = "Вы успешно добавили Фотографию"
                    pDialog.contentText = "Спасибо, что делаете приложение лучше!"
                    pDialog.confirmText = "Готово"
                    pDialog.progressHelper.rimColor = Color.parseColor("#264599")
                    pDialog.setCancelable(false)
                    pDialog.setConfirmClickListener {
                        btnUploadImage.isEnabled = false
                        pDialog.dismiss()
                    }
                    pDialog.progressHelper.spin()
                    pDialog.show()
                }

            }

        }



        btnAddImage.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Выбор фотографии")
            builder.setItems(Items) { _, which ->
                if (Items[which] == "Камера") {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAMERA_CODE)
                } else if (Items[which] == "Галерея") {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryIntent.type = "image/*"
                    galleryIntent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(galleryIntent, GALLERY_CODE)
                }
            }
            builder.show()


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            0 -> {
                val bundle: Bundle? = data?.extras
                bitmapImage = bundle?.get("data") as Bitmap?

            }
            1 -> {
                if (data != null) {
                    try {
                        val imageUri = data.data
                        val imageStream: InputStream? =
                            contentResolver?.openInputStream(imageUri!!)
                        bitmapImage = BitmapFactory.decodeStream(imageStream)

                    } catch (e: FileNotFoundException) {
                        //e.getMessage()
                    }
                }
            }
        }
        if (bitmapImage != null) {
            imageView2.setImageBitmap(bitmapImage)
            btnUploadImage.isEnabled = true
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}



