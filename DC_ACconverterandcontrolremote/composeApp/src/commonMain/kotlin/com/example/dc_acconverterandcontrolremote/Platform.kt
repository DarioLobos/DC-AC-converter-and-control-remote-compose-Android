package com.example.dc_acconverterandcontrolremote

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform