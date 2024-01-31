package com.example.desafiojavapop

import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.UserModel

class FakeHomeModel {
    fun createFakeHomeModel() = HomeModel(
        id = 25965034,
        repo = "MaterialEditText",
        owner = UserModel("rengwuxian","https://avatars.githubusercontent.com/u/4454687?v=4"),
        description = "Repository Description",
        starsCount = 100,
        forksCount = 50,
        fullName = "rengwuxian/MaterialEditText",
        pageNumber = 1
    )
}
