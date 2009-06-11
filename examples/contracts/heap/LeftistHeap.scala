package contracts.heap

import scala.collection.immutable.Multiset

/**
 * Example adapted from book "Purely Functional Data Structures"
 * by Chris Okasaki.
 */ 
  
object LeftistHeap {
  
  // UTILITARY FUNCTS USED FOR THE SPECIFICATION
  def min(xs: List[Elem]): Elem = {
    def inner(xs: List[Elem], local: Elem): Elem = xs match {
      case Nil => local
      case head :: tail =>
        if(local <= head)
          inner(tail,local)
        else
          inner(tail,head)
    }
    inner(xs,Elem(Integer.MAX_VALUE))
  }
  
   def content(t : Heap): Multiset[Elem] = {
    def inner(t: Heap, mset: Multiset[Elem]): Multiset[Elem] = t match {
      case E => mset
      case T(rank,el,left,right) => inner(right, inner(left, mset +++ List(el)))
    }
    inner(t,Multiset.empty[Elem])
  } 
}

sealed abstract class Heap  {
  import funcheck.lib.Specs._
  import LeftistHeap._
  
  /** mege the current heap <code>this</code> with the <code>that</code> 
   *  heap.
   */
  def merge(that: Heap): Heap = { this match {
    case E => that
    case T(_,x,a1,b1) => that match {
      case E => this
      case T(_,y,_,_) if x <= y =>
        a1.makeT(x,b1.merge(that))
      case T(_,y,a2,b2) if x > y =>
        a2.makeT(y,merge(b2))
    }
  }} ensuring(res => content(res).equals(content(this) +++ content(that)))
  
  //val heapMerge = forAll( (thiz: Heap, that: Heap) => content(thiz.merge(that)).equals(content(thiz) +++ content(that)))
  //val heapMerge = forAll[(Heap,Heap)] (heaps => content(heaps._1.merge(heaps._2)).equals(content(heaps._1) +++ content(heaps._2)))
  
  /** helper function that calculates the rank of a <code>T</code> node
   *  and swaps its children if necessary.
   */ 
  private def makeT(x: Elem, that: Heap): Heap = { 
    if(rankk >= that.rankk) {
      T(that.rankk + 1, x, this, that)
    } else {
      T(rankk + 1, x, that, this)
    }
  } 

  /** find the rank of a heap */
  def rankk: Int = { this match {
    case E => 0
    case T(rank,_,_,_) => rank
  }} 
  
  /** insert an element in the current heap <code>this</code>*/
  def insert(x: Elem): Heap = {
    merge(T(1,x,E,E))
  } ensuring(res => content(res)(x) == content(this)(x) + 1)
  
  //val heapInsert = forAll( (heap: Heap, value: Elem) => content(heap.insert(value))(value) == content(heap)(value) + 1)
  //val heapInsert = forAll[(Heap,Elem)]( p => content(p._1.insert(p._2))(p._2) == content(p._1)(p._2) + 1)
  
  /** Find the smallest element of the current heap <code>this</code>. 
   *  Invariant on this data structure entails that the minimum is at the root.
   */ 
  def findMin: Elem = { this match {
    case E => error(toString())
    case T(_,x,_,_) => x
  }} ensuring(res => res == min(content(this).elements.toList))
  
  //val heapFindMin = forAll{ heap : Heap => (heap.rankk > 0) ==> (heap.findMin == min(content(heap).elements.toList))}
  //val heapFindMin = forAll{ heap : Heap => (heap.rankk > 0) ==> (heap.findMin == min(content(heap).elements.toList))}
  
  /** Delete the smallest element of the current heap <code>this</code>.
   *  Invariant on this data structure entails that the minimum is at the root.
   */
  def deleteMin: Heap = {this match {
    case E => Predef.error(toString())
    case T(_,_,a: Heap,b: Heap) => a.merge(b)
  }} ensuring(res  => content(res).equals(content(this) - findMin))
  
  //val heapDeleteMin = forAll{ heap: Heap => (heap.rankk > 0) ==> (content(heap.deleteMin).equals(content(heap) - heap.findMin))}
  //val heapDeleteMin = forAll{ heap: Heap => (heap.rankk > 0) ==> (content(heap.deleteMin).equals(content(heap) - heap.findMin))}
  
}

case class T(val rank: Int, val el: Elem, val left: Heap,val right: Heap) extends Heap
case object E extends Heap


case class Elem(val value: Int) extends Ordered[Elem] {
  override def compare(that: Elem): Int = value compare that.value
   
  override def toString = "Elem("+value+")"
}

