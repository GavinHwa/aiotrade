/*
 * Copyright (c) 2006-2007, AIOTrade Computing Co. and Contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *    
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *    
 *  o Neither the name of AIOTrade Computing Co. nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.aiotrade.lib.charting.chart.handledchart

import java.awt.Cursor
import java.awt.geom.GeneralPath
import org.aiotrade.lib.charting.chart.Chart
import org.aiotrade.lib.charting.chart.segment.ValuePoint
import org.aiotrade.lib.charting.view.pane.DrawingPane
import org.aiotrade.lib.util.collection.ArrayList

/**
 * 
 * 
 *
 * @author Caoyuan Deng
 */

trait HandledChart extends Comparable[HandledChart] {

  type C <: Chart

  /** init with known points */
  def init(drawing: DrawingPane, points: ArrayList[ValuePoint]): Unit
    
  def attachDrawingPane(drawing: DrawingPane): Unit
    
  def removeMouseAdapterOnPane: Unit

  /**
   * Only one handledChart can be active (means can be modified) concurrently,
   * we use the name 'active' instead of 'selected' here. (There may be more
   * than one handledChart being selected)
   */
  def activate: Unit
    
  def passivate: Unit
    
  def isActivated: Boolean
    
  def isAccomplished: Boolean
    
  def getCurrentHandlesPoints: ArrayList[ValuePoint]
    
  def getChart: C
    
  def getAllCurrentHandlesPath: GeneralPath
    
  def createNewInstance: HandledChart
    
  def getCursor: Cursor
    
}



