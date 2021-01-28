package spiridonov.no_to_garbage


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.FileNotFoundException
import java.io.InputStream


class DeleteMapFragment : Fragment() {
    val CAMERA_CODE = 0
    val GALLERY_CODE = 1
    val Items = arrayOf("Camera", "Gallery")
    lateinit var img: ImageView
    lateinit var bitmapImage: Bitmap
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
        val uploadBtn = root.findViewById<Button>(R.id.btnUploadImage)
        val AddBtn = root.findViewById<Button>(R.id.btnAddImage)
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


        AddBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Title")
            builder.setItems(Items) { _, which ->
                if (Items[which] == "Camera") {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    Log.i("CameraCode", "" + CAMERA_CODE)
                    startActivityForResult(cameraIntent, CAMERA_CODE)
                } else if (Items[which].equals("Gallery")) {
                    Log.i("GalleryCode", "" + GALLERY_CODE)
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
                Log.i("CameraCode", "" + CAMERA_CODE)
                val bundle: Bundle? = data?.extras
                bitmapImage = bundle?.get("data") as Bitmap
                img.setImageBitmap(bitmapImage)

            }
            1 -> {
                Log.i("GalleryCode", "" + requestCode)

                if (data != null) {
                    try {

                        val imageUri = data.data
                        val imageStream: InputStream? =
                            context?.contentResolver?.openInputStream(imageUri!!)
                        bitmapImage = BitmapFactory.decodeStream(imageStream)
                        img.setImageBitmap(bitmapImage)
                    } catch (e: FileNotFoundException) {
                        //e.getMessage()
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


}



