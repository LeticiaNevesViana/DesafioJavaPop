package com.example.desafiojavapop

import com.example.desafiojavapop.model.HomeModel
import com.example.desafiojavapop.model.UserModel

class FakeHomeModel {
    val fakeHomeModel = HomeModel(
        id = 25965034,
        repo = "MaterialEditText",
        description = "Repository Description",
        owner = UserModel(
            login = "rengwuxian",
            avatarUrl = "https://avatars.githubusercontent.com/u/4454687?v=4"
        ),
        stars = 100,
        forks = 50,
        fullName = "rengwuxian/MaterialEditText",
        pageNumber = 1
    )
}