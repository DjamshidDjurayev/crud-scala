package models

/**
 * Created by dzhuraev on 3/15/16.
 */

import sorm._

object DBase extends Instance(
  entities = Set(
    Entity[Staffer](),
    Entity[PaperNew](),
    Entity[Positions](),
    Entity[History](),
    Entity[Admin]()),
    url = "jdbc:postgresql://localhost/timespot_db",
    user = "postgres",
    password = "postgres",
    initMode = InitMode.Create
)

