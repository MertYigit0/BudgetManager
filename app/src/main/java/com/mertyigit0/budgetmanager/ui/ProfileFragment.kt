package com.mertyigit0.budgetmanager.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import com.mertyigit0.budgetmanager.ImageHelper.Companion.PICK_IMAGE_REQUEST
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentProfileBinding
import com.mertyigit0.budgetmanager.databinding.FragmentSettingsBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale


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

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Export Options")


            val view = layoutInflater.inflate(R.layout.export_dialog_layout, null)
            builder.setView(view)


            val spinner = view.findViewById<Spinner>(R.id.spinner)
            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)


            val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
            spinner.adapter = adapter


            builder.setPositiveButton("Export") { dialog, _ ->

                val selectedMonth = spinner.selectedItem.toString()
                val radioButtonId = radioGroup.checkedRadioButtonId
                val radioButton = view.findViewById<RadioButton>(radioButtonId)
                val selectedOption = radioButton.text.toString()


               // createExcelFile(selectedMonth, selectedOption)
                //storeFirebase(selectedMonth, selectedOption )
                createAndStoreExcelFile(selectedMonth,selectedOption)

                dialog.dismiss() // Dialog'u kapat
            }


            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Dialog'u kapat
            }


            val dialog = builder.create()
            dialog.show()
        }

        binding.itemAccount.setOnClickListener{
            navController.navigate(R.id.action_profileFragment_to_regularTransactionsFragment)
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



        Glide.with(this)
            .load( if (userData != null) {
                userData.photo
            } else {
                userData?.photo ?: R.drawable.avatar2
            }
            )
            .into(binding.userImage)

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


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            val selectedImageUri = data.data
            selectedImageUri?.let { uri ->

                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val imageBytes = outputStream.toByteArray()

                imageByte = imageBytes



                val dbHelper = DatabaseHelper(requireContext())
                val currentUserEmail = auth.currentUser?.email
                val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
                val userid = userData?.id

                if (userid != null) {
                    dbHelper.addPhotoToUser(currentUserEmail,imageBytes)



                    Glide.with(this)
                        .load(imageBytes)
                        .into(binding.userImage)

                }
            }
        }











    }




    private fun createAndStoreExcelFile(selectedMonth: String, selectedOption: String) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val userId = userData?.id


        val (incomes, expenses) = when (selectedOption) {
            "Incomes" -> {
                val monthIndex = getMonthIndex(selectedMonth)
                val totalIncomesForMonth =
                    userId?.let { dbHelper.getAllIncomesForMonth(it, monthIndex + 1) }
                Pair(totalIncomesForMonth, emptyList())
            }
            "Expenses" -> {
                val monthIndex = getMonthIndex(selectedMonth)
                val totalExpensesForMonth =
                    userId?.let { dbHelper.getAllExpensesForMonth(it, monthIndex + 1) }
                Pair(emptyList(), totalExpensesForMonth)
            }
            else -> {
                val totalIncomes = userId?.let { dbHelper.getAllIncomesByUserId(it) }
                val totalExpenses = userId?.let { dbHelper.getAllExpensesByUserId(it) }
                Pair(totalIncomes, totalExpenses)
            }
        }


        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")


        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Amount")
        headerRow.createCell(1).setCellValue("Currency")
        headerRow.createCell(2).setCellValue("Category Name")
        headerRow.createCell(3).setCellValue("Date")
        headerRow.createCell(4).setCellValue("Note")


        var rowIndex = 1
        if (incomes != null) {
            for (income in incomes) {
                val row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(income.amount)
                row.createCell(1).setCellValue(income.currency)
                row.createCell(2).setCellValue(income.categoryName)
                row.createCell(3).setCellValue(income.date)
                row.createCell(4).setCellValue(income.note)
            }
        }


        val emptyRow = sheet.createRow(rowIndex++)
        for (i in 0 until 5) {
            emptyRow.createCell(i).setCellValue("")
        }

        if (expenses != null) {

            val expensesRow = sheet.createRow(rowIndex++)
            expensesRow.createCell(0).setCellValue("Expenses")
            expensesRow.createCell(1).setCellValue("")
            expensesRow.createCell(2).setCellValue("")
            expensesRow.createCell(3).setCellValue("")
            expensesRow.createCell(4).setCellValue("")
            for (expense in expenses) {
                val row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(expense.amount)
                row.createCell(1).setCellValue(expense.currency)
                row.createCell(2).setCellValue(expense.categoryName)
                row.createCell(3).setCellValue(expense.date)
                row.createCell(4).setCellValue(expense.note)
            }
        }

        val fileName = "transactions_${selectedOption.toLowerCase(Locale.ROOT)}_${selectedMonth.toLowerCase(Locale.ROOT)}.xlsx"

        // Dosyayı cihaza ve Firebase Storage'a yükle
        val storage = Firebase.storage
        val storageRef = storage.reference
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userFolderRef = storageRef.child("user_files/$currentUserUid")
        val fileRef = userFolderRef.child(fileName)

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, fileName)

        val fileOutputStream = FileOutputStream(file)
        workbook.write(fileOutputStream)
        fileOutputStream.close()
        workbook.close()


        val fileUri = Uri.fromFile(file)
        val uploadTask = fileRef.putFile(fileUri)


        uploadTask.addOnSuccessListener { taskSnapshot ->

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                println(downloadUrl)



                val sharedPreferences = requireContext().getSharedPreferences("DownloadUrls", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(fileName, downloadUrl)
                editor.apply()

                Toast.makeText(requireContext(), "Excel file uploaded to Firebase Storage. Download URL: $downloadUrl", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->

            }
        }.addOnFailureListener { exception ->

        }
    }






    private fun getMonthIndex(selectedMonth: String): Int {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return months.indexOf(selectedMonth)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}