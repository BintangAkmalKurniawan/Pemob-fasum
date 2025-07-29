package com.example.kelompok4.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kelompok4.DefaultActivity // Ganti jika nama Activity login Anda berbeda
import com.example.kelompok4.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth // DITAMBAHKAN: Import Firebase Auth

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    // DITAMBAHKAN: Deklarasi variabel untuk Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)

        // DITAMBAHKAN: Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tombol kembali ke halaman sebelumnya
        binding.btnKembali.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // DIUBAH: Tombol keluar menjadi Sign Out
        binding.btnKeluar.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), DefaultActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}