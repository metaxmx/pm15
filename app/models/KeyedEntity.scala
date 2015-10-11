package models

import slick.lifted.Rep

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