import agilesitesng.deploy.model.DeployModel.{Site, Asset}
import agilesitesng.deploy.model.Spooler

val s1 = Site(1l, "TestSite")
val a2 = Asset("A", 2l, "TestAsset")

Spooler.insert(1, s1)

Spooler.serialize

Spooler.extract()

Spooler.insert(1, s1)

Spooler.insert(2, a2)

Spooler.serialize

Spooler.boh

Spooler.extract()
Spooler.extract()

