package com.example.travel.data.network.mapper

import com.example.travel.data.network.dto.AudioDTO
import com.example.travel.data.network.dto.CityDTO
import com.example.travel.data.network.dto.LoginDTO
import com.example.travel.data.network.dto.PlaceDTO
import com.example.travel.data.network.dto.TokenDTO
import com.example.travel.data.network.dto.UserDTO
import com.example.travel.domain.model.AudioModel
import com.example.travel.domain.model.CityModel
import com.example.travel.domain.model.LoginModel
import com.example.travel.domain.model.PlaceModel
import com.example.travel.domain.model.RegisterModel
import com.example.travel.domain.model.TokenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.util.Date

class UserMapper {
    fun mapDtoToModel(userDto: UserDTO) = RegisterModel(
        username = userDto.username,
        email = userDto.email,
        password = userDto.password
    )

    fun mapDtoToModel(tokenDto: TokenDTO) = TokenModel(
        accessToken = tokenDto.accessToken,
    )

    fun mapModelToDto(userModel: RegisterModel) = UserDTO(
        username = userModel.username!!,
        email = userModel.email!!,
        password = userModel.password!!
    )

    fun mapModelToDto(userModel: LoginModel) = LoginDTO(
        email = userModel.email!!,
        password = userModel.password!!
    )

//    fun mapListDtoToList(dtoList: List<AudioDTO>): List<AudioModel> {
//        val resList = mutableListOf<AudioModel>()
//        dtoList.forEach { resList.add(mapDtoToModel(it)) }
//        return resList
//    }
}