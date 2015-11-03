package com.feec.search.api.controllers

import com.typesafe.config.ConfigFactory
import play.api.mvc._
import javax.inject._

@Singleton
class CheckEnv extends Controller {
  val config = ConfigFactory.load

  var currentCounter = 0

  def check = Action {
    currentCounter += 1
    println(config.getString("DB.EC.url"))
    Ok(s"current env on ${config.getString("env.debug")} : $currentCounter")
  }
}
