package com.example.kelompok4.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.kelompok4.R
import com.example.kelompok4.ui.jadwal.Jadwal
import com.example.kelompok4.ui.jadwal.JadwalAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RiwayatFragment : Fragment() {

    private lateinit var adapterUpcoming: JadwalAdapter
    private lateinit var adapterPast: JadwalAdapter
    private lateinit var db: FirebaseFirestore

    private val listUpcoming = mutableListOf<Jadwal>()
    private val listPast = mutableListOf<Jadwal>()
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        val adapter = RiwayatPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "AKAN DATANG" else "SELESAI"
        }.attach()
    }

    private fun ambilDataRiwayat() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())

        db.collection("JadwalSewa")
            .get()
            .addOnSuccessListener { result ->
                listUpcoming.clear()
                listPast.clear()

                for (doc in result) {
                    val tanggal = doc.getString("tanggal") ?: continue
                    val jadwal = Jadwal(
                        nama = doc.getString("nama") ?: "",
                        jamMulai = doc.getString("jamMulai") ?: "",
                        jamSelesai = doc.getString("jamSelesai") ?: "",
                        fasilitas = doc.getString("fasilitas") ?: "",
                        tanggal = tanggal
                    )

                    if (tanggal >= today) {
                        listUpcoming.add(jadwal)
                    } else {
                        listPast.add(jadwal)
                    }
                }

                adapterUpcoming.notifyDataSetChanged()
                adapterPast.notifyDataSetChanged()
            }
    }
}
