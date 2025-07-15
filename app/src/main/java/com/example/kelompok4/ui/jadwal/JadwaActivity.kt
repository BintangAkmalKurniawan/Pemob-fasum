package com.example.kelompok4.ui.jadwal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok4.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class JadwalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var jadwalAdapter: JadwalAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnPilihSlot: Button
    private lateinit var btnBack: Button
    private lateinit var header: TextView

    private val listJadwal: MutableList<Jadwal> = mutableListOf()
    private var fasilitasDipilih: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal)

        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()

        // View Binding
        recyclerView = findViewById(R.id.recycler_view_jadwal)
        btnPilihSlot = findViewById(R.id.btn_pilih_slot)
        btnBack = findViewById(R.id.btn_back)
        header = findViewById(R.id.header_judul)

        recyclerView.layoutManager = LinearLayoutManager(this)
        jadwalAdapter = JadwalAdapter(listJadwal)
        recyclerView.adapter = jadwalAdapter

        // Ambil data dari Intent
        fasilitasDipilih = intent.getStringExtra("fasilitas") ?: ""
        header.text = fasilitasDipilih

        // Ambil dan tampilkan data
        ambilDataJadwal()

        btnBack.setOnClickListener {
            finish()
        }

        btnPilihSlot.setOnClickListener {
            val intent = Intent(this, TambahSlotActivity::class.java)
            intent.putExtra("fasilitas", fasilitasDipilih)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        ambilDataJadwal()
    }

    private fun ambilDataJadwal() {
        firestore.collection("JadwalSewa")
            .whereEqualTo("fasilitas", fasilitasDipilih)
            .get()
            .addOnSuccessListener { result ->
                listJadwal.clear()
                for (document in result.documents) {
                    val jadwal = Jadwal(
                        nama = document.getString("nama") ?: "",
                        jamMulai = document.getString("jamMulai") ?: "",
                        tanggal = document.getString("tanggal")?: "",
                        jamSelesai = document.getString("jamSelesai") ?: "",
                        fasilitas = document.getString("fasilitas") ?: ""
                    )
                    listJadwal.add(jadwal)
                }

                if (listJadwal.isEmpty()) {
                    Toast.makeText(this, "Belum ada jadwal untuk $fasilitasDipilih", Toast.LENGTH_SHORT).show()
                }

                jadwalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Gagal memuat data", exception)
                Toast.makeText(this, "Gagal memuat data dari Firestore", Toast.LENGTH_SHORT).show()
            }
    }
}
