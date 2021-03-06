package org.aiotrade.lib.trading

import java.util.Date
import java.util.logging.Logger
import scala.collection.mutable

/**
 * 
 * @author Caoyuan Deng
 */
abstract class TradeBroker extends Broker {
  private val log = Logger.getLogger(getClass.getName)
  
  /**
   * For opening order:
   *     If price is set, the funds will be ignored finally, but:
   *           If quantity is set, everything is ok 
   *           If quantity is not set, your should calcQuantityToOpen by funds and set it, and then don't set funds
   *     If price is not set
   *           If quantity is set, that makes no sense, you should set funds
   *           If quantity is not set, you should set funds, then, the price/quantity will be decided by Algorithmic Trading Strategy.
   * For closing order: 
   *     1. You should set quantity always, the funds makes no sense and will be ignored.
   *     2. If price is set, everything is ok
   *        If price is not set, the price will be decided by Algorithmic Trading Strategy.
   */  
  def toOrder(oc: OrderCompose): Option[Order] = {
    import oc._
    
    val time = timestamps(referIndex)
    val order = 
      if (side.isOpening) {
        
        if (account.availableFunds > 0) {
          if (price.isSet) {
            if (quantity.isSet) {
              Some(Order(account, sec, price, quantity, side, OrderType.Market))
            } else {
              if (funds.isSet) {
                val quantityToOpen = account.calcQuantityToOpen(price, funds, sec)
                Some(Order(account, sec, price, quantityToOpen, side, OrderType.Market))
              } else {
                println("You should set either quantity or funds " + oc)
                None
              }
            }
          } else { // price is not set
            if (quantity.isSet) {
              println("You should also set price when quantity is set, since even funds is set, this opening stratery is suspicious " + oc)
              None
            } else {
              if (funds.isSet) {
                Some(Order(account, sec, price, quantity, side, OrderType.Market, funds))
              } else {
                println("You should set funds when both price and quantity are not set " + oc)
                None
              }
            }
          }
        
        } else { // no availableFunds
          None
        }
          
      } else { // closing
          
        // @Note quantity of position may be negative because of sellShort etc.
        val quantityToClose = if (quantity.isSet) {
          math.abs(quantity)
        } else {
          positionOf(sec) match {
            case Some(position) => math.abs(position.quantity)
            case None => 0
          }  
        }
        
        Some(Order(account, sec, price, quantityToClose, side, OrderType.Market))
          
      }
       
    println("To order: %s".format(order))
    order map {x => x.time = time; x}
  }
  
  /**
   * For real trading, thie method can receive execution report one by one.
   */
  @throws(classOf[BrokerException])
  def processTrade(order: Order, time: Long, price: Double, quantity: Double, amount: Double, expenses: Double) {
    var deltas = List[OrderDelta]()

    val sec = order.sec
    log.info("Process trade for: " + order + ", time=" + new java.util.Date(time))
    val secOrders = executingOrders synchronized {executingOrders.getOrElse(sec, new mutable.HashSet[Order]())}

    var toRemove = List[Order]()
    order.status match {
      case OrderStatus.PendingNew | OrderStatus.Partial =>
        order.tpe match {
          case OrderType.Market =>
            deltas ::= OrderDelta.Updated(order)
            fill(order, time, price, quantity, amount, expenses)

          case OrderType.Limit =>
            order.side match {
              case (OrderSide.Buy | OrderSide.SellShort) if price <= order.price =>
                deltas ::= OrderDelta.Updated(order)
                fill(order, time, price, quantity, amount, expenses)

              case (OrderSide.Sell | OrderSide.BuyCover) if price >= order.price =>
                deltas ::= OrderDelta.Updated(order)
                fill(order, time, price, quantity, amount, expenses)
                
              case _ =>
            }

          case _ =>
            log.info("Order tpe is " + order.tpe + ", won't be filled!")
        }

      case _ => 
        log.info("Order status is " + order.status + ", won't be filled!")
    }

    if (order.status == OrderStatus.Filled) {
      toRemove ::= order
    }

    if (toRemove.nonEmpty) {
      executingOrders synchronized {
        secOrders --= toRemove
        if (secOrders.isEmpty) {
          executingOrders -= sec
        } else {
          executingOrders(sec) = secOrders
        }
      }
    }

    if (deltas.nonEmpty) {
      publish(OrderDeltasEvent(this, deltas))
    }
  }
  
  /**
   * Fill order by price and size, this will also process binding account.
   * Usually this method is used only in paper worker
   */
  def fill(order: Order, time: Long, price: Double, quantity: Double, amount: Double, expenses: Double) {
    val fillingQuantity = math.min(quantity, order.remainQuantity)
    
    if (fillingQuantity > 0) {
      val execution = FullExecution(order, time, price, fillingQuantity, amount, expenses)
      order.account.processTransaction(order, execution)
    } else {
      log.warning("Filling Quantity <= 0: feedPrice=%s, feedSize=%s, remainQuantity=%s".format(price, quantity, order.remainQuantity))
    }
  }

}
