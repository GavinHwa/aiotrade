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
package org.aiotrade.platform.modules.indicator.basic;

import org.aiotrade.lib.math.timeseries.Var;
import org.aiotrade.lib.math.timeseries.computable.Factor;
import org.aiotrade.lib.math.timeseries.plottable.Plot;
import org.aiotrade.lib.indicator.ContIndicator;

/**
 *
 * @author Caoyuan Deng
 */
class MACDIndicator extends ContIndicator {
    _sname = "MACD"
    _lname = "Moving Average Convergence/Divergence"
    
    val periodFast   = Factor("Period EMA Fast", 12)
    val periodSlow   = Factor("Period EMA Slow", 26)
    val periodSignal = Factor("Period Signal",    9)
    
    val macd   = TimeVar[Float]("MACD",   Plot.Line)
    val signal = TimeVar[Float]("SIGNAL", Plot.Line)
    val osc    = TimeVar[Float]("OSC",    Plot.Stick)
    
    protected def computeCont(begIdx:Int, size:Int) :Unit = {
        var i = begIdx
        while (i < size) {
            macd(i) = macd(i, C, periodSlow, periodFast)
            
            signal(i) = ema(i, macd, periodSignal)
            
            osc(i) = macd(i) - signal(i)
            i += 1
        }
    }
    
}