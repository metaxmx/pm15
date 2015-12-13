package dao

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import models.{ KeyedEntity, KeyedEntityTable }
import slick.driver.MySQLDriver
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery
import slick.profile.RelationalProfile

abstract class GenericDAO[A <: KeyedEntity, B <: KeyedEntityTable with RelationalProfile#Table[A]](
    dbConfigProvider: DatabaseConfigProvider, tableQuery: TableQuery[B]) {

  def db = dbConfigProvider.get[MySQLDriver].db

  def getAll() = db.run {
    tableQuery.result
  }

  def getById(id: Int) = db.run {
    tableQuery.filter(_.id === id).result.headOption
  }

  def insert(entity: A) = db.run {
    (tableQuery returning tableQuery.map { _.id }) += entity
  }

  def delete(id: Int) = db.run {
    models.Tags.filter(_.id === id).delete
  } map {
    numChanged => numChanged > 0
  }

}