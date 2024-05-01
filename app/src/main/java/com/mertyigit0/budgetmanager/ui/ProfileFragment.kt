package com.mertyigit0.budgetmanager.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.ImageHelper.Companion.PICK_IMAGE_REQUEST
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentProfileBinding
import com.mertyigit0.budgetmanager.databinding.FragmentSettingsBinding
import java.io.ByteArrayOutputStream


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





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}