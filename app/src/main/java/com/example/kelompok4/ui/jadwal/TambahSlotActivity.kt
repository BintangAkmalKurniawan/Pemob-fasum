package com.example.kelompok4.ui.jadwal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.kelompok4.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TambahSlotActivity : AppCompatActivity() {

    private lateinit var inputNama: EditText
    private lateinit var dateDisplay: TextView
    private lateinit var datePick: ImageView
    private lateinit var btnJamMulai: Button
    private lateinit var btnJamSelesai: Button
    private lateinit var spinnerFasilitas: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: Button

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_slot)

        // Inisialisasi View
        inputNama = findViewById(R.id.edit_nama)
        dateDisplay = findViewById(R.id.txt_tanggal)
        datePick = findViewById(R.id.icon_calendar)
        btnJamMulai = findViewById(R.id.btn_jam_mulai)
        btnJamSelesai = findViewById(R.id.btn_jam_selesai)
        spinnerFasilitas = findViewById(R.id.spinner_fasilitas)
        btnSimpan = findViewById(R.id.btn_tambah_slot)
        btnBack = findViewById(R.id.btn_kembali)

        updateDateDisplay()

        // Spinner Fasilitas
        val fasilitasOptions = arrayOf("Lapangan Badminton", "Lapangan Futsal", "Lapangan Billiard")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fasilitasOptions)
        spinnerFasilitas.adapter = adapter

        // Date Picker
        datePick.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    updateDateDisplay()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Jam Mulai
        btnJamMulai.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val waktu = String.format("%02d:%02d", hourOfDay, minute)
                btnJamMulai.text = "Jam Mulai: $waktu"
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Jam Selesai
        btnJamSelesai.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val waktu = String.format("%02d:%02d", hourOfDay, minute)
                btnJamSelesai.text = "Jam Selesai: $waktu"
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Tombol Simpan
        btnSimpan.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val nama = inputNama.text.toString().trim()
            val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            val jamMulai = btnJamMulai.text.toString().replace("Jam Mulai: ", "").trim()
            val jamSelesai = btnJamSelesai.text.toString().replace("Jam Selesai: ", "").trim()
            val fasilitas = spinnerFasilitas.selectedItem.toString()

            if (nama.isNotEmpty() && jamMulai.isNotEmpty() && jamSelesai.isNotEmpty()) {
                // Cek apakah sudah ada data bentrok dengan fasilitas & waktu yang sama
                db.collection("JadwalSewa")
                    .whereEqualTo("tanggal", tanggal)
                    .whereEqualTo("jamMulai", jamMulai)
                    .whereEqualTo("fasilitas", fasilitas) // hanya yang fasilitas sama
                    .get()
                    .addOnSuccessListener { result ->
                        if (result.isEmpty) {
                            // Tidak bentrok, boleh simpan
                            val data = hashMapOf(
                                "nama" to nama,
                                "tanggal" to tanggal,
                                "jamMulai" to jamMulai,
                                "jamSelesai" to jamSelesai,
                                "fasilitas" to fasilitas
                            )

                            db.collection("JadwalSewa")
                                .add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Slot berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal menambahkan slot", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            val penyewa = result.documents.first().getString("nama") ?: "orang lain"
                            Toast.makeText(
                                this,
                                "Slot jam $jamMulai untuk $fasilitas sudah dipakai oleh $penyewa",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal mengecek slot bentrok", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateDateDisplay() {
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        dateDisplay.text = formatter.format(selectedDate.time)
    }
}
