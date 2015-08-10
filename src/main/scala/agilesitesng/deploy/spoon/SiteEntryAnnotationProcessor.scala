package agilesitesng.deploy.spoon

import agilesites.annotations.SiteEntry
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass

/**
 * Created by msciab on 06/08/15.
 */
class SiteEntryAnnotationProcessor extends AbstractAnnotationProcessor[SiteEntry, CtClass[_]] {

  def process(a: SiteEntry, cl: CtClass[_]) {
    val name = cl.getQualifiedName
    Spooler.insert(50, SpoonModel.SiteEntry(Uid.generate(s"SiteEntry.${name}"), name))
    println("...SiteEntry!!!")
  }

}
