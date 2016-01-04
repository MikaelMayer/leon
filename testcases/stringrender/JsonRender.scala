/** 
  * Name:     ListRender.scala
  * Creation: 14.10.2015
  * Author:   Mika�l Mayer (mikael.mayer@epfl.ch)
  * Comments: First benchmark ever for solving string transformation problems. List specifications.
  */

import leon.lang._
import leon.annotation._
import leon.collection._
import leon.collection.ListOps._
import leon.lang.synthesis._

object JsonRender {
  sealed abstract class JValue
  
  case class JArray(l: List[JValue]) extends JValue
  case class JDict(l: List[(String, JValue)]) extends JValue
  case class JString(v: String) extends JValue
  case class JInt(v: Int) extends JValue
  case class JBoolean(b: Boolean) extends JValue
  
  /** Synthesis by example specs */
  @inline def psStandard(js: JValue) = (res: String) => (js, res) passes {
    case JArray(JInt(1)::JBoolean(true)::JString("abc")::Nil()) => """[1, true, "abc"]"""
    case JDict(("Cost", JInt(1))::("IsLimited", JBoolean(true))::("title", JString("abc"))::Nil()) => """{ Cost: 1, IsLimited: true, title: "abc"}"""
  }
  
  @inline def psIndented(js: JValue) = (res: String) => (js, res) passes {
    case JArray(JInt(1)::JBoolean(true)::JString("abc")::Nil()) =>
"""[
  1,
  true,
  "abc"
]"""
    case JDict(("Cost", JInt(1))::("IsLimited", JBoolean(true))::("title", JString("abc"))::Nil()) =>
"""{
  Cost: 1,
  IsLimited: true,
  title: "abc"
}"""

  @inline def psTupled(x: Int, yz: (String, Boolean)) = (res: String) => ((x, yz), res) passes {
    case (31, ("routed", true)) => """{ field1: 31, field2: "routed", field3: true }"""
  }
  
  def json(a : JValue): String =  {
    ???[String]
  } ensuring {
    (res : String) => (a, res) passes {
      case _ if false =>
        ""
      case JInt(0) =>
        "0"
      case JBoolean(true) =>
        "true"
      case JArray(Nil()) =>
        "[]"
      case JDict(Nil()) =>
        "{}"
      case JBoolean(false) =>
        "false"
      case JArray(Cons(JInt(0), Nil())) =>
        "[0]"
      case JArray(Cons(JBoolean(true), Nil())) =>
        "[true]"
      case JArray(Cons(JArray(Nil()), Nil())) =>
        "[[]]"
      case JArray(Cons(JDict(Nil()), Nil())) =>
        "[{}]"
      case JArray(Cons(JBoolean(false), Nil())) =>
        "[false]"
      case JArray(Cons(JInt(0), Cons(JInt(0), Nil()))) =>
        "[0, 0]"
      case JArray(Cons(JArray(Cons(JInt(0), Nil())), Nil())) =>
        "[[0]]"
      case JArray(Cons(JBoolean(true), Cons(JInt(0), Nil()))) =>
        "[true, 0]"
      // Problem: it does not cover everything !
    }
  }
  
  //////////////////////////////////////////////
  // Non-incremental examples: pure synthesis //
  //////////////////////////////////////////////
  def synthesizeStandard(x: Int, y: String, z: Boolean): String = {
    ???[String]
  } ensuring psStandard(x, y, z)

  def synthesizeIndented(xy: (Int, String), z: Boolean): String = {
    ???[String]
  } ensuring psIndented(xy, z)
  
  def synthesizeTupled(x: Int, yz: (String, Boolean)): String = {
    ???[String]
  } ensuring psTupled(x, yz)
}