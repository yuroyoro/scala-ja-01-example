package com.yuroyoro.scja01ex.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util.Helpers._

class Member extends LongKeyedMapper[Member] with IdPK {
  def getSingleton = Member

  object name extends MappedString(this, 100) 
  object description extends MappedString(this, 100) 
}

object Member extends Member 
  with LongKeyedMetaMapper[Member] 
  with LongCRUDify[Member]{
  override def fieldOrder = List(name , description )
}
