package com.example.travel.domain.usecase.review

import com.example.travel.domain.model.DayPlaceModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.repository.DayPlacesRepository
import com.example.travel.domain.repository.ReviewRepository

class CreateReviewUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(token: String, review: CreateReviewModel) {
        repository.createReview(token, review)
    }
}