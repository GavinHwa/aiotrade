/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: SeqFactory.scala 18895 2009-10-02 17:57:16Z odersky $


package org.aiotrade.lib.collection
package generic

/** A template for companion objects of Seq and subclasses thereof.
 *
 *  @since 2.8
 */
abstract class SeqFactory[CC[X] <: Seq[X] with GenericTraversableTemplate[X, CC]] extends TraversableFactory[CC] {
  
  /** This method is called in a pattern match { case Seq(...) => }.
   *
   *  @param x the selector value
   *  @return  sequence wrapped in an option, if this is a Seq, otherwise none
   */
  def unapplySeq[A](x: CC[A]): Some[CC[A]] = Some(x)
}
