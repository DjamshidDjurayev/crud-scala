package models

/**
 * Created by dzhuraev on 3/15/16.
 */

import sorm._

object Db extends Instance(
  entities = Set(
    Entity[Staffer](),
    Entity[PaperNew](),
    Entity[Positions](),
    Entity[History](),
    Entity[Admin](),
    Entity[Device](),
    Entity[Sms](),
    Entity[SmsRoma]()),
    url = "jdbc:postgresql://ec2-54-235-86-129.compute-1.amazonaws.com:5432/deffgmlr9s0npd",
//    url = "jdbc:postgresql://localhost/timespot_db",
    user = "wcsjeytyoxkjfd",
    password = "BqkOFGgnNGhgmsyYcvpVoN7JHx",
//    user = "postgres",
//    password = "postgres",
    initMode = InitMode.Create
)

