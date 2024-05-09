package com.mertyigit0.budgetmanager.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
            // AlertDialog oluştur
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Export Options")

            // Alert dialog içeriğini ayarla
            val view = layoutInflater.inflate(R.layout.export_dialog_layout, null)
            builder.setView(view)

            // Spinner ve RadioGroup referanslarını al
            val spinner = view.findViewById<Spinner>(R.id.spinner)
            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

            // Spinner için adapter oluştur ve ayarla
            val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
            spinner.adapter = adapter

            // Pozitif buton için tıklama olayını ayarla
            builder.setPositiveButton("Export") { dialog, _ ->
                // Seçilen ayı ve seçilen radio button'u al
                val selectedMonth = spinner.selectedItem.toString()
                val radioButtonId = radioGroup.checkedRadioButtonId
                val radioButton = view.findViewById<RadioButton>(radioButtonId)
                val selectedOption = radioButton.text.toString()

                // createExcelFile() fonksiyonunu çağır
                createExcelFile(selectedMonth, selectedOption)

                dialog.dismiss() // Dialog'u kapat
            }

            // Negatif buton için tıklama olayını ayarla
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Dialog'u kapat
            }

            // AlertDialog'u göster
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




    private fun createExcelFile(selectedMonth: String, selectedOption: String) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val userId = userData?.id

        // Kullanıcının seçtiği ay için gelir ve giderleri al
        val (incomes, expenses) = when (selectedOption) {
            "Incomes" -> {
                val monthIndex = getMonthIndex(selectedMonth)
                val totalIncomesForMonth =
                    userId?.let { dbHelper.getAllIncomesForMonth(it, monthIndex+1) }
                Pair(totalIncomesForMonth, emptyList())
            }
            "Expenses" -> {
                val monthIndex = getMonthIndex(selectedMonth)
                val totalExpensesForMonth =
                    userId?.let { dbHelper.getAllExpensesForMonth(it, monthIndex+1) }
                Pair(emptyList(), totalExpensesForMonth)
            }
            else -> {
                val totalIncomes = userId?.let { dbHelper.getAllIncomesByUserId(it) }
                val totalExpenses = userId?.let { dbHelper.getAllExpensesByUserId(it) }
                Pair(totalIncomes, totalExpenses)
            }
        }

        // Excel dosyasını oluştur
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")

        // Başlık satırını oluştur
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Amount")
        headerRow.createCell(1).setCellValue("Currency")
        headerRow.createCell(2).setCellValue("Category Name")
        headerRow.createCell(3).setCellValue("Date")
        headerRow.createCell(4).setCellValue("Note")

        // Verileri yazdır
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

        // Boş bir satır ekle
        val emptyRow = sheet.createRow(rowIndex++)
        for (i in 0 until 5) {
            emptyRow.createCell(i).setCellValue("")
        }

        if (expenses != null) {
            // Giderlerin başına "Expenses" yaz
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

        // Dosyayı kaydet
        // Dosya adını belirle
        val fileName = "transactions_${selectedOption.toLowerCase(Locale.ROOT)}_${selectedMonth.toLowerCase(Locale.ROOT)}.xlsx"

        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(storageDir, fileName)

        val fileOutputStream = FileOutputStream(file)
        workbook.write(fileOutputStream)
        fileOutputStream.close()
        workbook.close()

// Kullanıcıya dosyanın oluşturulduğunu bildir
        Toast.makeText(requireContext(), "Excel file created: ${file.absolutePath}", Toast.LENGTH_SHORT).show()

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