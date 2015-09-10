package agilesitesng.deploy.spoon

import agilesites.annotations.{Parent, Required, Attribute, ParentDefinition}
import agilesitesng.deploy.model.{Spooler, SpoonModel, Uid}
import org.slf4j.LoggerFactory
import spoon.processing.AbstractAnnotationProcessor
import spoon.reflect.declaration.CtClass
import scala.collection.JavaConversions._

/**
 * Created by msciab on 06/08/15.
 */
class ParentDefinitionAnnotationProcessor extends AbstractAnnotationProcessor[ParentDefinition, CtClass[_]] {

  def logger =  LoggerFactory.getLogger(classOf[ParentDefinitionAnnotationProcessor])

  def process(a: ParentDefinition, cl: CtClass[_]) {
    val name = cl.getSimpleName
    val description = if (a.description() == null || a.description().isEmpty) name else  a.description()
    val parentType = cl.getSuperclass.getSimpleName
    val attributes = cl.getFields  collect {
      case b if b.getAnnotation(classOf[Attribute]) != null => SpoonModel.AssetAttribute(b.getSimpleName, b.getAnnotation(classOf[Required]) != null)
    }
    val parent = if (cl.getAnnotation(classOf[Parent]) != null) Some(cl.getAnnotation(classOf[Parent]).value()) else None
    Spooler.insert(70, SpoonModel.ParentDefinition(Uid.generate(s"ParentDefinition.$name"), name, description, parentType, parent, attributes.toList))
    logger.debug(s"Parent definition - name:$name description: $description parentType: $parentType attributes: $attributes ")
  }

}
