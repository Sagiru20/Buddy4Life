package com.buddy4life.model

import com.google.gson.annotations.SerializedName

data class Breed(
    @SerializedName("id")
    val id: Int,

    @SerializedName("breedName")
    val breedName: String,

    @SerializedName("breedType")
    val breedType: String,

    @SerializedName("breedDescription")
    val breedDescription: String,

    @SerializedName("furColor")
    val furColor: String,

    @SerializedName("origin")
    val origin: String,

    @SerializedName("minHeightInches")
    val minHeightInches: Double,

    @SerializedName("maxHeightInches")
    val maxHeightInches: Double,

    @SerializedName("minWeightPounds")
    val minWeightPounds: Double,

    @SerializedName("maxWeightPounds")
    val maxWeightPounds: Double,

    @SerializedName("minLifeSpan")
    val minLifeSpan: Int,

    @SerializedName("maxLifeSpan")
    val maxLifeSpan: Int,

    @SerializedName("imgThumb")
    val imgThumb: String,

    @SerializedName("imgSourceURL")
    val imgSourceURL: String,

    @SerializedName("imgAttribution")
    val imgAttribution: String,

    @SerializedName("imgCreativeCommons")
    val imgCreativeCommons: Boolean
)
