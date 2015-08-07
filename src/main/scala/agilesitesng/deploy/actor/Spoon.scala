package agilesitesng.deploy.actor

import java.io.File

import akka.actor.{ActorLogging, Actor, Props}
import akka.event.LoggingReceive
import spoon.Launcher
import scala.collection.{mutable, JavaConversions}

/**
 * Created by msciab on 05/08/15.
 */
object Spoon {

  import Protocol._
  import JavaConversions._

  def actor() = Props[SpoonActor]

  class SpoonActor extends Actor with ActorLogging {

    def receive: Receive = config

    def config: Receive  = LoggingReceive {
      case SpoonInit(source: String, target: String, classpath: Seq[String]) =>
        println("--- spooon init ---")
        //this.getClass.getClassLoader.loadClass("spoon.Launcher")
        //println(source+target+classpath)
        //classpath.filter(_.indexOf("spoon")!= -1).foreach(println)
        //spoon.addInputResource(source)
        //spoon.setOutputDirectory(target)
        //spoon.setArgs(Array("--source-classpath", classpath.mkString(File.pathSeparator)))
        println("--- waiting for ask ---")
        context.become(process(null /*spoon*/))
    }

    def process(launcher: Launcher): Receive = LoggingReceive {
      case Ask(sender, SpoonRun(args)) =>
        //launcher.run(args.toArray)
        //val buffer: mutable.Buffer[CtType[_]] = mutable.Buffer() //launcher.getFactory.Class().getAll
        //sender ! SpoonReply(buffer.mkString("\n"))
        sender ! SpoonReply("hello world")
        context.become(config)
    }

    /*
    spoon.run();
    Factory factory = spoon.getFactory();
    // list all packages of the model
    for(CtPackage p : factory.Package().getAll()) {
      System.out.println("package: "+p.getQualifiedName());
    }
    // list all classes of the model
    for(CtSimpleType s : factory.Class().getAll()) {
      System.out.println("class: "+s.getQualifiedName());
    }
   */

  }

}
