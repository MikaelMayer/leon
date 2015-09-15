/* Copyright 2009-2015 EPFL, Lausanne */

package leon
package synthesis

import purescala.Definitions._
import purescala.Constructors._
import purescala.Expressions._
import purescala.ExprOps._
import Witnesses._

/** Storage location for a "choose" problem.
  * 
  * @param fd The function definition
  * @param pc The Precondition
  * @param source The expression generating the choose. Can be equal to the parameter ch.
  * @param ch The Choose syntax tree itself
  * @param eb The examples attached to the function definition if the choose is top-level.
  */
case class ChooseInfo(fd: FunDef,
                      pc: Expr,
                      source: Expr,
                      ch: Choose,
                      eb: ExamplesBank) {

  val problem = Problem.fromChooseInfo(this)
}

/** Companion object for [[ChooseInfo]] */
object ChooseInfo {
  
  /** Extracts all [[ChooseInfo]] from the program */
  def extractFromProgram(ctx: LeonContext, prog: Program): List[ChooseInfo] = {
    val functions = ctx.findOption(SharedOptions.optFunctions) map { _.toSet }

    def excludeByDefault(fd: FunDef): Boolean = {
      fd.annotations contains "library"
    }

    val fdFilter = {
      import OptionsHelpers._
      filterInclusive(functions.map(fdMatcher(prog)), Some(excludeByDefault _))
    }

    // Look for choose()
    val results = for (f <- prog.definedFunctions if f.body.isDefined && fdFilter(f);
                       ci <- extractFromFunction(ctx, prog, f)) yield {
      ci
    }

    results.sortBy(_.source.getPos)
  }

  /** Extracts all [[ChooseInfo]] from a function definition */
  def extractFromFunction(ctx: LeonContext, prog: Program, fd: FunDef): Seq[ChooseInfo] = {

    val actualBody = and(fd.precondition.getOrElse(BooleanLiteral(true)), fd.body.get)
    val term = Terminating(fd.typed, fd.params.map(_.id.toVariable))

    val eFinder = new ExamplesFinder(ctx, prog)

    // We are synthesizing, so all examples are valid ones
    val functionEb = eFinder.extractFromFunDef(fd, partition = false)

    for ((ch, path) <- new ChooseCollectorWithPaths().traverse(actualBody)) yield {
      val outerEb = if (path == BooleanLiteral(true)) {
        functionEb
      } else {
        ExamplesBank.empty
      }

      val ci = ChooseInfo(fd, and(path, term), ch, ch, outerEb)

      val pcEb = eFinder.generateForPC(ci.problem.as, path, 20)
      val chooseEb = eFinder.extractFromProblem(ci.problem)

      ci.copy(eb = (outerEb union chooseEb) union pcEb)
    }
  }
}
