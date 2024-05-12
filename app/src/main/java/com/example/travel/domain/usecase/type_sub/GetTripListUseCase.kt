package com.example.travel.domain.usecase.type_sub

import com.example.travel.domain.model.TypeSubModel
import com.example.travel.domain.repository.TypeSubRepository

class GetTypeSubListUseCase(
    private val repository: TypeSubRepository
) {
    suspend operator fun invoke(): List<TypeSubModel> {
        return repository.getTypeSubList()
    }
}