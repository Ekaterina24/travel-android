package com.example.travel.domain.usecase.review

import com.example.travel.domain.model.review.GetReviewModel
import com.example.travel.domain.repository.ReviewRepository

class GetReviewListByPlaceUseCase(
    private val repository: ReviewRepository
) {
    suspend operator fun invoke(id: String): List<GetReviewModel> {
        return repository.getReviewListByPlace(id)
    }
}