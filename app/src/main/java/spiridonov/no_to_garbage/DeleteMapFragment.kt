package spiridonov.no_to_garbage



import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class DeleteMapFragment : Fragment() {
    val CAMERA_CODE = 0
    val GALLERY_CODE = 1
    val Items = arrayOf("Камера", "Галерея")
    lateinit var img: ImageView
    lateinit var AddBtn: Button
    lateinit var uploadBtn: Button
    var bitmapImage: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_delete_map, container, false)
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
        val spinner = root.findViewById<Spinner>(R.id.spinnerImage)
        uploadBtn = root.findViewById<Button>(R.id.btnUploadImage)
        AddBtn = root.findViewById<Button>(R.id.btnAddImage)
        var category = allGarbage[0]
        img = root.findViewById(R.id.imageView2)


        val adaptermain: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                allGarbage
            )
        adaptermain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adaptermain
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View, selectedItemPosition: Int, selectedId: Long
            ) {


                category = allGarbage[selectedItemPosition]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        uploadBtn.setOnClickListener {
            val storageRef = FirebaseStorage.getInstance().reference
            val random = (1000000..9999999).random()
            val imageRef = storageRef.child("Garbage/$category").child(random.toString())
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

                    val pDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                    pDialog.progressHelper.barColor = Color.parseColor("#264599")
                    pDialog.titleText = "Вы успешно добавили Фотографию"
                    pDialog.contentText = "Спасибо, что делаете приложение лучше!"
                    pDialog.confirmText = "Готово"
                    pDialog.progressHelper.rimColor = Color.parseColor("#264599")
                    pDialog.setCancelable(false)
                    pDialog.setConfirmClickListener {
                        uploadBtn.isEnabled = false
                        pDialog.dismiss()
                    }
                    pDialog.progressHelper.spin()
                    pDialog.show()
                }

            }

        }



        AddBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
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


        return root
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
                            context?.contentResolver?.openInputStream(imageUri!!)
                        bitmapImage = BitmapFactory.decodeStream(imageStream)

                    } catch (e: FileNotFoundException) {
                        //e.getMessage()
                    }
                }
            }
        }
        if (bitmapImage != null) {
            img.setImageBitmap(bitmapImage)
            uploadBtn.isEnabled = true
        }


        super.onActivityResult(requestCode, resultCode, data)
    }

}



