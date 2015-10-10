package dao

import slick.driver.MySQLDriver.api._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.MySQLDriver
import slick.lifted.TableQuery
import slick.lifted.AbstractTable
import models.KeyedEntity
import models.KeyedEntityTable

abstract class GenericDAO[A <: KeyedEntity with B#TableElementType, B <: AbstractTable[A] with KeyedEntityTable](
    dbConfigProvider: DatabaseConfigProvider, tableQuery: TableQuery[B]) {

  def db = dbConfigProvider.get[MySQLDriver].db

  def getAll() = db.run {
    tableQuery.result
  }

  def getById(id: Int) = db.run {
    tableQuery.filter(_.id === id).result.headOption
  }

  def insert(entity: A) = db.run {
    tableQuery += entity
  }

}