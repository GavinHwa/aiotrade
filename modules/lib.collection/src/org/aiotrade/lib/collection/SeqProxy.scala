/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id: SeqProxy.scala 18895 2009-10-02 17:57:16Z odersky $


package org.aiotrade.lib.collection

/** This trait implements a proxy for sequence objects. It forwards
 *  all calls to a different sequence object.
 *
 *  @author  Martin Odersky
 *  @version 2.8
 *  @since   2.8
 */
trait SeqProxy[+A] extends Seq[A] with SeqProxyLike[A, Seq[A]] 
