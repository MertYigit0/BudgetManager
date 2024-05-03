package com.mertyigit0.budgetmanager.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.ImageHelper.Companion.PICK_IMAGE_REQUEST
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentProfileBinding
import com.mertyigit0.budgetmanager.databinding.FragmentSettingsBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val navController by lazy { Navigation.findNavController(requireView()) }

    private var imageByte: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        // FirebaseAuth örneğini al
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.itemExportData.setOnClickListener {
            createExcelFile()
        }


        binding.itemSettings.setOnClickListener{
            navController.navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.itemLogout.setOnClickListener{
            auth.signOut()
            redirectToLoginScreen()
        }

        binding.userImage.setOnClickListener{
            openGallery()


        }
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }


        // Resmi yükleme işlemi
        Glide.with(this)
            .load( if (userData != null) {
                userData.photo
            } else {
                userData?.photo ?: R.drawable.avatar2
            }
            ) // Yüklenecek resmin byte dizisi
            .into(binding.userImage) // Resmin yükleneceği ImageView

        displayUserData()

    }

    private fun redirectToLoginScreen() {
        navController.navigate(R.id.action_profileFragment_to_loginFragment)
    }

    fun displayUserData() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        if (userData != null) {
            binding.textView12.text = "Email: ${userData.email}"+"User ID: ${userData.id}"

        }
    }





    // Kullanıcının galerisini açma işlemi
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Kullanıcının galeriden resim seçtikten sonra geri dönüş işlemi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Seçilen resmi işle
            val selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                // URI'den Bitmap oluşturma
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                // Bitmap'i byte dizisine dönüştürme
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val imageBytes = outputStream.toByteArray()

                imageByte = imageBytes

                // SQLite veritabanına resmi kaydetme işlemi

                val dbHelper = DatabaseHelper(requireContext())
                val currentUserEmail = auth.currentUser?.email
                val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
                val userid = userData?.id

                if (userid != null) {
                    dbHelper.addPhotoToUser(currentUserEmail,imageBytes)


                    // Resmi yükleme işlemi
                    Glide.with(this)
                        .load(imageBytes) // Yüklenecek resmin byte dizisi
                        .into(binding.userImage) // Resmin yükleneceği ImageView

                }
            }
        }
    }



    private fun createExcelFile() {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Veriler")

        // Örnek veri listesi
        val data = listOf("Veri 1", "Veri 2", "Veri 3", "Veri 4")

        // Başlık satırı oluştur
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Sıra No")

        // Veri satırlarını oluştur
        for ((index, item) in data.withIndex()) {
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue((index + 1).toDouble())
            row.createCell(1).setCellValue(item)
        }

        // Dosyayı oluştur
        val file = File(requireContext().externalCacheDir?.absolutePath + "/veriler.xlsx")
        val fileOutputStream = FileOutputStream(file)
        workbook.write(fileOutputStream)
        fileOutputStream.close()
        workbook.close()



        Toast.makeText(requireContext(), "XML Dosya Olusturuldu ", Toast.LENGTH_SHORT).show()
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}