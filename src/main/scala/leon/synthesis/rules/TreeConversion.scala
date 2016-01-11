/* Copyright 2009-2015 EPFL, Lausanne */

package leon
package synthesis
package rules

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

import bonsai.enumerators.MemoizedEnumerator
import leon.evaluators.DefaultEvaluator
import leon.evaluators.StringTracingEvaluator
import leon.synthesis.programsets.DirectProgramSet
import leon.synthesis.programsets.JoinProgramSet
import leon.purescala.Common.FreshIdentifier
import leon.purescala.Common.Identifier
import leon.purescala.DefOps
import leon.purescala.Definitions.FunDef
import leon.purescala.Definitions.FunDef
import leon.purescala.Definitions.ValDef
import leon.purescala.ExprOps
import leon.solvers.Model
import leon.solvers.ModelBuilder
import leon.solvers.string.StringSolver
import leon.utils.DebugSectionSynthesis
import purescala.Constructors._
import purescala.Definitions._
import purescala.ExprOps._
import purescala.Expressions._
import purescala.Extractors._
import purescala.TypeOps
import purescala.Types._

/**
 * @author Mikael
 */
case object TreeConversion extends Rule("TreeConversion") {
  def instantiateOn(implicit hctx: SearchContext, p: Problem): Traversable[RuleInstantiation] = {
    //hctx.reporter.debug("StringRender:Output variables="+p.xs+", their types="+p.xs.map(_.getType))
    p.xs match {
      case List(v) if v.getType.isInstanceOf[ClassType] =>
        val description = "Creates a tree-to-tree conversion function"
        
        val examplesFinder = new ExamplesFinder(hctx.context, hctx.program)
        val examples = examplesFinder.extractFromProblem(p)
        
        val ruleInstantiations = ListBuffer[RuleInstantiation]()
        ruleInstantiations += RuleInstantiation("Tree to tree") {
          /*val (expr, synthesisResult) = createFunDefsTemplates(StringSynthesisContext.empty, p.as.map(Variable))
          val funDefs = synthesisResult.adtToString
          
          /*val toDebug: String = (("\nInferred functions:" /: funDefs)( (t, s) =>
                t + "\n" + s._2._1.toString
              ))*/
          //hctx.reporter.debug("Inferred expression:\n" + expr + toDebug)
          
          findSolutions(examples, expr, funDefs.values.toSeq)*/
          ???
        }
        
        ruleInstantiations.toList
        
      case _ => Nil
    }
  }
}