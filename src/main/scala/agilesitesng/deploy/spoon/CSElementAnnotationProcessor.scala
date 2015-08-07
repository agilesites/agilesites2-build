package agilesitesng.deploy.spoon

import agilesites.annotations.{CSElement, Site}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class CSElementAnnotationProcessor extends AbstractAnnotationProcessor[CSElement, CtClass[_]] {

  def process(a: CSElement, cl: CtClass[_]) {
    println("...CSElement!!!")
  }

}
