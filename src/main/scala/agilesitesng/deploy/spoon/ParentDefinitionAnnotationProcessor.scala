package agilesitesng.deploy.spoon

import agilesites.annotations.{ParentDefinition, ContentDefinition}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class ParentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ParentDefinition, CtClass[_]] {

  def process(a: ParentDefinition, cl: CtClass[_]) {
    println("...ParentDefinition!!!")
  }

}
