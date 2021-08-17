package com.example.vacationproject2.main

data class RecyclerData(
    val user: String = "",
    val contents: String = "",
    val keyword: List<String> = listOf(),
    val postNumber: Int = 0,
    val comment: HashMap<String,String> = hashMapOf()
    )
