package com.example.travel.domain.repository

import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.model.review.GetReviewModel

interface ReviewRepository {

    // Network
    suspend fun createReview(token: String, review: CreateReviewModel)
    suspend fun getReviewListByUser(token: String): List<GetReviewModel>
    suspend fun getReviewListByPlace(id: String): List<GetReviewModel>

}