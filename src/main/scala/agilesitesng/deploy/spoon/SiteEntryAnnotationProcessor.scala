package agilesitesng.deploy.spoon

import agilesites.annotations.{SiteEntry, Site}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class SiteEntryAnnotationProcessor extends AbstractAnnotationProcessor[SiteEntry, CtClass[_]] {

  def process(a: SiteEntry, cl: CtClass[_]) {
    println("...SiteEntry!!!")
  }

}
