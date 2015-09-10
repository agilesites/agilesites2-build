package agilesitesng.deploy.spoon

import agilesites.annotations._
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import org.slf4j.LoggerFactory
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass
import scala.collection.JavaConversions._


/**
 * Created by msciab on 06/08/15.
 */
class ContentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ContentDefinition, CtClass[_]] {

  def logger = LoggerFactory.getLogger(classOf[ContentDefinitionAnnotationProcessor])

  def process(a: ContentDefinition, cl: CtClass[_]) {
    val name = cl.getSimpleName
    val description = if (a.description() == null || a.description().isEmpty) name else a.description()
    val contentType = cl.getSuperclass.getSimpleName
    val attributes = cl.getFields collect {
      case b if b.getAnnotation(classOf[Attribute]) != null => SpoonModel.AssetAttribute(b.getSimpleName, b.getAnnotation(classOf[Required]) != null)
    }
    val parent = if (cl.getAnnotation(classOf[Parent]) != null) Some(cl.getAnnotation(classOf[Parent]).value()) else None
    Spooler.insert(70, SpoonModel.ContentDefinition(Uid.generate(s"ContentDefinition.${name}"), name, description, contentType, parent, attributes.toList))
    logger.debug(s"Content definition - name:$name description: $description contentType: $contentType attributes: $attributes ")
  }
}
