package com.yuroyoro.scja01ex.comet

import java.net.{URL, HttpURLConnection}

import _root_.scala.xml.{XML, Text}
import _root_.scala.util.matching._

import _root_.net.liftweb._
import http._
import util._
import Helpers._
import js._
import JsCmds._
import S._ 
import SHtml._
import JE._
import net.liftweb.http.js.jquery.JqJsCmds._

class Twitter extends CometActor {
  override def defaultPrefix = Full("tw")
  
  // 10毎にリフレッシュ 
  ActorPing.schedule(this, FriendsTimeline, 10 seconds)

  private lazy val spanId = uniqueId+"_timelinespan"
  def render = bind("timeline" -> timelineSpan)
  def timelineSpan = (<span id={spanId}><div></div></span>)

  val url = "http://twitter.com/statuses/public_timeline.xml"
  var since_id = 0

  override def lowPriority = {
    case FriendsTimeline =>
      val urlConn = new URL(url)
        .openConnection.asInstanceOf[HttpURLConnection]

      urlConn.connect();
      urlConn.getResponseCode

      for (s <- XML.load(urlConn.getInputStream) \ "status" reverse ;
           id = s \ "id" text;
           if(id.toInt > since_id )) { 
        var (text, user_name, img_url) = (s \ "text" text,
           s \ "user" \ "name" text,
           s \ "user" \ "profile_image_url")
        
        since_id = id.toInt
        partialUpdate(PrependHtml(spanId,
          <div class="status">
            <span class="profile_img"><img src={img_url}/></span>
            <span class="user_name">{user_name}</span>
            <span class="message">{text}</span>
          </div> ))
          
      } 
      ActorPing.schedule(this, FriendsTimeline, 10 seconds)
  }
}

case object FriendsTimeline
