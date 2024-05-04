package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentAddFinancialGoalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Calendar


class AddFinancialGoalFragment : Fragment() {
    private var _binding: FragmentAddFinancialGoalBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var dbHelper: DatabaseHelper
    private var selectedDate: String? = null

    private var imageByte: ByteArray? = null



    private val PICK_IMAGE_REQUEST = 2 // Resim seçim isteği kodu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        dbHelper = DatabaseHelper(requireContext()) // Başlat



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFinancialGoalBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        var userCurrency = userData?.currency
        userCurrency?.let { currency ->
            val currencyIndex = getIndexOfCurrencyInSpinner(currency)
            binding.currencySpinner.setSelection(currencyIndex)
        }


        createToggleButtonsForIncomeCategories()

        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.addButton.setOnClickListener{
            addFinancialGoal()
        }
        binding.imageAdd.setOnClickListener{
            openGallery()
        }


    }


    // Örnek olarak bir Fragment sınıfı içinde

    // Eklemek için tıklandığında çağrılan bir fonksiyon
    fun addFinancialGoal() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val title = binding.titleEditText.text.toString()
        val description = binding.editTextText.text.toString()
        val amount = binding.amountEditText.text.toString().toDouble()
        val deadline = binding.dateTextView.text.toString()
        val categoryId = getSelectedCategoryId()
        val percentage = binding.percentageEditTextNumber.text.toString().toInt()
        val currency = binding.currencySpinner.selectedItem.toString()
        val userCurrency = getUserCurrency()
        val navController = Navigation.findNavController(requireView())

        // Kullanıcının belirlediği para birimine dönüştürme işlemi
        val targetAmountInUserCurrency = if (currency == userCurrency) {
            amount // Eğer para birimi aynı ise dönüştürme işlemi yapma
        } else {
            // Para birimini USD'ye dönüştürme
            val exchangeRateToUSD = dbHelper.getExchangeRate(currency)
            val amountInUSD = amount / exchangeRateToUSD

            // USD'yi kullanıcının belirlediği para birimine dönüştürme
            val exchangeRateToUserCurrency = dbHelper.getExchangeRate(userCurrency)
            amountInUSD * exchangeRateToUserCurrency
        }

        // Yeni bir FinancialGoal nesnesi oluştur
        val newGoal = FinancialGoal(
            id = 0,
            userId = userId,
            title = title,
            description = description,
            targetAmount = targetAmountInUserCurrency, // Dönüştürülmüş hedef miktarını ata
            currentAmount = 0.0,
            deadline = deadline,
            createdAt = "", // Bu alan veritabanında otomatik ayarlanacağı için boş geçilebilir
            categoryId = categoryId,
            percentage = percentage,
            photo = imageByte,
            currency = userCurrency // Kullanıcının belirlediği para birimini kullan
        )

        // Yeni finansal hedefi veritabanına ekleyin
        val dbHelper = DatabaseHelper(requireContext())
        val success = dbHelper.addFinancialGoal(newGoal)

        if (success) {
            Toast.makeText(requireContext(), "Financial goal successfully added!", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_addFinancialGoalFragment_to_financialGoalFragment)
        } else {
            Toast.makeText(requireContext(), "Failed to add financial goal", Toast.LENGTH_SHORT).show()
        }
    }



    private fun getSelectedCategory(): String {
        val selectedButtonId = binding.toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun getSelectedCategoryId(): Int {
        val selectedCategoryName = getSelectedCategory()
        val dbHelper = DatabaseHelper(requireContext())
        val  selectedCategoryId =   dbHelper.getIncomeCategoryIdByCategoryName(selectedCategoryName)
        return selectedCategoryId
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Tarihi doğru formatta ayarlayın
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                binding.dateTextView.text = selectedDate // Seçilen tarihi bir TextView'a yazdırın
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun createToggleButtonsForIncomeCategories() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val incomeCategories = dbHelper.getAllIncomeCategoriesByUserId(userId)

        for (category in incomeCategories) {
            val button = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.text = category
            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            button.setOnClickListener {

            }

            // ToggleGroup'a butonları ekleme işlemi
            binding.toggleButtonGroup.addView(button)
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

            }
        }
    }
    private fun getIndexOfCurrencyInSpinner(currency: String): Int {
        val spinnerAdapter = binding.currencySpinner.adapter
        val count = spinnerAdapter.count
        for (i in 0 until count) {
            if (spinnerAdapter.getItem(i).toString() == currency) {
                return i
            }
        }
        return 0 // Varsayılan olarak 0. indeksi döndür
    }
    private fun getUserCurrency(): String {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        return currentUserEmail?.let { dbHelper.getUserData(it)?.currency } ?: "USD" // Varsayılan olarak USD kullan
    }

}