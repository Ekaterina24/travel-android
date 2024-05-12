package com.example.travel.data.network

import android.app.Application
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.mapper.ReviewMapper
import com.example.travel.data.network.api.RetrofitInstance
import com.example.travel.data.mapper.SubscribeMapper
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.model.review.GetReviewModel
import com.example.travel.domain.repository.ReviewRepository
import com.example.travel.domain.repository.SubscribeRepository

class ReviewRepositoryImpl(
    application: Application
): ReviewRepository {
    private val mapper = ReviewMapper()

    override suspend fun createReview(token: String, review: CreateReviewModel) {
        RetrofitInstance.travelApi.createReview(token, mapper.mapModelToDto(review))
    }

    override suspend fun getReviewListByUser(token: String): List<GetReviewModel> {
        return mapper.mapGetListDtoToList(RetrofitInstance.travelApi.getReviewListByUser(token))
    }

    override suspend fun getReviewListByPlace(id: String): List<GetReviewModel> {
        return mapper.mapGetListDtoToList(RetrofitInstance.travelApi.getReviewListByPlace(id))
    }

}