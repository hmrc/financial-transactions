import models.{DirectDebits, DirectDebitDetail}
import play.api.libs.json._

//val x: DirectDebits = DirectDebits(true)

val details = Seq(DirectDebitDetail("000000001234567899", "VPP", "2018-04-08", "bill", "406082", "87654321"),
  DirectDebitDetail("000000001234567899", "VPP", "2018-04-09", "fred", "406082", "87654321"))

val x: DirectDebits = DirectDebits(true, Some(details))

val s = Json.toJson(x)
s