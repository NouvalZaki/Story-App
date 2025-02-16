package com.example.submisionstoryapp.api

import com.google.gson.annotations.SerializedName

data class RegistResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
