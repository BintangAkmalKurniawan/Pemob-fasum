package com.example.kelompok4.ui.jadwal

data class Jadwal(
    val nama: String = "",
    val tanggal: String ="",
    val jamMulai: String = "",
    val jamSelesai: String = "",
    val fasilitas: String = ""
) {
    val waktu: String
        get() = "$jamMulai - $jamSelesai"
}
