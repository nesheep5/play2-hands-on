package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.i18n.{ MessagesApi, I18nSupport }
import play.api.db.slick._
import slick.driver.JdbcProfile
import models.Tables._
import javax.inject.Inject
import scala.concurrent.Future
import slick.driver.H2Driver.api._
import java.lang.Number
// コンパニオンオブジェクトに定義したFormを参照するためにimport文を追加
import UserController._

/*  memo:
 *  オブジェクト ... シングルトンオブジェクト。静的なメソッドやメンバーを定義する。
 *  コンパニオンオブジェクト ... クラスと同じファイル内に同じ名前で定義されたオブジェクト。
 *                          クラス・オブジェクト間で互いにprivateなメンバーにアクセスできる
 */
object UserController {

  // memo: ケースクラス ... イミュータブルなオブジェクトを作るのに便利なので、JavaBean的な使い方をすることが多い。
  // フォームの値を格納するケースクラス
  case class UserForm(id: Option[Long], name: String, companyId: Option[Int])

  // formから送信されたデータ⇔クラスの変換を行う
  val userForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText(maxLength = 20),
      "companyId" -> optional(number))(UserForm.apply)(UserForm.unapply))

}


class UserController @Inject() (
  val dbConfigProvider: DatabaseConfigProvider,
  val messagesApi: MessagesApi) extends Controller
    with HasDatabaseConfigProvider[JdbcProfile]
    with I18nSupport {
  // memo @Inject...DI機能の利用(Play2.4から導入)
  // memo with...traitをmix-inするための記法(class ChildClass extends ParentClass with TraitA with TraitB)

  /**
   * 一覧表示
   */
  // memo: 「def メソッド名 = 本体」 この記法の場合、呼び出し時に()をつけない
  // memo: 無名関数「パラメータ => 本体」例：(x: Int) => x * x

  def list = Action.async { implicit rs =>
    // IDの昇順にすべてのユーザ情報を取得
    db.run(Users.sortBy(t => t.id).result).map { users =>
      // 一覧画面を表示
      Ok(views.html.user.list(users))
    }
  }

  /**
   * 編集画面表示
   */
  /*
   *  memo:
   *  Option ... 値があるかないかを表す型。SomeかNoneを返す。
   *  Some ... 値があることを表す型。Optionのサブクラス。
   *  None ... 値がないことを表す型。Optionのサブクラス。
   */
  def edit(id: Option[Long]) = Action.async { implicit rs =>
    // リクエストパラメータにIDが存在する場合
    val form = if(id.isDefined) {
      // IDからユーザ情報を1件取得
      db.run(Users.filter(t => t.id === id.get.bind).result.head).map { user =>
        // 値をフォームに詰める
        userForm.fill(UserForm(Some(user.id), user.name, user.companyId))
      }
    }else{
      // リクエストパラメータにIDが存在しない場合
      Future { userForm }
    }
    form.flatMap { form =>
      // 会社一覧を取得
      // memo: SELECT * FROM COMPANIES ORDER BY ID
      db.run(Companies.sortBy(_.id).result).map { companies =>
        Ok(views.html.user.edit(form, companies))
      }
    }
  }

  /**
   * 登録実行
   */
  def create = TODO

  /**
   * 更新実行
   */
  def update = TODO

  /**
   * 削除実行
   */
  def remove(id: Long) = TODO

}