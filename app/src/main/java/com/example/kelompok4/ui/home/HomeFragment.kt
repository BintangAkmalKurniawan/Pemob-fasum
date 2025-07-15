package com.example.kelompok4.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.kelompok4.R
import com.example.kelompok4.ui.jadwal.JadwalActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardBadminton = view.findViewById<CardView>(R.id.cardBadminton)
        val cardFutsal = view.findViewById<CardView>(R.id.cardFutsal)
        val cardBilliard = view.findViewById<CardView>(R.id.cardBilliard)

        cardBadminton.setOnClickListener {
            bukaJadwal("Lapangan Badminton")
        }

        cardFutsal.setOnClickListener {
            bukaJadwal("Lapangan Futsal")
        }

        cardBilliard.setOnClickListener {
            bukaJadwal("Lapangan Billiard")
        }
    }

    private fun bukaJadwal(fasilitas: String) {
        val intent = Intent(requireContext(), JadwalActivity::class.java)
        intent.putExtra("fasilitas", fasilitas)
        startActivity(intent)
    }
}
