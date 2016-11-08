/* Copyright 2009-2016 EPFL, Lausanne */

package leon
package genc
package ir

import PrimitiveTypes._

/*
 * Collection of operators for IR with their precedence from the Scala language perspective.
 */
private[genc] object Operators {

  // NOTE It is possible to have more than one "From", but not several "To".
  //      (It will compile but ungracefully do not what is expected...)
  //
  // NOTE It is also expected that ToIntegral is combined with FromIntegral.
  //
  // NOTE The subset of operators supported here has luckily the same precedence
  //      rules in Scala/Java and C. We base the numbering here on the C one:
  //      http://en.cppreference.com/w/c/language/operator_precedence#Literals
  sealed trait Operator { this: From with To =>
    val symbol: String
    val precedence: Int

    override def toString = symbol
  }

  sealed trait From
  trait FromIntegral extends From
  trait FromLogical extends From

  sealed trait To
  trait ToIntegral extends To
  trait ToLogical extends To

  trait Integral extends FromIntegral with ToIntegral
  trait Logical extends FromLogical with ToLogical
  trait Comparative extends FromIntegral with ToLogical

  abstract class UnaryOperator(val symbol: String, val precedence: Int) extends Operator { this: From with To => }
  abstract class BinaryOperator(val symbol: String, val precedence: Int) extends Operator { this: From with To => }

  case object Plus extends BinaryOperator("+", 4) with Integral
  case object UMinus extends UnaryOperator("-", 2) with Integral
  case object Minus extends BinaryOperator("-", 4) with Integral
  case object Times extends BinaryOperator("*", 3) with Integral
  case object Div extends BinaryOperator("/", 3) with Integral
  case object Modulo extends BinaryOperator("%", 3) with Integral

  case object LessThan extends BinaryOperator("<", 6) with Comparative
  case object LessEquals extends BinaryOperator("<=", 6) with Comparative
  case object GreaterEquals extends BinaryOperator(">=", 6) with Comparative
  case object GreaterThan extends BinaryOperator(">", 6) with Comparative
  case object Equals extends BinaryOperator("==", 7) with FromIntegral with FromLogical with ToLogical
  case object NotEquals extends BinaryOperator("!=", 7) with FromIntegral with FromLogical with ToLogical

  case object Not extends UnaryOperator("!", 2) with Logical
  case object And extends BinaryOperator("&&", 11) with Logical
  case object Or extends BinaryOperator("||", 12) with Logical

  case object BNot extends UnaryOperator("~", 2) with Integral
  case object BAnd extends BinaryOperator("&", 8) with Integral
  case object BXor extends BinaryOperator("^", 9) with Integral
  case object BOr extends BinaryOperator("|", 10) with Integral
  case object BLeftShift extends BinaryOperator("<<", 5) with Integral
  case object BRightShift extends BinaryOperator(">>", 5) with Integral

}

