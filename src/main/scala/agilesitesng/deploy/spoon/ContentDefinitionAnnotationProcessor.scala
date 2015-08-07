package agilesitesng.deploy.spoon

import agilesites.annotations.{ContentDefinition, Site}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class ContentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ContentDefinition, CtClass[_]] {

  def process(a: ContentDefinition, cl: CtClass[_]) {
    println("...ContentDefinition!!!")
  }

}
