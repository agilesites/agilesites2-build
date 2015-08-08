package agilesitesng.deploy.spoon

import agilesites.annotations.Attribute
import agilesitesng.deploy.model.{DeployModel, Uid, Spooler}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.{CtField, CtClass}

/**
 * Created by msciab on 06/08/15.
 */
class AttributeAnnotationProcessor extends AbstractAnnotationProcessor[Attribute, CtField[_]] {

  def process(a: Attribute, cl: CtField[_]) {
    val name = cl.getSimpleName
    Spooler.insert(90, DeployModel.Attribute(Uid.generate(s"Attribute.${name}"), name))
    println("...AttributeDefinition!!!")
  }

}
