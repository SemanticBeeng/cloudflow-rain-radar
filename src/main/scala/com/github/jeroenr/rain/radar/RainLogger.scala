package com.github.jeroenr.rain.radar

import akka.event.Logging
import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import org.apache.avro.specific.SpecificRecordBase

import scala.reflect.ClassTag

abstract class LoggerStreamlet[T <: SpecificRecordBase : ClassTag](template: String,
                                                                   logLevel: Logging.LogLevel = Logging.InfoLevel) extends AkkaStreamlet {
  val inlet = AvroInlet[T](name = "in")
  val shape = StreamletShape.withInlets(inlet)

  override def createLogic = new RunnableGraphStreamletLogic() {
    def runnableGraph = {
      sourceWithOffsetContext(inlet).map { element ⇒
        system.log.log(logLevel, template, element)
        element
      }.to(sinkWithOffsetContext)
    }
  }
}

class RainLogger extends LoggerStreamlet[Rain]("Rain detected: {}")

class ClutterLogger extends LoggerStreamlet[Clutter]("Clutter detected: {}")
