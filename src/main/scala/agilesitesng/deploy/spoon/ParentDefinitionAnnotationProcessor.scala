package agilesitesng.deploy.spoon

import agilesites.annotations.{ParentDefinition, ContentDefinition}
import agilesitesng.deploy.model.{Uid, DeployModel, Spooler}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class ParentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ParentDefinition, CtClass[_]] {

  def process(a: ParentDefinition, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(70, DeployModel.ParentDefinition(Uid.generate(s"ParentDefinition.${name}"), name))
    println("...ParentDefinition!!!")
  }

}
