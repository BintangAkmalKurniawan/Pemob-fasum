package com.example.kelompok4.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok4.R
import com.example.kelompok4.ui.jadwal.Jadwal
import com.example.kelompok4.ui.jadwal.JadwalAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SelesaiFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JadwalAdapter
    private val list = mutableListOf<Jadwal>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_riwayat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = JadwalAdapter(list)
        recyclerView.adapter = adapter
        ambilData()
    }

    private fun ambilData() {
        val sdfTanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfWaktu = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()

        db.collection("JadwalSewa")
            .get()
            .addOnSuccessListener { result ->
                list.clear()
                for (doc in result) {
                    val tanggalStr = doc.getString("tanggal") ?: continue
                    val jamSelesaiStr = doc.getString("jamSelesai") ?: continue
                    val nama = doc.getString("nama") ?: ""
                    val fasilitas = doc.getString("fasilitas") ?: ""
                    val jamMulai = doc.getString("jamMulai") ?: ""

                    try {
                        // Parse tanggal dan jam selesai
                        val tanggal = sdfTanggal.parse(tanggalStr)
                        val jamSelesai = sdfWaktu.parse(jamSelesaiStr)

                        if (tanggal != null && jamSelesai != null) {
                            val waktuSelesai = Calendar.getInstance().apply {
                                time = tanggal
                                val jam = Calendar.getInstance().apply { time = jamSelesai }
                                set(Calendar.HOUR_OF_DAY, jam.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, jam.get(Calendar.MINUTE))
                                set(Calendar.SECOND, 0)
                            }


                            if (waktuSelesai.before(now)) {
                                val jadwal = Jadwal(
                                    nama = nama,
                                    fasilitas = fasilitas,
                                    jamMulai = jamMulai,
                                    jamSelesai = jamSelesaiStr,
                                    tanggal = tanggalStr
                                )
                                list.add(jadwal)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }


}
