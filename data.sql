CREATE DATABASE rumahsakit;
USE rumahsakit;

-- Tabel administrator (untuk login)
CREATE TABLE administrator (
    id_admin VARCHAR(20) PRIMARY KEY,
    password VARCHAR(50)
);

-- Insert admin default
INSERT INTO administrator VALUES ('admin', 'admin123');

-- Tabel pasien
CREATE TABLE pasien (
    id_pasien VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100),
    alamat TEXT,
    no_hp VARCHAR(15)
);

-- Tabel dokter
CREATE TABLE dokter (
    id_dokter VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100),
    spesialisasi VARCHAR(50)
);

-- Tabel obat
CREATE TABLE obat (
    id_obat VARCHAR(20) PRIMARY KEY,
    nama_obat VARCHAR(100),
    harga DOUBLE
);

-- Tabel daftar_berobat
CREATE TABLE daftar_berobat (
    id_daftar VARCHAR(20) PRIMARY KEY,
    id_pasien VARCHAR(20),
    id_dokter VARCHAR(20),
    tgl_daftar DATE,
    FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter)
);

-- Tabel periksa
CREATE TABLE periksa (
    id_periksa VARCHAR(20) PRIMARY KEY,
    id_daftar VARCHAR(20),
    diagnosa TEXT,
    tgl_periksa DATE,
    FOREIGN KEY (id_daftar) REFERENCES daftar_berobat(id_daftar)
);

-- Tabel resep
CREATE TABLE resep (
    id_resep VARCHAR(20) PRIMARY KEY,
    id_periksa VARCHAR(20),
    id_obat VARCHAR(20),
    jumlah INT,
    FOREIGN KEY (id_periksa) REFERENCES periksa(id_periksa),
    FOREIGN KEY (id_obat) REFERENCES obat(id_obat)
);
