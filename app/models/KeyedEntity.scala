package models

import slick.model.Table
import slick.lifted.Rep

trait KeyedEntity {

  def id: Int

}

trait KeyedEntityTable {

  def id: Rep[Int]

}