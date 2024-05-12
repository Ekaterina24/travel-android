package com.example.travel.data.mapper

import com.example.travel.data.local.db.UserItem
import com.example.travel.data.network.dto.LoginDTO
import com.example.travel.data.network.dto.TokenDTO
import com.example.travel.data.network.dto.UserDTO
import com.example.travel.data.network.dto.UserProfileDTO
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import com.example.travel.domain.model.UserProfileModel

class UserMapper {
    fun mapDtoToModel(userDto: UserDTO) = RegisterModel(
        username = userDto.username,
        email = userDto.email,
        password = userDto.password
    )

    fun mapDtoToModel(tokenDto: TokenDTO) = TokenModel(
        accessToken = tokenDto.accessToken,
        expiresIn = tokenDto.expiresIn,
    )

    fun mapModelToDto(userModel: RegisterModel) = UserDTO(
        username = userModel.username,
        email = userModel.email,
        password = userModel.password
    )

    fun mapModelToDto(userModel: UserProfileModel) = UserProfileDTO(
        id = userModel.id,
        username = userModel.username,
        email = userModel.email,
        scores = userModel.scores
    )

    fun mapDtoToModel(userDto: UserProfileDTO) = UserProfileModel(
        id = userDto.id,
        username = userDto.username,
        email = userDto.email,
        scores = userDto.scores
    )

    fun mapModelToDto(userModel: LoginModel) = LoginDTO(
        email = userModel.email,
        password = userModel.password
    )

    fun mapListDtoToList(dtoList: List<UserProfileDTO>): List<UserProfileModel> {
        val resList = mutableListOf<UserProfileModel>()
        dtoList.forEach { resList.add(mapDtoToModel(it)) }
        return resList
    }

    fun mapModelToDbModel(userModel: UserProfileModel) = UserItem(
        id = userModel.id,
        name = userModel.username,
        email = userModel.email,
        scores = userModel.scores
    )

//    fun mapModelListToDbModelList(audioList: List<AudioModel>) =
//        audioList.map { mapModelToDbModel(it) }

    fun mapDbModelToModel(userDb: UserItem) = UserProfileModel(
        id = userDb.id,
        username = userDb.name,
        email = userDb.email,
        scores = userDb.scores
    )

//    fun mapDbModelListToModelList(audioDbList: List<AudioItem>) =
//        audioDbList.map { mapDbModelToModel(it) }
}