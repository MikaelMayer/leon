/* Creation date: 26/02/2016
 * Author: Larabot
 */
import leon.lang._
import leon.collection._
import leon.annotation._
import synthesis._

object SynthesisChallenge {
  case class PhDstudent(approvals: Set[PhDstudent])
  
  def approves(colleague: PhDstudent, candidate: PhDstudent): Boolean = {
    colleague.approvals contains candidate
  }
  
  def approvePhD(colleagues: List[PhDstudent], candidate: PhDstudent): Boolean = colleagues match {
    case Nil() => true
    case Cons(colleague, tail) => approves(colleague, candidate) && approvePhD(tail, candidate)
  }

  /** Will Etienne obtain his PhD ?*/
  def approvals(etienne: PhDstudent): List[PhDstudent] = {
    choose((colleagues: List[PhDstudent]) =>
      approvePhD(colleagues, etienne) && colleagues.size > 7
    )
  }
}
