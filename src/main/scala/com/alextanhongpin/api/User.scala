package com.alextanhongpin.api


trait UsersApi extends JsonMappings{
  val usersApi =
    (path("users") & get ) {
       complete (UsersDao.findAll.map(_.toJson))
    }~
    (path("users"/IntNumber) & get) { id =>
        complete (UsersDao.findById(id).map(_.toJson))
    }~
    (path("users") & post) { entity(as[User]) { user =>
        complete (UsersDao.create(user).map(_.toJson))
      }
    }~
    (path("users"/IntNumber) & put) { id => entity(as[User]) { user =>
        complete (UsersDao.update(user, id).map(_.toJson))
      }
    }~
    (path("users"/IntNumber) & delete) { userId =>
      complete (UsersDao.delete(userId).map(_.toJson))
    }
}

trait JsonMappings extends DefaultJsonProtocol {
  implicit val userFormat = jsonFormat5(User)
  implicit val postFormat = jsonFormat4(Post)
  implicit val commentFormat = jsonFormat4(Comment)
}


def toJson(implicit writer: JsonWriter[T]): JsValue