package com.example.travel.data.mapper

import com.example.travel.data.network.dto.CreateReviewDTO
import com.example.travel.data.network.dto.CreateSubscribeDTO
import com.example.travel.data.network.dto.GetReviewDTO
import com.example.travel.data.network.dto.GetSubscribeDTO
import com.example.travel.domain.model.CreateSubscribeModel
import com.example.travel.domain.model.GetSubscribeModel
import com.example.travel.domain.model.review.CreateReviewModel
import com.example.travel.domain.model.review.GetReviewModel

class ReviewMapper {

    // Network
    private fun mapDtoToModel(review: CreateReviewDTO) = CreateReviewModel(
        placeId = review.placeId,
        text = review.text,
        rating = review.rating
    )

    fun mapModelToDto(review: CreateReviewModel) = CreateReviewDTO(
        placeId = review.placeId,
        text = review.text,
        rating = review.rating
    )

    fun mapCreateListDtoToList(dtoList: List<CreateReviewDTO>): List<CreateReviewModel> {
        val resList = mutableListOf<CreateReviewModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }

    private fun mapDtoToModel(getReviewDto: GetReviewDTO) = GetReviewModel(
        placeId = getReviewDto.placeId,
        text = getReviewDto.text,
        rating = getReviewDto.rating,
        id = getReviewDto.id,
        userId = getReviewDto.userId,
        date = getReviewDto.date
    )

    fun mapModelToDto(getReviewModel: GetReviewModel) = GetReviewDTO(
        placeId = getReviewModel.placeId,
        text = getReviewModel.text,
        rating = getReviewModel.rating,
        id = getReviewModel.id,
        userId = getReviewModel.userId,
        date = getReviewModel.date
    )

    fun mapGetListDtoToList(dtoList: List<GetReviewDTO>): List<GetReviewModel> {
        val resList = mutableListOf<GetReviewModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }

    // Local
//    fun mapModelToDbModel(subscribeModel: SubscribeModel) = SubscribeItem(
////        id = subscribeModel.id,
////        userId = subscribeModel.userId,
//        city = subscribeModel.city,
//        typeId = subscribeModel.typeId,
////        date = subscribeModel.date
//    )
//
//    fun mapModelListToDbModelList(subscribeList: List<SubscribeModel>) =
//        subscribeList.map { mapModelToDbModel(it) }
//
//    fun mapDbModelToModel(subscribeDb: SubscribeItem) = SubscribeModel(
////        id = subscribeDb.id,
////        userId = subscribeDb.userId,
//        typeId = subscribeDb.typeId,
//        city = subscribeDb.city,
////        date = subscribeDb.date
//    )
//
//    fun mapDbModelListToModelList(subscribeDbList: List<SubscribeItem>) =
//        subscribeDbList.map { mapDbModelToModel(it) }
}