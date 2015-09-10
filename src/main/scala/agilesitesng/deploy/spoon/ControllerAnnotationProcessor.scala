package agilesitesng.deploy.spoon

import agilesites.annotations.{Controller, CSElement}
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class ControllerAnnotationProcessor
  extends AbstractAnnotationProcessor[Controller, CtClass[_]]
  with SpoonUtils {

  def process(a: Controller, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(50,
      SpoonModel.Controller(Uid.generate(s"Controller.${name}"),
        name, class2file(name)))
    println("...Controller")
  }

}
