package com.example.travel.presentation.places

enum class TypeCategory {
    LANDMARK,
    PARK,
    MUSEUM,
    CAFE,
    PRODUCT
}

val TypeCategoryApi = mapOf(
    TypeCategory.LANDMARK to "attraction",
    TypeCategory.PARK to "place",
    TypeCategory.MUSEUM to "building",
    TypeCategory.CAFE to "branch",
    TypeCategory.PRODUCT to "branch",
)

val TypeCategoryRu = mapOf(
    TypeCategory.LANDMARK to "Достопримечательности",
    TypeCategory.PARK to "Парки",
    TypeCategory.MUSEUM to "Музеи",
    TypeCategory.CAFE to "Кафе",
    TypeCategory.PRODUCT to "Продукты",
)