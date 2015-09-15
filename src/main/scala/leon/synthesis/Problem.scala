/* Copyright 2009-2015 EPFL, Lausanne */

package leon
package synthesis

import leon.purescala.Expressions._
import leon.purescala.ExprOps._
import leon.purescala.Types._
import leon.purescala.Common._
import leon.purescala.Constructors._
import leon.purescala.Extractors._
import Witnesses._

/** A synthesis triplet of the form:
  * ⟦ as ⟨ ws && pc | phi ⟩ xs ⟧
  * 
  * @param as The list of input identifiers so far
  * @param ws The axioms and other already proven theorems
  * @param pc The path condition so far
  * @param phi The formula on `as` and `xs` to satisfy
  * @param xs The list of output identifiers for which we want to compute a function
  */
case class Problem(as: List[Identifier], ws: Expr, pc: Expr, phi: Expr, xs: List[Identifier], eb: ExamplesBank = ExamplesBank.empty) {

  def inType  = tupleTypeWrap(as.map(_.getType))
  def outType = tupleTypeWrap(xs.map(_.getType))

  def asString(implicit ctx: LeonContext): String = {
    val pcws = and(ws, pc)

    val ebInfo = "/"+eb.valids.size+","+eb.invalids.size+"/"

    "⟦ "+as.map(_.asString).mkString(";")+", "+(if (pcws != BooleanLiteral(true)) pcws.asString+" ≺ " else "")+" ⟨ "+phi.asString+" ⟩ "+xs.map(_.asString).mkString(";")+" ⟧  "+ebInfo
  }

  // Qualified example bank, allows us to perform operations (e.g. filter) with expressions
  def qeb(implicit sctx: SearchContext) = QualifiedExamplesBank(this.as, this.xs, eb)
}

object Problem {
  def fromChoose(ch: Choose, pc: Expr = BooleanLiteral(true), eb: ExamplesBank = ExamplesBank.empty): Problem = {
    val xs = {
      val tps = ch.pred.getType.asInstanceOf[FunctionType].from
      tps map (FreshIdentifier("x", _, true))
    }.toList

    val phi = application(simplifyLets(ch.pred), xs map { _.toVariable})
    val as = (variablesOf(And(pc, phi)) -- xs).toList.sortBy(_.name)

    val TopLevelAnds(clauses) = pc

    val (pcs, wss) = clauses.partition {
      case w : Witness => false
      case _ => true
    }

    Problem(as, andJoin(wss), andJoin(pcs), phi, xs, eb)
  }

  def fromChooseInfo(ci: ChooseInfo): Problem = {
    // Same as fromChoose, but we order the input variables by the arguments of
    // the functions, so that tests are compatible
    val p = fromChoose(ci.ch, ci.pc, ci.eb)
    val argsIndex = ci.fd.params.map(_.id).zipWithIndex.toMap.withDefaultValue(100)

    p.copy( as = p.as.sortBy(a => argsIndex(a)))
  }
}
