/* Copyright 2009-2015 EPFL, Lausanne
 * Author: Ravi
 * Date: 20.11.2013
 **/

import leon._
import leon.lang._
import leon.lang.string._
import leon.collection._

object String {
  def helloWorldConstant() = {
    "Hello"
  } ensuring ((res: String) => res == "Hello world")
  
  def helloWorldWhom(whom: String) = {
    val a = "Hello"
    a + " " + whom
  } ensuring ((res: String) => (whom, res) passes { case "World" => "Welcome world"})
  
  def helloWorldWhomComma(whom: String) = {
    val a = "Hello"
    a + "," + whom
  } ensuring ((res: String) => (whom, res) passes { case "World" => "Welcome world"})
  
  def helloWorldWhomSpace(whom: String) = {
    val a = "Hello"
    a + " " + whom
  } ensuring ((res: String) => (whom, res) passes { case "World" => "Hello,world"})

  def helloWorld3(greet: String, whom: String) = {
    greet + whom
  } ensuring ((res: String) => ((greet, whom), res) passes { case ("Hello", "World") => "Hello World" })
}


