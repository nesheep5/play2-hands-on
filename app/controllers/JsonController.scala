package controllers

import play.api.mvc._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._

// memo: play2が提供するJSONライブラリ(内部的にはJavaのJacksonを利用)
import play.api.libs.json._
import play.api.libs.functional.syntax._

/** コンパニオンオブジェクト */
object JsonController {
  // UserRowをJSONに変換するためWritesを定義
  // memo: オブジェクト→JSON変換は Writes
  implicit val UserRowWritesWrites = (
      // memo: 「__」は JsPath のエイリアス
    (__ \ "id"       ).write[Long]   and
    (__ \ "name"     ).write[String] and
    (__ \ "companyId").writeNullable[Int]
  )(unlift(UsersRow.unapply))
}

class JsonController @Inject()(val dbConfigProvider: DatabaseConfigProvider)
    extends Controller
    with HasDatabaseConfigProvider[JdbcProfile] {

  /**
   * 一覧表示
   */
  def list = TODO

  /**
   * ユーザ登録
   */
  def create = TODO

  /**
   * ユーザ更新
   */
  def update = TODO

  /**
   * ユーザ削除
   */
  def remove(id: Long) = TODO
}