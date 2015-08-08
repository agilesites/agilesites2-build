package agilesitesng.deploy.spoon

import agilesites.annotations.{CSElement, Site}
import agilesitesng.deploy.model.{Uid, DeployModel, Spooler}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class CSElementAnnotationProcessor extends AbstractAnnotationProcessor[CSElement, CtClass[_]] {

  def process(a: CSElement, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(50, DeployModel.CSElement(Uid.generate(s"CSElement.${name}"), name))
    println("...CSElement!!!")
  }

}
