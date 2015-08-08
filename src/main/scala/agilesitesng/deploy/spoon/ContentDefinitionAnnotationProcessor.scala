package agilesitesng.deploy.spoon

import agilesites.annotations.{ContentDefinition, Site}
import agilesitesng.deploy.model.DeployModel.Attribute
import agilesitesng.deploy.model.{DeployModel, Uid, Spooler}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class ContentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ContentDefinition, CtClass[_]] {

  def process(a: ContentDefinition, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(70, DeployModel.ContentDefinition(Uid.generate(s"ContentDefinition.${name}"), name))
    println("...ContentDefinition!!!")
  }

}
