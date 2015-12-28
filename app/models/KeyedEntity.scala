package models

import slick.lifted.Rep
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import slick.lifted.AbstractTable

/**
 * Entity type with numeric ID.
 */
trait KeyedEntity {

  /**
   * Get numeric ID.
   */
  def id: Int

}

/**
 * Table of an entity with numeric ID.
 */
trait KeyedEntityTable {

  /**
   * Get column with numeric ID.
   */
  def id: Rep[Int]

}

trait BaseTableQuery {

  self: TableQuery[_ <: AbstractTable[_]] =>

  def truncate = sqlu"TRUNCATE `#${baseTableRow.tableName}`"

}