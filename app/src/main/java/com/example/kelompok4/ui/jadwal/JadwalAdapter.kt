package com.example.kelompok4.ui.jadwal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kelompok4.R

// Adapter RecyclerView
class JadwalAdapter(private val listJadwal: List<Jadwal>) :
    RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder>() {

    inner class JadwalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNama: TextView = itemView.findViewById(R.id.txt_nama)
        val txtWaktu: TextView = itemView.findViewById(R.id.txt_waktu)
        val txtFasilitas: TextView = itemView.findViewById(R.id.txt_fasilitas)
        val txtTanggal = itemView.findViewById<TextView>(R.id.txt_tanggal)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return JadwalViewHolder(view)
    }

    override fun onBindViewHolder(holder: JadwalViewHolder, position: Int) {
        val item = listJadwal[position]
        holder.txtNama.text = item.nama
        holder.txtWaktu.text = "${item.jamMulai} - ${item.jamSelesai}"
        holder.txtTanggal.text = item.tanggal
        holder.txtFasilitas.text = item.fasilitas
    }

    override fun getItemCount(): Int = listJadwal.size
}
