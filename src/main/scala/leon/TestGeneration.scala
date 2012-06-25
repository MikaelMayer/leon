package leon

import purescala.Common._
import purescala.Definitions._
import purescala.Trees._
import purescala.TypeTrees._
import purescala.ScalaPrinter
import Extensions._
import scala.collection.mutable.{Set => MutableSet}


class TestGeneration(reporter: Reporter) extends Analyser(reporter) {

  def description: String = "Generate random testcases"
  override def shortDescription: String = "test"

  private val z3Solver = new FairZ3Solver(reporter)

  def analyse(program: Program) {
    z3Solver.setProgram(program)
    reporter.info("Running test generation")

    val testcases = generateTestCases(program)

    val topFunDef = program.definedFunctions.find(fd => isMain(fd)).get
//fd.body.exists(body => body match {
//      case Waypoint(1, _) => true
//      case _ => false
//    })
    val testFun = new FunDef(FreshIdentifier("test"), UnitType, Seq())
    val funInvocs = testcases.map(testcase => {
      val params = topFunDef.args
      val args = topFunDef.args.map{
        case VarDecl(id, tpe) => testcase.get(id) match {
          case Some(v) => v
          case None => simplestValue(tpe)
        }
      }
      FunctionInvocation(topFunDef, args)
    }).toSeq
    testFun.body = Some(Block(funInvocs, UnitLiteral))

    val Program(id, ObjectDef(objId, defs, invariants)) = program
    val testProgram = Program(id, ObjectDef(objId, testFun +: defs , invariants))
    testProgram.writeScalaFile("TestGen.scala")

    reporter.info("Running from waypoint with the following testcases:\n")
    reporter.info(testcases.mkString("\n"))
  }

  private def isMain(fd: FunDef): Boolean = {
    fd.annotations.exists(_ == "main")
  }

  def generatePathConditions(program: Program): Set[Expr] = {

    val callGraph = new CallGraph(program)
    callGraph.writeDotFile("testgen.dot")
    val constraints = callGraph.findAllPaths.map(path => {
      println("Path is: " + path)
      val cnstr = callGraph.pathConstraint(path)
      println("constraint is: " + cnstr)
      cnstr
    })
    constraints
  }

  private def generateTestCases(program: Program): Set[Map[Identifier, Expr]] = {
    val allPaths = generatePathConditions(program)

    allPaths.flatMap(pathCond => {
      reporter.info("Now considering path condition: " + pathCond)

      var testcase: Option[Map[Identifier, Expr]] = None
      //val z3Solver: FairZ3Solver = loadedSolverExtensions.find(se => se.isInstanceOf[FairZ3Solver]).get.asInstanceOf[FairZ3Solver]
        
      z3Solver.init()
      z3Solver.restartZ3
      val (solverResult, model) = z3Solver.decideWithModel(pathCond, false)

      solverResult match {
        case None => Seq()
        case Some(true) => {
          reporter.info("The path is unreachable")
          Seq()
        }
        case Some(false) => {
          reporter.info("The model should be used as the testcase")
          Seq(model)
        }
      }
    })
  }

  //private def generatePathConditions(funDef: FunDef): Seq[Expr] = if(!funDef.hasImplementation) Seq() else {
  //  val body = funDef.body.get
  //  val cleanBody = hoistIte(expandLets(matchToIfThenElse(body)))
  //  collectWithPathCondition(cleanBody)
  //}

  //private def generateTestCases(funDef: FunDef): Seq[Map[Identifier, Expr]] = {
  //  val allPaths = generatePathConditions(funDef)

  //  allPaths.flatMap(pathCond => {
  //    reporter.info("Now considering path condition: " + pathCond)

  //    var testcase: Option[Map[Identifier, Expr]] = None
  //    //val z3Solver: FairZ3Solver = loadedSolverExtensions.find(se => se.isInstanceOf[FairZ3Solver]).get.asInstanceOf[FairZ3Solver]
  //      
  //    z3Solver.init()
  //    z3Solver.restartZ3
  //    val (solverResult, model) = z3Solver.decideWithModel(pathCond, false)

  //    solverResult match {
  //      case None => Seq()
  //      case Some(true) => {
  //        reporter.info("The path is unreachable")
  //        Seq()
  //      }
  //      case Some(false) => {
  //        reporter.info("The model should be used as the testcase")
  //        Seq(model)
  //      }
  //    }
  //  })
  //}

  //prec: ite are hoisted and no lets nor match occurs
  //private def collectWithPathCondition(expression: Expr): Seq[Expr] = {
  //  var allPaths: Seq[Expr] = Seq()

  //  def rec(expr: Expr, path: List[Expr]): Seq[Expr] = expr match {
  //    case IfExpr(cond, then, elze) => rec(then, cond :: path) ++ rec(elze, Not(cond) :: path)
  //    case _ => Seq(And(path.toSeq))
  //  }

  //  rec(expression, List())
  //}

}



