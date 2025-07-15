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

class AkanDatangFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JadwalAdapter
    private val list = mutableListOf<Jadwal>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Calendar.getInstance()

        db.collection("JadwalSewa")
            .get()
            .addOnSuccessListener { result ->
                list.clear()

                for (doc in result) {
                    val tanggalStr = doc.getString("tanggal") ?: continue
                    val jamSelesaiStr = doc.getString("jamSelesai") ?: continue

                    val jadwal = Jadwal(
                        nama = doc.getString("nama") ?: "",
                        jamMulai = doc.getString("jamMulai") ?: "",
                        jamSelesai = jamSelesaiStr,
                        fasilitas = doc.getString("fasilitas") ?: "",
                        tanggal = tanggalStr
                    )

                    val tanggalDate = sdfDate.parse(tanggalStr)

                    if (tanggalDate != null) {
                        val waktuSelesai = Calendar.getInstance().apply {
                            time = tanggalDate
                            val jamSelesai = sdfTime.parse(jamSelesaiStr)
                            if (jamSelesai != null) {
                                val calJam = Calendar.getInstance()
                                calJam.time = jamSelesai
                                set(Calendar.HOUR_OF_DAY, calJam.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, calJam.get(Calendar.MINUTE))
                            }
                        }

                        if (waktuSelesai.after(now)) {
                            list.add(jadwal)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }
}
