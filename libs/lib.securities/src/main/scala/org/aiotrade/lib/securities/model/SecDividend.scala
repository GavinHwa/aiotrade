package org.aiotrade.lib.securities.model

import java.io.Serializable
import ru.circumflex.orm.Table

object SecDividends extends Table[SecDividend] {
  val sec = "secs_id" BIGINT() REFERENCES(Secs)

  val prevClose = "prevClose" DOUBLE()
  val adjWeight = "adjWeight" DOUBLE()
  //val adjOffset = "adjOffset" DOUBLE()
  val cashBonus = "cashBonus" DOUBLE()
  val shareBonus = "shareBonus" DOUBLE()
  val shareRight = "shareRight" DOUBLE()
  //val sharePrice = "sharePrice" DOUBLE()
  val sharePrice = "shareRightPrice" DOUBLE()
  val registerDate = "registerDate" BIGINT()
  val dividendDate = "dividendDate" BIGINT()
}

@serializable
class SecDividend {
  @transient
  var sec: Sec = _

  var prevClose: Double = _
  var adjWeight: Double = _
  var adjOffset: Double = _
  var cashBonus: Double = _
  var shareBonus: Double = _ // bonus issue, entitle bonus share
  var shareRight: Double = _ // allotment of shares in sharePrice
  var sharePrice: Double = _ // price of allotment of share
  var registerDate: Long = _
  var dividendDate: Long = _
  
  final def dividedClose = accurateAdjust(prevClose)
  
  /**
   * p' = (p - offset) / weight 
   * weight = (p - offset) / p'
   * offset = p - p' * weight
   */
  final def setAdjParams(p1: Double, p1Adj: Double, p2: Double, p2Adj: Double) {
    adjWeight = (p1 - p2) / (p1Adj - p2Adj)
    adjOffset = p1 - p1Adj * adjWeight
  }
  
  private def cashAfterwards = cashBonus - shareRight * sharePrice  // adjWeight
  private def shareAfterwards = 1 + shareRight + shareBonus         // adjOffset

  final def accurateAdjust(price: Double)   = (price - cashBonus + shareRight * sharePrice) / (1 + shareRight + shareBonus)
  final def accurateUnadjust(price: Double) = price * (1 + shareRight + shareBonus) + (cashBonus - shareRight * sharePrice)
  
  final def adjust(price: Double) = {
    val p = accurateAdjust(price)
    if (p != price) p else (if (adjWeight != 0) (price - adjOffset) / adjWeight else price)
  }
  
  final def unadjust(price: Double) = {
    val p = accurateUnadjust(price)
    if (p != price) p else (if (adjWeight != 0) price * adjWeight + adjOffset else price)
  }
  
  final def forwardAdjust(price: Double) = adjust(price)
  final def backwradAdjust(price: Double) = unadjust(price)
}

