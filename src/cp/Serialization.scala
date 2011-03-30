package cp

trait Serialization {
  import java.io.{FileInputStream,FileOutputStream,ObjectInputStream,ObjectOutputStream,File}
  import purescala.Definitions._
  import purescala.Trees._

  private val filePrefix = "serialized"
  private val fileSuffix = ""
  private val dirName = "serialized"
  private val directory = new java.io.File(dirName)

  private var cachedProgram : Option[Program] = None
  private val exprMap = new scala.collection.mutable.HashMap[String,Expr]()
  private val outputVarListMap = new scala.collection.mutable.HashMap[String,List[String]]()

  def programFileName(prog : Program) : String = {
    prog.mainObject.id.toString + fileSuffix
  }

  private def writeObject(file : File, obj : Any) : String = {
    val fos = new FileOutputStream(file)
    val oos = new ObjectOutputStream(fos)

    oos.writeObject(obj)
    oos.flush()
    fos.close()

    file.getAbsolutePath()

  }

  def writeObject(obj : Any) : String = {
    directory.mkdir()

    val file = java.io.File.createTempFile(filePrefix, fileSuffix, directory)
    
    writeObject(file, obj)
  }

  def writeProgram(prog : Program) : String = {
    directory.mkdir()

    val file = new java.io.File(directory, programFileName(prog))
    file.delete()

    writeObject(file, prog)
  }

  private def readObject[A](filename : String) : A = {
    val fis = new FileInputStream(filename)
    val ois = new ObjectInputStream(fis)

    val recovered : A = ois.readObject().asInstanceOf[A]
    fis.close()

    recovered
  }

  private def readProgram(filename : String) : Program = {
    readObject[Program](filename)
  }

  private def readExpr(filename : String) : Expr = {
    readObject[Expr](filename)
  }

  private def readOutputVarList(filename : String) : List[String] = {
    readObject[List[String]](filename)
  }

  def getProgram(filename : String) : Program = cachedProgram match {
    case None => val r = readProgram(filename); cachedProgram = Some(r); r
    case Some(cp) => cp
  }

  def getExpr(filename : String) : Expr = exprMap.get(filename) match {
    case Some(e) => e
    case None => val e = readExpr(filename); exprMap += (filename -> e); e
  }

  def getOutputVarList(filename : String) : List[String] = outputVarListMap.get(filename) match {
    case Some(l) => l
    case None => val l = readOutputVarList(filename); outputVarListMap += (filename -> l); l
  }
}

object Serialization extends Serialization
