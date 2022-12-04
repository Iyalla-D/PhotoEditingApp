package com.example.photoeditor

class RecyclerData(image: ByteArray) {
    private var image: ByteArray? = image
    fun getImage(): ByteArray? {
        return image
    }

    fun setImage(image: ByteArray) {
        this.image = image
    }

}