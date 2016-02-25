/* Creation date: 26/02/2016
 * Author: Larabot
 */
import leon.lang._
import leon.annotation._
import synthesis._

object SynthesisChallenge {
  sealed abstract class Colleague
  case object PhD extends Colleague
  case object PostDoc extends Colleague
  case object Failed extends ((Colleague, Colleague) => Boolean) {
    def apply(c: Colleague, d: Colleague) = c == PhD
  }
  case object Succeeded extends ((Colleague, Colleague) => Boolean) {
    def apply(c: Colleague, d: Colleague) = c != PhD && d != PostDoc
  }

  /** Will Etienne obtain his PhD ?*/
  def irremplacabilityLemma(etienne: Colleague) = {
    choose((replacement: (Colleague, Colleague) => Boolean) =>
	  replacement(etienne, PhD))
  } ensuring {
    (kneuss: (Colleague, Colleague) => Boolean) => (etienne, kneuss) passes {
  	  case PhD => Succeeded
  	  case PostDoc => Failed
  	}
  }
}

