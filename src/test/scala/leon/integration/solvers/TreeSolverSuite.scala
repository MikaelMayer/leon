package leon.integration.solvers
package leon.integration.solvers

import org.scalatest.FunSuite
import org.scalatest.Matchers
import leon.test.helpers.ExpressionsDSL
import leon.solvers.string.StringSolver
import leon.purescala.Common.FreshIdentifier
import leon.purescala.Common.Identifier
import scala.collection.mutable.{HashMap => MMap}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.Timeouts
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import org.scalatest.FunSuite
import org.scalatest.concurrent.Timeouts
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._

/**
 * @author Mikael
 */
class TreeSolverSuite extends FunSuite with Matchers with ScalaFutures {
  val k = new MMap[String, Identifier]
  
  val x = FreshIdentifier("x")
  val y = FreshIdentifier("y")
  val z = FreshIdentifier("z")
  val u = FreshIdentifier("u")
  val v = FreshIdentifier("v")
  val w = FreshIdentifier("w")
  k ++= List("x" -> x, "y" -> y, "z" -> z, "u" -> u, "v" -> v, "w" -> w)
  
  test("FindMapping"){
    /*Cons(Person("John",15), Cons(Person("Mikael", 29), Nil()) =>
Ul(
  Cons(Li(Cons(B("John"), Cons(Text(" is "), Cons(I("15"), Cons(Text("years old"), Nil()))))),
  Cons(Li(Cons(B("John"), Cons(Text(" is "), Cons(I("29"), Cons(Text("years old"), Nil()))))),
  Nil())))*/
  }
}