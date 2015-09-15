/* Copyright 2009-2015 EPFL, Lausanne */

package leon
package repair

import purescala.Definitions._
import purescala.DefOps._

/** The repair phase. It list the available functions, and launches a [[leon.repair.Repairman repair manager]] on each of them. */
object RepairPhase extends UnitPhase[Program]() {
  val name = "Repair"
  val description = "Repairing"

  implicit val debugSection = utils.DebugSectionRepair

  def apply(ctx: LeonContext, program: Program) = {
    val repairFuns: Option[Seq[String]] = ctx.findOption(SharedOptions.optFunctions)
    val verifTimeoutMs: Option[Long] = ctx.findOption(SharedOptions.optTimeout) map { _ * 1000 }

    val reporter = ctx.reporter

    val fdFilter = {
      import OptionsHelpers._

      filterInclusive(repairFuns.map(fdMatcher(program)), None)
    }

    val toRepair = funDefsFromMain(program).toList.filter(fdFilter).filter{ _.hasPostcondition }.sortWith((fd1, fd2) => fd1.getPos < fd2.getPos)

    if (toRepair.isEmpty) reporter.warning("No functions found with the given names")
    
    for (fd <- toRepair) {
      val rep = new Repairman(ctx, program, fd, verifTimeoutMs, verifTimeoutMs)
      rep.repair()
    }

  }
}
